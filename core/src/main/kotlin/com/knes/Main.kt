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
import net.beadsproject.beads.core.AudioContext
import net.beadsproject.beads.core.io.JavaSoundAudioIO
import net.beadsproject.beads.ugens.Function
import net.beadsproject.beads.ugens.WaveShaper
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicBoolean
import javax.sound.sampled.AudioSystem


class Main(var romName : String) : ApplicationAdapter() {
    private lateinit var batch : SpriteBatch
    private lateinit var camera : OrthographicCamera
    private lateinit var viewport : StretchViewport
    private lateinit var texture : Texture
    private lateinit var bus : Bus
    private lateinit var audio : AudioContext

    private val busFree = AtomicBoolean(true)

    override fun create() {
        batch = SpriteBatch()
        camera = OrthographicCamera(256f, 240f)
        viewport = StretchViewport(256f, 240f, camera)
        viewport.apply()
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f)

        val jsa = JavaSoundAudioIO()
        jsa.selectMixer(findAudioOutput())
        audio = AudioContext(jsa)

        bus = Bus(romName, State(), audio.sampleRate.toInt())
        texture = Texture(bus.state.ppu.SCREEN_WIDTH, bus.state.ppu.SCREEN_HEIGHT, Pixmap.Format.RGBA8888)
        bus.reset()

        val audioProcessor: Function = object : Function(WaveShaper(audio)) {
            override fun calculate(): Float {
                var ready = false
                while (!ready && busFree.get()) {
                    ready = bus.tick()
                }

                return bus.audioSample
            }
        }

        audio.out.addInput(audioProcessor)
        audio.start()
    }

    override fun render() {
        busFree.set(false)

        if (bus.state.ppu.frameComplete) {
            ScreenUtils.clear(0f,0f,0f,0f)
            camera.update()
            checkInputs()

            bus.state.ppu.frameComplete = false
            //bus.state.ppu.screenBuffer.position(0)

            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)
            Gdx.gl.glTexImage2D(
                GL20.GL_TEXTURE_2D,
                0,
                GL20.GL_RGBA,
                bus.state.ppu.SCREEN_WIDTH,
                bus.state.ppu.SCREEN_HEIGHT,
                0,
                GL20.GL_RGBA,
                GL20.GL_BYTE,
                bus.state.ppu.screenBuffer)

            batch.projectionMatrix = camera.combined
            batch.begin()
            texture.bind()
            batch.draw(texture, 0f, 0f)
            batch.end()
        }

        busFree.set(true)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        camera.position[camera.viewportWidth / 2, camera.viewportHeight / 2] = 0f
    }

    override fun dispose() {
        batch.dispose()
        audio.stop()
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

    private fun findAudioOutput() : Int {
        var checker : AudioContext
        AudioSystem.getMixerInfo().mapIndexed { index, mixer ->
            val jsa = JavaSoundAudioIO()
            jsa.selectMixer(index)
            checker = AudioContext(jsa)

            val valid = BooleanArray(1)
            try {
                val check: Function = object : Function(WaveShaper(checker)) {
                    override fun calculate(): Float {
                        valid[0] = true
                        return 0f
                    }
                }

                checker.out.addInput(check)
                checker.start()
                Thread.sleep(200)
                if (valid[0]) {
                    checker.stop()
                    return index
                }

            } catch(e : IllegalArgumentException) {
                // Eat it
            }  catch (e : InterruptedException) {
                // Eat it
            }
        }

        return 0
    }


}
