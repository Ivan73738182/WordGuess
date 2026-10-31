package com.ivangames.wordguess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class LetterCircleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var letters: List<Char> = listOf('К', 'О', 'Т')
        set(value) {
            field = value
            // ВАЖНО: Пересчитываем позиции, когда меняются буквы!
            calculatePositions()
            invalidate()
        }

    var onWordCompleted: ((String) -> Unit)? = null

    private val textPaintNormal = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        textSize = 100f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    private val textPaintActive = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC107")
        textSize = 100f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E6FFFFFF") 
        style = Paint.Style.FILL
    }

    private val swipeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC107")
        strokeWidth = 15f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val activeCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC107")
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val shuffleBtnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        style = Paint.Style.FILL
    }
    private val shuffleIconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    private var centerX = 0f
    private var centerY = 0f
    private var bgRadius = 0f
    private var letterRadius = 75f 
    private val shuffleRadius = 50f 
    
    private var positions = mutableListOf<Pair<Float, Float>>()
    
    private val selectedIndices = mutableListOf<Int>()
    private var currentX = 0f
    private var currentY = 0f
    private var isShuffleClicked = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        bgRadius = Math.min(w, h) / 2.5f 
        calculatePositions()
    }

    private fun calculatePositions() {
        positions.clear()
        val count = letters.size
        if (count == 0) return
        
        // Если размеры еще не известны (0), используем дефолтные для расчета
        val cx = if (centerX == 0f) 500f else centerX
        val cy = if (centerY == 0f) 500f else centerY
        val bgRad = if (bgRadius == 0f) 300f else bgRadius

        val radius = bgRad - letterRadius - 20f
        val angleStep = (2 * Math.PI / count).toFloat()
        val startAngle = -Math.PI.toFloat() / 2 

        for (i in 0 until count) {
            val angle = startAngle + i * angleStep
            val x = cx + radius * cos(angle)
            val y = cy + radius * sin(angle)
            positions.add(Pair(x, y))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        canvas.drawCircle(centerX, centerY, bgRadius, bgPaint)

        canvas.drawCircle(centerX, centerY, shuffleRadius, shuffleBtnPaint)
        
        val iconOffset = 20f 
        canvas.drawLine(centerX - iconOffset, centerY - iconOffset, centerX + iconOffset, centerY + iconOffset, shuffleIconPaint)
        canvas.drawLine(centerX - iconOffset, centerY + iconOffset, centerX + iconOffset, centerY - iconOffset, shuffleIconPaint)

        for (i in 0 until selectedIndices.size - 1) {
            val p1 = positions[selectedIndices[i]]
            val p2 = positions[selectedIndices[i + 1]]
            canvas.drawLine(p1.first, p1.second, p2.first, p2.second, swipeLinePaint)
        }
        if (selectedIndices.isNotEmpty() && (currentX != 0f || currentY != 0f)) {
            val lastP = positions[selectedIndices.last()]
            canvas.drawLine(lastP.first, lastP.second, currentX, currentY, swipeLinePaint)
        }

        // ЗАЩИТА: если позиций меньше, чем букв, просто не рисуем лишние
        for (i in letters.indices) {
            if (i >= positions.size) break
            
            val pos = positions[i]
            val isSelected = selectedIndices.contains(i)
            
            if (isSelected) {
                canvas.drawCircle(pos.first, pos.second, letterRadius, activeCirclePaint)
            }
            
            val paint = if (isSelected) textPaintActive else textPaintNormal
            val textY = pos.second - (paint.descent() + paint.ascent()) / 2
            canvas.drawText(letters[i].toString(), pos.first, textY, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentX = event.x
        currentY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val distToShuffle = Math.hypot((event.x - centerX).toDouble(), (event.y - centerY).toDouble()).toFloat()
                if (distToShuffle < shuffleRadius) {
                    isShuffleClicked = true
                    letters = letters.shuffled()
                    invalidate()
                    return true
                }
                
                selectedIndices.clear()
                checkTouch(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isShuffleClicked) {
                    checkTouch(event.x, event.y)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (!isShuffleClicked && selectedIndices.isNotEmpty()) {
                    val word = selectedIndices.map { letters[it] }.joinToString("")
                    onWordCompleted?.invoke(word)
                }
                selectedIndices.clear()
                currentX = 0f
                currentY = 0f
                isShuffleClicked = false
            }
        }
        invalidate()
        return true
    }

    private fun checkTouch(x: Float, y: Float) {
        for (i in positions.indices) {
            if (selectedIndices.contains(i)) continue
            val pos = positions[i]
            val dist = Math.hypot((x - pos.first).toDouble(), (y - pos.second).toDouble()).toFloat()
            if (dist < letterRadius) {
                selectedIndices.add(i)
                break
            }
        }
    }
}
