package com.knes.cpu

class CPUState {

    // Registers
    var acc : Int = 0x00
    var x : Int = 0x00
    var y : Int = 0x00
    var status : Int = 0x00
    var pc : Int = 0x00
    var sp : Int = 0x00

    // State Tracking
    var cyclesRemaining : Int = 0
    var totalCycles : Int = 0
    var halted : Boolean = false

    // Instruction Tracking
    var addressAbsolute : Int = 0x00
    var addressRelative : Int = 0x00
    var fetched : Int = 0x00
    var currentInstruction : CPU.I? = null


    fun reset() {
        halted = false
        acc = 0x00
        x = 0x00
        y = 0x00
        sp = 0xFD
        status = (Flags.I.value or Flags.U.value)
        addressRelative = 0x00
        addressAbsolute = 0x0000
    }

    fun statusString() : String {
        fun f(flag : Flags) : String {
            if ((status and flag.value) == flag.value) {
                return flag.name.uppercase()
            } else {
                return flag.name.lowercase()
            }
        }

        return f(Flags.N) +
            f(Flags.V) +
            f(Flags.U) +
            f(Flags.B) +
            f(Flags.D) +
            f(Flags.I) +
            f(Flags.Z) +
            f(Flags.C)
    }
}
