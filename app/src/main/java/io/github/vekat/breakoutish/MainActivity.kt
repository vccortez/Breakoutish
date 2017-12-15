package io.github.vekat.breakoutish

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Math.sin

class MainActivity : AppCompatActivity() {
  /**
   * Declara uma instância de MenuView que será
   * inicializada dentro do método `onCreate()`.
   */
  private lateinit var menuView: MenuView

  // TODO: declarar variáveis necessárias [Tarefa A - Passo 2]

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Inicializa a instância de MenuView
    menuView = MenuView(this)

    setContentView(menuView)

    // TODO: inicializar e configurar objetos necessários [Tarefa A - Passo 2]
  }

  override fun onResume() {
    super.onResume()

    // TODO: implementar ação quando a Activity for iniciada [Tarefa A - Passo 3]
  }

  override fun onPause() {
    super.onPause()

    // TODO: implementar ação quando a Activity for pausada [Tarefa A - Passo 3]
  }

  /**
   * Essa classe interna implementa a View do menu inicial do jogo com dois
   * botões inativos e uma seta que segue o toque do jogador.
   * TODO: adicionar e implementar uma interface que receba eventos [Tarefa A - Passo 1]
   */
  inner class MenuView(context: Context) : RelativeLayout(context) {
    private var screenWidth: Int
    private var screenHeight: Int

    private var isTapping: Boolean = false
    private var downX: Float = 0f
    private var downY: Float = 0f
    private val moveThreshold: Float = 20f

    /**
     * A value inside the range [-1, 1], representing the X coordinate of the latest screen touch
     * from left to right. E.g.: if (xRelativeToWidth == 0f) the latest touch was in the middle.
     */
    private var xRelativeToWidth: Float = 0f

    init {
      LayoutInflater.from(context).inflate(R.layout.activity_main, this, true)

      val outSize = Point()

      windowManager.defaultDisplay.getSize(outSize)

      screenWidth = outSize.x
      screenHeight = outSize.y

      updateView()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
      when (event.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
          downX = event.x
          downY = event.y
          isTapping = true
          return true
        }
        MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (isTapping) {
          isTapping = false
          return performClick()
        }
        MotionEvent.ACTION_MOVE -> {
          debugLog("${Math.abs(downX - event.x)}, ${Math.abs(downY - event.y)}")
          if (Math.abs(downX - event.x) > moveThreshold || Math.abs(downY - event.y) > moveThreshold) {
            isTapping = false
          }

          if (!isTapping) {
            val xToWidthRatio = event.x / screenWidth

            // Range conversion from [0, 1] to [-1, 1]
            xRelativeToWidth = (xToWidthRatio * 2) - 1

            updateView()
          }

          return true
        }
      }

      return false
    }

    override fun performClick(): Boolean {
      super.performClick()

      when (true) {
        play_label.isEnabled -> iniciarBreakoutActivity()
        quit_label.isEnabled -> fecharActivity()
      }

      return true
    }

    private fun updateView() {
      image_arrow.rotation = 45 * xRelativeToWidth
      debug_axis.text = getString(R.string.axis, xRelativeToWidth)

      when (xRelativeToWidth) {
        in -1f..-0.5f -> {
          play_label.isEnabled = true
          quit_label.isEnabled = false
        }
        in 0.5f..1f -> {
          play_label.isEnabled = false
          quit_label.isEnabled = true
        }
        else -> {
          play_label.isEnabled = false
          quit_label.isEnabled = false
        }
      }
    }

    // TODO: chamar essa função com o valor azimuth da orientação [Tarefa A - Passo 4]
    private fun atualizarEixo(azimuth: Float) {
      val seno = sin(azimuth.toDouble()).toFloat()

      xRelativeToWidth = seno

      updateView()
    }
  }

  fun iniciarBreakoutActivity() {
    startActivity(Intent(this, BreakoutActivity::class.java))
  }

  fun fecharActivity() {
    finish()
  }

  fun debugLog(message: String) {
    Log.d(this.localClassName, message)
  }
}
