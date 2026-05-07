package com.fenixgames.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.fenixDataStore by preferencesDataStore(name = "fenix_settings")

class SettingsManager(private val context: Context) {
    private val firstLaunchKey = booleanPreferencesKey("first_launch")

    val isFirstLaunch: Flow<Boolean> = context.fenixDataStore.data.map { preferences ->
        preferences[firstLaunchKey] ?: true
    }

    suspend fun markOpened() {
        context.fenixDataStore.edit { preferences ->
            preferences[firstLaunchKey] = false
        }
    }
}

