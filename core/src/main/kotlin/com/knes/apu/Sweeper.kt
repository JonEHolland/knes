package com.knes.apu

class Sweeper {

    var enabled = false
    var down = false
    var reload = false
    var muted = false
    var shift = 0x00
    var period = 0x00

    private var change = 0
    private var timer = 0x00


    fun track(reload: Int) {
        if (enabled) {
            change = reload shr shift
            muted = reload < 8 || reload > 0x7FF
        }
    }


    fun tick(reload: Int, channel: Int): Int {
        var reload = reload
        if (timer == 0 && enabled && shift > 0 && !muted) {
            if (reload >= 8 && change < 0x07FF) {
                if (down) reload -= change - (channel and 1) else reload += change
            }
        }
        if (enabled) {
            if (timer == 0 || this.reload) {
                timer = period
                this.reload = false
            } else timer--
            muted = reload < 8 || reload > 0x7FF
        }
        return reload
    }
}
