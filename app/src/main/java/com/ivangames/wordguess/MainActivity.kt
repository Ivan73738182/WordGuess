package com.ivangames.wordguess

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var letterCircle: LetterCircleView
    private lateinit var crosswordView: CrosswordView
    private lateinit var tvCurrentWord: TextView
    private lateinit var rootLayout: ConstraintLayout

    private var currentLevel = 0

    private val levels = listOf(
        Level(listOf('К', 'О', 'Т'), listOf("КОТ", "ТОК")),
        Level(listOf('Р', 'А', 'К'), listOf("РАК", "КАР")),
        Level(listOf('С', 'О', 'Н'), listOf("СОН", "НОС")),
        
        Level(listOf('Р', 'У', 'К', 'А'), listOf("РУКА", "РАК", "УРА")),
        Level(listOf('М', 'А', 'К', 'О'), listOf("МАКО", "КОМА", "МАК")),
        Level(listOf('Л', 'И', 'С', 'А'), listOf("ЛИСА", "СИЛА", "ЛИС")),
        
        Level(listOf('З', 'И', 'М', 'А'), listOf("ЗИМА", "ЗИМ")),
        Level(listOf('Г', 'О', 'Р', 'А'), listOf("ГОРА", "РОГ")),
        Level(listOf('С', 'Л', 'О', 'Н'), listOf("СЛОН", "НОС", "СОН")),
        Level(listOf('В', 'О', 'Д', 'А'), listOf("ВОДА", "ВОД")),
        
        Level(listOf('К', 'У', 'К', 'Л', 'А'), listOf("КУКЛА", "ЛУК", "КУЛАК")),
        Level(listOf('П', 'Е', 'Н', 'А', 'Л'), listOf("ПЕНАЛ", "ПЕНА", "ЛЕН")),
        Level(listOf('Т', 'О', 'Ч', 'К', 'А'), listOf("ТОЧКА", "ТОК", "КОТ")),
        Level(listOf('С', 'Т', 'Е', 'Н', 'А'), listOf("СТЕНА", "СЕТ", "СТЕН")),
        Level(listOf('К', 'А', 'Р', 'Т', 'А'), listOf("КАРТА", "КАРТ", "РАК")),
        
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
        rootLayout.setBackgroundResource(backgrounds[Random.nextInt(backgrounds.size)])
        letterCircle.letters = level.letters

        // Генерируем координаты и центрируем сетку
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

    private fun generatePlacements(words: List<String>): List<CrosswordView.WordPlacement> {
        val placements = mutableListOf<CrosswordView.WordPlacement>()
        if (words.isEmpty()) return placements

        val firstWord = words[0]
        // Начинаем с (0,0)
        placements.add(CrosswordView.WordPlacement(firstWord, 0, 0, true))

        for (i in 1 until words.size) {
            val word = words[i]
            var placed = false
            
            for (j in firstWord.indices) {
                val char = firstWord[j]
                val indexInWord = word.indexOf(char)
                if (indexInWord != -1) {
                    val startX = j
                    val startY = -indexInWord // Считаем относительно (0,0)
                    
                    // ВАЖНО: Проверяем, что слово не выйдет за пределы сверху или слева
                    // Если Y отрицательный, значит слово пойдет вверх (это нормально, мы потом сдвинем всю сетку)
                    placements.add(CrosswordView.WordPlacement(word, startX, startY, false))
                    placed = true
                    break
                }
            }
            
            // Если не нашли пересечение - ставим в отдельную зону (например, на 3 клетки правее)
            if (!placed) {
                placements.add(CrosswordView.WordPlacement(word, firstWord.length + 1, 0, true))
            }
        }
        
        // Теперь вычисляем минимальные X и Y, чтобы сдвинуть всю сетку вправо и вниз (в центр экрана)
        var minX = Int.MAX_VALUE
        var minY = Int.MAX_VALUE
        for (p in placements) {
            minX = min(minX, p.startX)
            minY = min(minY, p.startY)
        }
        
        // Сдвигаем все слова, чтобы они не уходили в минус
        val shiftX = -minX
        val shiftY = -minY
        
        return placements.map {
            it.copy(startX = it.startX + shiftX, startY = it.startY + shiftY)
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
        val words: List<String>
    )
}
