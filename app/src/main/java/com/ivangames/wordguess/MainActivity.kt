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

    // ВСЕ 20 УРОВНЕЙ (Проверенные координаты)
    private val levels = listOf(
        // 1. КОТ
        Level(listOf('К', 'О', 'Т'), listOf(
            CrosswordView.WordPlacement("КОТ", 1, 1, true),
            CrosswordView.WordPlacement("ТОК", 3, 1, false)
        )),
        // 2. РАК
        Level(listOf('Р', 'А', 'К'), listOf(
            CrosswordView.WordPlacement("РАК", 1, 1, true),
            CrosswordView.WordPlacement("КАР", 3, 1, false)
        )),
        // 3. СОН
        Level(listOf('С', 'О', 'Н'), listOf(
            CrosswordView.WordPlacement("СОН", 1, 1, true),
            CrosswordView.WordPlacement("НОС", 3, 1, false)
        )),
        // 4. РУКА
        Level(listOf('Р', 'У', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("РУКА", 1, 1, true),  // Горизонтально
            CrosswordView.WordPlacement("РАК", 1, 1, false),  // Вертикально от Р (1,1)
            CrosswordView.WordPlacement("УРА", 2, 1, false)   // Вертикально от У (2,1)
        )),
        // 5. МАКО
        Level(listOf('М', 'А', 'К', 'О'), listOf(
            CrosswordView.WordPlacement("МАКО", 1, 1, true),
            CrosswordView.WordPlacement("МАК", 1, 1, false),
            CrosswordView.WordPlacement("КОМА", 3, 1, false)
        )),
        // 6. ЛИСА
        Level(listOf('Л', 'И', 'С', 'А'), listOf(
            CrosswordView.WordPlacement("ЛИСА", 1, 1, true),
            CrosswordView.WordPlacement("СИЛА", 3, 1, false),
            CrosswordView.WordPlacement("ЛИС", 1, 1, false)
        )),
        // 7. ЗИМА
        Level(listOf('З', 'И', 'М', 'А'), listOf(
            CrosswordView.WordPlacement("ЗИМА", 1, 1, true),
            CrosswordView.WordPlacement("ЗИМ", 1, 1, false)
        )),
        // 8. ГОРА
        Level(listOf('Г', 'О', 'Р', 'А'), listOf(
            CrosswordView.WordPlacement("ГОРА", 1, 1, true),
            CrosswordView.WordPlacement("РОГ", 3, 1, false)
        )),
        // 9. СЛОН (Исправлено)
        Level(listOf('С', 'Л', 'О', 'Н'), listOf(
            CrosswordView.WordPlacement("СЛОН", 1, 1, true),  // Горизонтально
            CrosswordView.WordPlacement("СОН", 1, 1, false),  // Вертикально от первой 'С'
            CrosswordView.WordPlacement("НОС", 4, 1, false)   // Вертикально от последней 'Н'
        )),
        // 10. ВОДА
        Level(listOf('В', 'О', 'Д', 'А'), listOf(
            CrosswordView.WordPlacement("ВОДА", 1, 1, true),
            CrosswordView.WordPlacement("ВОД", 1, 1, false)
        )),
        
        // 11. КУКЛА
        Level(listOf('К', 'У', 'К', 'Л', 'А'), listOf(
            CrosswordView.WordPlacement("КУКЛА", 1, 1, true),
            CrosswordView.WordPlacement("ЛУК", 4, 1, false),
            CrosswordView.WordPlacement("КУЛАК", 1, 1, false)
        )),
        // 12. ПЕНАЛ
        Level(listOf('П', 'Е', 'Н', 'А', 'Л'), listOf(
            CrosswordView.WordPlacement("ПЕНАЛ", 1, 1, true),
            CrosswordView.WordPlacement("ПЕНА", 1, 1, false),
            CrosswordView.WordPlacement("ЛЕН", 4, 1, false)
        )),
        // 13. ТОЧКА
        Level(listOf('Т', 'О', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("ТОЧКА", 1, 1, true),
            CrosswordView.WordPlacement("ТОК", 1, 1, false),
            CrosswordView.WordPlacement("КОТ", 4, 1, false)
        )),
        // 14. СТЕНА
        Level(listOf('С', 'Т', 'Е', 'Н', 'А'), listOf(
            CrosswordView.WordPlacement("СТЕНА", 1, 1, true),
            CrosswordView.WordPlacement("СЕТ", 1, 1, false),
            CrosswordView.WordPlacement("СТЕН", 1, 1, false)
        )),
        // 15. КАРТА
        Level(listOf('К', 'А', 'Р', 'Т', 'А'), listOf(
            CrosswordView.WordPlacement("КАРТА", 1, 1, true),
            CrosswordView.WordPlacement("КАРТ", 1, 1, false),
            CrosswordView.WordPlacement("РАК", 2, 1, false)
        )),
        
        // 16. МАСКА
        Level(listOf('М', 'А', 'С', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("МАСКА", 1, 1, true),
            CrosswordView.WordPlacement("МАК", 1, 1, false),
            CrosswordView.WordPlacement("КАСКА", 3, 1, false)
        )),
        // 17. РУЧКА
        Level(listOf('Р', 'У', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("РУЧКА", 1, 1, true),
            CrosswordView.WordPlacement("РУКА", 1, 1, false),
            CrosswordView.WordPlacement("РАК", 5, 1, false)
        )),
        // 18. ПЕЧКА
        Level(listOf('П', 'Е', 'Ч', 'К', 'А'), listOf(
            CrosswordView.WordPlacement("ПЕЧКА", 1, 1, true),
            CrosswordView.WordPlacement("ПЕЧЬ", 1, 1, false),
            CrosswordView.WordPlacement("РАК", 5, 1, false)
        )),
        // 19. ГРУША
        Level(listOf('Г', 'Р', 'У', 'Ш', 'А'), listOf(
            CrosswordView.WordPlacement("ГРУША", 1, 1, true),
            CrosswordView.WordPlacement("ГРУШ", 1, 1, false)
        )),
        // 20. ШКОЛА
        Level(listOf('Ш', 'К', 'О', 'Л', 'А'), listOf(
            CrosswordView.WordPlacement("ШКОЛА", 1, 1, true),
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
            tvCurrentWord.text = "Все 20 уровней пройдены! 🎉"
            return
        }

        val level = levels[levelIndex]
        rootLayout.setBackgroundResource(backgrounds[Random.nextInt(backgrounds.size)])
        letterCircle.letters = level.letters

        crosswordView.words = level.words
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
