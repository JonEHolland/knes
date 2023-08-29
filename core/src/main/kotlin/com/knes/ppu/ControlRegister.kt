package com.knes.ppu

class ControlRegister {

    var nameTableX = false
    var nameTableY = false
    var incrementMode = false
    var patternSprite = false
    var patternBackground = false
    var spriteSize = false
    var enableNMI = false
    var slaveMode = false

    fun set(value:  Int) {
        nameTableX = value and 0x01 == 0x01
        nameTableY = value and 0x02 == 0x02
        incrementMode = value and 0x04 == 0x04
        patternSprite = value and 0x08 == 0x08
        patternBackground = value and 0x10 == 0x10
        spriteSize = value and 0x20 == 0x20
        slaveMode = value and 0x40 == 0x40
        enableNMI = value and 0x80== 0x80
    }

    fun get():  Int {
        var value = 0x00
        value = value or if (nameTableX) 0x01 else 0x00
        value = value or if (nameTableY) 0x02 else 0x00
        value = value or if (incrementMode) 0x04 else 0x00
        value = value or if (patternSprite) 0x08 else 0x00
        value = value or if (patternBackground) 0x10 else 0x00
        value = value or if (spriteSize) 0x20 else 0x00
        value = value or if (slaveMode) 0x40 else 0x00
        value = value or if (enableNMI) 0x80 else 0x00
        return (value and 0xFF)
    }

    fun incrementAmount() : Int = if (incrementMode) 32 else 1

}
