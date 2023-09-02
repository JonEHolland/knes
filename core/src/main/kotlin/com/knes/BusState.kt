package com.knes

class BusState {

    // Le Ram
    var cpuRam = ByteArray(2048)

    // DMA State
    var dmaEnabled = false
    var dmaWaitCycle = true
    var dmaData : Int = 0x00
    var dmaPage : Int = 0x00
    var dmaOffset : Int = 0x00

    // Audio
    var audioTime : Double = 0.0

    // Write to this from LibGDX
    var controller : IntArray = IntArray(2)

    // Shift Registers for Controller Serial input
    var controllerState : IntArray = IntArray(2)

    fun reset() {
        cpuRam = ByteArray(2048)

        // Setup memory in alternating 4 bytes of 0x0 or 0xFF.
        // This matches how FCEUX sets memory on start
        // A real NES is undefined, but matching FCEUX helps me
        // debug
        for (i in cpuRam.indices) {
            cpuRam[i] = if ((i / 4) % 2 == 0) 0 else 255.toByte()
        }

        dmaEnabled = false
        dmaWaitCycle = true
        dmaData = 0x00
        dmaPage = 0x00
        dmaOffset = 0x00
    }
}
