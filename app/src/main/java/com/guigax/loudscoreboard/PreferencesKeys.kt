package com.guigax.loudscoreboard

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val team1Name = stringPreferencesKey("team1NamePreferenceKey")
    val team2Name = stringPreferencesKey("team2NamePreferenceKey")
    val team1Score = intPreferencesKey("team1ScorePreferenceKey")
    val team2Score = intPreferencesKey("team2ScorePreferenceKey")
}