package com.fenixgames.domain.session

import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.GameSession
import com.fenixgames.domain.model.Player
import com.fenixgames.domain.model.Round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SessionManager(private val cardRepository: CardRepository) {
    private val _state = MutableStateFlow(
        GameSession(
            id = UUID.randomUUID().toString(),
            players = listOf(
                Player(id = "player-1", name = "Player Alpha"),
                Player(id = "player-2", name = "Player Beta")
            ),
            mode = GameMode.NEVER_HAVE_I_EVER
        )
    )

    val state: StateFlow<GameSession> = _state.asStateFlow()

    suspend fun prepare() {
        cardRepository.ensureBundledContentLoaded()
        nextCard()
    }

    suspend fun selectMode(mode: GameMode) {
        _state.value = _state.value.copy(
            id = UUID.randomUUID().toString(),
            mode = mode,
            round = Round(index = 1, mode = mode)
        )
        nextCard()
    }

    suspend fun nextCard() {
        val current = _state.value
        val card = cardRepository.nextCard(
            mode = current.mode,
            maxIntensity = current.maxIntensity,
            sessionId = current.id
        )
        _state.value = current.copy(
            round = Round(
                index = current.round.index + if (current.round.currentCard == null) 0 else 1,
                mode = current.mode,
                currentCard = card
            )
        )
    }
}
