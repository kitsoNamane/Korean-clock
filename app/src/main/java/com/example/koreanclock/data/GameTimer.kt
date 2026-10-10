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
            _endTime = System.currentTimeMillis() + startTimeInSeconds * 1000L
            _isTimerRunning = true
            _timerRunnable = object : Runnable {
                override fun run() {
                    val timeLeftMs = _endTime - System.currentTimeMillis()
                    if (timeLeftMs <= 0) {
                        timer.value = 0
                        onFinish()
                        return
                    }
                    val secondsLeft = ((timeLeftMs + 999) / 1000).toInt()
                    Log.d("time left", secondsLeft.toString())
                    timer.value = secondsLeft
                    val delay = maxOf(1L, timeLeftMs - (secondsLeft - 1) * 1000L)
                    _handler.postDelayed(this, delay)
                }
            }
        }
        _handler.post(_timerRunnable!!)
    }

    fun pauseTimer() {
        if (_isTimerRunning) {
            _timerRunnable?.let { _handler.removeCallbacks(it) }
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