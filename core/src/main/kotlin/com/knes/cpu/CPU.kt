package com.knes.cpu

import com.knes.Bus
import com.knes.Debug.hex16
import com.knes.Debug.hex8

class CPU(val bus : Bus) {

    data class I(
        val operation : () -> Int,
        val addressMode: () -> Int,
        val cycles: Int
    ) {
        // TODO - Change these to not be reflection hacks
        private val operationName : String by lazy {
            operation.toString()
                .replace("function ", "")
                .replace(" (Kotlin reflection is not available)", "")
        }

        // TODO - Change these to not be reflection hacks
        private val addressModeName : String by lazy {
            addressMode.toString()
                .replace("function ", "")
                .replace(" (Kotlin reflection is not available)", "")
        }

        fun pretty(pc : Int, index: Int, cpu : CPU) : String {
            with (cpu.bus.state.cpu) {
                val parsedAddress = when (addressModeName) {
                    "impl" -> "\t"
                    "acc" -> "A"
                    "abs" -> "$" + hex16(addressAbsolute)
                    "zpg" -> "$" + hex8(addressAbsolute)
                    "zpx" -> "$" + hex8(addressAbsolute) + ", X"
                    "abx" -> "$" + hex16(addressAbsolute) + ", X"
                    "aby" -> "$" + hex16(addressAbsolute) + ", Y"
                    "imm" -> "#$" + hex8(cpu.read(addressAbsolute))
                    "rel" -> "$" + hex16(addressAbsolute)
                    "ind" -> "($" + hex16(addressAbsolute) + ")"
                    "indy" -> "($" + hex16(addressAbsolute) + "), Y"
                    "indx" -> "($" + hex16(addressAbsolute) + "), X"
                    else -> "XXX"
                }

                return "${hex16(pc)}: XX XX XX  ${operationName.uppercase()} $parsedAddress"
            }
        }
    }

    val a = AddressingModes(bus.state.cpu, this)
    val o = Opcodes(bus.state.cpu, this)

    private val opcodes : ArrayList<I> = arrayListOf(
        // Row 0
        I(o::brk, a::impl, 7),
        I(o::ora, a::indx,  6),
        I(o::jam, a::impl, 2),
        I(o::slo, a::indx, 8),
        I(o::nop, a::zpg, 2),
        I(o::ora, a::zpg, 3),
        I(o::asl, a::zpg, 5),
        I(o::slo, a::zpg, 5),
        I(o::php, a::impl, 3),
        I(o::ora, a::imm,2),
        I(o::asl, a::acc,2),
        I(o::anc, a::imm,2),
        I(o::nop, a::abs,4),
        I(o::ora, a::abs,4),
        I(o::asl, a::abs,6),
        I(o::slo, a::abs,6),
        // Row 1
        I(o::bpl, a::rel,2),
        I(o::ora, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::slo, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::ora, a::zpx,4),
        I(o::asl, a::zpx,6),
        I(o::slo, a::zpx,6),
        I(o::clc, a::impl,2),
        I(o::ora, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::slo, a::aby,7),
        I(o::nop, a::abx,4),
        I(o::ora, a::abx,4),
        I(o::asl, a::abx,6),
        I(o::slo, a::abx,7),
        // Row 2
        I(o::jsr, a::abs,6),
        I(o::and, a::indx,6),
        I(o::jam, a::impl,2),
        I(o::rla, a::indx,8),
        I(o::bit, a::zpg,3),
        I(o::and, a::zpg,3),
        I(o::rol, a::zpg,5),
        I(o::rla, a::zpg,5),
        I(o::plp, a::impl,4),
        I(o::and, a::imm,2),
        I(o::rol, a::acc,2),
        I(o::anc, a::impl,2),
        I(o::bit, a::abs,3),
        I(o::and, a::abs,4),
        I(o::rol, a::abs,6),
        I(o::rla, a::abs,6),
        // Row 3
        I(o::bmi, a::rel,2),
        I(o::and, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::rla, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::and, a::zpx,4),
        I(o::rol, a::zpx,6),
        I(o::rla, a::zpx,6),
        I(o::sec, a::impl,2),
        I(o::and, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::rla, a::aby,7),
        I(o::nop, a::abx,3),
        I(o::and, a::abx,4),
        I(o::rol, a::abx,7),
        I(o::rla, a::abx,7),
        // Row 4
        I(o::rti, a::impl,6),
        I(o::eor, a::indx,6),
        I(o::jam, a::impl,2),
        I(o::sre, a::indx,8),
        I(o::nop, a::zpg,3),
        I(o::eor, a::zpg,3),
        I(o::lsr, a::zpg,5),
        I(o::sre, a::zpg,5),
        I(o::pha, a::impl,3),
        I(o::eor, a::imm,2),
        I(o::lsr, a::acc,2),
        I(o::alr, a::impl,2),
        I(o::jmp, a::abs,3),
        I(o::eor, a::abs,4),
        I(o::lsr, a::abs,6),
        I(o::sre, a::abs,6),
        // Row 5
        I(o::bvc, a::rel,2),
        I(o::eor, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::sre, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::eor, a::zpx,4),
        I(o::lsr, a::zpx,6),
        I(o::sre, a::zpx,6),
        I(o::cli, a::impl,2),
        I(o::eor, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::sre, a::aby,8),
        I(o::nop, a::abx,3),
        I(o::eor, a::abx,4),
        I(o::lsr, a::abx,7),
        I(o::sre, a::abx,8),
        // Row 6
        I(o::rts, a::impl,6),
        I(o::adc, a::indx,6),
        I(o::jam, a::impl,2),
        I(o::rra, a::indx,8),
        I(o::nop, a::zpg,3),
        I(o::adc, a::zpg,3),
        I(o::ror, a::zpg,5),
        I(o::rra, a::zpg,5),
        I(o::pla, a::impl,4),
        I(o::adc, a::imm,2),
        I(o::ror, a::acc,2),
        I(o::arr, a::impl,2),
        I(o::jmp, a::ind,5),
        I(o::adc, a::abs,4),
        I(o::ror, a::abs,6),
        I(o::rra, a::abs,6),
        // Row 7
        I(o::bvs, a::rel,2),
        I(o::adc, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::rra, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::adc, a::zpx,4),
        I(o::ror, a::zpx,6),
        I(o::rra, a::zpx,6),
        I(o::sei, a::impl,2),
        I(o::adc, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::rra, a::aby,7),
        I(o::nop, a::abx,2),
        I(o::adc, a::abx,4),
        I(o::ror, a::abx,7),
        I(o::rra, a::abx,7),
        // Row 8
        I(o::nop, a::imm,2),
        I(o::sta, a::indx,6),
        I(o::nop, a::imm,2),
        I(o::sax, a::indy,2),
        I(o::sty, a::zpg,3),
        I(o::sta, a::zpg,4),
        I(o::stx, a::zpg,3),
        I(o::sax, a::zpg,3),
        I(o::dey, a::impl,2),
        I(o::nop, a::imm,2),
        I(o::txa, a::impl,2),
        I(o::ane, a::impl,2),
        I(o::sty, a::abs,2),
        I(o::sta, a::abs,5),
        I(o::stx, a::abs,4),
        I(o::sax, a::abs,4),
        // Row 9
        I(o::bcc, a::rel,2),
        I(o::sta, a::indy,6),
        I(o::jam, a::impl,2),
        I(o::sha, a::indy,6),
        I(o::sty, a::zpx,4),
        I(o::sta, a::zpx,4),
        I(o::stx, a::zpy,4),
        I(o::sax, a::zpy,4),
        I(o::tya, a::impl,2),
        I(o::sta, a::aby,5),
        I(o::txs, a::impl,2),
        I(o::tas, a::aby,5),
        I(o::shy, a::abx,5),
        I(o::sta, a::abx,5),
        I(o::shx, a::aby,5),
        I(o::sha, a::aby,5),
        // Row A
        I(o::ldy, a::imm,2),
        I(o::lda, a::indx,6),
        I(o::ldx, a::imm,2),
        I(o::lax, a::indy,5),
        I(o::ldy, a::zpg,3),
        I(o::lda, a::zpg,3),
        I(o::ldx, a::zpg,3),
        I(o::lax, a::zpg,3),
        I(o::tay, a::impl,2),
        I(o::lda, a::imm,2),
        I(o::tax, a::impl,2),
        I(o::lxa, a::impl,2),
        I(o::ldy, a::abs,4),
        I(o::lda, a::abs,4),
        I(o::ldx, a::abs,4),
        I(o::lax, a::abs,4),
        // Row B
        I(o::bcs, a::rel,2),
        I(o::lda, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::lax, a::indy,5),
        I(o::ldy, a::zpx,4),
        I(o::lda, a::zpx,4),
        I(o::ldx, a::zpy,4),
        I(o::lax, a::zpy,4),
        I(o::clv, a::impl,2),
        I(o::lda, a::aby,4),
        I(o::tsx, a::impl,2),
        I(o::las, a::aby,4),
        I(o::ldy, a::abx,4),
        I(o::lda, a::abx,4),
        I(o::ldx, a::aby,4),
        I(o::lax, a::aby,4),
        // Row C
        I(o::cpy, a::imm,2),
        I(o::cmp, a::indx,6),
        I(o::nop, a::imm,2),
        I(o::dcp, a::indx,8),
        I(o::cpy, a::zpg,3),
        I(o::cmp, a::zpg,3),
        I(o::dec, a::zpg,5),
        I(o::dcp, a::zpg,5),
        I(o::iny, a::impl,2),
        I(o::cmp, a::imm,2),
        I(o::dex, a::impl,2),
        I(o::sax, a::impl,2),
        I(o::cpy, a::abs,4),
        I(o::cmp, a::abs,4),
        I(o::dec, a::abs,6),
        I(o::dcp, a::abs,6),
        // Row D
        I(o::bne, a::rel,2),
        I(o::cmp, a::indy,5),
        I(o::jam, a::impl,2),
        I(o::dcp, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::cmp, a::zpx,4),
        I(o::dec, a::zpx,6),
        I(o::dcp, a::zpx,6),
        I(o::cld, a::impl,2),
        I(o::cmp, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::dcp, a::aby,7),
        I(o::nop, a::abx,4),
        I(o::cmp, a::abx,4),
        I(o::dec, a::abx,6),
        I(o::dcp, a::abx,7),
        // Row E
        I(o::cpx, a::imm,2),
        I(o::sbc, a::indx,6),
        I(o::nop, a::imm,2),
        I(o::isc, a::indx,8),
        I(o::cpx, a::zpg,3),
        I(o::sbc, a::zpg,3),
        I(o::inc, a::zpg,5),
        I(o::isc, a::zpg,5),
        I(o::inx, a::impl,2),
        I(o::sbc, a::imm,2),
        I(o::nop, a::impl,2),
        I(o::usbc, a::impl,2),
        I(o::cpx, a::abs,4),
        I(o::sbc, a::abs,4),
        I(o::inc, a::abs,6),
        I(o::isc, a::abs,6),
        // Row F
        I(o::beq, a::rel,2),
        I(o::sbc, a::indy,6),
        I(o::jam, a::impl,2),
        I(o::isc, a::indy,8),
        I(o::nop, a::zpx,4),
        I(o::sbc, a::zpx,4),
        I(o::inc, a::zpx,6),
        I(o::isc, a::zpx,6),
        I(o::sed, a::impl,2),
        I(o::sbc, a::aby,4),
        I(o::nop, a::impl,2),
        I(o::isc, a::aby,7),
        I(o::nop, a::abx,4),
        I(o::sbc, a::abx,4),
        I(o::inc, a::abx,7),
        I(o::isc, a::abx,7),
    )

    var log  = true
    var logCount = 0

    fun tick() {
        with (bus.state.cpu) {
            if (halted) return

            if (cyclesRemaining <= 0) {

                // Clear State for next operation
                fetched = 0
                addressAbsolute = 0
                addressRelative = 0
                val busPrev = bus.debug()

                val pcTmp = pc // Store for debugging
                if (logCount > 50000) {
                    log = false
                }

                val index = read(pc)
                currentInstruction = opcodes[index]

                setFlag(Flags.U, true)
                pc++
                pc = pc and 0xFFFF

                val cyclesUsedByAddressing = currentInstruction!!.addressMode()
                val cyclesUsedByExecuting = currentInstruction!!.operation()

                cyclesRemaining = currentInstruction!!.cycles + cyclesUsedByAddressing + cyclesUsedByExecuting
                totalCycles += cyclesRemaining

                if (log) {
                    val debug = currentInstruction!!.pretty(pcTmp, index, this@CPU) + "   " + busPrev
                    println(debug)
                    logCount++
                }

                setFlag(Flags.U, true)

            }

            cyclesRemaining--
            bus.state.clock++
        }
    }

    fun fetch() : Int {
        with (bus.state.cpu) {
            fetched = when(currentInstruction!!.addressMode) {
                a::impl -> fetched
                a::acc -> fetched
                else -> read(addressAbsolute)
            }

            return fetched
        }
    }

    fun read(address : Int) : Int {
        return bus.read(address).toInt() and 0xFF
    }

    fun write (address: Int, data : Int) {
        bus.write(address, (data and 0xFF).toByte())
    }

    fun getFlag(flag : Flags) : Boolean {
        return (bus.state.cpu.status and flag.value) == flag.value
    }

    fun setFlag(flag : Flags, value : Boolean) {
        with (bus.state.cpu) {
            if (value) {
                status = (status or flag.value)
            } else {
                status = (status and flag.value.inv())
            }
        }
    }

    fun nmi() {
        with (bus.state.cpu) {
            // The I Flag cannot stop this
            if (!halted) {
                interrupt(8, nmiVector())
            }
        }
    }

    fun irq() {
        with (bus.state.cpu) {
            // I flag can mask this interrupt
            if (!getFlag(Flags.I) && !halted) {
                interrupt(7, irqVector())
            }
        }
    }

    fun reset() {
        with (bus.state.cpu) {
            pc = resetVector() and 0xFFFF
        }
    }

    fun interrupt(cyclesNeeded : Int, vectorAddress : Int) {

        with (bus.state.cpu) {

            // Push PC to stack, LSB first
            pushStack((pc shr 8) and 0xFF)
            pushStack((pc and 0xFF))

            // Push Status Register to stack
            setFlag(Flags.B, false)
            setFlag(Flags.U, true)
            setFlag(Flags.I, true)
            pushStack(status)

            /// Jump to the Interrupt Vector
            pc = vectorAddress

            // Add the cycles consumed by the interrupt
            cyclesRemaining += cyclesNeeded
        }
    }

    fun pushStack(data : Int) {
        with (bus.state.cpu) {
            write((0x0100 + sp), data)
            sp--
            sp = sp and 0xFF
        }
    }

    fun popStack() : Int {
        with (bus.state.cpu) {
            sp++
            sp = sp and 0xFF
            return read(0x0100 + sp)
        }
    }

    fun resetVector() : Int {
        with (bus.state.cpu) {
            addressAbsolute = 0xFFFC and 0xFFFF

            val low = read(0xFFFC)
            val high = read(0xFFFD)
            return (high shl 8) or low
        }
    }

    fun nmiVector() : Int {
        with (bus.state.cpu) {
            addressAbsolute = 0xFFFA and 0xFFFF

            val low = read(0xFFFA)
            val high = read(0xFFFB)
            return (high shl 8) or low
        }
    }

    fun irqVector() : Int {
        with (bus.state.cpu) {
            addressAbsolute = 0xFFFE and 0xFFFF

            val low = read(0xFFFE)
            val high = read(0xFFFF)
            return (high shl 8) or low
        }
    }

    fun decompile() : String? {
        with (bus.state.cpu) {
            val address = pc

            if (address == resetVector() -1 and 0xFFFF) {
                return null
            }

            val index = read(pc)
            currentInstruction = opcodes[index]

            pc++
            pc = pc and 0xFFFF

            // Addressing modes that rely on data to exist in memory won't work
            // when decompiling, so manually set the addresses
            if (currentInstruction!!.addressMode == a::imm ||
                currentInstruction!!.addressMode == a::indy ||
                currentInstruction!!.addressMode == a::indx ||
                currentInstruction!!.addressMode == a::ind) {

                addressAbsolute = pc++
                addressAbsolute = addressAbsolute and 0xFFFF
                pc = pc and 0xFFFF
            } else {
                currentInstruction!!.addressMode()
            }


            return currentInstruction!!.pretty(address, index, this@CPU)
        }
    }

}
