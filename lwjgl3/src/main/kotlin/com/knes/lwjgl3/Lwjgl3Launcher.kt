@file:JvmName("Lwjgl3Launcher")

package com.knes.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.knes.Main

/** Launches the desktop (LWJGL3) application. */
fun main(args : Array<String>) {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
      return
    Lwjgl3Application(Main(args[0]), Lwjgl3ApplicationConfiguration().apply {
        setTitle("knes")
        setForegroundFPS(60)
        useVsync(true)
        setWindowedMode(640, 480)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}
