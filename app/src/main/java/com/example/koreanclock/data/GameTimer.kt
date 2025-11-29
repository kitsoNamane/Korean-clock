package com.example.koreanclock.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow

class GameTimer(
    private val startTimeInSeconds: Int
) {
    private var _handler = Handler(Looper.getMainLooper())
    private var _timerRunnable: Runnable? = null
    private var _isTimerRunning = false
    private var _endTime = 0L
    val timer = MutableStateFlow(startTimeInSeconds)

    fun startTimer() {
        if (!_isTimerRunning) {
            timer.value = startTimeInSeconds
            _endTime = System.currentTimeMillis() + startTimeInSeconds * 1000
            _isTimerRunning = true
            _timerRunnable = object : Runnable {
                override fun run() {
                    val timeLeftCalculation = _endTime - System.currentTimeMillis()
                    val timeLeft = (timeLeftCalculation / 1000).toInt()
                    Log.d("time left", timeLeft.toString())
                    if (timeLeft <= 0) {
                        timer.value = 0
                        onFinish()
                        return
                    }
                    timer.value = timeLeft
                    val delay = timeLeftCalculation % 1000
                    _handler.postDelayed(this, delay)
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
        timer.value = startTimeInSeconds
    }

    fun onFinish() {
        pauseTimer()
    }
}