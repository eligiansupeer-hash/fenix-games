package com.fenixgames.data.repository

import com.fenixgames.data.content.ContentPackManager
import com.fenixgames.data.local.dao.CardDao
import com.fenixgames.data.local.entity.CardEntity
import com.fenixgames.data.local.entity.PackEntity
import com.fenixgames.data.local.entity.UsedCardEntity
import com.fenixgames.domain.model.Card
import com.fenixgames.domain.model.GameMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CardRepository(
    private val cardDao: CardDao,
    private val contentPackManager: ContentPackManager
) {
    fun observeCardCount(): Flow<Int> = cardDao.observeCardCount()

    suspend fun ensureBundledContentLoaded() = withContext(Dispatchers.IO) {
        if (cardDao.packCount() > 0) return@withContext

        val pack = contentPackManager.loadBundledPack()
        cardDao.insertPacks(
            listOf(
                PackEntity(
                    id = pack.id,
                    name = pack.name,
                    version = pack.version
                )
            )
        )
        cardDao.insertCards(
            pack.cards.map { card ->
                CardEntity(
                    id = card.id,
                    packId = pack.id,
                    mode = card.mode,
                    text = card.text,
                    intensity = card.intensity
                )
            }
        )
    }

    suspend fun nextCard(
        mode: GameMode,
        maxIntensity: Int,
        sessionId: String
    ): Card? = withContext(Dispatchers.IO) {
        cardDao.nextUnusedCard(mode.name, maxIntensity, sessionId)?.also { entity ->
            cardDao.markUsed(
                UsedCardEntity(
                    sessionId = sessionId,
                    cardId = entity.id,
                    usedAtEpochMillis = System.currentTimeMillis()
                )
            )
        }?.toDomain()
    }

    private fun CardEntity.toDomain(): Card = Card(
        id = id,
        mode = GameMode.valueOf(mode),
        text = text,
        intensity = intensity
    )
}

