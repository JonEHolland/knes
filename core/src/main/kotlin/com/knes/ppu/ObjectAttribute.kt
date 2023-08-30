package com.knes.ppu


class ObjectAttribute {
    var y : Int = 0x00
    var id : Int  = 0x00
    var attribute : Int  = 0x00
    var x : Int = 0x00

    fun clear(value: Int) {
        y = value
        id = value
        attribute = value
        x = value
    }

    fun set(o: ObjectAttribute) {
        y = o.y
        x = o.x
        attribute = o.attribute
        id = o.id
    }

    fun verticalFlipped() : Boolean {
        return attribute and 0x80 != 0x80
    }

    fun horizontalFlipped() : Boolean {
        return attribute and 0x40 != 0x40
    }
}
