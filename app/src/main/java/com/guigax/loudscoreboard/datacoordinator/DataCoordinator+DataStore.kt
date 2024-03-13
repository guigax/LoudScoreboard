package com.guigax.loudscoreboard.datacoordinator

import androidx.datastore.preferences.core.edit
import com.guigax.loudscoreboard.preferences.PreferencesKeys
import kotlinx.coroutines.flow.firstOrNull

suspend fun DataCoordinator.getTeam1NameDataStore(): String {
    val context = this.context ?: return defaultTeam1NamePreferenceValue
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Name)
        ?: defaultTeam1NamePreferenceValue
}

suspend fun DataCoordinator.setTeam1NameDataStore(value: String) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Name] = value.ifEmpty { defaultTeam1NamePreferenceValue }
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
        preferences[PreferencesKeys.team2Name] = value.ifEmpty { defaultTeam2NamePreferenceValue }
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

suspend fun DataCoordinator.getTeam1ColorDataStore(): Int {
    val context = this.context ?: return defaultTeam1ColorPreferenceVariable
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Color)
        ?: defaultTeam1ColorPreferenceVariable
}

suspend fun DataCoordinator.setTeam1ColorDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Color] = value
    }
}

suspend fun DataCoordinator.getTeam2ColorDataStore(): Int {
    val context = this.context ?: return defaultTeam2ColorPreferenceVariable
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Color)
        ?: defaultTeam2ColorPreferenceVariable
}

suspend fun DataCoordinator.setTeam2ColorDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Color] = value
    }
}

suspend fun DataCoordinator.getIsMuted(): Boolean {
    val context = this.context ?: return defaultIsMutedPreferenceVariable
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.isMuted)
        ?: defaultIsMutedPreferenceVariable
}

suspend fun DataCoordinator.setIsMuted(value: Boolean) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.isMuted] = value
    }
}