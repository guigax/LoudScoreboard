package com.guigax.loudscoreboard.datacoordinator

import androidx.datastore.preferences.core.edit
import com.guigax.loudscoreboard.PreferencesKeys
import kotlinx.coroutines.flow.firstOrNull

suspend fun DataCoordinator.getTeam1NameDataStore(): String {
    val context = this.context ?: return defaultTeam1NamePreferenceValue
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Name)
        ?: defaultTeam1NamePreferenceValue
}

suspend fun DataCoordinator.setTeam1NameDataStore(value: String) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Name] = value
    }
}

suspend fun DataCoordinator.getTeam2NameDataStore(): String {
    val context = this.context ?: return defaultTeam2NamePreferenceValue
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Name)
        ?: defaultTeam2NamePreferenceValue
}

suspend fun DataCoordinator.setTeam2NameDataStore(value: String) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Name] = value
    }
}

suspend fun DataCoordinator.getTeam1ScoreDataStore(): Int {
    val context = this.context ?: return defaultTeam1ScorePreferenceVariable
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Score)
        ?: defaultTeam1ScorePreferenceVariable
}

suspend fun DataCoordinator.setTeam1ScoreDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Score] = value
    }
}

suspend fun DataCoordinator.getTeam2ScoreDataStore(): Int {
    val context = this.context ?: return defaultTeam2ScorePreferenceVariable
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Score)
        ?: defaultTeam2ScorePreferenceVariable
}

suspend fun DataCoordinator.setTeam2ScoreDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Score] = value
    }
}