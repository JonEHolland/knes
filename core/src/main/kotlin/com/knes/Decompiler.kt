package com.knes


fun main() {
    val bus = Bus("./nestest.nes", State())
    bus.reset()

    var decompiled = bus.cpu.decompile()
    while (decompiled != null) {
        println(decompiled)
        decompiled = bus.cpu.decompile()
    }
}
