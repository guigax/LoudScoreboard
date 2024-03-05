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
    }

    var context: Context? = null
    var team1NamePreferenceVariable: String = ""
    val defaultTeam1NamePreferenceValue: String =
        context?.getString(R.string.team1DefaultName) ?: ""
    var team2NamePreferenceVariable: String = ""
    val defaultTeam2NamePreferenceValue: String =
        context?.getString(R.string.team2DefaultName) ?: ""

    var team1ScorePreferenceVariable: Int = 0
    val defaultTeam1ScorePreferenceVariable: Int = 0
    var team2ScorePreferenceVariable: Int = 0
    val defaultTeam2ScorePreferenceVariable: Int = 0

    private val USER_PREFERENCES_NAME = "loudScoreboard_preferences"
    val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    fun initialize(context: Context, onLoad: () -> Unit) {
        this.context = context

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