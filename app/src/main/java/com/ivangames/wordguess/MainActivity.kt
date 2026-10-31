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

    private var currentLevel = 0

    private val levels = listOf(
        Level(listOf('К', 'О', 'Т'), listOf(
            CrosswordView.WordPlacement("КОТ", 1, 1, true),
            CrosswordView.WordPlacement("ТОК", 3, 1, false)
        )),
        Level(listOf('Р', 'А', 'К'), listOf(
            CrosswordView.WordPlacement("РАК", 1, 1, true),
            CrosswordView.WordPlacement("КАР", 3, 1, false)
        )),
        Level(listOf('С', 'О', 'Н'), listOf(
            CrosswordView.WordPlacement("СОН", 1, 1, true),
            CrosswordView.WordPlacement("НОС", 3, 1, false)
        )),
        Level(listOf('Р', 'У', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("РУКА", 1, 2, true),
            CrosswordView.WordPlacement("РАК", 1, 2, false),
            CrosswordView.WordPlacement("УРА", 2, 1, false)
        )),
        Level(listOf('М', 'А', 'К', 'О'), listOf(
            CrosswordView.WordPlacement("МАКО", 1, 2, true),
            CrosswordView.WordPlacement("МАК", 1, 2, false),
            CrosswordView.WordPlacement("КОМА", 2, 1, false)
        )),
        Level(listOf('Л', 'И', 'С', 'А'), listOf(
            CrosswordView.WordPlacement("ЛИСА", 1, 2, true),
            CrosswordView.WordPlacement("СИЛА", 2, 1, false),
            CrosswordView.WordPlacement("ЛИС", 1, 2, false)
        )),
        Level(listOf('З', 'И', 'М', 'А'), listOf(
            CrosswordView.WordPlacement("ЗИМА", 1, 1, true),
            CrosswordView.WordPlacement("ЗИМ", 1, 1, false)
        )),
        Level(listOf('Г', 'О', 'Р', 'А'), listOf(
            CrosswordView.WordPlacement("ГОРА", 1, 1, true),
            CrosswordView.WordPlacement("РОГ", 2, 1, false)
        )),
        Level(listOf('С', 'Л', 'О', 'Н'), listOf(
            CrosswordView.WordPlacement("СЛОН", 1, 2, true),
            CrosswordView.WordPlacement("НОС", 3, 1, false),
            CrosswordView.WordPlacement("СОН", 1, 1, false)
        )),
        Level(listOf('В', 'О', 'Д', 'А'), listOf(
            CrosswordView.WordPlacement("ВОДА", 1, 1, true),
            CrosswordView.WordPlacement("ВОД", 1, 1, false)
        )),
        Level(listOf('К', 'У', 'К', 'Л', 'А'), listOf(
            CrosswordView.WordPlacement("КУКЛА", 1, 2, true),
            CrosswordView.WordPlacement("ЛУК", 3, 1, false),
            CrosswordView.WordPlacement("КУЛАК", 1, 1, false)
        )),
        Level(listOf('П', 'Е', 'Н', 'А', 'Л'), listOf(
            CrosswordView.WordPlacement("ПЕНАЛ", 1, 2, true),
            CrosswordView.WordPlacement("ПЕНА", 1, 1, false),
            CrosswordView.WordPlacement("ЛЕН", 4, 1, false)
        )),
        Level(listOf('Т', 'О', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("ТОЧКА", 1, 2, true),
            CrosswordView.WordPlacement("ТОК", 1, 1, false),
            CrosswordView.WordPlacement("КОТ", 3, 2, false)
        )),
        Level(listOf('С', 'Т', 'Е', 'Н', 'А'), listOf(
            CrosswordView.WordPlacement("СТЕНА", 1, 2, true),
            CrosswordView.WordPlacement("СЕТ", 1, 1, false),
            CrosswordView.WordPlacement("СТЕН", 1, 2, false)
        )),
        Level(listOf('К', 'А', 'Р', 'Т', 'А'), listOf(
            CrosswordView.WordPlacement("КАРТА", 1, 2, true),
            CrosswordView.WordPlacement("КАРТ", 1, 2, false),
            CrosswordView.WordPlacement("РАК", 2, 1, false)
        )),
        Level(listOf('М', 'А', 'С', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("МАСКА", 1, 2, true),
            CrosswordView.WordPlacement("МАК", 1, 1, false),
            CrosswordView.WordPlacement("КАСКА", 3, 1, false)
        )),
        Level(listOf('Р', 'У', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("РУЧКА", 1, 2, true),
            CrosswordView.WordPlacement("РУКА", 1, 1, false),
            CrosswordView.WordPlacement("РАК", 4, 1, false)
        )),
        Level(listOf('П', 'Е', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("ПЕЧКА", 1, 2, true),
            CrosswordView.WordPlacement("ПЕЧЬ", 1, 1, false),
            CrosswordView.WordPlacement("РАК", 4, 1, false)
        )),
        Level(listOf('Г', 'Р', 'У', 'Ш', 'А'), listOf(
            CrosswordView.WordPlacement("ГРУША", 1, 2, true),
            CrosswordView.WordPlacement("ГРУШ", 1, 1, false)
        )),
        Level(listOf('Ш', 'К', 'О', 'Л', 'А'), listOf(
            CrosswordView.WordPlacement("ШКОЛА", 1, 2, true),
            CrosswordView.WordPlacement("ШКОЛ", 1, 1, false),
            CrosswordView.WordPlacement("КОЛ", 2, 1, false)
        ))
    )

    private val backgrounds = listOf(
        R.drawable.bg_autumn,
        R.drawable.bg_forest,
        R.drawable.bg_mountains,
        R.drawable.bg_beach
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            val errorText = android.util.Log.getStackTraceString(e)
            runOnUiThread {
                showErrorScreen(errorText)
                logToFile("CRASH: $errorText")
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        letterCircle = findViewById(R.id.letterCircle)
        crosswordView = findViewById(R.id.crosswordView)
        tvCurrentWord = findViewById(R.id.tvCurrentWord)

        logToFile("Приложение запущено")
        loadLevel(currentLevel)
    }

    private fun showErrorScreen(error: String) {
        val tv = TextView(this).apply {
            text = "ОШИБКА:\n\n$error"
            setTextColor(android.graphics.Color.RED)
            setBackgroundColor(android.graphics.Color.BLACK)
            textSize = 12f
            setPadding(20, 20, 20, 20)
            isVerticalScrollBarEnabled = true
        }
        setContentView(tv)
    }

    private fun loadLevel(levelIndex: Int) {
        try {
            if (levelIndex >= levels.size) {
                tvCurrentWord.text = "Все 20 уровней пройдены! 🎉"
                return
            }

            logToFile("Начинаем загрузку уровня ${levelIndex + 1}")

            val level = levels[levelIndex]
            
            logToFile("Ставим фон")
            rootLayout.setBackgroundResource(backgrounds[Random.nextInt(backgrounds.size)])
            
            logToFile("Ставим буквы: ${level.letters}")
            letterCircle.letters = level.letters

            logToFile("Ставим слова: ${level.words.map { it.word }}")
            crosswordView.words = level.words
            
            logToFile("Сбрасываем сетку")
            crosswordView.reset()

            tvCurrentWord.text = "Уровень ${levelIndex + 1}"

            val validWords = level.words.map { it.word }.toSet()

            letterCircle.onWordCompleted = { word ->
                tvCurrentWord.text = "Собрано: $word"
                
                if (validWords.contains(word)) {
                    crosswordView.addGuessedWord(word)
                    tvCurrentWord.text = "Отлично! $word"
                    
                    if (crosswordView.isLevelComplete()) {
                        showWinDialog(levelIndex)
                    }
                } else {
                    tvCurrentWord.text = "Не угадал: $word"
                }
            }
            logToFile("Уровень ${levelIndex + 1} успешно загружен")
            
        } catch (e: Exception) {
            logToFile("ОШИБКА при загрузке: ${e.message}\n${e.stackTraceToString()}")
            throw e
        }
    }

    private fun logToFile(message: String) {
        try {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, "wordguess_log.txt")
            file.appendText("${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}: $message\n")
        } catch (e: Exception) {
            // Если не пишется - ничего не делаем
        }
    }

    private fun showWinDialog(currentLevelIndex: Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_win)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val btnContinue = dialog.findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            currentLevel++
            loadLevel(currentLevel)
        }
        dialog.setCancelable(false)
        dialog.show()
    }

    data class Level(
        val letters: List<Char>,
        val words: List<CrosswordView.WordPlacement>
    )
}
