package com.ivangames.wordguess

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val letterCircle = findViewById<LetterCircleView>(R.id.letterCircle)
        val tvCurrentWord = findViewById<TextView>(R.id.tvCurrentWord)

        // Задаем буквы первого уровня
        letterCircle.letters = listOf('К', 'О', 'Т')

        // Когда свайп закончен
        letterCircle.onWordCompleted = { word ->
            tvCurrentWord.text = "Собрано: $word"
        }
    }
}
