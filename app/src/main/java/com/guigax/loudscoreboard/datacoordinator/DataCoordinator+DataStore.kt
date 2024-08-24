package com.guigax.loudscoreboard.datacoordinator

import androidx.datastore.preferences.core.edit
import com.guigax.loudscoreboard.preferences.PreferencesKeys
import kotlinx.coroutines.flow.firstOrNull

suspend fun DataCoordinator.getTeam1NameDataStore(): String {
    val context = this.context ?: return defaultTeam1Name
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Name)
        ?: defaultTeam1Name
}

suspend fun DataCoordinator.setTeam1NameDataStore(value: String) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Name] = value.ifEmpty { defaultTeam1Name }
    }
}

suspend fun DataCoordinator.getTeam2NameDataStore(): String {
    val context = this.context ?: return defaultTeam2Name
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Name)
        ?: defaultTeam2Name
}

suspend fun DataCoordinator.setTeam2NameDataStore(value: String) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Name] = value.ifEmpty { defaultTeam2Name }
    }
}

suspend fun DataCoordinator.getTeam1ScoreDataStore(): Int {
    val context = this.context ?: return defaultTeam1Score
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Score)
        ?: defaultTeam1Score
}

suspend fun DataCoordinator.setTeam1ScoreDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Score] = value
    }
}

suspend fun DataCoordinator.getTeam2ScoreDataStore(): Int {
    val context = this.context ?: return defaultTeam2Score
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Score)
        ?: defaultTeam2Score
}

suspend fun DataCoordinator.setTeam2ScoreDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Score] = value
    }
}

suspend fun DataCoordinator.getTeam1ColorDataStore(): Int {
    val context = this.context ?: return defaultTeam1Color
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team1Color)
        ?: defaultTeam1Color
}

suspend fun DataCoordinator.setTeam1ColorDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team1Color] = value
    }
}

suspend fun DataCoordinator.getTeam2ColorDataStore(): Int {
    val context = this.context ?: return defaultTeam2Color
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.team2Color)
        ?: defaultTeam2Color
}

suspend fun DataCoordinator.setTeam2ColorDataStore(value: Int) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.team2Color] = value
    }
}

suspend fun DataCoordinator.getIsMuted(): Boolean {
    val context = this.context ?: return defaultIsMuted
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.isMuted)
        ?: defaultIsMuted
}

suspend fun DataCoordinator.setIsMuted(value: Boolean) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.isMuted] = value
    }
}

suspend fun DataCoordinator.getTTSSpeedRate(): Float {
    val context = this.context ?: return defaultTTSSpeedRate
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.ttsSpeedRate)
        ?: defaultTTSSpeedRate
}

suspend fun DataCoordinator.setTTSSpeedRateDataStore(value: Float) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.ttsSpeedRate] = value
    }
}

suspend fun DataCoordinator.getTTSPitch(): Float {
    val context = this.context ?: return defaultTTSPitch
    return context.dataStore.data.firstOrNull()?.get(PreferencesKeys.ttsPitch)
        ?: defaultTTSPitch
}

suspend fun DataCoordinator.setTTSPitchDataStore(value: Float) {
    val context = this.context ?: return
    context.dataStore.edit { preferences ->
        preferences[PreferencesKeys.ttsPitch] = value
    }
}