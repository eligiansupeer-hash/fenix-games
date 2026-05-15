package com.fenixgames.data.repository

import com.fenixgames.data.content.ContentPackManager
import com.fenixgames.data.local.dao.CardDao
import com.fenixgames.data.local.entity.CardEntity
import com.fenixgames.data.local.entity.PackEntity
import com.fenixgames.data.local.entity.UsedCardEntity
import com.fenixgames.domain.model.Card
import com.fenixgames.domain.model.CardType
import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.PenaltyPolicy
import com.fenixgames.domain.model.TargetPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CardRepository(
    private val cardDao: CardDao,
    private val contentPackManager: ContentPackManager
) {
    fun observeCardCount(): Flow<Int> = cardDao.observeCardCount()

    suspend fun ensureBundledContentLoaded() = withContext(Dispatchers.IO) {
        val pack = contentPackManager.loadBundledPack()
        val currentVersion = cardDao.packVersion(pack.id)
        if (cardDao.packCount() > 0 && currentVersion != null && currentVersion >= pack.version) {
            return@withContext
        }

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
                val rating = ContentRating.fromName(card.rating)
                CardEntity(
                    id = card.id,
                    packId = pack.id,
                    mode = card.mode,
                    rating = rating.name,
                    ratingRank = rating.rank,
                    cardType = card.cardType,
                    category = card.category,
                    textTemplate = card.textTemplate,
                    targetPolicy = card.targetPolicy,
                    penaltyPolicy = card.penaltyPolicy
                )
            }
        )
    }

    suspend fun nextCard(
        mode: GameMode,
        rating: ContentRating,
        sessionId: String
    ): Card? = withContext(Dispatchers.IO) {
        val entity = if (rating == ContentRating.TEEN) {
            cardDao.nextTeenCard(mode.name, sessionId)
        } else {
            cardDao.nextAdultCard(mode.name, rating.rank, sessionId)
        }
        entity?.also { card ->
            cardDao.markUsed(
                UsedCardEntity(
                    sessionId = sessionId,
                    cardId = card.id,
                    usedAtEpochMillis = System.currentTimeMillis()
                )
            )
        }?.toDomain()
    }

    private fun CardEntity.toDomain(): Card = Card(
        id = id,
        mode = GameMode.valueOf(mode),
        rating = ContentRating.valueOf(rating),
        cardType = CardType.valueOf(cardType),
        category = category,
        textTemplate = textTemplate,
        targetPolicy = TargetPolicy.valueOf(targetPolicy),
        penaltyPolicy = PenaltyPolicy.valueOf(penaltyPolicy)
    )
}

