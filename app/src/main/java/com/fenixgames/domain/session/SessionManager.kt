package com.fenixgames.domain.session

import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.model.Card
import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.GameSession
import com.fenixgames.domain.model.PenaltyPolicy
import com.fenixgames.domain.model.Player
import com.fenixgames.domain.model.Round
import com.fenixgames.domain.model.TargetPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class SessionManager(private val cardRepository: CardRepository) {
    private val defaultPlayers = listOf(
        Player(id = "player-1", name = "Player Alpha"),
        Player(id = "player-2", name = "Player Beta"),
        Player(id = "player-3", name = "Player Gamma")
    )

    private val _state = MutableStateFlow(
        GameSession(
            id = UUID.randomUUID().toString(),
            players = defaultPlayers,
            mode = GameMode.NEVER_HAVE_I_EVER
        )
    )

    val state: StateFlow<GameSession> = _state.asStateFlow()

    suspend fun prepare() {
        cardRepository.ensureBundledContentLoaded()
    }

    suspend fun startSession(
        playerNames: List<String>,
        mode: GameMode,
        rating: ContentRating,
        penaltyPolicy: PenaltyPolicy
    ) {
        val players = playerNames
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .takeIf { it.size >= 2 }
            ?.mapIndexed { index, name -> Player(id = "player-${index + 1}", name = name) }
            ?: defaultPlayers

        _state.value = GameSession(
            id = UUID.randomUUID().toString(),
            players = players,
            mode = mode,
            rating = rating,
            penaltyPolicy = penaltyPolicy,
            legalAccepted = true,
            round = Round(index = 1, mode = mode)
        )
        nextCard()
    }

    suspend fun nextCard() {
        val current = _state.value
        val card = cardRepository.nextCard(
            mode = current.mode,
            rating = current.rating,
            sessionId = current.id
        )
        val nextIndex = current.round.index + if (current.round.currentCard == null) 0 else 1
        val actor = current.players[(nextIndex - 1).floorMod(current.players.size)]
        val targetSelection = selectTargets(current.players, actor, nextIndex, card)
        _state.value = current.copy(
            round = Round(
                index = nextIndex,
                mode = current.mode,
                actor = actor,
                target = targetSelection.target,
                targetA = targetSelection.targetA,
                targetB = targetSelection.targetB,
                currentCard = card,
                renderedText = card?.render(actor, targetSelection)
            )
        )
    }

    private fun selectTargets(
        players: List<Player>,
        actor: Player,
        roundIndex: Int,
        card: Card?
    ): TargetSelection {
        if (card == null || card.targetPolicy == TargetPolicy.NONE) return TargetSelection()
        val candidates = players.filterNot { it.id == actor.id }.ifEmpty { players }
        val first = candidates[roundIndex.floorMod(candidates.size)]
        val second = candidates[(roundIndex + 1).floorMod(candidates.size)]
        return when (card.targetPolicy) {
            TargetPolicy.ONE_TARGET -> TargetSelection(target = first)
            TargetPolicy.TWO_TARGET_OPTIONS -> TargetSelection(targetA = first, targetB = second)
            TargetPolicy.NONE -> TargetSelection()
        }
    }

    private fun Card.render(actor: Player, targets: TargetSelection): String =
        textTemplate
            .replace("{actor}", actor.name)
            .replace("{target}", targets.target?.name.orEmpty())
            .replace("{targetA}", targets.targetA?.name.orEmpty())
            .replace("{targetB}", targets.targetB?.name.orEmpty())

    private fun Int.floorMod(divisor: Int): Int = Math.floorMod(this, divisor)
}

data class TargetSelection(
    val target: Player? = null,
    val targetA: Player? = null,
    val targetB: Player? = null
)

