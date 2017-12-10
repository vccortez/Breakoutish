package io.github.vekat.breakoutish

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class BreakoutActivity : AppCompatActivity() {

  private lateinit var breakoutView: BreakoutView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    breakoutView = BreakoutView(this, windowManager.defaultDisplay)

    setContentView(breakoutView)
  }

  override fun onResume() {
    super.onResume()

    breakoutView.resume()
  }

  override fun onPause() {
    super.onPause()

    breakoutView.pause()
  }
}
