package com.fenixgames.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class GameMode(
    val label: String,
    val supportsCompetition: Boolean,
    val animationKind: AnimationKind
) {
    NEVER_HAVE_I_EVER("Yo Nunca", false, AnimationKind.NONE),
    TRUTH_OR_DARE("Verdad o Reto", true, AnimationKind.NONE),
    QUESTIONS("Preguntas", false, AnimationKind.NONE),
    ROULETTE("Ruleta", true, AnimationKind.ROULETTE),
    PREVIA("Previa", true, AnimationKind.DICE),
    ARGENTO("Argento Salvaje", true, AnimationKind.DICE),
    CHARADES("Mímica", true, AnimationKind.CHARADES),
    IMPOSTOR("El Impostor", true, AnimationKind.SECRET),
    TABOO("Tabú", true, AnimationKind.TABOO),
    TRIVIA("Trivia", true, AnimationKind.DICE)
}

enum class AnimationKind {
    NONE,
    ROULETTE,
    DICE,
    CHARADES,
    SECRET,
    TABOO
}

