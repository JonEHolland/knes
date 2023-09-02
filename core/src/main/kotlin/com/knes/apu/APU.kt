package com.knes.apu

import com.knes.Bus

class APU(bus : Bus) {

    fun tick() {

    }

    fun audioSample() : Float {
        return 0f
    }

    fun cpuBusRead(address : Int) : Byte {
        return 0
    }

    fun cpuBusWrite(address : Int, data : Byte) {

    }

    fun interruptRequested() : Boolean {
        return false
    }
}
