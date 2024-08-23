package com.guigax.loudscoreboard.datacoordinator

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.guigax.loudscoreboard.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DataCoordinator {
    companion object {
        val shared = DataCoordinator()
        const val identifier = "[DataCoordinator]"
        const val USER_PREFERENCES_NAME = "loudScoreboard_preferences"
    }

    var context: Context? = null
    var team1NamePreferenceVariable: String = ""
    var defaultTeam1NamePreferenceValue: String = ""
    var team2NamePreferenceVariable: String = ""
    var defaultTeam2NamePreferenceValue: String = ""

    var team1ScorePreferenceVariable: Int = 0
    val defaultTeam1ScorePreferenceVariable: Int = 0
    var team2ScorePreferenceVariable: Int = 0
    val defaultTeam2ScorePreferenceVariable: Int = 0

    var team1ColorPreferenceVariable: Int = 0
    val defaultTeam1ColorPreferenceVariable: Int = android.R.color.holo_blue_light
    var team2ColorPreferenceVariable: Int = 0
    val defaultTeam2ColorPreferenceVariable: Int = android.R.color.holo_orange_light

    var isMutedPreferenceVariable: Boolean = false
    val defaultIsMutedPreferenceVariable: Boolean = false

    var ttsSpeedRateVariable: Float = 0f
    var defaultTTSSpeedRate: Float = 1.5f
    var ttsPitchVariable: Float = 0f
    var defaultTTSPitch: Float = 0.8f

    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    fun initialize(context: Context, onLoad: () -> Unit) {
        this.context = context

        defaultTeam1NamePreferenceValue = this.context!!.getString(R.string.team1DefaultName)
        defaultTeam2NamePreferenceValue = this.context!!.getString(R.string.team2DefaultName)

        GlobalScope.launch(Dispatchers.Default) {
            team1NamePreferenceVariable = getTeam1NameDataStore()
            team2NamePreferenceVariable = getTeam2NameDataStore()

            team1ScorePreferenceVariable = getTeam1ScoreDataStore()
            team2ScorePreferenceVariable = getTeam2ScoreDataStore()

            team1ColorPreferenceVariable = getTeam1ColorDataStore()
            team2ColorPreferenceVariable = getTeam2ColorDataStore()

            isMutedPreferenceVariable = getIsMuted()

            ttsSpeedRateVariable = getTTSSpeedRate()
            ttsPitchVariable = getTTSPitch()
            // Callback
            onLoad()
        }
    }
}