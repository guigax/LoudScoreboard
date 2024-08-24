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

    var defaultTeam1Name: String = ""
    var team1NamePref: String = ""
    var defaultTeam2Name: String = ""
    var team2NamePref: String = ""

    val defaultTeam1Score: Int = 0
    var team1ScorePref: Int = 0

    val defaultTeam2Score: Int = 0
    var team2ScorePref: Int = 0

    val defaultTeam1Color: Int = android.R.color.holo_blue_light
    var team1ColorPref: Int = 0
    val defaultTeam2Color: Int = android.R.color.holo_orange_light
    var team2ColorPref: Int = 0

    val defaultIsMuted: Boolean = false
    var isMutedPref: Boolean = false

    var defaultTTSSpeedRate: Float = 1.5f
    var ttsSpeedRatePref: Float = 0f
    var defaultTTSPitch: Float = 0.8f
    var ttsPitchPref: Float = 0f

    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    fun initialize(context: Context, onLoad: () -> Unit) {
        this.context = context

        defaultTeam1Name = this.context!!.getString(R.string.team1DefaultName)
        defaultTeam2Name = this.context!!.getString(R.string.team2DefaultName)

        GlobalScope.launch(Dispatchers.Default) {
            team1NamePref = getTeam1NameDataStore()
            team2NamePref = getTeam2NameDataStore()

            team1ScorePref = getTeam1ScoreDataStore()
            team2ScorePref = getTeam2ScoreDataStore()

            team1ColorPref = getTeam1ColorDataStore()
            team2ColorPref = getTeam2ColorDataStore()

            isMutedPref = getIsMuted()

            ttsSpeedRatePref = getTTSSpeedRate()
            ttsPitchPref = getTTSPitch()
            // Callback
            onLoad()
        }
    }
}