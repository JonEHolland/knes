package com.knes.cpu


enum class Flags(val value: Int) {
    // Carry
    C(1),

    // Zero
    Z(1 shl 1),

    // Interrupt Disable
    I(1 shl 2),

    // Unused
    D(1 shl 3),

    // Unused
    B(1 shl 4),

    // Unused
    U(1 shl 5),

    // Overflow
    V(1 shl 6),

    // Negative
    N(1 shl 7)
}
