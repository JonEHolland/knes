package com.knes.mappers

import com.knes.Cartridge
import com.knes.CartridgeState


class MMC1(
    private val rawBytes : ByteArray,
    private val header : Header,
    state : CartridgeState) : Cartridge(rawBytes, header, state) {

    private var chr4kLowBank : Int = 0x00
    private var chr4kHighBank : Int = 0x00
    private var chr8kBank = 0x00

    private var prg16kLowBank = 0x00
    private var prg16kHighBank = 0x00
    private var prg32kBank = 0x00

    private var loadRegister = 0x00
    private var loadRegisterCount = 0x00
    private var controlRegister = 0X00

    private var mirrorMode: MirrorMode = MirrorMode.HORIZONTAL

    private var ram: ByteArray = ByteArray(32768)

    init {
        // TODO - RAM should be loaded from a file if it exists
        reset()
    }

    override fun willCartInterceptCPURead(address: Int): Boolean {
        return (address in 0x6000..0x7FFF) || (address >= 0x8000)
    }

    override fun willCartInterceptCPUWrite(address: Int, data: Byte): Boolean {
        return (address in 0x6000..0x7FFF) || (address >= 0x8000)
    }

    override fun willCartInterceptPPURead(address: Int): Boolean {
        return (address <= 0x1FFF)
    }

    override fun willCartInterceptPPUWrite(address: Int, data: Byte): Boolean {
        return address <= 0x1FFF && chrBanks == 0
    }

    override fun cpuBusRead(address: Int): Byte {
        return if (address in 0x6000..0x7FFF) {
            // Mapped to Cart RAM
            ram[address and 0x1FFF]
        } else if ((controlRegister and 0x08) == 0x08) {
            // One of the two 16k Banks
            val bank = if (address < 0xBFFF) prg16kLowBank else prg16kHighBank
            prgMemory[((bank * 0x4000) + (address and 0x3FFF))]
        } else {
            // 32K bank
            prgMemory[(prg32kBank * 0x8000) + (address and 0x7FFF)]
        }
    }

    override fun cpuBusWrite(address: Int, data: Byte) {
        if (address in 0x6000..0x7FFF) {
            // Writing to RAM
            ram[address and 0x1FFF] = data
            return
        }

        // Writing to the Mapper Control Register
        if (address >= 0x8000) {
            val d = data.toInt() and 0xFF

            // If LSB is set, clear shift register
            if ((d and 0x80) == 0x80) {
                loadRegister = 0x0
                loadRegisterCount = 0
                controlRegister = controlRegister or 0x0C
                return
            }

            // Update shift register with the new data
            loadRegister = loadRegister shr 1
            loadRegister = loadRegister or ((d and 0x01) shl 4)
            loadRegisterCount++

            // All bits are loaded, set the internal settings of the mapper
            if (loadRegisterCount == 5) {
                when ((address shr 13) and 0x03) {
                    0 -> {
                        // Update Mirroring Mode
                        controlRegister = loadRegister and 0x1F
                        mirrorMode = when(controlRegister and 0x03) {
                            0 -> MirrorMode.ONE_SCREEN_LOW
                            1 -> MirrorMode.ONE_SCREEN_HIGH
                            2 -> MirrorMode.VERTICAL
                            3 -> MirrorMode.HORIZONTAL
                            else -> {
                                // Should never happen
                                MirrorMode.HORIZONTAL
                            }
                        }
                    }

                    1 -> {
                        // Update Lower Half of CHR Range, or map to one 8k bank
                        if ((controlRegister and 0x08) == 0x08) {
                            chr4kLowBank = loadRegister and 0x1F
                        } else {
                            chr8kBank = (loadRegister and 0x1E) shr 1
                        }
                    }

                    2 -> {
                        // Update higher half of CHR Range
                        if ((controlRegister and 0x08) == 0x08) {
                            chr4kHighBank = loadRegister and 0x1F
                        }
                    }

                    else -> {
                        // Update PRG Banks
                        val prgMode = (controlRegister shr 2) and 0x03
                        if (prgMode == 0 || prgMode == 1) {
                            // 32k Mode
                            prg32kBank = (loadRegister and 0x0E) shr 1
                        } else if (prgMode == 2) {
                            // 16k Mode with lower bank fixed to the first bank
                            prg16kLowBank = 0
                            prg16kHighBank = loadRegister and 0x0F
                        } else {
                            // 16k Mode with higher bank fixed to the last bank
                            prg16kLowBank = loadRegister and 0x0F
                            prg16kHighBank = prgBanks - 1
                        }
                    }

                }

                loadRegister = 0x00
                loadRegisterCount = 0
            }
        }
    }

    override fun ppuBusRead(address: Int): Byte {
        return if (chrBanks == 0) {
            chrMemory[address]
        } else {
            val bank = if (address <= 0x0FFF) chr4kLowBank else chr4kHighBank
            val mapped = if ((controlRegister and 0x10) == 0x10) {
                (bank * 0x1000) + (address and 0x0FFF)
            } else {
                (chr8kBank * 0x2000) + (address and 0x1FFF)
            }

            chrMemory[mapped]
        }
    }

    override fun ppuBusWrite(address: Int, data: Byte) {
        if (chrBanks == 0) {
            chrMemory[address] = data
        }
    }

    override fun mirrorMode(): MirrorMode {
        return mirrorMode
    }

    override fun mapperId(): Int {
        return 1
    }

    override fun reset() {
        controlRegister = 0x1C
        loadRegister = 0x00
        loadRegisterCount = 0x00

        chr4kLowBank = 0x0
        chr4kHighBank = 0x0
        chr8kBank = 0x0

        prg16kLowBank = 0x0
        prg16kHighBank = prgBanks - 1
        prg32kBank = 0
    }
}
