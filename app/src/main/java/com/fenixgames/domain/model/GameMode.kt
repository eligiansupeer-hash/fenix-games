package com.fenixgames.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameMode {
    NEVER_HAVE_I_EVER,
    TRUTH_OR_DARE,
    QUESTIONS,
    ROULETTE,
    PREVIA,
    ARGENTO,
    CHARADES,
    IMPOSTOR,
    TABOO,
    TRIVIA
}

