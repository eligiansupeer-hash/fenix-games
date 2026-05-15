package com.fenixgames.core.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fenixgames.data.content.ContentPackManager
import com.fenixgames.data.diagnostics.BlackBoxLogger
import com.fenixgames.data.diagnostics.DiagnosticRepository
import com.fenixgames.data.local.AppDatabase
import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.session.SessionManager

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val blackBoxLogger = BlackBoxLogger(appContext)
    val diagnosticRepository = DiagnosticRepository(appContext, blackBoxLogger)

    val database: AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "fenix-games.db"
    )
        .addMigrations(MIGRATION_1_2)
        .addMigrations(MIGRATION_2_3)
        .build()

    val contentPackManager = ContentPackManager(appContext)
    val cardRepository = CardRepository(
        cardDao = database.cardDao(),
        contentPackManager = contentPackManager
    )
    val sessionManager = SessionManager(cardRepository)

    private companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS packs (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        version INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS cards")
                db.execSQL("DROP TABLE IF EXISTS used_cards")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cards (
                        id TEXT NOT NULL PRIMARY KEY,
                        packId TEXT NOT NULL,
                        mode TEXT NOT NULL,
                        rating TEXT NOT NULL,
                        ratingRank INTEGER NOT NULL,
                        cardType TEXT NOT NULL,
                        category TEXT NOT NULL,
                        textTemplate TEXT NOT NULL,
                        targetPolicy TEXT NOT NULL,
                        penaltyPolicy TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cards_packId ON cards(packId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cards_mode ON cards(mode)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cards_rating ON cards(rating)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cards_ratingRank ON cards(ratingRank)")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS used_cards (
                        sessionId TEXT NOT NULL,
                        cardId TEXT NOT NULL,
                        usedAtEpochMillis INTEGER NOT NULL,
                        PRIMARY KEY(sessionId, cardId)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
