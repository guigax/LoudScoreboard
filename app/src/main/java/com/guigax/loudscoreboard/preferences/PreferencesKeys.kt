package com.guigax.loudscoreboard.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val team1Name = stringPreferencesKey("team1NamePreferenceKey")
    val team2Name = stringPreferencesKey("team2NamePreferenceKey")
    val team1Score = intPreferencesKey("team1ScorePreferenceKey")
    val team2Score = intPreferencesKey("team2ScorePreferenceKey")

    val team1Color = intPreferencesKey("team1ColorPreferenceKey")
    val team2Color = intPreferencesKey("team2ColorPreferenceKey")

    val isMuted = booleanPreferencesKey("isMutedPreferenceKey")
}