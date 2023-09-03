package com.knes.apu

class PulseChannel {

    var enabled : Boolean = false
    var halted : Boolean = false

    var lengthCounter = 0x00
    var sample = 0.0
    val envelope = Envelope()
    val sweeper = Sweeper()
    val oscillator = Oscillator()
    val sequencer = Sequencer()

    fun compute(time : Double)  {
        if (enabled && lengthCounter > 0 && !sweeper.muted && envelope.output > 2) {
            sequencer.tick(true) { s ->
                ((s and 0x01) shl 7) or ((s and 0xFE) shr 1)
            }

            if (sequencer.timer >= 8) {
                oscillator.frequency = 1789773.0f / (16.0f * (sequencer.reload + 1))
                oscillator.amplitude = (envelope.output - 1) / 16.0f
                sample = (oscillator.sample(time) / 2).toDouble()
            }
        } else {
            sample = 0.0
        }
    }

    fun setDutyCycle(data : Int) {
        when ((data and 0xC0) shr 6) {
            0x00 -> {
                sequencer.sequence = 0b00000001
                oscillator.duty_cycle = 0.125f
            }

            0x01 -> {
                sequencer.sequence = 0b00000011
                oscillator.duty_cycle = 0.250f
            }

            0x02 -> {
                sequencer.sequence = 0b00001111
                oscillator.duty_cycle = 0.500f
            }

            0x03 -> {
                sequencer.sequence = 0b11111100
                oscillator.duty_cycle = 0.750f
            }
        }
        halted = (data and 0x20) == 0x20
        envelope.volume = (data and 0x0F)
        envelope.disabled = (data and 0x10) == 0x10
    }

    fun setSweep(data : Int) {
        sweeper.enabled = data and 0x80 == 0x80
        sweeper.period = data and 0x70 shr 4
        sweeper.down = data and 0x08 == 0x08
        sweeper.shift = data and 0x07
        sweeper.reload = true
    }

    fun setTimerLow(data : Int) {
        sequencer.reload = (sequencer.reload and 0xFF00) or data
    }

    fun setTimerHigh(data : Int) {
        sequencer.reload = (sequencer.reload and 0x00FF) or ((data and 0x7) shl 8)
        sequencer.timer = sequencer.reload
    }

    fun updateLengthCounter(data : Int) {
        lengthCounter = APU.LENGTH_TABLE[(data and 0xF8) shr 3]
        envelope.started = true
    }

    fun resetLengthCounter() {
        lengthCounter = 0
    }

    fun tickLengthCounter() {
        if (!enabled) {
            lengthCounter = 0
        } else {
            if (lengthCounter > 0 && !halted) {
                lengthCounter--
            }
        }
    }

    fun tickEnvelope() {
        envelope.tick(halted)
    }

    fun tickSweeper(channel : Int)  {
        sequencer.reload = sweeper.tick(sequencer.reload, channel)
    }

    fun trackSweeper() {
        sweeper.track(sequencer.reload)
    }

}
