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
        // КОТ: x=0, y=0, горизонтально
        // ТОК: x=2, y=0, вертикально (начинается с 'Т' от КОТ)
        crosswordView.words = listOf(
            CrosswordView.WordPlacement("КОТ", 0, 0, true),
            CrosswordView.WordPlacement("ТОК", 2, 0, false)
        )

        // Когда свайп закончен
        letterCircle.onWordCompleted = { word ->
            tvCurrentWord.text = "Собрано: $word"
            
            // Если слово правильное (КОТ или ТОК), вписываем в сетку
            if (word == "КОТ" || word == "ТОК") {
                crosswordView.addGuessedWord(word)
            }
        }
    }
}
