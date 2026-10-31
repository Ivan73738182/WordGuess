package com.ivangames.wordguess

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CrosswordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class WordPlacement(val word: String, val startX: Int, val startY: Int, val isHorizontal: Boolean)

    var words: List<WordPlacement> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    private val guessedLetters = mutableMapOf<String, Char>()

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B3D4F0") 
        style = Paint.Style.FILL
    }
    private val cellGuessedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 80f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    private var cellSize = 100f
    private var offsetX = 0f
    private var offsetY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // УМЕНЬШИЛИ КЛЕТКИ (/ 8f), чтобы сетка не была огромной
        cellSize = Math.min(w, h) / 8f
        
        // Центрируем сетку (примерно на глаз, по 5 клеткам в ряд)
        offsetX = (w - cellSize * 5) / 2f
        offsetY = (h - cellSize * 5) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF()

        for (word in words) {
            for (i in word.word.indices) {
                val x = if (word.isHorizontal) word.startX + i else word.startX
                val y = if (word.isHorizontal) word.startY else word.startY + i
                
                val left = offsetX + x * cellSize
                val top = offsetY + y * cellSize
                
                // Чуть меньше зазоры, чтобы сетка смотрелась плотнее
                rect.set(left + 4f, top + 4f, left + cellSize - 4f, top + cellSize - 4f)

                val key = "$x,$y"
                
                if (guessedLetters.containsKey(key)) {
                    canvas.drawRoundRect(rect, 12f, 12f, cellGuessedPaint)
                    val char = guessedLetters[key]!!
                    val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2
                    canvas.drawText(char.toString(), left + cellSize / 2, textY, textPaint)
                } else {
                    canvas.drawRoundRect(rect, 12f, 12f, cellPaint)
                }
            }
        }
    }

    fun addGuessedWord(word: String) {
        for (w in words) {
            if (w.word == word) {
                for (i in word.indices) {
                    val x = if (w.isHorizontal) w.startX + i else w.startX
                    val y = if (w.isHorizontal) w.startY + i else w.startY
                    guessedLetters["$x,$y"] = word[i]
                }
            }
        }
        invalidate()
    }
}
