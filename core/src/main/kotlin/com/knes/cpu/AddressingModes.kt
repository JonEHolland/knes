package com.knes.cpu

class AddressingModes(
    val state : CPUState,
    val cpu : CPU) {

    // Implied Addressing - No Data to Fetch
    // Uses No Extra Cycle
    fun impl() : Int {
        state.fetched = state.acc and 0xFF
        return 0
    }

    // Uses Data in the ACC
    fun acc() : Int {
        state.fetched = state.acc and 0xFF
        return 0
    }

    // Zero Page Addressing
    // Value following the opcode is an offset into the 0th page
    fun zpg() : Int {
        state.addressAbsolute = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        return 0
    }

    // Zero Page Addressing with X Offset
    // The value following the OPCode is considered as an offset
    // the content of the X Register is added to that offset
    // this offset is used to index the 0th page
    fun zpx() : Int {
        state.addressAbsolute = cpu.read(state.pc++) + state.x
        state.addressAbsolute = state.addressAbsolute and 0x00FF
        state.pc = state.pc and 0xFFFF

        return 0
    }

    // Zero Page Addressing with Y Offset
    // The value following the OPCode is considered as an offset
    // the content of the Y Register is added to that offset
    // this offset is used to index the 0th page
    fun zpy() : Int {
        state.addressAbsolute = cpu.read(state.pc++) + state.y
        state.addressAbsolute = state.addressAbsolute and 0x00FF
        state.pc = state.pc and 0xFFFF

        return 0
    }

    // Absolute Addressing
    // The immediate word after the opcode is read
    fun abs() : Int {
        val low = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val high = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        state.addressAbsolute = ((high shl 8) or low) and 0xFFFF

        return 0
    }

    // Absolute Addressing with X Offset
    // The immediate word after the opcode is read, the X register is then added to that address
    // Return extra cycle if crossing page boundary
    fun abx() : Int {
        val low = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val high = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        state.addressAbsolute = ((high shl 8) or low) + state.x
        state.addressAbsolute = state.addressAbsolute and 0xFFFF

        // TODO - What does this dummy read do?
        if (low + state.x > 0xFF || state.currentInstruction!!.operation == cpu.o::rol) {
            cpu.read(((high shl 8) and 0xFF00) or (state.addressAbsolute and 0xFF))
        }

        return if ((state.addressAbsolute and 0xFF00) != (high shl 8)) 1 else 0
    }

    // Absolute Addressing with Y Offset
    // The immediate word after the opcode is read, the Y register is then added to that address
    // Return extra cycle if crossing page boundary
    fun aby() : Int {
        val low = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val high = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        state.addressAbsolute = ((high shl 8) or low) + state.y
        state.addressAbsolute = state.addressAbsolute and 0xFFFF

        return if ((state.addressAbsolute and 0xFF00) != (high shl 8)) 1 else 0
    }

    // Immediate Addressing
    // The value following the opcode is directly used
    fun imm() : Int {
        state.addressAbsolute = state.pc++
        state.pc = state.pc and 0xFFFF

        return 0
    }

    // Relative Addressing
    // Value after opcode is considered a signed 8-bit value
    // That value is added to the current address
    fun rel() : Int {
        state.addressRelative = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        if (state.addressRelative and 0x80 == 0x80) {
            state.addressRelative = state.addressRelative or 0xFFFFFF00.toInt()
        }

        return 0
    }

    //  * Indirect Addressing
    //     * an address is read from the 16bit following the OPCode (8 LSB then 8 MSB)
    //     * We then read the Address at that location (8 LSB followed by 8 MSB)
    //     * This Addressing mode contains a bug in real hardware
    //     * If the address following the OPCode has the 8 LSB equals to 0xFF
    //     * then when reading the effective address the page boundary isn't crossed
    //     * it then read the first address of the same page

    fun ind() : Int {
        val low = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF
        val high = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val ptr = (high shl 8) or low
        if (low == 0xFF) {
            //Page boundary bug
            state.addressAbsolute = ((cpu.read(ptr and 0xFF00) shl 8) or cpu.read(ptr))
        }
        else {
            state.addressAbsolute = ((cpu.read((ptr + 1)) shl 8) or cpu.read(ptr))
        }

        return 0
    }

    // Indirect X
    // Read the address next to the OPCode and index in the 0th page
    // The X Register is then added to that address
    // We then load a 16bit value from that offset address
    fun indx() : Int {
        var ptr = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val lowPtr = ((ptr + state.x) and 0xFFFF) and 0x00FF
        val highPtr = ((ptr + state.x + 1) and 0xFFFF) and 0x00FF
        val low = cpu.read(lowPtr)
        val high = cpu.read(highPtr)

        state.addressAbsolute = ((high shl 8) or low) and 0xFFFF

        return 0
    }

    // Indirect Y
    //
    // Byte after opcode an offset in ZP. Read that to get 16 bit address, add Y to it to get final address
    fun indy() : Int {
        val ptr = cpu.read(state.pc++)
        state.pc = state.pc and 0xFFFF

        val low = cpu.read(ptr)
        val high = cpu.read((ptr + 1) and 0x00FF)

        state.addressAbsolute = (high shl 8) or low
        state.addressAbsolute = state.addressAbsolute + state.y
        state.addressAbsolute = state.addressAbsolute and 0xFFFF

        if (low + state.y > 0xFF) {
            cpu.read(((high shl 8) and 0xFF00) or (state.addressAbsolute and 0xFF))
        }

        return if ((state.addressAbsolute and 0xFF00) != (high shl 8)) 1 else 0
    }
}
