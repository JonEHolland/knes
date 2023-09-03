package com.knes

import com.knes.Debug.hex8
import com.knes.apu.APU
import com.knes.cpu.CPU
import com.knes.ppu.PPU

class Bus(
    romName : String,
    val state : State) {

    val cpu = CPU(this)
    val cart = Cartridge.Load(romName)
    val ppu = PPU(this)
    val apu = APU(this)


    fun read(addr :Int) : Byte {
        val address = addr and 0xFFFF

        with (state.bus) {
            var data : Byte = 0

            if (cart.willCartInterceptCPURead(address)) {
                // Cartridge ROM
                data = cart.cpuBusRead(address)
            } else if (address in 0..0x1FFF) {
                // System RAM
                data = cpuRam[address and 0x07FF]
            } else if (address in 0x2000..0x3FFF) {
                // PPU Registers
                // 8 registers, mirrored through the range
                data = ppu.cpuBusRead((address and 0x0007))
            } else if(address == 0x4015) {
                // APU
                apu.cpuBusRead(address)
            } else if (address in 0x4016..0x4017) {
                // Read LSB from controller state and shift register
                data = if (controllerState[address and 0x0001] and 0x80 > 0) 0x1 else 0x0
                controllerState[address and 0x0001] = controllerState[address and 0x0001] shl 1
            }

            return data
        }
    }

    fun write(addr : Int, data : Byte) {
        val address = addr and 0xFFFF

        with (state.bus) {
            if (cart.willCartInterceptCPUWrite(address, data)) {
                // Cartridge RAM
                cart.cpuBusWrite(address, data)
            } else if (address in 0x0000..0x1FFF) {
                // System RAM
                cpuRam[address and 0x07FF]= data
            } else if (address in 0x2000..0x3FFF) {
                // PPU Registers
                // 8 registers, mirrored through the range
                ppu.cpuBusWrite((address and 0x0007), data)
            } else if (address == 0x4013 || address == 0x4015 || address == 0x4017) {
                // APU Registers
                apu.cpuBusWrite(address, data)
            } else if (address == 0x4014) {
                // A write to this address enables DMA mode
                // So we will setup the DMA state so the CPU can halt and
                // OAM data can be copied to the PPU. The data written indicates the
                // page of data to copy to OAM.
                dmaPage = data.toInt() and 0xFF
                dmaOffset = 0x00
                dmaEnabled = true
                dmaWaitCycle = true
            } else if (address in 0x4016..0x4017) {
                // Controllers
               controllerState[data.toInt() and 0x1] = controller[data.toInt() and 0x1]
            }
        }
    }

    fun tick() {
        ppu.tick()
        apu.tick()

        // CPU runs 3x slower than PPU and APU
        if (state.clock % 3 == 0) {
            // If in DMA mode, CPU is halted
            if (!handleDMA()) {
                cpu.tick()
            }
        }

        // Pass NMI to CPU if PPU is requesting it
        if (ppu.nmiRequested()) {
            cpu.nmi()
        }

        if (apu.interruptRequested()) {
            cpu.irq()
        }

        // Pass IRQ to CPU if Cartridge is requesting it.
        if (cart.irqRequested()) {
            cart.irqClear()
            cpu.irq()
        }

        state.clock++
    }

    private fun handleDMA() : Boolean {
        with (state.bus) {
            if (!dmaEnabled) {
                return false
            }

            if (dmaWaitCycle) {
                // This is used to ensure DMA starts on an even clock cycle
                // Return early until this is false
                dmaWaitCycle = state.clock % 2 == 0
                return true
            }

            // DMA Can Start
            if (state.clock % 2 == 0) {
                // Even cycle means read from CPU
                dmaData = read(((dmaPage shl 8) or dmaOffset)).toInt() and 0xFF
            } else {
                // Odd cycle writes to PPU
                ppu.dma(dmaOffset, dmaData)
                dmaOffset++
                dmaOffset = dmaOffset and 0xFF

                // If wrapped around, all 256 bytes have been written
                // and DMA is complete
                if (dmaOffset == 0) {
                    dmaEnabled = false
                    dmaWaitCycle = true
                    dmaData = 0x00
                    dmaPage = 0x00
                }
            }
        }

        return true
    }

    fun reset() {
        state.reset()
        cart.reset() // Needed to ensure mappers setup correctly
        cpu.reset() // Needed because of the Reset Vector
    }

    fun debug() : String {
        val registers = "A:${hex8(state.cpu.acc)} X:${hex8(state.cpu.x)} Y:${hex8(state.cpu.y)}"
        val registers2 = "S:${hex8( state.cpu.sp)} P:${state.cpu.statusString()}"

        return "$registers $registers2"
    }
}
