package com.knes.ppu


class StatusRegister {
    var spriteOverflow = false
    var spriteZeroHit = false
    var verticalBlank = false

    fun set(value: Int) {
        val v = value and 0xFF
        verticalBlank = v and 0x80 == 0x80
        spriteZeroHit = v and 0x40 == 0x40
        spriteOverflow = v and 0x20 == 0x20
    }

    fun get(): Int {
        var value = 0x00
        value = value or if (verticalBlank) 0x80 else 0x00
        value = value or if (spriteZeroHit) 0x40 else 0x00
        value = value or if (spriteOverflow) 0x20 else 0x00
        return (value and 0xFF)
    }
}
