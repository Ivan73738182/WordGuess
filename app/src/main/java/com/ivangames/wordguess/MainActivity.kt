package com.ivangames.wordguess

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val letterCircle = findViewById<LetterCircleView>(R.id.letterCircle)
        val crosswordView = findViewById<CrosswordView>(R.id.crosswordView)
        val tvCurrentWord = findViewById<TextView>(R.id.tvCurrentWord)

        // Буквы для круга (уровень 1)
        letterCircle.letters = listOf('К', 'О', 'Т')

        // Слова для сетки (пересекаются на букве 'Т')
        crosswordView.words = listOf(
            CrosswordView.WordPlacement("КОТ", 0, 0, true), // Горизонтально
            CrosswordView.WordPlacement("ТОК", 2, 0, false) // Вертикально, начинается с 'Т' (x=2)
        )

        letterCircle.onWordCompleted = { word ->
            tvCurrentWord.text = "Собрано: $word"
            
            if (word == "КОТ" || word == "ТОК") {
                crosswordView.addGuessedWord(word)
            }
        }
    }
}
