package io.github.vekat.breakoutish

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.vekat.gamepad.api.Gamepad
import io.github.vekat.gamepad.api.gamepad

class BreakoutActivity : AppCompatActivity() {

  private lateinit var breakoutView: BreakoutView

  private lateinit var gamepad: Gamepad

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    breakoutView = BreakoutView(this, windowManager.defaultDisplay)

    setContentView(breakoutView)

    gamepad = breakoutView.gamepad(config)
  }

  override fun onResume() {
    super.onResume()

    breakoutView.resume()

    gamepad.enableInputEvents()
  }

  override fun onPause() {
    super.onPause()

    breakoutView.pause()

    gamepad.disableInputEvents()
  }
}
