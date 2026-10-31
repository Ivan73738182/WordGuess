package com.ivangames.wordguess

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var letterCircle: LetterCircleView
    private lateinit var crosswordView: CrosswordView
    private lateinit var tvCurrentWord: TextView
    private lateinit var rootLayout: ConstraintLayout

    // Текущий уровень
    private var currentLevel = 0

    // База данных уровней (пока 2 уровня для теста)
    private val levels = listOf(
        // Уровень 1
        Level(
            letters = listOf('К', 'О', 'Т'),
            words = listOf(
                CrosswordView.WordPlacement("КОТ", 0, 0, true),
                CrosswordView.WordPlacement("ТОК", 2, 0, false)
            )
        ),
        // Уровень 2
        Level(
            letters = listOf('Р', 'А', 'К'),
            words = listOf(
                CrosswordView.WordPlacement("РАК", 0, 0, true),
                CrosswordView.WordPlacement("КАР", 2, 0, false)
            )
        )
    )

    // Список всех фонов
    private val backgrounds = listOf(
        R.drawable.bg_autumn,
        R.drawable.bg_forest,
        R.drawable.bg_mountains,
        R.drawable.bg_beach
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        letterCircle = findViewById(R.id.letterCircle)
        crosswordView = findViewById(R.id.crosswordView)
        tvCurrentWord = findViewById(R.id.tvCurrentWord)

        loadLevel(currentLevel)
    }

    private fun loadLevel(levelIndex: Int) {
        if (levelIndex >= levels.size) {
            tvCurrentWord.text = "Все уровни пройдены!"
            return
        }

        val level = levels[levelIndex]
        
        // 1. Ставим рандомный фон
        val randomBg = backgrounds[Random.nextInt(backgrounds.size)]
        rootLayout.setBackgroundResource(randomBg)

        // 2. Загружаем буквы и слова
        letterCircle.letters = level.letters
        crosswordView.words = level.words
        
        // Сбрасываем сетку перед загрузкой
        crosswordView.reset()

        tvCurrentWord.text = "Уровень ${levelIndex + 1}"

        // 3. Логика проверки слов
        val validWords = level.words.map { it.word }.toSet()

        letterCircle.onWordCompleted = { word ->
            tvCurrentWord.text = "Собрано: $word"
            
            if (validWords.contains(word)) {
                crosswordView.addGuessedWord(word)
                tvCurrentWord.text = "Отлично! $word"
                
                // Проверяем, все ли слова собраны
                if (crosswordView.isLevelComplete()) {
                    showWinDialog(levelIndex)
                }
            } else {
                tvCurrentWord.text = "Не угадал: $word"
            }
        }
    }

    private fun showWinDialog(currentLevelIndex: Int) {
        // Создаем кастомный диалог
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_win)
        
        // Делаем фон прозрачным, чтобы были видны скругления
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Кнопка "Продолжить"
        val btnContinue = dialog.findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            currentLevel++
            loadLevel(currentLevel)
        }
        
        dialog.setCancelable(false)
        dialog.show()
    }

    // Класс для уровня
    data class Level(
        val letters: List<Char>,
        val words: List<CrosswordView.WordPlacement>
    )
}
