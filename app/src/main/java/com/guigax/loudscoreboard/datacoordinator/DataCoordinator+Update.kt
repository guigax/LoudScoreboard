package com.guigax.loudscoreboard.datacoordinator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun DataCoordinator.updateTeam1Name(value: String) {
    this.team1NamePref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Name(value: String) {
    this.team2NamePref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2NameDataStore(value)
    }
}

fun DataCoordinator.updateTeam1Score(value: Int) {
    this.team1ScorePref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1ScoreDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Score(value: Int) {
    this.team2ScorePref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2ScoreDataStore(value)
    }
}

fun DataCoordinator.updateTeam1Color(value: Int) {
    this.team1ColorPref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam1ColorDataStore(value)
    }
}

fun DataCoordinator.updateTeam2Color(value: Int) {
    this.team2ColorPref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTeam2ColorDataStore(value)
    }
}

fun DataCoordinator.updateIsMuted(value: Boolean) {
    this.isMutedPref = value
    GlobalScope.launch(Dispatchers.Default) {
        setIsMuted(value)
    }
}

fun DataCoordinator.updateTTSSpeedRate(value: Float) {
    this.ttsSpeedRatePref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTTSSpeedRateDataStore(value)
    }
}

fun DataCoordinator.updateTTSPitch(value: Float) {
    this.ttsPitchPref = value
    GlobalScope.launch(Dispatchers.Default) {
        setTTSPitchDataStore(value)
    }
}