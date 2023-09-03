package com.knes.apu

class Oscillator {

    var harmonics = 10
    var frequency = 0f
    var duty_cycle = 0f
    var amplitude = 1f

    fun sample(t: Double): Float {
        var a = 0f
        var b = 0f
        val p = duty_cycle * 6.2918530f
        for (n in 1 until harmonics) {
            val c = (n * frequency * 6.2918530f * t).toFloat()
            a -= sin(c) / n
            b -= sin(c - p * n) / n
        }
        return amplitude / 3.14159265f * (a - b) + amplitude * (1 - duty_cycle)
    }

    private fun sin(t: Float): Float {
        var j = t * 0.15915f
        j -= j.toInt()
        return 20.785f * j * (j - 0.5f) * (j - 1.0f)
    }
}
