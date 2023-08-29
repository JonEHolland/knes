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

    // Write to this from LibGDX
    var controller : IntArray = IntArray(2)

    // Shift Registers for Controller Serial input
    var controllerState : IntArray = IntArray(2)

    fun reset() {
        cpuRam = ByteArray(2048)
        dmaEnabled = false
        dmaWaitCycle = true
        dmaData = 0x00
        dmaPage = 0x00
        dmaOffset = 0x00
    }
}
