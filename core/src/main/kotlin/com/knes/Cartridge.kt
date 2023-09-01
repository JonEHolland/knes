package com.knes

import com.knes.mappers.*
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths

abstract class Cartridge(
    private val rawBytes : ByteArray,
    private val header : Header) {

    enum class MirrorMode {
        HARDWARE,
        HORIZONTAL,
        VERTICAL,
        ONE_SCREEN_LOW,
        ONE_SCREEN_HIGH
    }

    class Header(data : ByteArray) {
        val prgRomChunks : Int
        val chrRomChunks : Int
        val prgRamSize : Int
        val mapperId : Int
        val trainerExists : Boolean
        val mirror : MirrorMode
        val iNES2Format : Boolean

        private val flag6 : Int
        private val flag7 : Int

        init {
            prgRomChunks = data[4].toInt() and 0xFF
            chrRomChunks = data[5].toInt() and 0xFF
            flag6 = data[6].toInt() and 0xFF
            flag7 = data[7].toInt() and 0xFF
            prgRamSize = data[8].toInt() and 0xFF

            mapperId = ((flag7 shr 4) shl 4) or (flag6 shr 4)
            mirror = if ((flag6 and 0x01) == 0x01) {
                MirrorMode.VERTICAL
            } else {
                MirrorMode.HORIZONTAL
            }

            trainerExists = (flag6 and 0x04) == 0x04
            iNES2Format = (flag7 and 0x0C) == 0x08
        }
    }

    companion object {
        fun Load(romName : String) : Cartridge {

            val bytes = Files.readAllBytes(Paths.get(romName))
            val header = Header(bytes)

            when (header.mapperId) {
                0 -> return NROM(bytes, header)
                1 -> return MMC1(bytes, header)
                2 -> return MMC2(bytes, header)
                3 -> return CNROM(bytes, header)
                4 -> return MMC3(bytes, header)

                else -> {
                    throw RuntimeException("Unsupported Mapper or Invalid ROM format.")
                }
            }

        }
    }

    val prgMemory : MutableList<Byte> = mutableListOf()
    val chrMemory : MutableList<Byte> = mutableListOf()

    protected var prgBankCount : Int
    protected var chrBankCount : Int

    init {

        if (header.iNES2Format) {
            prgBankCount = ((header.prgRamSize and 0x07) shl 8) or header.prgRomChunks
            chrBankCount = ((header.prgRamSize and 0x38) shl 8) or header.chrRomChunks
        } else {
            prgBankCount = header.prgRomChunks
            chrBankCount = header.chrRomChunks
        }

        // Positions in byte array where PRG and CHR roms start
        val prgStart = if (header.trainerExists) { 528 } else { 16 }
        val chrStart = prgStart + (prgBankCount * 16384)

        // Read PRG ROM
        for (i in 0..<((prgBankCount * 16384))) {
            prgMemory.add((rawBytes[prgStart + i]))
        }

        // Read CHR ROM
        // If no banks, just create the 8k space
        if (chrBankCount == 0) {
            for (i in 0..8192) {
                chrMemory.add(0)
            }
        } else {
            for (i in 0..<(chrBankCount * 8192)) {
                chrMemory.add((rawBytes[chrStart + i]))
            }
        }
    }

    abstract fun willCartInterceptCPURead(address : Int) : Boolean

    abstract fun willCartInterceptCPUWrite(address : Int, data : Byte) : Boolean

    abstract fun willCartInterceptPPURead(address : Int) : Boolean

    abstract fun willCartInterceptPPUWrite(address : Int, data : Byte) : Boolean

    abstract fun cpuBusRead(address: Int) : Byte

    abstract fun cpuBusWrite(address : Int, data : Byte)

    abstract fun ppuBusRead(address : Int) : Byte

    abstract fun ppuBusWrite(address : Int, data : Byte)

    abstract fun mirrorMode() : MirrorMode

    abstract fun mapperId() : Int

    abstract fun reset()

    open fun irqRequested() : Boolean {
        return false
    }

    open fun irqClear() {}

    open fun notifyScanline() {}
}
