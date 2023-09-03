package com.knes.apu

import java.util.function.Function

class Sequencer {
    var sequence = 0
    var timer = 0
    var output = 0
    var reload = 0

    fun tick(enabled: Boolean, func: Function<Int, Int>) {
        if (enabled) {
            timer--
            if (timer == -1) {
                timer = reload + 1
                sequence = func.apply(sequence)
                output = sequence and 0x1
            }
        }
    }
}
