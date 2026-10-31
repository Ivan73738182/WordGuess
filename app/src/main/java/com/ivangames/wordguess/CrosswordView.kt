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

    // Слово, его координаты (x, y) и направление (true = горизонтально, false = вертикально)
    data class WordPlacement(val word: String, val startX: Int, val startY: Int, val isHorizontal: Boolean)

    // Список слов для текущего уровня
    var words: List<WordPlacement> = listOf(
        WordPlacement("КОТ", 0, 0, true),
        WordPlacement("ТОК", 0, 0, false)
    )
        set(value) {
            field = value
            invalidate()
        }

    // Буквы, которые уже угаданы (ключ - "x,y", значение - буква)
    private val guessedLetters = mutableMapOf<String, Char>()

    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1976D2")
        textSize = 60f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }
    
    private var cellSize = 100f
    private var offsetX = 0f
    private var offsetY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Вычисляем размер клетки, чтобы сетка влезла
        cellSize = Math.min(w, h) / 6f
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
                rect.set(left, top, left + cellSize, top + cellSize)
                
                // Рисуем пустой белый квадрат
                canvas.drawRoundRect(rect, 15f, 15f, cellPaint)

                // Если буква угадана, рисуем её
                val key = "$x,$y"
                if (guessedLetters.containsKey(key)) {
                    val char = guessedLetters[key]!!
                    val textY = top + cellSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2
                    canvas.drawText(char.toString(), left + cellSize / 2, textY, textPaint)
                }
            }
        }
    }

    // Метод для добавления угаданного слова
    fun addGuessedWord(word: String) {
        for (w in words) {
            if (w.word == word) {
                for (i in word.indices) {
                    val x = if (w.isHorizontal) w.startX + i else w.startX
                    val y = if (w.isHorizontal) w.startY else w.startY + i
                    guessedLetters["$x,$y"] = word[i]
                }
            }
        }
        invalidate()
    }
}
