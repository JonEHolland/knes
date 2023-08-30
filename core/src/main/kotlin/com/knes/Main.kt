package com.knes

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.StretchViewport
import kotlin.system.exitProcess


class Main : ApplicationAdapter() {
    private lateinit var batch : SpriteBatch
    private lateinit var camera : OrthographicCamera
    private lateinit var viewport : StretchViewport
    private lateinit var texture : Texture
    private lateinit var bus : Bus
    private lateinit var frameRate : FrameRate

    override fun create() {
        frameRate = FrameRate()
        batch = SpriteBatch()
        camera = OrthographicCamera(256f,240f)
        viewport = StretchViewport(256f,240f, camera)
        viewport.apply()
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)

        bus = Bus("../dk.nes", State())
        texture = Texture(bus.state.ppu.SCREEN_WIDTH, bus.state.ppu.SCREEN_HEIGHT, Pixmap.Format.RGBA8888)
        bus.reset()
    }

    override fun render() {
        ScreenUtils.clear(0f,0f,0f,0f)
        camera.update()

        checkInputs()

        bus.tick()
        while (!bus.state.ppu.frameComplete) {
            bus.tick()
        }

        bus.state.ppu.frameComplete = false
        bus!!.state.ppu.screenBuffer.position(0)

        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
        Gdx.gl.glTexImage2D(
            GL20.GL_TEXTURE_2D,
            0,
            GL20.GL_RGBA,
            bus!!.state.ppu.SCREEN_WIDTH,
            bus!!.state.ppu.SCREEN_HEIGHT,
            0,
            GL20.GL_RGBA,
            GL20.GL_BYTE,
            bus!!.state.ppu.screenBuffer)

        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        texture.bind()
        batch.draw(texture, 0f, 0f)
        batch.end()

        frameRate.update()
        frameRate.render()

    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.position[camera.viewportWidth / 2, camera.viewportHeight / 2] = 0f
    }

    override fun dispose() {
        batch.dispose()
    }

    private fun checkInputs() {
        var c : Int = bus.state.bus.controller[0]

        fun checkKey(keyPressed : Boolean, bit: Int) {
            c = if(keyPressed) {
                c or bit
            } else {
                c and bit.inv()
            }
        }

        checkKey(Gdx.input.isKeyPressed(Input.Keys.UP), 0x08)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.DOWN), 0x04)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.LEFT), 0x02)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.RIGHT), 0x01)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.F), 0x80)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.D), 0x40)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.S), 0x20)
        checkKey(Gdx.input.isKeyPressed(Input.Keys.ENTER), 0x10)

        bus.state.bus.controller[0] = c and 0xFF
    }
}
