package com.knes


fun main() {
    val bus = Bus("./nestest.nes", State(), 44100)
    bus.reset()

    var decompiled = bus.cpu.decompile()
    while (decompiled != null) {
        println(decompiled)
        decompiled = bus.cpu.decompile()
    }
}

