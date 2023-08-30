package com.knes

class CartridgeState {

    var irqRequested = false

    fun reset() {
        irqRequested = false
    }
}
