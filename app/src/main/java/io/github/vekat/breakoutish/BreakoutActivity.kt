package io.github.vekat.breakoutish

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class BreakoutActivity : AppCompatActivity() {
  /**
   * Declara uma instância de BreakoutView que será
   * inicializada dentro do método `onCreate()`.
   */
  private lateinit var breakoutView: BreakoutView

  // TODO: declarar variáveis necessárias [Tarefa B - Passo 2]

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Inicializa a instância de BreakoutView
    breakoutView = BreakoutView(this, windowManager.defaultDisplay)

    setContentView(breakoutView)

    // TODO: inicializar e configurar objetos necessários [Tarefa B - Passo 2]
  }

  override fun onResume() {
    super.onResume()

    breakoutView.resume()

    // TODO: implementar ação quando a Activity for iniciada [Tarefa B - Passo 3]
  }

  override fun onPause() {
    super.onPause()

    breakoutView.pause()

    // TODO: implementar ação quando a Activity for iniciada [Tarefa B - Passo 3]
  }
}
