package com.fenixgames.domain.random

import kotlin.random.Random

class Randomizer(private val random: Random = Random.Default) {
    fun <T> shuffled(values: List<T>): List<T> {
        val copy = values.toMutableList()
        for (index in copy.lastIndex downTo 1) {
            val swapIndex = random.nextInt(index + 1)
            val current = copy[index]
            copy[index] = copy[swapIndex]
            copy[swapIndex] = current
        }
        return copy
    }
}

