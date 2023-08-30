package com.knes.ppu

import com.badlogic.gdx.utils.BufferUtils
import java.nio.ByteBuffer

class PPUState {

    // Buffer
    val SCREEN_WIDTH = 256
    val SCREEN_HEIGHT = 240
    var screenBuffer : ByteBuffer = BufferUtils.newByteBuffer(SCREEN_HEIGHT * SCREEN_WIDTH * 4)

    // Memory
    var nameTableMem: Array<ByteArray> = Array(2) { ByteArray(1024)}
    var patternTableMem: Array<ByteArray> = Array(2) { ByteArray(4096)}
    var paletteMem: ByteArray = ByteArray(32)

    // Registers
    var maskRegister: MaskRegister = MaskRegister()
    var controlRegister: ControlRegister = ControlRegister()
    var statusRegister: StatusRegister = StatusRegister()
    var vramRegister: VRAMRegister = VRAMRegister()
    var tempVramRegister: VRAMRegister = VRAMRegister()

    // Sprite OAM
    var oams: Array<ObjectAttribute> = Array(64) { ObjectAttribute() }
    var visibleOams: Array<ObjectAttribute> = Array(8) { ObjectAttribute() }
    var spriteShiftPatternLow: IntArray = IntArray(8)
    var spriteShiftPatternHigh: IntArray = IntArray(8)
    var spriteCount : Int  = 0
    var oamAddress : Int = 0x00
    var spriteZeroHitPossible : Boolean = false
    var spriteZeroBeingRendered : Boolean = false

    // Background
    var bgNextTileId : Int = 0x00
    var bgNextTileAttribute : Int = 0x00
    var bgNextTileLSB : Int = 0x00
    var bgNextTileMSB : Int = 0x00
    var bgShiftPatternLow : Int = 0x00
    var bgShiftPatternHigh : Int = 0x00
    var bgShiftAttribLow : Int = 0x00
    var bgShiftAttribHigh : Int = 0x00

    // Render State
    var frameComplete : Boolean = false
    var scanline : Int = 0
    var cycle : Int = 0
    var oddFrame : Boolean = false
    var fineX : Int = 0x00
    var addressLatch : Int = 0x00
    var ppuDataBuffer : Int = 0x00

    // Signalling
    var nmiRequested : Boolean = false

    fun reset() {
        nmiRequested = false
        fineX = 0x00
        addressLatch = 0x00
        ppuDataBuffer = 0x00
        scanline = 0
        cycle = 0
        bgNextTileAttribute = 0x00
        bgNextTileId = 0x00
        bgNextTileLSB = 0x00
        bgNextTileMSB = 0x00
        bgShiftPatternHigh = 0x00
        bgShiftPatternLow = 0x00
        bgShiftAttribHigh = 0x00
        bgShiftAttribLow = 0x00
        statusRegister.set(0x00)
        maskRegister.set(0x00)
        controlRegister.set(0x00)
        vramRegister.setAddress(0x0000)
        tempVramRegister.setAddress(0x0000)
        oddFrame = false
        oams = Array(64) { ObjectAttribute() }
        visibleOams = Array(8) { ObjectAttribute()}
        screenBuffer.clear()
    }
}
