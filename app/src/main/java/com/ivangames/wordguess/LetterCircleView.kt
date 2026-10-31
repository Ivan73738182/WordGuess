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
            invalidate()
        }

    var onWordCompleted: ((String) -> Unit)? = null

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val circleActivePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC107") // Желтый
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    // Красивая тонкая линия для "паутины" (фоновые соединения)
    private val webLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80FFFFFF") // Полупрозрачный белый
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    
    // Толстая линия для свайпа
    private val swipeLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFC107") // Желтый
        strokeWidth = 15f
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private var centerX = 0f
    private var centerY = 0f
    private val letterRadius = 70f
    private var positions = mutableListOf<Pair<Float, Float>>()
    
    // Для свайпа
    private val selectedIndices = mutableListOf<Int>()
    private var currentX = 0f
    private var currentY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        calculatePositions()
    }

    private fun calculatePositions() {
        positions.clear()
        val count = letters.size
        if (count == 0) return
        
        // Располагаем буквы по кругу, как на скрине (сверху, справа, снизу, слева и т.д.)
        val radius = Math.min(centerX, centerY) - letterRadius - 20f
        val angleStep = (2 * Math.PI / count).toFloat()
        val startAngle = -Math.PI.toFloat() / 2 // Начинаем с верхней точки

        for (i in 0 until count) {
            val angle = startAngle + i * angleStep
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            positions.add(Pair(x, y))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // 1. Рисуем ФОНОВУЮ "паутину" (соединяем ВСЕ буквы со всеми)
        for (i in positions.indices) {
            for (j in i + 1 until positions.size) {
                val p1 = positions[i]
                val p2 = positions[j]
                canvas.drawLine(p1.first, p1.second, p2.first, p2.second, webLinePaint)
            }
        }

        // 2. Рисуем ЖЕЛТЫЕ линии свайпа (поверх паутины)
        for (i in 0 until selectedIndices.size - 1) {
            val p1 = positions[selectedIndices[i]]
            val p2 = positions[selectedIndices[i + 1]]
            canvas.drawLine(p1.first, p1.second, p2.first, p2.second, swipeLinePaint)
        }
        // Линия от последней буквы к пальцу
        if (selectedIndices.isNotEmpty() && (currentX != 0f || currentY != 0f)) {
            val lastP = positions[selectedIndices.last()]
            canvas.drawLine(lastP.first, lastP.second, currentX, currentY, swipeLinePaint)
        }

        // 3. Рисуем кружки и буквы
        for (i in letters.indices) {
            val pos = positions[i]
            val paint = if (selectedIndices.contains(i)) circleActivePaint else circlePaint
            canvas.drawCircle(pos.first, pos.second, letterRadius, paint)
            
            // Буква (смещение для центрирования)
            val textY = pos.second - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(letters[i].toString(), pos.first, textY, textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        currentX = event.x
        currentY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                selectedIndices.clear()
                checkTouch(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                checkTouch(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                if (selectedIndices.isNotEmpty()) {
                    val word = selectedIndices.map { letters[it] }.joinToString("")
                    onWordCompleted?.invoke(word)
                }
                selectedIndices.clear()
                currentX = 0f
                currentY = 0f
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
