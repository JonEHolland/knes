package com.knes.mappers

import com.knes.Cartridge
import com.knes.CartridgeState

class NROM(rawBytes : ByteArray, val header : Header, state : CartridgeState) : Cartridge(rawBytes, header, state) {

    override fun willCartInterceptCPURead(address: Int): Boolean {
        return address >= 0x8000
    }

    override fun willCartInterceptCPUWrite(address: Int, data: Byte): Boolean {
        return address >= 0x8000
    }

    override fun willCartInterceptPPURead(address: Int): Boolean {
        return address <= 0x1FFF
    }

    override fun willCartInterceptPPUWrite(address: Int, data: Byte): Boolean {
        return address <= 0x1FFF && chrBanks == 0
    }

    override fun cpuBusRead(address: Int): Byte {
        return prgMemory[mappedAddress(address)]
    }

    override fun cpuBusWrite(address: Int, data : Byte) {
        prgMemory[mappedAddress(address)] = data
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
        return 0
    }

    override fun reset() {

    }

    private fun mappedAddress(address : Int) : Int {
        val mask = if (prgBanks > 1) 0x7FFF else 0x3FFF
        return address and mask
    }
}
