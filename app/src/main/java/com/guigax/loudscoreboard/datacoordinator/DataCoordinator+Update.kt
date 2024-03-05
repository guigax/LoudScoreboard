package com.guigax.loudscoreboard.datacoordinator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun DataCoordinator.updateTeam1Name(value: String) {
    // Update Value
    this.team1NamePreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setTeam1NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Name(value: String) {
    // Update Value
    this.team2NamePreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setTeam2NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam1Score(value: Int) {
    // Update Value
    this.team1ScorePreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setTeam1ScoreDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Score(value: Int) {
    // Update Value
    this.team2ScorePreferenceVariable = value
    // Save to System
    GlobalScope.launch(Dispatchers.Default) {
        // Update DataStore
        setTeam2ScoreDataStore(value)
    }
}