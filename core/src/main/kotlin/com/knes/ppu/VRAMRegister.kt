package com.knes.ppu

class VRAMRegister {

    var coarseX: Int = 0
    var coarseY: Int = 0
    var nameTableX: Boolean = false
    var nameTableY: Boolean = false
    var fineY: Int = 0

    fun setAddress(value: Int) {
        coarseX = value and 0x1F
        coarseY = (value shr 5) and 0x1F
        nameTableX = (value and 0x0400) != 0
        nameTableY = (value and 0x0800) != 0
        fineY = (value shr 12) and 0x07
    }

    fun address(): Int {
        return (coarseX and 0x1F) or
            ((coarseY and 0x1F) shl 5) or
            (if (nameTableX) 0x0400 else 0) or
            (if (nameTableY) 0x0800 else 0) or
            ((fineY and 0x07) shl 12)
    }

    fun increment(controlRegister: ControlRegister) {
        this.setAddress(this.address() + controlRegister.incrementAmount())
    }
}
