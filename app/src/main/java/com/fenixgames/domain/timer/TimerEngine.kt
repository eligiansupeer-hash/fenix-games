package com.fenixgames.domain.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TimerEngine {
    fun seconds(maxSeconds: Int): Flow<Int> = flow {
        for (remaining in maxSeconds downTo 0) {
            emit(remaining)
            delay(1_000)
        }
    }
}

