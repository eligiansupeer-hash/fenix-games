package com.fenixgames.data.content

import android.content.Context
import com.fenixgames.core.serialization.JsonConfig
import com.fenixgames.data.content.dto.ContentPackDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString

class ContentPackManager(private val context: Context) {
    suspend fun loadBundledPack(assetName: String = "cards.json"): ContentPackDto =
        withContext(Dispatchers.IO) {
            val raw = context.assets.open(assetName).bufferedReader().use { it.readText() }
            JsonConfig.default.decodeFromString<ContentPackDto>(raw)
        }
}

