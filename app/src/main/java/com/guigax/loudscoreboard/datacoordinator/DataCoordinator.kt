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
            // Callback
            onLoad()
        }
    }
}