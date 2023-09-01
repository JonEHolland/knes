package com.knes.ppu

class MaskRegister {

    var grayscale = false
    var renderBackgroundLeft = true
    var renderSpriteLeft = false
    var renderBackground = true
    var renderSprites = false
    var enhanceRed = false
    var enhanceGreen = false
    var enhanceBlue = false

    fun set(value: Int) {
        grayscale = value and 0x01 == 0x01
        renderBackgroundLeft = value and 0x02 == 0x02
        renderSpriteLeft = value and 0x04 == 0x04
        renderBackground = value and 0x08 == 0x08
        renderSprites = value and 0x10 == 0x10
        enhanceRed = value and 0x20 == 0x20
        enhanceGreen = value and 0x40 == 0x40
        enhanceBlue = value and 0x80 == 0x80
    }

    fun get(): Int {
        var value = 0x00
        value = value or if (grayscale) 0x01 else 0x00
        value = value or if (renderBackgroundLeft) 0x02 else 0x00
        value = value or if (renderSpriteLeft) 0x04 else 0x00
        value = value or if (renderBackground) 0x08 else 0x00
        value = value or if (renderSprites) 0x10 else 0x00
        value = value or if (enhanceRed) 0x20 else 0x00
        value = value or if (enhanceGreen) 0x40 else 0x00
        value = value or if (enhanceBlue) 0x80 else 0x00
        return (value and 0xFF)
    }

    fun spriteZeroOffset() : Int {
        return if (!(renderBackgroundLeft || renderSpriteLeft)) 9 else 1
    }

    fun shouldRender() : Boolean {
        return renderSprites || renderBackground
    }

}
