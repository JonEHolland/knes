package com.knes.apu

class Envelope {
    var started =  false
    var disabled = false

    var volume = 0
    var output = 0

    private var dividerCount = 0
    private var decayCount = 0

    fun tick(loop : Boolean) {
        if (!started) {
            if (dividerCount == 0) {
                dividerCount = volume
                if (decayCount == 0) {
                    if (loop) decayCount = 15
                } else decayCount--
            } else dividerCount--
            dividerCount = dividerCount and 0xFFFF
        } else {
            started = false
            decayCount = 15
            dividerCount = volume
        }
        if (disabled) output = volume else output = decayCount
    }
}
