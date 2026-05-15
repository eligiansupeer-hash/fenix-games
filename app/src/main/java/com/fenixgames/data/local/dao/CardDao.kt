package com.fenixgames.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fenixgames.data.local.entity.CardEntity
import com.fenixgames.data.local.entity.PackEntity
import com.fenixgames.data.local.entity.UsedCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Query("SELECT COUNT(*) FROM cards")
    fun observeCardCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM packs")
    suspend fun packCount(): Int

    @Query("SELECT version FROM packs WHERE id = :packId LIMIT 1")
    suspend fun packVersion(packId: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacks(packs: List<PackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<CardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markUsed(usedCard: UsedCardEntity)

    @Query(
        """
        SELECT * FROM cards
        WHERE mode = :mode
        AND rating = 'TEEN'
        AND id NOT IN (
            SELECT cardId FROM used_cards WHERE sessionId = :sessionId
        )
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun nextTeenCard(
        mode: String,
        sessionId: String
    ): CardEntity?

    @Query(
        """
        SELECT * FROM cards
        WHERE mode = :mode
        AND ratingRank BETWEEN 1 AND :maxRatingRank
        AND id NOT IN (
            SELECT cardId FROM used_cards WHERE sessionId = :sessionId
        )
        ORDER BY RANDOM()
        LIMIT 1
        """
    )
    suspend fun nextAdultCard(
        mode: String,
        maxRatingRank: Int,
        sessionId: String
    ): CardEntity?
}

