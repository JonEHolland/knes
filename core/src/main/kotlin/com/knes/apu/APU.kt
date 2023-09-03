package com.knes.apu

import com.knes.Bus

class APU(val bus : Bus) {

    companion object {
        private val CLOCK_TIME = .333333333 / 1789773.0
        val LENGTH_TABLE = intArrayOf(10, 254, 20, 2, 40, 4, 80, 6, 160, 8, 60, 10, 14, 12, 26, 14, 12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30)
    }


    val pulse1 : PulseChannel = PulseChannel()
    val pulse2 : PulseChannel = PulseChannel()

    fun tick(sampling : Boolean, timePerTick : Double) {
        with (bus.state.apu) {
            var quarter_frame = false
            var half_frame = false

            totalTime += CLOCK_TIME
            if (clockCounter % 3 == 0) {
                //dmc.clock()
                //if (enable_sampling) triangle.computeSample(timePerClock / 3, raw_audio)
                if (clockCounter % 6 == 0) {
                    //A write to 0x4017 will cause the frame counter to be reset after 4 CPU cycles (2 APU cycles)
                    if (cycle_remaining_since_4017_write == 0) {
                        frameCounter = 0
                        cycle_remaining_since_4017_write = -1
                    }
                    if (cycle_remaining_since_4017_write >= 0) cycle_remaining_since_4017_write -= 2
                    frameCounter++
                    if (flag5StepMode) {
                        quarter_frame =
                            frameCounter == 3729 || frameCounter == 7457 || frameCounter == 11186 || frameCounter == 18641
                        half_frame = frameCounter == 7457 || frameCounter == 18641
                        if (frameCounter == 18641) frameCounter = 0
                    } else {
                        quarter_frame =
                            frameCounter == 3729 || frameCounter == 7457 || frameCounter == 11186 || frameCounter == 14916
                        half_frame = frameCounter == 7457 || frameCounter == 14916
                        if (frameCounter == 14916) {
                            frameCounter = 0
                            if (!flagIRQinhibit) frameIRQ = true
                        }
                    }
                    if (quarter_frame) {
                        //triangle.clockLinearCounter()
                        if (sampling) {
                            pulse1.tickEnvelope()
                            pulse2.tickEnvelope()
                            //noise.tickEnvelope()
                        }
                    }
                    if (half_frame) {
                        pulse1.tickLengthCounter()
                        pulse2.tickLengthCounter()
//                        triangle.tickLengthCounter()
//                        noise.tickLengthCounter()
                        pulse1.tickSweeper(0)
                        pulse2.tickSweeper(1)
                    }
                    if (sampling) {
                        if (audio_sample_until_skip >= 2) {
                            pulse1.compute(totalTime)
                            pulse2.compute(totalTime)
                            //noise.computeSample()
                            //dmc.computeSample()
                            audio_sample_until_skip = 0
                        }
                        audio_sample_until_skip++
                    }
                }
            }
            pulse1.trackSweeper()
            pulse2.trackSweeper()

            clockCounter++
        }
    }

    fun audioSample() : Double {
        var sample = 0.0
        var p1 = pulse1.sample * 15
        val p2 = pulse2.sample * 15
        val t = 0.0
        val n = 0.0
        val d = 0.0

        sample = (0.00752 * (p1 + p2) + 0.00851 * t + 0.00494 * n + 0.00335 * d) * 1.5

        return sample * 1
    }

    fun reset() {
        // Clear all APU Registers
        for (i in 0x4000..0x4007) {
            cpuBusWrite(i, 0x00)
        }
        for (i in 0x4010..0x4013) {
            cpuBusWrite(i, 0x00)
        }

        bus.state.apu.reset()
        cpuBusWrite(0x4015, 0x00)
    }

    fun cpuBusRead(address : Int) : Byte {
        var data = 0x00
        if (address == 0x4015) {
            data = data or if (pulse1.lengthCounter > 0) 0x01 else 0x0
            data = data or if (pulse2.lengthCounter > 0) 0x02 else 0x0
//            data = data or if (triangle.lengthCounter > 0) 0x04 else 0x0
//            data = data or if (noise.lengthCounter > 0) 0x08 else 0x0
//            data = data or if (dmc.hasBytesLeft()) 0x10 else 0x0
//            data = data or if (dmc.hasInterrupt()) 0x80 else 0x0
            data = data or if (bus.state.apu.frameIRQ) 0x40 else 0x0
        }

        return (data and 0xFF).toByte()
    }

    fun cpuBusWrite(address : Int, d : Byte) {
        var data = d.toInt()
        with (bus.state.apu) {

            when (address) {
                0x4000 -> pulse1.setDutyCycle(data)
                0x4001 -> pulse1.setSweep(data)
                0x4002 -> pulse1.setTimerLow(data)
                0x4003 -> {
                    pulse1.setTimerHigh(data)
                    pulse1.updateLengthCounter(data)
                }

                0x4004 -> pulse2.setDutyCycle(data)
                0x4005 -> pulse2.setSweep(data)
                0x4006 -> pulse2.setTimerLow(data)
                0x4007 -> {
                    pulse2.setTimerHigh(data)
                    pulse2.updateLengthCounter(data)
                }

//                0x4008 -> triangle.writeLinearCounter(data)
//                0x400A -> triangle.writeTimerLow(data)
//                0x400B -> {
//                    triangle.writeTimerHigh(data)
//                    triangle.loadLengthCounter(data)
//                }

//                0x400C -> noise.writeEnvelope(data)
//                0x400E -> noise.updateReload(data)
//                0x400F -> noise.writeLengthCounter(data)
//                0x4010 -> dmc.writeRate(data)
//                0x4011 -> dmc.directLoad(data)
//                0x4012 -> dmc.writeSampleAddr(data)
//                0x4013 -> dmc.writeSampleLength(data)
                0x4015 -> {
                    pulse1.enabled = false
                    pulse2.enabled = false
//                    triangle.enable(false)
//                    noise.enable(false)
//                    dmc.clearIrq()
                    //if (data and 0x10 == 0x00) dmc.clearReader()
                    if (data and 0x1 == 0x1) pulse1.enabled = true else pulse1.resetLengthCounter()
                    if (data and 0x2 == 0x2) pulse2.enabled = true else pulse2.resetLengthCounter()
//                    if (data and 0x4 == 0x4) triangle.enable(true) else {
//                        triangle.resetLengthCounter()
//                        triangle.resetLinearCounter()
//                    }
//                    if (data and 0x8 == 0x8) noise.enable(true) else noise.resetLengthCounter()
                }

                0x4017 -> {
                    flag5StepMode = data and 0x80 == 0x80
                    flagIRQinhibit = data and 0x40 == 0x40
                    if (flagIRQinhibit) frameIRQ = false
                    cycle_remaining_since_4017_write = 4
                    if (flag5StepMode) {
                        pulse1.tickLengthCounter()
                        pulse1.tickEnvelope()
                        pulse1.tickSweeper(0)
                        pulse2.tickLengthCounter()
                        pulse2.tickEnvelope()
                        pulse2.tickSweeper(1)
//                        triangle.clockLinearCounter()
//                        triangle.clockLengthCounter()
//                        noise.clockEnvelope()
//                        noise.clockLengthCounter()
                    }
                }
            }
        }
    }

    fun interruptRequested() : Boolean {
        return false
    }
}
