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

        letterCircle.letters = listOf('К', 'О', 'Т')

        val levelWords = listOf(
            CrosswordView.WordPlacement("КОТ", 0, 0, true),
            CrosswordView.WordPlacement("ТОК", 2, 0, false)
        )
        crosswordView.words = levelWords

        val validWords = levelWords.map { it.word }.toSet()

        letterCircle.onWordCompleted = { word ->
            tvCurrentWord.text = "Собрано: $word"
            
            if (validWords.contains(word)) {
                crosswordView.addGuessedWord(word)
                tvCurrentWord.text = "Отлично! $word"
            } else {
                tvCurrentWord.text = "Не угадал: $word"
            }
        }
    }
}
