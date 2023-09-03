package com.knes.apu

class APUState {

    var clockCounter = 0
    var totalTime = 0.0
    var frameCounter = 0
    var cycle_remaining_since_4017_write = -1
    var audio_sample_until_skip = 0
    var frameIRQ = false
    var flagIRQinhibit = false
    var flag5StepMode = false

    fun reset() {
        frameCounter = 15
    }
}
