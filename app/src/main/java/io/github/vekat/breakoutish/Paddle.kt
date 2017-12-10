package io.github.vekat.breakoutish

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class Paddle(unit: Int) : GameEntity {
  companion object {
    val LEFT = -1
    val NONE = 0
    val RIGHT = 1
  }

  val width: Float = unit * 4f
  val height: Float = unit * 2f

  override val rect: RectF = RectF(0f, 0f, width, height)

  private val velocity: Float = 450f

  var movement = NONE

  override fun update(delta: Float) {
    when (movement) {
      LEFT -> offset(-velocity / delta)
      RIGHT -> offset(+velocity / delta)
    }
  }

  override fun draw(alpha: Float, canvas: Canvas, paint: Paint) {
    val displayRect = RectF(rect)

    when (movement) {
      LEFT -> displayRect.offset(-velocity * alpha, 0f)
      RIGHT -> displayRect.offset(+velocity * alpha, 0f)
    }

    canvas.drawRect(displayRect, paint)
  }
}

