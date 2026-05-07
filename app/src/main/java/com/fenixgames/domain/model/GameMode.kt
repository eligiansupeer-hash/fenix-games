package com.fenixgames.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameMode {
    NEVER_HAVE_I_EVER,
    TRUTH,
    DARE,
    TRUTH_OR_DARE,
    CHARADES,
    IMPOSTOR,
    TABOO,
    ROULETTE,
    TRIVIA
}

