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

    private val guessedWords = mutableSetOf<String>()

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
        cellSize = Math.min(w, h) / 6f
        offsetX = (w - cellSize * 5) / 2f
        offsetY = (h - cellSize * 5) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF()

        // Пустые клетки
        for (word in words) {
            for (i in word.word.indices) {
                val x = if (word.isHorizontal) word.startX + i else word.startX
                val y = if (word.isHorizontal) word.startY else word.startY + i
                
                val left = offsetX + x * cellSize
                val top = offsetY + y * cellSize
                
                rect.set(left + 4f, top + 4f, left + cellSize - 4f, top + cellSize - 4f)
                canvas.drawRoundRect(rect, 12f, 12f, cellPaint)
            }
        }

        // Угаданные клетки
        for (word in words) {
            if (guessedWords.contains(word.word)) {
                for (i in word.word.indices) {
                    val x = if (word.isHorizontal) word.startX + i else word.startX
                    val y = if (word.isHorizontal) word.startY else word.startY + i
                    
                    val left = offsetX + x * cellSize
                    val top = offsetY + y * cellSize
                    
                    rect.set(left + 4f, top + 4f, left + cellSize - 4f, top + cellSize - 4f)
                    canvas.drawRoundRect(rect, 12f, 12f, cellGuessedPaint)
                    
                    val char = word.word[i]
                    val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2
                    canvas.drawText(char.toString(), left + cellSize / 2, textY, textPaint)
                }
            }
        }
    }

    fun addGuessedWord(word: String) {
        if (words.any { it.word == word }) {
            guessedWords.add(word)
            invalidate()
        }
    }

    // Проверка, все ли слова угаданы
    fun isLevelComplete(): Boolean {
        return words.all { guessedWords.contains(it.word) }
    }

    // Сброс сетки для нового уровня
    fun reset() {
        guessedWords.clear()
        invalidate()
    }
}
