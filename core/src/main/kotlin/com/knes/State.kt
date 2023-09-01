package com.knes

import com.knes.apu.APUState
import com.knes.cpu.CPUState
import com.knes.ppu.PPUState

@OptIn(ExperimentalUnsignedTypes::class)
class State {
    companion object {
        fun Load() : State {
            // TODO - Save State
            return State()
        }
    }

    val bus = BusState()
    val cpu = CPUState()
    val ppu = PPUState()
    val apu = APUState()

    var clock : Int = 0

    fun reset() {
        bus.reset()
        cpu.reset()
        ppu.reset()
        apu.reset()

        clock = 0
    }

    fun save() {
        // TODO - Save State
    }
}
