package com.knes

object Debug {
    fun hex8(num : Int) : String {
        return String.format("%02X", num)
    }

    fun hex16(num : Int) : String {
        return String.format("$%04X", num)
    }
}
