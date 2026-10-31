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

    // Список 20 уровней
    private val levels = listOf(
        // 1-3: 3 буквы
        Level(listOf('К', 'О', 'Т'), listOf("КОТ", "ТОК")),
        Level(listOf('Р', 'А', 'К'), listOf("РАК", "КАР")),
        Level(listOf('С', 'О', 'Н'), listOf("СОН", "НОС")),
        
        // 4-6: 4 буквы (простые)
        Level(listOf('Р', 'У', 'К', 'А'), listOf("РУКА", "РАК", "УРА")),
        Level(listOf('М', 'А', 'К', 'О'), listOf("МАКО", "КОМА", "МАК")),
        Level(listOf('Л', 'И', 'С', 'А'), listOf("ЛИСА", "СИЛА", "ЛИС")),
        
        // 7-10: 4 буквы (сложнее)
        Level(listOf('З', 'И', 'М', 'А'), listOf("ЗИМА", "ЗИМ")),
        Level(listOf('Г', 'О', 'Р', 'А'), listOf("ГОРА", "РОГ")),
        Level(listOf('С', 'Л', 'О', 'Н'), listOf("СЛОН", "НОС", "СОН")),
        Level(listOf('В', 'О', 'Д', 'А'), listOf("ВОДА", "ВОД")),
        
        // 11-15: 5 букв (средние)
        Level(listOf('К', 'У', 'К', 'Л', 'А'), listOf("КУКЛА", "ЛУК", "КУЛАК")),
        Level(listOf('П', 'Е', 'Н', 'А', 'Л'), listOf("ПЕНАЛ", "ПЕНА", "ЛЕН")),
        Level(listOf('Т', 'О', 'Ч', 'К', 'А'), listOf("ТОЧКА", "ТОК", "КОТ")),
        Level(listOf('С', 'Т', 'Е', 'Н', 'А'), listOf("СТЕНА", "СЕТ", "СТЕН")),
        Level(listOf('К', 'А', 'Р', 'Т', 'А'), listOf("КАРТА", "КАРТ", "РАК")),
        
        // 16-20: 5 букв (сложные)
        Level(listOf('М', 'А', 'С', 'К', 'А'), listOf("МАСКА", "МАК", "КАСКА")),
        Level(listOf('Р', 'У', 'Ч', 'К', 'А'), listOf("РУЧКА", "РУКА", "РАК")),
        Level(listOf('П', 'Е', 'Ч', 'К', 'А'), listOf("ПЕЧКА", "ПЕЧЬ", "РАК")),
        Level(listOf('Г', 'Р', 'У', 'Ш', 'А'), listOf("ГРУША", "ГРУШ")),
        Level(listOf('Ш', 'К', 'О', 'Л', 'А'), listOf("ШКОЛА", "ШКОЛ", "КОЛ"))
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
        
        // Рандомный фон
        rootLayout.setBackgroundResource(backgrounds[Random.nextInt(backgrounds.size)])

        // Буквы для круга
        letterCircle.letters = level.letters

        // Генерируем правильные координаты для сетки
        val placements = generatePlacements(level.words)
        crosswordView.words = placements
        crosswordView.reset()

        tvCurrentWord.text = "Уровень ${levelIndex + 1}"

        val validWords = level.words.toSet()

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

    // АВТОМАТИЧЕСКАЯ РАССТАНОВКА СЛОВ
    private fun generatePlacements(words: List<String>): List<CrosswordView.WordPlacement> {
        val placements = mutableListOf<CrosswordView.WordPlacement>()
        if (words.isEmpty()) return placements

        // Первое слово ставим горизонтально в центр
        val firstWord = words[0]
        placements.add(CrosswordView.WordPlacement(firstWord, 0, 2, true)) 
        
        // Остальные слова пытаемся пристроить к первому (пересечение по букве)
        for (i in 1 until words.size) {
            val word = words[i]
            var placed = false
            
            // Ищем общую букву с первым словом
            for (j in firstWord.indices) {
                val char = firstWord[j]
                val indexInWord = word.indexOf(char)
                if (indexInWord != -1) {
                    // Нашли пересечение! 
                    // Ставим вертикально под этой буквой
                    val startX = j
                    val startY = 2 - indexInWord
                    placements.add(CrosswordView.WordPlacement(word, startX, startY, false))
                    placed = true
                    break
                }
            }
            
            // Если не нашли пересечение - ставим отдельно справа
            if (!placed) {
                placements.add(CrosswordView.WordPlacement(word, 4, 2, true))
            }
        }
        return placements
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
        val words: List<String>
    )
}
