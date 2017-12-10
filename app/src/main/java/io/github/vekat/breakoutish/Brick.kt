package io.github.vekat.breakoutish

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

data class Brick(private val row: Int, private val column: Int, private val size: Int) : GameEntity {
  private val padding: Float = 1f

  var visible: Boolean = true

  override val rect: RectF = RectF(
    column * size + padding,
    row * size + padding,
    (column * size) + size - padding,
    (row * size) + size - padding
  )

  override fun draw(alpha: Float, canvas: Canvas, paint: Paint) {
    canvas.drawRect(rect, paint)
  }
}
