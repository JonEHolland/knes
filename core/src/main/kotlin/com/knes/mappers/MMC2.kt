package com.knes.mappers

import com.knes.Cartridge
import com.knes.CartridgeState

class MMC2 (
    private val rawBytes : ByteArray,
    private val header : Header,
    state : CartridgeState) : Cartridge(rawBytes, header, state) {

    private var prgBankLow = 0x00
    private var prgBankHigh = 0x00

    init {
        reset()
    }

    override fun willCartInterceptCPURead(address: Int): Boolean {
        return (address in 0x6000..0xBFFF) || (address >= 0xC000)
    }

    override fun willCartInterceptCPUWrite(address: Int, data: Byte): Boolean {
        return (address >= 0x8000)
    }

    override fun willCartInterceptPPURead(address: Int): Boolean {
        return (address <= 0x1FFF)
    }

    override fun willCartInterceptPPUWrite(address: Int, data: Byte): Boolean {
        return address <= 0x1FFF && chrBanks == 0
    }

    override fun cpuBusRead(address: Int): Byte {
        val bank =
            if (address in 0x6000..0xBFFF) prgBankLow
            else if (address >= 0xC000) prgBankHigh
            else 0

        return prgMemory[(bank * 0x4000) + (address and 0x3FFF)]
    }

    override fun cpuBusWrite(address: Int, data: Byte) {
        prgBankLow = (data.toInt() and 0xFF) and 0x0F
    }

    override fun ppuBusRead(address: Int): Byte {
        return chrMemory[address]
    }

    override fun ppuBusWrite(address: Int, data: Byte) {
        if (chrBanks == 0) {
            chrMemory[address] = data
        }
    }

    override fun mirrorMode(): MirrorMode {
        return header.mirror
    }

    override fun mapperId(): Int {
        return 2
    }

    override fun reset() {
        prgBankLow = 0
        prgBankHigh = prgBanks - 1
    }
}
