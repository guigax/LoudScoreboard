package com.guigax.loudscoreboard.datacoordinator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun DataCoordinator.updateTeam1Name(value: String) {
    this.team1NamePreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Name(value: String) {
    this.team2NamePreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam1Score(value: Int) {
    this.team1ScorePreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1ScoreDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Score(value: Int) {
    this.team2ScorePreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2ScoreDataStore(value)
    }
}

fun DataCoordinator.updateTeam1Color(value: Int) {
    this.team1ColorPreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1ColorDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Color(value: Int) {
    this.team2ColorPreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2ColorDataStore(value)
    }
}

fun DataCoordinator.updateIsMuted(value: Boolean) {
    this.isMutedPreferenceVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setIsMuted(value)
    }
}

fun DataCoordinator.updateTTSSpeedRate(value: Float) {
    this.ttsSpeedRateVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTTSSpeedRateDataStore(value)
    }
}

fun DataCoordinator.updateTTSPitch(value: Float) {
    this.ttsPitchVariable = value
    GlobalScope.launch(Dispatchers.Default) {
        setTTSPitchDataStore(value)
    }
}