package io.github.vekat.breakoutish

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

data class Ball(val size: Int) : GameEntity {
  override val rect: RectF = RectF(0f, 0f, 0f + size, 0f + size)

  private val velocity: Float = 200f

  private var xDirection: Int = 1
  private var yDirection: Int = -1

  override fun update(delta: Float) {
    offset(
      (velocity * xDirection * delta),
      (velocity * yDirection * delta)
    )
  }

  override fun draw(delta: Float, alpha: Float, canvas: Canvas, paint: Paint) {
    val displayRect = RectF(rect)

    displayRect.offset(
      velocity * xDirection * delta * alpha,
      velocity * yDirection * delta * alpha
    )

    canvas.drawRect(displayRect, paint)
  }

  fun reverseYVelocity() {
    yDirection = -yDirection
  }

  fun reverseXVelocity() {
    xDirection = -xDirection
  }

  fun onHitPaddle(paddleMovement: Int, paddleTop: Float) {
    offsetTo(y = paddleTop - size)

    if (paddleMovement == Paddle.LEFT && xDirection > 0 || paddleMovement == Paddle.RIGHT && xDirection < 0) {
      reverseXVelocity()
    }
  }
}
