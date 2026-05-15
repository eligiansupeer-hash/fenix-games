package com.fenixgames.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ContentRating(val rank: Int, val label: String, val adult: Boolean) {
    TEEN(0, "Adolescente", false),
    ADULT_1(1, "Adulto 1", true),
    ADULT_2(2, "Adulto 2", true),
    ADULT_3(3, "Adulto 3", true),
    ADULT_4(4, "Adulto 4", true),
    ADULT_5(5, "Adulto 5", true),
    ADULT_6(6, "Adulto 6", true);

    companion object {
        fun fromName(value: String): ContentRating = entries.first { it.name == value }
    }
}

