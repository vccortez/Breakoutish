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

/**
 * Essa classe implementa a View de animação principal do jogo.
 * No jogo **breakout**, o jogador controla um rebatedor e seu
 * objetivo é rebater a bola para destruir os blocos restantes.
 * TODO: adicionar e implementar uma interface que receba eventos [Tarefa B - Passo 1]
 */
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
    paused = true

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
    var dt = STEP
    var accumulator = 0.0

    while (running) {
      val t = System.currentTimeMillis()

      if (!paused) {
        accumulator += dt

        while (accumulator >= STEP) {
          accumulator -= STEP

          update(STEP.toFloat())
        }
      }

      if (holder.surface.isValid) {
        try {
          canvas = holder.lockCanvas()

          synchronized(holder) {
            drawToSurface((accumulator / STEP).toFloat())
          }
        } finally {
          holder.unlockCanvasAndPost(canvas)
        }
      }

      dt = (System.currentTimeMillis() - t) / 1000.0
    }
  }

  private fun update(dt: Float) {

    paddleCenterXRatio = paddle.rect.centerX() / screenWidth

    paddle.movement = when {
      touchXRatio - 0.05f > paddleCenterXRatio -> Paddle.RIGHT
      touchXRatio + 0.05f < paddleCenterXRatio -> Paddle.LEFT
      else -> Paddle.NONE
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

    if (paddle.rect.left < 0f) {
      paddle.offsetTo(x = 0f)
      paddle.movement = Paddle.NONE
    } else if (paddle.rect.right > screenWidth) {
      paddle.offsetTo(x = screenWidth - paddle.width)
      paddle.movement = Paddle.NONE
    }

    if (bricks.none { it.visible }) {
      resetGameState()
    }
  }

  private fun drawToSurface(alpha: Float) {
    canvas.drawColor(Color.WHITE)

    paint.color = Color.BLACK

    paddle.draw(STEP.toFloat(), alpha, canvas, paint)

    paint.color = Color.RED

    ball.draw(STEP.toFloat(), alpha, canvas, paint)

    paint.color = Color.BLACK

    bricks.filter { it.visible }.forEach { it.draw(STEP.toFloat(), alpha, canvas, paint) }

    paint.color = Color.BLACK

    paint.textSize = unit.toFloat()

    canvas.drawText(resources.getString(R.string.score, score), unit * 0.5f, (screenHeight / 2) + unit.toFloat(), paint)
    canvas.drawText(resources.getString(R.string.lives, lives), unit * 0.5f, (screenHeight / 2) + (unit * 3).toFloat(), paint)
    canvas.drawText(resources.getString(R.string.axis, touchXRatio), unit * 0.5f, (screenHeight / 2) + (unit * 5).toFloat(), paint)
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

        updateTouchX(motionEvent.x)
      }

      MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        updateTouchX(motionEvent.x)
      }
    }
    return true
  }

  // TODO: chamar essa função com o valor azimuth da orientação [Tarefa B - Passo 4]
  private fun atualizarEixo(azimuth: Float) {
    val seno = Math.sin(azimuth.toDouble()).toFloat()

    touchXRatio = (1f + seno) / 2f
  }

  private fun updateTouchX(motionEventX: Float) {
    touchXRatio = motionEventX / screenWidth
  }
}
