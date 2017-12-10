package io.github.vekat.breakoutish

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceView

const val UPDATES_PER_MS = 30
const val DT = 1000f / UPDATES_PER_MS

class BreakoutView(context: Context, display: Display) : SurfaceView(context), Runnable {
  private var gameThread: HandlerThread? = null
  private var gameHandler: Handler? = null

  @Volatile
  private var running: Boolean = false

  private var paused = true

  private var canvas: Canvas = Canvas()
  private var paint: Paint = Paint()

  private val unit: Int = 16
  private var screenWidth: Int = 0
  private var screenHeight: Int = 0

  private var isTouching: Boolean = false
  private var touchXRatio: Float = 0.5f
  private var paddleCenterXRatio: Float = 0f

  private var paddle: Paddle

  private var ball: Ball

  private val brickSize: Int = unit * 2
  private var maxBricks: Int = 0
  private val brickPoints = 10
  private lateinit var bricks: ArrayList<Brick>

  private var score = 0

  private var lives = 3

  init {
    val outSize = Point()
    display.getSize(outSize)

    screenWidth = outSize.x
    screenHeight = outSize.y

    paddle = Paddle(unit)
    paddleCenterXRatio = paddle.rect.centerX() / screenWidth

    ball = Ball(unit)

    resetGameState()
  }

  private fun resetGameState() {
    val bricksPerColumn = screenWidth / brickSize
    val bricksPerRow = (screenHeight / 2) / brickSize

    maxBricks = bricksPerColumn * bricksPerRow

    bricks = ArrayList(maxBricks)

    for (row in 0 until bricksPerRow) {
      for (column in 0 until bricksPerColumn) {
        bricks.add(Brick(row, column, brickSize))
      }
    }

    ball.offsetTo(screenWidth / 2f, screenHeight - paddle.height - ball.size * 2)
    paddle.offsetTo((screenWidth / 2) - (paddle.width / 2), screenHeight - paddle.height)

    bricks.forEach { it.visible = true }

    if (lives == 0) {
      score = 0
      lives = 3
    }
  }

  override fun run() {
    var alpha = 0f
    var accumulator = 0f

    var lastFrameTime = 0L

    while (running) {
      val startTime = System.currentTimeMillis()

      if (!paused) {
        accumulator += lastFrameTime

        while (accumulator >= DT) {
          update(DT)
          accumulator -= DT
        }

        alpha = accumulator / (DT * DT)
      }

      if (holder.surface.isValid) {
        try {
          canvas = holder.lockCanvas()
          synchronized(holder) {
            drawToSurface(canvas, alpha)
          }
        } finally {
          holder.unlockCanvasAndPost(canvas)
        }
      }

      lastFrameTime = System.currentTimeMillis() - startTime
    }
  }

  private fun update(dt: Float) {

    if (isTouching) {
      when {
        touchXRatio - 0.025 <= paddleCenterXRatio && touchXRatio + 0.025 >= paddleCenterXRatio ->
          paddle.movement = Paddle.NONE
        touchXRatio > paddleCenterXRatio -> paddle.movement = Paddle.RIGHT
        touchXRatio < paddleCenterXRatio -> paddle.movement = Paddle.LEFT
      }
    } else {
      paddle.movement = Paddle.NONE
    }

    paddle.update(dt)
    ball.update(dt)

    bricks.filter { it.visible }.forEach {
      if (ball intersects it) {
        it.visible = false
        ball.reverseYVelocity()
        score += brickPoints
      }
    }

    if (ball intersects paddle) {
      ball.onHitPaddle(paddle.movement, screenHeight - paddle.height)
      ball.reverseYVelocity()
    }

    if (ball.rect.bottom > screenHeight) {
      ball.offsetTo(y = screenHeight - ball.size.toFloat())
      ball.reverseYVelocity()

      lives--

      if (lives == 0) {
        paused = true
        resetGameState()
      }
    }

    if (ball.rect.top < 0) {
      ball.offsetTo(y = 0f)
      ball.reverseYVelocity()
    }

    if (ball.rect.left < 0) {
      ball.offsetTo(x = 0f)
      ball.reverseXVelocity()
    }

    if (ball.rect.right > screenWidth) {
      ball.offsetTo(x = screenWidth - ball.size.toFloat())
      ball.reverseXVelocity()
    }

    if (bricks.none { it.visible }) {
      paused = true
      resetGameState()
    }
  }

  private fun drawToSurface(canvas: Canvas, alpha: Float) {
    canvas.drawColor(Color.WHITE)

    paint.color = Color.BLACK

    paddle.draw(alpha, canvas, paint)

    paint.color = Color.RED

    ball.draw(alpha, canvas, paint)

    paint.color = Color.BLACK

    bricks.filter { it.visible }.forEach { it.draw(alpha, canvas, paint) }

    paint.color = Color.BLACK

    paint.textSize = unit.toFloat()

    canvas.drawText(resources.getString(R.string.score, score), unit * 0.5f, (screenHeight / 2) + unit.toFloat(), paint)
    canvas.drawText(resources.getString(R.string.lives, lives), unit * 0.5f, (screenHeight / 2) + (unit * 3).toFloat(), paint)
  }

  fun resume() {
    running = true

    gameThread = HandlerThread("GameThread")
    gameThread!!.start()

    gameHandler = Handler(gameThread!!.looper)
    gameHandler!!.post(this)
  }

  fun pause() {
    running = false

    gameThread!!.quit()

    gameThread = null
    gameHandler = null
  }

  override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
    when (motionEvent.action and MotionEvent.ACTION_MASK) {

      MotionEvent.ACTION_DOWN -> {
        paused = false
        isTouching = true
        touchXRatio = motionEvent.x / screenWidth
        paddleCenterXRatio = paddle.rect.centerX() / screenWidth
      }

      MotionEvent.ACTION_MOVE -> {
        touchXRatio = motionEvent.x / screenWidth
        paddleCenterXRatio = paddle.rect.centerX() / screenWidth
      }

      MotionEvent.ACTION_UP -> {
        isTouching = false
      }
    }
    return true
  }

}