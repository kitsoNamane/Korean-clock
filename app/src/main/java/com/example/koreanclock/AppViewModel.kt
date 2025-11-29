package com.example.koreanclock

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.koreanclock.data.GameTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class AppUiState (
    val clockHour: Int? = null,
    val clockMinutes: Int? = null,
    val correctGuesses: Int? = null,
    val guessTurns: Int? = null,
    val playerTimeText: String = "",
    val gameStarted: Boolean = false,
    val guessCorrect: Boolean? = null,
    val guessWrong: Boolean? = null,
)

class AppViewModel(
    private val gameTimer: GameTimer
) : ViewModel() {
    private val _gameTimer: MutableStateFlow<Int> = gameTimer.timer
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()
    val timer: StateFlow<Int> = _gameTimer.asStateFlow()

    init {
        if (!_uiState.value.gameStarted) {
            _uiState.update {
                it.copy(
                    clockHour = 0,
                    clockMinutes = 0,
                    correctGuesses = 0,
                    guessTurns = 0,
                    gameStarted = false,
                )
            }
        }
        startGame()
    }

    fun startGame() {
        _uiState.update {
            it.copy(
                guessTurns = 1,
            )
        }
    }

    fun updatePlayerTimeText(newText: String) {
        _uiState.update {
            it.copy(
                playerTimeText = newText,
            )
        }
    }

    fun checkGuess() {
        gameTimer.pauseTimer()
        val playerGuess = _uiState.value.playerTimeText.trim()
        Log.d("Player guess", playerGuess)
        val correctTimeText = String.format(TIME_TEMPLATE,
            Translator.KOREAN_HOURS[_uiState.value.clockHour],
            Translator.KOREAN_MINUTES[_uiState.value.clockMinutes]
        )
        Log.d("Correct time text", correctTimeText)

        if (correctTimeText.equals(playerGuess, ignoreCase = true)) {
            _uiState.update {
                it.copy(
                    correctGuesses = it.correctGuesses?.plus(1),
                    guessCorrect = true,
                    guessWrong = false,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    guessCorrect = false,
                    guessWrong = true,
                )
            }
        }
    }

    fun loadNextTurn() {
        gameTimer.resetTimer()
        _uiState.update {
            it.copy(
                clockHour = Random.nextInt(1, 13),
                clockMinutes = Random.nextInt(0, 60),
                guessTurns = it.guessTurns?.plus(1),
                guessCorrect = null,
                guessWrong = null,
                gameStarted = true,
                playerTimeText = "",
            )
        }
        gameTimer.startTimer()
    }

    fun resetGame() {
        gameTimer.resetTimer()
    }
}

