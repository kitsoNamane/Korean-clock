package com.example.koreanclock.data

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameTimer(
    private val startTime: Long
) {
    private var _handler = Handler(Looper.getMainLooper())
    private var _timerRunnable: Runnable? = null
    private var _isTimerRunning = false
    private var _endTime = 0L
    val timer = MutableStateFlow(startTime.toInt())

    fun startTimer() {
        if (!_isTimerRunning) {
            _endTime = System.currentTimeMillis() + startTime
            _isTimerRunning = true
            _timerRunnable = object : Runnable {
                override fun run() {
                    val timeLeft = (_endTime - System.currentTimeMillis()).toInt()
                    if (timeLeft > 0) {
                        timer.value = timeLeft
                        _handler.postDelayed(this, 1000)
                    } else {
                        onFinish()
                    }
                }
            }
        }
        _handler.post(_timerRunnable!!)
    }

    fun pauseTimer() {
        if (_isTimerRunning) {
            _handler.removeCallbacks(_timerRunnable!!)
            _isTimerRunning = false
        }
    }

    fun resetTimer() {
        pauseTimer()
        timer.value = startTime.toInt()
    }

    fun onFinish() {
        _isTimerRunning = false
    }
}