package com.knes.mappers

import com.knes.Cartridge

class CNROM (
    private val rawBytes : ByteArray,
    private val header : Header) : Cartridge(rawBytes, header) {

    private var chrBank = 0x00

    init {
        reset()
    }

    override fun willCartInterceptCPURead(address: Int): Boolean {
        return  (address >= 0x8000)
    }

    override fun willCartInterceptCPUWrite(address: Int, data: Byte): Boolean {
        return (address >= 0x8000)
    }

    override fun willCartInterceptPPURead(address: Int): Boolean {
        return (address <= 0x1FFF)
    }

    override fun willCartInterceptPPUWrite(address: Int, data: Byte): Boolean {
        return false
    }

    override fun cpuBusRead(address: Int): Byte {
        val mask = if (prgBankCount == 1) 0x3FFF else 0x7FFF
        return prgMemory[address and mask]
    }

    override fun cpuBusWrite(address: Int, data: Byte) {
        chrBank = (data.toInt() and 0xFF) and 0x03
    }

    override fun ppuBusRead(address: Int): Byte {
        return chrMemory[(chrBank * 0x2000) + address]
    }

    override fun ppuBusWrite(address: Int, data: Byte) {}

    override fun mirrorMode(): MirrorMode {
        return header.mirror
    }

    override fun mapperId(): Int {
        return 3
    }

    override fun reset() {
        chrBank = 0x00
    }
}
