package com.knes.cpu


class Opcodes(
    val state : CPUState,
    val cpu : CPU) {

    fun adc() : Int {
        with (state) {
            cpu.fetch()
            val tmp = acc + fetched + (if (cpu.getFlag(Flags.C)) 0x1 else 0x0) and 0x01FF

            cpu.setFlag(Flags.V, ((tmp xor acc) and (tmp xor fetched ) and 0x80) == 0x80)
            cpu.setFlag(Flags.C, tmp > 0xFF)
            cpu.setFlag(Flags.Z, tmp and 0xFF == 0)
            cpu.setFlag(Flags.N, tmp and 0x80 == 0x80)

            acc = tmp and 0xFF
            return 1
        }
    }

    fun and() : Int {
        with (state) {
            cpu.fetch()
            acc = acc and fetched
            acc = acc and 0xFF

            cpu.setFlag(Flags.Z, acc == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) == 0x80)

            return 1
        }
    }

    fun asl() : Int {
        with (state) {
            cpu.fetch()
            val tmp = fetched shl 1

            cpu.setFlag(Flags.C, (tmp and 0xFF00) > 0)
            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            if (currentInstruction!!.addressMode == cpu.a::impl ||
                currentInstruction!!.addressMode == cpu.a::acc) {
                acc = tmp and 0xFF
            } else {
                cpu.write(addressAbsolute, tmp and 0xFF)
            }
        }

        return 0
    }

    fun bit() : Int {
        with (state) {
            cpu.fetch()
            val tmp = acc and fetched

            cpu.setFlag(Flags.Z, (tmp and 0xFF)  == 0x0000)
            cpu.setFlag(Flags.N, (fetched and 0x80) == 0x80)
            cpu.setFlag(Flags.V, (fetched and 0x40) == 0x40)
        }
        return 0
    }

    fun bcc() : Int {
        return branch(!cpu.getFlag(Flags.C))
    }

    fun bcs() : Int {
        return branch(cpu.getFlag(Flags.C))
    }

    fun beq() : Int {
        return branch(cpu.getFlag(Flags.Z))
    }

    fun bmi() : Int {
        return branch(cpu.getFlag(Flags.N))
    }

    fun bne() : Int {
        return branch(!cpu.getFlag(Flags.Z))
    }

    fun bpl() : Int {
        return branch(!cpu.getFlag(Flags.N))
    }

    fun bvc() : Int {
        return branch(!cpu.getFlag(Flags.V))
    }

    fun bvs() : Int {
        return branch(cpu.getFlag(Flags.V))
    }

    private fun branch(shouldBranch : Boolean) : Int {
        with (state) {

            addressAbsolute = (pc + addressRelative)
            addressAbsolute = addressAbsolute and 0xFFFF

            if (shouldBranch) {
                cyclesRemaining++

                if ((addressAbsolute and 0xFF00) != (pc and 0xFF00)) {
                    cyclesRemaining++
                }

                pc = addressAbsolute
            }
        }
        return 0
    }

    fun brk() : Int {
        with (state) {
            // Dummy Read ???
            cpu.read((pc - 1) and 0xFFFF)

            cpu.pushStack(((pc shr 8) and 0xFF))
            cpu.pushStack((pc and 0xFF))
            cpu.pushStack(((status or Flags.B.value) and 0xFF))
            pc = cpu.irqVector()
            cpu.setFlag(Flags.I, true)
        }

        return 0
    }


    // Clears Carry Flag
    fun clc() : Int {
        cpu.setFlag(Flags.C, false)
        return 0
    }

    // Clears Decimal Flag
    fun cld() : Int {
        cpu.setFlag(Flags.D, false)
        return 0
    }

    // Clears Interrupt Flag
    fun cli() : Int {
        cpu.setFlag(Flags.I, false)
        return 0
    }

    // Clears Overflow Flag
    fun clv() : Int {
        cpu.setFlag(Flags.V, false)
        return 0
    }

    fun cmp() : Int {
        with (state) {
            cpu.fetch()
            val tmp = acc - fetched

            cpu.setFlag(Flags.C, acc >= fetched)
            cpu.setFlag(Flags.Z, tmp and 0xFF == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            return 1
        }
    }

    fun cpx() : Int {
        with (state) {
            cpu.fetch()
            val tmp = x - fetched

            cpu.setFlag(Flags.C, x >= fetched)
            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            return 0
        }
    }

    fun cpy() : Int {
        with (state) {
            cpu.fetch()
            val tmp = y - fetched

            cpu.setFlag(Flags.C, y >= fetched)
            cpu.setFlag(Flags.Z, tmp and 0xFF == 0)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            return 0
        }
    }

    fun dec() : Int {
        with(state) {
            cpu.fetch()
            val tmp = (fetched - 1) and 0xFF
            cpu.write(addressAbsolute, tmp)

            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)
        }
        return 0
    }

    fun dex() : Int {
        with(state) {
            x--
            x = x and 0xFF
            cpu.setFlag(Flags.Z, (x and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (x and 0x80) == 0x80)
        }
        return 0
    }

    fun dey() : Int {
        with(state) {
            y--
            y = y and 0xFF

            cpu.setFlag(Flags.Z, ((y and 0xFF) == 0x00))
            cpu.setFlag(Flags.N, (y and 0x80) == 0x80)
        }
        return 0
    }

    fun inc() : Int {
        with(state) {
            cpu.fetch()
            val tmp = (fetched + 1) and 0xFF
            cpu.write(addressAbsolute, tmp)

            cpu.setFlag(Flags.Z, ((tmp and 0xFF) == 0x00))
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)
        }
        return 0
    }

    fun inx() : Int {
        with(state) {
            x++
            x = x and 0xFF

            cpu.setFlag(Flags.Z, (x and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (x and 0x80) == 0x80)
        }
        return 0
    }

    fun iny() : Int {
        with(state) {
            y++
            y = y and 0xFF

            cpu.setFlag(Flags.Z, (y and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (y and 0x80) == 0x80)
        }
        return 0
    }

    fun jmp() : Int {
        with (state) {
            pc = addressAbsolute and 0xFFFF
        }
        return 0
    }

    fun jsr() : Int {
        with (state) {
            pc--
            pc = pc and 0xFFFF

            cpu.pushStack((pc shr 8) and 0xFF)
            cpu.pushStack(pc and 0xFF )

            pc = addressAbsolute and 0xFFFF
        }
        return 0
    }

    fun lda() : Int {
        with (state) {
            cpu.fetch()
            acc = fetched and 0xFF

            cpu.setFlag(Flags.Z, (acc and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) == 0x80)
        }

        return 1
    }

    fun ldx() : Int {
        with (state) {
            cpu.fetch()
            x = fetched and 0xFF

            cpu.setFlag(Flags.Z, (x and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (x and 0x80) == 0x80)
        }

        return 1
    }

    fun ldy() : Int {
        with (state) {
            cpu.fetch()
            y = fetched and 0xFF

            cpu.setFlag(Flags.Z, (y and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (y and 0x80) == 0x80)
        }

        return 1
    }

    fun lsr() : Int {
        with (state) {
            cpu.fetch()
            cpu.setFlag(Flags.C, (fetched and 0x01) == 0x01)
            val tmp = fetched shr 1

            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            if (currentInstruction!!.addressMode == cpu.a::impl ||
                currentInstruction!!.addressMode == cpu.a::acc) {
                acc = tmp and 0xFF
            } else {
                cpu.write(addressAbsolute, tmp and 0xFF)
            }
        }

        return 0
    }

    fun nop() : Int {
        return 0
    }

    fun ora() : Int {
        with (state) {
            cpu.fetch()
            acc = (acc or fetched) and 0xFF

            cpu.setFlag(Flags.Z, acc == 0x00)
            cpu.setFlag(Flags.N, acc and 0x80 == 0x80)
        }
        return 1
    }

    fun pha() : Int {
        cpu.pushStack(state.acc)
        return 0
    }

    fun php() : Int {
        with (state) {
            val tmp = (status or Flags.U.value or Flags.B.value) and 0xFF
            cpu.pushStack(tmp)
            cpu.setFlag(Flags.B, false)
            cpu.setFlag(Flags.U, false)
        }
        return 0
    }

    fun pla() : Int {
        with (state) {
            acc = cpu.popStack() and 0xFF
            cpu.setFlag(Flags.Z, acc == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) == 0x80)
        }
        return 0
    }

    fun plp() : Int {
        with (state) {
            status = cpu.popStack() and 0xFF
            cpu.setFlag(Flags.U, true)
        }
        return 0
    }

    fun rol() : Int {
        with (state) {
            cpu.fetch()
            val tmp = ((if (cpu.getFlag(Flags.C)) 1 else 0) or (fetched shl 1))

            cpu.setFlag(Flags.C, (tmp and 0xFF00) != 0x0000)
            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            if (currentInstruction!!.addressMode == cpu.a::impl ||
                currentInstruction!!.addressMode == cpu.a::acc) {
                acc = tmp and 0xFF
            } else {
                cpu.write(addressAbsolute, tmp and 0xFF)
            }
        }

        return 0
    }

    fun ror() : Int {
        with (state) {
            cpu.fetch()
            val tmp = ((if (cpu.getFlag(Flags.C)) 1 shl 7 else 0) or (fetched shr 1))

            cpu.setFlag(Flags.C, (fetched and 0x01) == 0x01)
            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)

            if (currentInstruction!!.addressMode == cpu.a::impl ||
                currentInstruction!!.addressMode == cpu.a::acc) {
                acc = tmp and 0xFF
            } else {
                cpu.write(addressAbsolute, tmp and 0xFF)
            }
        }

        return 0
    }

    fun rti() : Int {
        with (state) {
           // cpu.read(pc)
            status = cpu.popStack()
            pc = (cpu.popStack() and 0xFF)
            pc = pc or (cpu.popStack() shl 8)

            pc = pc and 0xFFFF

            status = status and (Flags.B.value.inv() and 0x00FF)
            status = status and (Flags.U.value.inv() and 0x00FF)
        }
        return 0
    }

    fun rts() : Int {
        with (state) {
            // Dummy read
            cpu.read(pc)

            pc = (cpu.popStack())
            pc = pc or (cpu.popStack() shl 8)
            pc += 1

            pc = pc and 0xFFFF
        }
        return 0
    }

    fun sbc() : Int {
        with (state) {
            cpu.fetch()
            val complement = fetched xor 0xFF
            val carry = if (cpu.getFlag(Flags.C)) 0x1 else 0x0
            val tmp = (acc + complement + carry) and 0x01FF

            cpu.setFlag(Flags.C, tmp > 0xFF)
            cpu.setFlag(Flags.Z, (tmp and 0xFF) == 0)
            cpu.setFlag(Flags.N, (tmp and 0x80) == 0x80)
            cpu.setFlag(Flags.V, (((tmp xor acc) and (tmp xor complement) and 0x80) == 0x80))

            acc = tmp and 0xFF
        }
        return 1
    }

    fun sec() : Int {
        cpu.setFlag(Flags.C, true)
        return 0
    }

    fun sed() : Int {
        cpu.setFlag(Flags.D, true)
        return 0
    }

    fun sei() : Int {
        cpu.setFlag(Flags.I, true)
        return 0
    }

    fun sta() : Int {
        with (state) {
            cpu.write(addressAbsolute, acc)
        }
        return 0
    }

    fun stx() : Int {
        with (state) {
            cpu.write(addressAbsolute, x)
        }
        return 0
    }

    fun sty() : Int {
        with (state) {
            cpu.write(addressAbsolute, y)
        }
        return 0
    }

    fun tax() : Int {
        with (state) {
            x = acc
            x = x and 0xFF

            cpu.setFlag(Flags.Z, x == 0x00)
            cpu.setFlag(Flags.N, x and 0x80 == 0x80)
        }
        return 0
    }

    fun tya() : Int {
        with (state) {
            acc = y
            acc = acc and 0xFF

            cpu.setFlag(Flags.Z, acc == 0x0)
            cpu.setFlag(Flags.N, acc and 0x80 == 0x80)
        }
        return 0
    }

    fun tay() : Int {
        with (state) {
            y = acc
            y = y and 0xFF

            cpu.setFlag(Flags.Z, y == 0x00)
            cpu.setFlag(Flags.N, y and 0x80 == 0x80)
        }
        return 0
    }

    fun tsx() : Int {
        with (state) {
            x = sp
            x = x and 0xFF

            cpu.setFlag(Flags.Z, x  == 0x0)
            cpu.setFlag(Flags.N, x  and 0x80 == 0x80)
        }
        return 0
    }

    fun txa() : Int {
        with (state) {
            acc = x
            acc = acc and 0xFF

            cpu.setFlag(Flags.Z, acc  == 0x0)
            cpu.setFlag(Flags.N, acc  and 0x80 == 0x80)
        }
        return 0
    }

    fun txs() : Int {
        with (state) {
            sp = x
            sp = sp and 0xFF
        }
        return 0
    }

    fun eor() : Int {
        with (state) {
            cpu.fetch()
            acc = (acc xor fetched) and 0x00FF

            cpu.setFlag(Flags.Z, (acc and 0xFF) == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) == 0x80)
        }
        return 0
    }

    // Illegal Opcodes - Most of these are from tying two opcode lines together
    // On the real hardware, so can be implemented by running both opcodes using the same
    // address mode
    //
    // Only a few games need this, but implementing them for completion sake

    fun lax() : Int {
        lda()
        ldx()
        return 1
    }

    fun jam() : Int {
        state.halted = true
        return 0
    }

    fun slo() : Int {
        asl()
        ora()
        return 0
    }

    fun rra() : Int {
        ror()
        adc()

        return 0
    }

    fun rla() : Int {
        rol()
        and()
        return 0
    }

    fun sre() : Int {
        lsr()
        eor()
        return 0
    }

    fun anc() : Int {
        with (state) {
            cpu.fetch()
            acc = (acc and fetched) and 0xFF

            cpu.setFlag(Flags.Z, acc == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) != 0)
            cpu.setFlag(Flags.C, (acc and 0x80) != 0)
        }
        return 0
    }

    fun arr() : Int {
        with (state) {
            and()
            cpu.fetch()
            val tmp = ((if (cpu.getFlag(Flags.C)) (1 shl 7) else 0) or (acc shr 1))

            cpu.setFlag(Flags.C, (acc and 0x01) == 0x01)
            cpu.setFlag(Flags.Z, (tmp and 0xFF)  == 0x0)
            cpu.setFlag(Flags.N, (tmp  and 0x80) == 0x80)
        }

        return 0
    }

    fun alr() : Int {
        with (state) {
            and()
            val tmp = acc shr 1
            cpu.setFlag(Flags.C, (acc and 0x01) == 0x01)
            cpu.setFlag(Flags.Z, (tmp and 0xFF)  == 0x0)
            cpu.setFlag(Flags.N, (tmp  and 0x80) == 0x80)
        }

        return 0
    }

    fun sax() : Int {
        with (state) {
            cpu.write(addressAbsolute, acc and x)
        }
        return 0
    }

    fun ane() : Int {
        txa()
        and()
        return 0
    }

    fun tas() : Int {
        with (state) {
            var tmp = (acc and x) and 0xFFF
            cpu.pushStack(tmp)
            tmp = (tmp and (cpu.read(pc - 1) + 1)) and 0xFF
            cpu.write(addressAbsolute, tmp)
        }
        return 0
    }

    fun shy() : Int {
        with (state) {
            val tmp = (y and (cpu.read(pc - 1) + 1)) and 0xFF
            cpu.write(addressAbsolute, tmp)
        }
        return 0
    }

    fun shx() : Int {
        with (state) {
            val tmp = (x and (cpu.read(pc - 1) + 1)) and 0xFF
            cpu.write(addressAbsolute, tmp)
        }
        return 0
    }

    fun sha() : Int {
        with (state) {
            val tmp = (acc and x and (cpu.read(pc - 1) + 1)) and 0xFF
            cpu.write(addressAbsolute, tmp)
        }
        return 0
    }

    fun las() : Int {
        with (state) {
            cpu.fetch()
            acc = (fetched and sp)

            cpu.setFlag(Flags.Z, acc == 0x00)
            cpu.setFlag(Flags.N, (acc and 0x80) != 0x0)
        }

        return 0
    }

    fun lxa() : Int {
        lax()
        return 0
    }

    fun dcp() : Int {
        dec()
        cmp()
        return 0
    }

    fun isc() : Int {
        inc()
        sbc()
        return 0
    }

    fun usbc() : Int {
        sbc()
        return 0
    }
}
