package com.knes.mappers

import com.knes.Cartridge

class MMC3 (
    private val rawBytes : ByteArray,
    private val header : Header) : Cartridge(rawBytes, header) {

    private var target : Int = 0x00
    private var prgBankMode = false
    private var chrInversion = false

    private var register : IntArray = IntArray(8)
    private var chrBanks : IntArray = IntArray(8)
    private var prgBanks : IntArray = IntArray(4)

    private var irqActive = false
    private var irqEnabled = false
    private var irqCounter = 0x0
    private var irqReload = 0x0

    private var ram : ByteArray = ByteArray(32768)

    private var mirrorMode : MirrorMode = MirrorMode.HORIZONTAL

    init {
        // TODO - Load RAM from file
        reset()
    }

    override fun willCartInterceptCPURead(address: Int): Boolean {
        return (address in 0x6000..0x7FFF) || (address in 0x8000..0xDFFF) || (address >= 0xE000)
    }

    override fun willCartInterceptCPUWrite(address: Int, data: Byte): Boolean {
        return (address in 0x6000..0x7FFF) || (address in 0x8000..0xDFFF) || (address >= 0xE000)
    }

    override fun willCartInterceptPPURead(address: Int): Boolean {
        return (address <= 0x1FFF)
    }

    override fun willCartInterceptPPUWrite(address: Int, data: Byte): Boolean {
        return false
    }

    override fun cpuBusRead(address: Int): Byte {
        val bank = when {
            address in 0x8000..0x9FFF -> 0
            address in 0xA000..0xBFFF -> 1
            address in 0xC000..0xDFFF -> 2
            address >= 0xE000 -> 3
            else -> 0
        }
        return if (address in 0x6000..0x7FFF) {
            // Mapped to Cart RAM
            ram[address and 0x1FFF]
        } else {
            // Return Mapped Data
            prgMemory[(prgBanks[bank]) + (address and 0x1FFF)]
        }
    }

    override fun cpuBusWrite(address: Int, data: Byte) {
        if (address in 0x6000..0x7FFF) {
            // Writing to RAM
            ram[address and 0x1FFF] = data
            return
        }

        // Otherwise configuring the mapper
        if (address in 0x8000..0x9FFF) {
            // Even updates the register to target
            // Odd updates the register value
            if ((address and 0x1) != 0x1) {
                target = (data.toInt() and 0xFF) and 0x7
                prgBankMode = ((data.toInt() and 0xFF) and 0x40) == 0x40
                chrInversion = ((data.toInt() and 0xFF) and 0x80) == 0x80
            } else {
                register[target] = (data.toInt() and 0xFF)

                if (chrInversion) {
                    chrBanks[0] = register[2] * 0x0400
                    chrBanks[1] = register[3] * 0x0400
                    chrBanks[2] = register[4] * 0x0400
                    chrBanks[3] = register[5] * 0x0400
                    chrBanks[4] = (register[0] and 0xFE) * 0x0400
                    chrBanks[5] = register[0] * 0x0400 + 0x0400
                    chrBanks[6] = (register[1] and 0xFE) * 0x0400
                    chrBanks[7] = register[1] * 0x0400 + 0x0400
                } else {
                    chrBanks[0] = (register[0] and 0xFE) * 0x0400
                    chrBanks[1] = register[0] * 0x0400 + 0x0400
                    chrBanks[2] = (register[1] and 0xFE) * 0x0400
                    chrBanks[3] = register[1] * 0x0400 + 0x0400
                    chrBanks[4] = register[2] * 0x0400
                    chrBanks[5] = register[3] * 0x0400
                    chrBanks[6] = register[4] * 0x0400
                    chrBanks[7] = register[5] * 0x0400
                }

                // Set PRG Banks

                if (prgBankMode) {
                    prgBanks[2] = (register[6] and 0x3F) * 0x2000
                    prgBanks[0] = (prgBankCount * 2 - 2) * 0x2000
                } else {
                    prgBanks[0] = (register[6] and 0x3F) * 0x2000
                    prgBanks[2] = (prgBankCount * 2 - 2) * 0x2000
                }

                prgBanks[1] = (register[7] and 0x3F) * 0x2000
                prgBanks[3] = (prgBankCount * 2 - 1) * 0x2000

                return
            }
        }

        // Set Mirror Mode
        if (address in 0xA000..0xBFFF) {
            if ((address and 0x1) != 0x1) {
                mirrorMode = if (((data.toInt() and 0xFF) and 0x1) == 0x1){
                    MirrorMode.HORIZONTAL
                } else {
                    MirrorMode.VERTICAL
                }
            }

            return
        }

        // Set scanline for IRQ
        if (address in 0xC000..0xDFFF) {
            if ((address and 0x1) != 0x1) {
                irqReload = data.toInt() and 0xFF
            } else {
                irqCounter = 0
            }

            return
        }

        // Set IRQ Active or Not
        if (address >= 0xE000) {
            if ((address and 0x1) != 0x1) {
                irqEnabled = false
                irqActive = false
            } else {
                irqEnabled = true
            }

            return
        }
    }

    override fun ppuBusRead(address: Int): Byte {
        val bank = (address shr 10) and 0x7
        return chrMemory[chrBanks[bank] + (address and 0x03FF)]
    }

    override fun ppuBusWrite(address: Int, data: Byte) {}

    override fun irqRequested() : Boolean {
        return irqActive
    }

    override fun irqClear() {
        irqActive = false
    }

    override fun notifyScanline() {
        if (irqCounter == 0) {
            irqCounter = irqReload
        } else {
            irqCounter--
        }

        if (irqCounter == 0 && irqEnabled) {
            irqActive = true
        }
    }

    override fun mirrorMode(): MirrorMode {
        return mirrorMode
    }

    override fun mapperId(): Int {
        return 4
    }

    override fun reset() {
        target = 0
        prgBankMode = false
        chrInversion = false
        mirrorMode = MirrorMode.HORIZONTAL
        irqActive = false
        irqEnabled = false
        irqCounter = 0
        irqReload = 0

        for (i in 0..<4) {
            prgBanks[i] = 0
        }
        for (i in 0..<8){
            chrBanks[i] = 0
            register[i] = 0
        }

        prgBanks[0] = 0
        prgBanks[1] = 0x2000
        prgBanks[2] = (prgBankCount * 2 - 2) * 0x2000
        prgBanks[3] = (prgBankCount * 2 - 1) * 0x2000
    }
}
