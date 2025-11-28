package com.example.koreanclock.data

data class Game(
    val clockHour: Int? = null,
    val clockMinutes: Int? = null,
    val correctGuesses: Int? = null,
    val guessTurns: Int? = null,
    val gameStarted: Boolean = false,
)
