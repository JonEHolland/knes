package com.knes.ppu

import com.badlogic.gdx.graphics.Color
import com.knes.Bus
import com.knes.Cartridge

class PPU(
    val bus : Bus
) {

    val systemPalette: Array<Color> = arrayOf(
        Color(84 / 255.0f, 84 / 255.0f, 84 / 255.0f, 1f),
        Color(0 / 255.0f, 30 / 255.0f, 116 / 255.0f, 1f),
        Color(8 / 255.0f, 16 / 255.0f, 144 / 255.0f, 1f),
        Color(48 / 255.0f, 0 / 255.0f, 136 / 255.0f, 1f),
        Color(68 / 255.0f, 0 / 255.0f, 100 / 255.0f, 1f),
        Color(92 / 255.0f, 0 / 255.0f, 48 / 255.0f, 1f),
        Color(84 / 255.0f, 4 / 255.0f, 0 / 255.0f, 1f),
        Color(60 / 255.0f, 24 / 255.0f, 0 / 255.0f, 1f),
        Color(32 / 255.0f, 42 / 255.0f, 0 / 255.0f, 1f),
        Color(8 / 255.0f, 58 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 64 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 60 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 50 / 255.0f, 60 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(152 / 255.0f, 150 / 255.0f, 152 / 255.0f, 1f),
        Color(8 / 255.0f, 76 / 255.0f, 196 / 255.0f, 1f),
        Color(48 / 255.0f, 50 / 255.0f, 236 / 255.0f, 1f),
        Color(92 / 255.0f, 30 / 255.0f, 228 / 255.0f, 1f),
        Color(136 / 255.0f, 20 / 255.0f, 176 / 255.0f, 1f),
        Color(160 / 255.0f, 20 / 255.0f, 100 / 255.0f, 1f),
        Color(152 / 255.0f, 34 / 255.0f, 32 / 255.0f, 1f),
        Color(120 / 255.0f, 60 / 255.0f, 0 / 255.0f, 1f),
        Color(84 / 255.0f, 90 / 255.0f, 0 / 255.0f, 1f),
        Color(40 / 255.0f, 114 / 255.0f, 0 / 255.0f, 1f),
        Color(8 / 255.0f, 124 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 118 / 255.0f, 40 / 255.0f, 1f),
        Color(0 / 255.0f, 102 / 255.0f, 120 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(236 / 255.0f, 238 / 255.0f, 236 / 255.0f, 1f),
        Color(76 / 255.0f, 154 / 255.0f, 236 / 255.0f, 1f),
        Color(120 / 255.0f, 124 / 255.0f, 236 / 255.0f, 1f),
        Color(176 / 255.0f, 98 / 255.0f, 236 / 255.0f, 1f),
        Color(228 / 255.0f, 84 / 255.0f, 236 / 255.0f, 1f),
        Color(236 / 255.0f, 88 / 255.0f, 180 / 255.0f, 1f),
        Color(236 / 255.0f, 106 / 255.0f, 100 / 255.0f, 1f),
        Color(212 / 255.0f, 136 / 255.0f, 32 / 255.0f, 1f),
        Color(160 / 255.0f, 170 / 255.0f, 0 / 255.0f, 1f),
        Color(116 / 255.0f, 196 / 255.0f, 0 / 255.0f, 1f),
        Color(76 / 255.0f, 208 / 255.0f, 32 / 255.0f, 1f),
        Color(56 / 255.0f, 204 / 255.0f, 108 / 255.0f, 1f),
        Color(56 / 255.0f, 180 / 255.0f, 204 / 255.0f, 1f),
        Color(60 / 255.0f, 60 / 255.0f, 60 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(236 / 255.0f, 238 / 255.0f, 236 / 255.0f, 1f),
        Color(168 / 255.0f, 204 / 255.0f, 236 / 255.0f, 1f),
        Color(188 / 255.0f, 188 / 255.0f, 236 / 255.0f, 1f),
        Color(212 / 255.0f, 178 / 255.0f, 236 / 255.0f, 1f),
        Color(236 / 255.0f, 174 / 255.0f, 236 / 255.0f, 1f),
        Color(236 / 255.0f, 174 / 255.0f, 212 / 255.0f, 1f),
        Color(236 / 255.0f, 180 / 255.0f, 176 / 255.0f, 1f),
        Color(228 / 255.0f, 196 / 255.0f, 144 / 255.0f, 1f),
        Color(204 / 255.0f, 210 / 255.0f, 120 / 255.0f, 1f),
        Color(180 / 255.0f, 222 / 255.0f, 120 / 255.0f, 1f),
        Color(168 / 255.0f, 226 / 255.0f, 144 / 255.0f, 1f),
        Color(152 / 255.0f, 226 / 255.0f, 180 / 255.0f, 1f),
        Color(160 / 255.0f, 214 / 255.0f, 228 / 255.0f, 1f),
        Color(160 / 255.0f, 162 / 255.0f, 160 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f),
        Color(0 / 255.0f, 0 / 255.0f, 0 / 255.0f, 1f)
    )

    fun tick() {

        fun shouldRender(): Boolean {
            return bus.state.ppu.maskRegister.renderBackground || bus.state.ppu.maskRegister.renderSprites
        }

        // Increments the tile pointer by one tile horizontally
        fun incrementScrollX() {
            with(bus.state.ppu) {
                if (shouldRender()) {
                    if (vramRegister.coarseX == 31) {
                        // Wrap around and flip nametable
                        vramRegister.coarseX = 0
                        vramRegister.nameTableX = !vramRegister.nameTableX
                    } else {
                        // Same nametable, so just increment
                        vramRegister.coarseX++
                    }
                }
            }
        }

        // Increments the tile pointer by one scanline vertically
        fun incrementScrollY() {
            with(bus.state.ppu) {
                if (shouldRender()) {

                    // If we have not scrolled a full tile, just update fineY
                    if (vramRegister.fineY < 7) {
                        vramRegister.fineY++
                    } else {
                        // Moved a full tile. Reset FineY, coarseY and update nametable if needed
                        vramRegister.fineY = 0

                        if (vramRegister.coarseY == 29) {
                            vramRegister.coarseY = 0
                            vramRegister.nameTableY = !vramRegister.nameTableY
                        } else if (vramRegister.coarseY == 31) {
                            vramRegister.coarseY = 0
                        } else {
                            // No wrapping, just increment the tile
                            vramRegister.coarseY++
                        }
                    }
                }
            }
        }

        // Transfers the temporary VRAM Register data for Horizontal nametable access into
        // the main VRAM Register
        fun transferAddressX() {
            with(bus.state.ppu) {
                if (shouldRender()) {
                    // FineX does not get transferred
                    vramRegister.nameTableX = tempVramRegister.nameTableX
                    vramRegister.coarseX = tempVramRegister.coarseX
                }
            }
        }

        // Transfers the temporary VRAM Register data for Vertical nametable access into
        // the main VRAM Register
        fun transferAddressY() {
            with(bus.state.ppu) {
                if (shouldRender()) {
                    vramRegister.fineY = tempVramRegister.fineY
                    vramRegister.nameTableY = tempVramRegister.nameTableY
                    vramRegister.coarseY = tempVramRegister.coarseY
                }
            }
        }

        // Loads the next 8 pixels into the shifters
        fun loadBackgroundShifters() {
            with(bus.state.ppu) {

                // Top 8 bits are the current 8 pixels being drawn
                // Bottom 8 bits are the next pixels being drawn
                bgShiftPatternLow = ((bgShiftPatternLow and 0xFF00) or bgNextTileLSB) and 0xFFFF
                bgShiftPatternHigh = ((bgShiftPatternHigh and 0xFF00) or bgNextTileMSB) and 0xFFFF

                // Attributes change every 8 pixels but are aligned
                val maskLow = if (bgNextTileAttribute and 0b01 == 0b01) 0xFF else 0x00
                val maskHigh = if (bgNextTileAttribute and 0b10 == 0b10) 0xFF else 0x00
                bgShiftAttribLow = ((bgShiftAttribLow and 0xFF00) or maskLow) and 0xFFFF
                bgShiftAttribHigh = ((bgShiftAttribHigh and 0xFF00) or maskHigh) and 0xFFFF
            }
        }

        // Every cycle the pattern and attribute information shifters shift by 1 bit
        // This represents one pixel being drawn per cycle
        fun updateShifters() {
            with(bus.state.ppu) {
                if (maskRegister.renderBackground) {
                    bgShiftPatternLow = bgShiftPatternLow shl 1
                    bgShiftPatternHigh = bgShiftPatternHigh shl 1
                    bgShiftAttribLow = bgShiftAttribLow shl 1
                    bgShiftAttribHigh = bgShiftAttribHigh shl 1
                }

                if (maskRegister.renderSprites && cycle >= 1 && cycle < 258) {
                    for (i in 0 until spriteCount) {
                        if (visibleOams[i].x > 0 ) {
                            visibleOams[i].x--
                        } else {
                            spriteShiftPatternLow[i] = spriteShiftPatternLow[i] shl 1
                            spriteShiftPatternHigh[i] = spriteShiftPatternHigh[i] shl 1
                        }
                    }
                }
            }
        }

        fun getBitPlaneAddress(offset: Int): Int {
            with(bus.state.ppu) {
                val address1 = (if (controlRegister.patternBackground) (0x1 shl 12) else 0)
                val address2 = (bgNextTileId shl 4)
                val address3 = (vramRegister.fineY + offset)
                return (address1 + address2 + address3) and 0xFFFF
            }
        }

        with(bus.state.ppu) {

            var bgPixel: Int = 0x00
            var bgPalette: Int = 0x00
            var spritePixel: Int = 0x00
            var spritePalette: Int = 0x00
            var spritePriority: Boolean = false
            var finalPixel: Int = 0x00
            var finalPalette: Int = 0x00

            if (scanline >= -1 && scanline < 240) {
                if (scanline == 0 && cycle == 0 && oddFrame && shouldRender()) {
                    // Skip a cycle on odd frames
                    cycle = 1
                }

                if (scanline == -1 && cycle == 1) {
                    screenBuffer.clear()
                    ppuDataBuffer = 0
                    statusRegister.verticalBlank = false
                    statusRegister.spriteOverflow = false
                    statusRegister.spriteZeroHit = false

                    for (i in 0..7) {
                        spriteShiftPatternLow[i] = 0x00
                        spriteShiftPatternHigh[i] = 0x00
                    }
                }

                if ((cycle >= 2 && cycle < 258) || (cycle >= 321 && cycle < 338)) {
                    updateShifters()

                    // Depending on the cycle, we execute a different step of the rendering process
                    when ((cycle - 1) % 8) {
                        0 -> {
                            // Step 1

                            // Setup Shifters
                            loadBackgroundShifters()

                            // Get the next tile Id
                            // "(vramAddress & 0x0FFF)" : Mask to 12 bits that are relevant
                            // "| 0x2000"          : Offset into nametable space on PPU address bus
                            bgNextTileId = ppuBusRead(0x2000 or (vramRegister.address() and 0x0FFF)).toInt()


                        }

                        2 -> {
                            // Step 2

                            // Get the next tile attribute
                            val ny = (if (vramRegister.nameTableY) (1 shl 11) else 0)
                            val nx = (if (vramRegister.nameTableX) (1 shl 10) else 0)
                            val cy = ((vramRegister.coarseY shr 2) shl 3)
                            val cx = ((vramRegister.coarseX shr 2))
                            val address = (0x23C0 or ny or nx or cy or cx)
                            bgNextTileAttribute = ppuBusRead(address).toInt()

                            if ((vramRegister.coarseY and 0x02) == 0x02) {
                                bgNextTileAttribute = (bgNextTileAttribute shr 4)
                            }
                            if ((vramRegister.coarseX and 0x02) == 0x02) {
                                bgNextTileAttribute = (bgNextTileAttribute shr 2)
                            }
                            bgNextTileAttribute = bgNextTileAttribute and 0x03
                        }

                        4 -> {
                            // Step 3
                            // Fetch next tile LSB BitPlane
                            bgNextTileLSB = ppuBusRead(getBitPlaneAddress(0)).toInt() and 0xFF
                        }

                        6 -> {
                            // Step 4
                            // Fetch next tile MSB BitPlane
                            bgNextTileMSB = ppuBusRead(getBitPlaneAddress(8)).toInt() and 0xFF
                        }

                        7 -> {
                            // Final Step
                            // Increment to next tile horizontally
                            incrementScrollX()
                        }

                        else -> {
                            // Should never happen
                        }
                    }
                }

                // End of visible scanline, increment Y
                if (cycle == 256) {
                    incrementScrollY()
                }

                // Reset X back to 0
                if (cycle == 257) {
                    loadBackgroundShifters()
                    transferAddressX()
                }

                // Dummy Fetches for timing purposes (MMC5 uses this?)
                if (cycle == 337 || cycle == 340) {
                    bgNextTileId = ppuBusRead(0x2000 or (vramRegister.address() and 0x0FFF)).toInt()
                }

                if (scanline == -1 && cycle >= 280 && cycle < 305) {
                    // End of VBlank, so reset Y
                    transferAddressY()
                }

                if (cycle == 257 && scanline >= 0) {
                    // Sprite OAM
                }

                if (cycle == 340) {
                    // Sprite Shifters
                }
            }

            if (scanline == 240) {
                // Post render scanline, do nothing
            }

            if (scanline == 241 && cycle == 1) {
                // End of frame
                statusRegister.verticalBlank = true
                frameComplete = true

                if (controlRegister.enableNMI) {
                    nmiRequested = true
                }
            }

            // Now to compose the pixel value
            if (maskRegister.renderBackground) {
                if (maskRegister.renderBackgroundLeft || (cycle >= 9)) {
                    // Use FineX to allow for smooth scrolling vertically
                    val bitMux = (0x8000 shr fineX)

                    val lowPixel = (if ((bgShiftPatternLow and bitMux) > 0) 0x1 else 0x0)
                    val highPixel = (if ((bgShiftPatternHigh and bitMux) > 0) 0x1 else 0x0)
                    val lowPalette = (if ((bgShiftAttribLow and bitMux) > 0) 0x1 else 0x0)
                    val highPalette = (if ((bgShiftAttribHigh and bitMux) > 0) 0x1 else 0x0)

                    // Combine to form pixel index
                    bgPixel = ((highPixel shl 1) or lowPixel)
                    bgPalette = ((highPalette shl 1) or lowPalette)
                }
            }

            if (maskRegister.renderSprites) {
                // Calculate Sprite Pixel
            }

            // Calculate the final pixel and palette values
            if (bgPixel == 0 && spritePixel > 0) {
                // If Background is transparent and Sprite is not, use sprite
                finalPixel = spritePixel
                finalPalette = spritePalette
            } else if (bgPixel > 0 && spritePixel == 0) {
                // If Sprite is transparent, use background
                finalPixel = bgPixel
                finalPalette = bgPalette
            } else if (bgPixel > 0 && spritePixel > 0) {
                // Both sprite and background have a color

                if (spritePriority) {
                    // If sprite is the priority, use it
                    finalPixel = spritePixel
                    finalPalette = spritePalette
                } else {
                    // Use the background
                    finalPixel = bgPixel
                    finalPalette = bgPalette
                }
            } else {
                finalPixel = bgPixel
                finalPalette = bgPalette
            }


            // Sprite0 Hit Detection goes here

            // If in visible space, time to draw the pixel!
            if (cycle - 1 in 0..<SCREEN_WIDTH && scanline >= 0 && scanline < SCREEN_HEIGHT) {
                val color = getColor(finalPalette, finalPixel)
                screenBuffer.put(((color.r * 127).toInt()).toByte())
                screenBuffer.put(((color.g * 127).toInt()).toByte())
                screenBuffer.put(((color.b * 127).toInt()).toByte())
                screenBuffer.put(((color.a * 127).toInt()).toByte())
            }

            // Notify Cartridge of scanline completion if needed
            if (shouldRender()) {
                if (cycle == 260 && scanline < 240) {
                    // TODO Notify Cartridge of Scanline for MMC3
                }
            }


            cycle++
            // Last cycle? Increment scanline and start from 0
            if (cycle >= 341) {
                cycle = 0
                scanline++

                // Last scanline?
                if (scanline >= 261) {
                    // Get started for next frame
                    scanline = -1
                    frameComplete = true
                    oddFrame = !oddFrame
                }
            }
        }
    }


    fun cpuBusRead(address : Int) : Byte {
        var data : Int = 0

        with (bus.state.ppu) {
            when (address) {
                0x0002 -> {
                    // Status Register
                    // Unused bits are filled with the last data that was read
                    data = ((statusRegister.get() and 0xE0) or (ppuDataBuffer and 0x1F)) and 0xFF

                    // Reading the status register causes mutations (Silly NES)
                    statusRegister.verticalBlank = false
                    addressLatch = 0
                }
                0x0004 -> {
                    // OAM Data
                    data = currentOAM()
                }
                0x0007 -> {
                    // PPU Data
                    // Return the previously fetched address and set up for the next one
                    // Odd behavior, but that is how the PPU works
                    data = ppuDataBuffer
                    ppuDataBuffer = ppuBusRead(vramRegister.address()).toInt() and 0xFF

                    if (vramRegister.address() >= 0x3F00) {
                        // If fetching palette there is no delay, immediatly return the buffer.
                        data = ppuDataBuffer
                    }

                    // Setup next one and increment address based on control register
                    vramRegister.increment(controlRegister)

//                    if (((loopyV.get() and 0x1000) == 0x1000)
//                        && (previousAddress and 0x1000) == 0) {
//                        // TODO - Notify scanline interrupt to MMC3 carts
//                        // cart.notify()
//                    }
                }
            }
        }

        return (data and 0xFF).toByte()
    }

    fun cpuBusWrite(address: Int, d : Byte) {
        val data = d.toInt() and 0xFF

        with (bus.state.ppu) {
            when (address) {
                0x0000 -> {
                    // Control Register
                    controlRegister.set(data)

                    // Mirror the new nametable settings into the temp vram register
                    tempVramRegister.nameTableX = controlRegister.nameTableX
                    tempVramRegister.nameTableY = controlRegister.nameTableY
                }
                0x0001 -> {
                    // Mask Register
                    maskRegister.set(data)
                }
                0x0003 -> {
                    // OAM Address
                    oamAddress = data
                }
                0x0004 -> {
                    // OAM Data - Normally DMA is used but this can also be used to update slowly
                    // One byte at a time.

                    // We can reuse the DMA function as long as the OAM Address is incremented here
                    dma(oamAddress, data)
                    oamAddress++
                    oamAddress = oamAddress and 0xFF
                }
                0x0005 -> {
                    // Scroll Registers
                    // If first write, it is the X offset
                    // Use the temp register to track state
                    if (addressLatch == 0) {
                        fineX = data and 0x07
                        tempVramRegister.coarseX = (data shr 3)
                        addressLatch = 1
                    } else {
                        // Second write is the Y offset
                        tempVramRegister.fineY = (data and 0x07)
                        tempVramRegister.coarseY = (data shr 3)
                        addressLatch = 0
                    }
                }
                0x0006 -> {
                    // PPU Address
                    // Two write cycles is needed to write the address, so use the addressLatch to keep track
                    // and a temp instance of the VRamRegister to store the interim state
                    if (addressLatch == 0) {
                        // MSB
                        val msb = ((data and 0x3F) shl 8) or (tempVramRegister.address() and 0x00FF)
                        tempVramRegister.setAddress(msb)
                        addressLatch = 1
                    } else {
                        // LSB
                        val lsb = (tempVramRegister.address() and 0xFF00) or data
                        tempVramRegister.setAddress(lsb)
                        // This is the full address, so update the main register as well
                        vramRegister.setAddress(tempVramRegister.address())
                        addressLatch = 0
                    }
                }
                0x0007 -> {
                    // PPU Data
                    // Data is written to VRAM
                    ppuBusWrite(vramRegister.address(), data.toByte())

                    // Auto increment VRAMAddress after write, just like the read
                    vramRegister.increment(controlRegister)
                }
                else -> {
                    // Do Nothing
                }
            }
        }
    }

    fun ppuBusRead(addr : Int) : Byte {
        var address = addr and 0x3FFF
        var data : Byte

        if (bus.cart.willCartInterceptPPURead(address)) {
            // A mapper might take over so delegate to the cart
            data = bus.cart.ppuBusRead(address)
        } else {
            with (bus.state.ppu) {
                if (address <= 0x1FFF) {
                    // Pattern Table
                    val offset1 = (address and 0x1000) shr 12
                    val offset2 = address and 0x0FFF
                    data = patternTableMem[offset1][offset2]
                } else if (address <= 0x3EFF) {
                    // Name Table
                    address = address and 0x0FFF
                    val offset1 = getNameTable(address, bus.cart.mirrorMode())
                    val offset2 = address and 0x03FF
                    data = nameTableMem[offset1][offset2]
                } else {
                    // Palette Memory
                    address = address and 0x001F
                    if (address == 0x0010) address = 0x0000
                    if (address == 0x0014) address = 0x0004
                    if (address == 0x0018) address = 0x0008
                    if (address == 0x001C) address = 0x000C
                    val greyScale = (if (maskRegister.grayscale) 0x30 else 0x3F)
                    data = ((paletteMem[address].toInt() and greyScale) and 0xFF).toByte()
                }
            }
        }

        // Update latch on cart if needed for MMC3

        return data
    }

    fun ppuBusWrite(addr: Int, data : Byte) {
        var address = addr and 0x3FFF

        if (bus.cart.willCartInterceptPPUWrite(address, data)) {
            // A mapper might take over so delegate to the cart
            bus.cart.ppuBusWrite(address, data)
        } else {
            with (bus.state.ppu) {
                if (address <= 0x1FFF) {
                    // Pattern Table
                    val offset1 = (address and 0x1000) shr 12
                    val offset2 = address and 0x0FFF
                    patternTableMem[offset1][offset2] = data
                } else if (address <= 0x3EFF) {

                    // Name Table
                    address = address and 0x0FFF
                    val offset1 = getNameTable(address, bus.cart.mirrorMode())
                    val offset2 = address and 0x03FF
                    nameTableMem[offset1][offset2] = data

                } else {
                    // Palette Memory
                    address = address and 0x001F
                    if (address == 0x0010) address = 0x0000
                    if (address == 0x0014) address = 0x0004
                    if (address == 0x0018) address = 0x0008
                    if (address == 0x001C) address = 0x000C
                    paletteMem[address] = data
                }
            }
        }
    }

    private fun getColor(paletteId : Int, pixel : Int) : Color {
        val address = (0x3F00 + ((paletteId shl 2) + pixel))
        val index = ppuBusRead(address)
        return systemPalette[index.toInt() and 0x3F]
    }

    private fun getNameTable(address: Int, mirror: Cartridge.MirrorMode): Int {
        return when (mirror) {
            Cartridge.MirrorMode.VERTICAL -> {
                when {
                    address <= 0x03FF -> 0
                    address in 0x0400..0x07FF -> 1
                    address in 0x0800..0x0BFF -> 0
                    else -> 1
                }
            }
            Cartridge.MirrorMode.HORIZONTAL -> {
                when {
                    address <= 0x03FF -> 0
                    address in 0x0400..0x07FF -> 0
                    address in 0x0800..0x0BFF -> 1
                    else -> 1
                }
            }

            else -> 0
        }
    }

    private fun currentOAM() : Int {
        with (bus.state.ppu) {
            return when (val index = (oamAddress shr 2)) {
                0x0 -> oams[index].y and 0xFF
                0x1 -> oams[index].id and 0xFF
                0x2 -> oams[index].attribute and 0xFF
                0x3 -> oams[index].x and 0xFF
                else -> 0
            }
        }
    }

    fun dma(offset : Int, data: Int) {
        // DMA is used to transfer OAM. Each OAM entry is 4 bytes
        // so this will be called 4 times to fully update the OAM entry
        // The CPU is halted while this is happening so that you don't
        // end up with a corrupted sprite during the transfer
        with (bus.state.ppu) {
            when (val index = (offset shr 2)) {
                0x0 -> oams[index].y = data and 0xFF
                0x1 -> oams[index].id = data and 0xFF
                0x2 -> oams[index].attribute = data and 0xFF
                0x3 -> oams[index].x = data and 0xFF
            }
        }
    }

    fun nmiRequested() : Boolean {
        with (bus.state.ppu) {
            if (nmiRequested) {
                nmiRequested = false
                return true
            }

            return false
        }
    }
}
