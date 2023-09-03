package com.knes.apu

import com.knes.Bus

class APU(bus : Bus) {

    private var time = 0.0  // Keeps track of the current time
    private val sampleRate = 44100.0  // Sample rate in Hz
    private val frequency = 440.0  // Frequency of the sine wave in Hz
    private val amplitude = 0.5  // Amplitude (volume) of the sine wave


    fun tick() {

    }

    fun audioSample() : Float {
        // Increment time for the next sample
        if (time >= Double.MAX_VALUE / 1000) {
            time = 0.0
        }

        time += 1 / sampleRate
        val sample = amplitude * Math.sin(2 * Math.PI * frequency * time).toFloat()


        return sample.toFloat()
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
