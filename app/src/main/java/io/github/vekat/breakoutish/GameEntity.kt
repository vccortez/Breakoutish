package io.github.vekat.breakoutish

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface GameEntity {
  val rect: RectF

  infix fun intersects(other: GameEntity): Boolean = RectF.intersects(this.rect, other.rect)

  fun offsetTo(x: Float = rect.left, y: Float = rect.top) = rect.offsetTo(x, y)

  fun offset(dx: Float = 0f, dy: Float = 0f) = rect.offset(dx, dy)

  fun update(delta: Float) = Unit

  fun draw(delta: Float, alpha: Float, canvas: Canvas, paint: Paint)
}