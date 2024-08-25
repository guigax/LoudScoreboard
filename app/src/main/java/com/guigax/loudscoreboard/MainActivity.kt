package com.guigax.loudscoreboard

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.getIsMuted
import com.guigax.loudscoreboard.datacoordinator.getTeam1ColorDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam1ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2ColorDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Name
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Score
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Name
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Score
import com.guigax.loudscoreboard.fragments.SettingsFragment
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.LinkedList


class MainActivity : AppCompatActivity() {
    companion object {
        val DEFAULT_DURATION_INCREASE_SCORE: Duration = Duration.ofMillis(1000)
        val DEFAULT_DURATION_DECREASE_SCORE: Duration = Duration.ofMillis(4000)
        val DURATION_NOW: Duration = Duration.ZERO
        val DURATION_DISABLED_WHISTLE: Duration = Duration.ofMillis(3000)
        val DURATION_HIGHLIGHT: Duration = Duration.ofMillis(5000)
    }

    private lateinit var team1Layout: LinearLayout
    private lateinit var team2Layout: LinearLayout
    private lateinit var team1ButtonsLayout: LinearLayout
    private lateinit var team2ButtonsLayout: LinearLayout
    private lateinit var team1ScoreV: TextView
    private lateinit var team2ScoreV: TextView
    private lateinit var team1NameV: TextView
    private lateinit var team2NameV: TextView
    private lateinit var team1MinusV: TextView
    private lateinit var team2MinusV: TextView
    private lateinit var resetV: ImageView
    private lateinit var announceV: ImageView
    private lateinit var swapV: ImageView
    private lateinit var whistleV: ImageView
    private lateinit var highlightV: ImageView
    private lateinit var settingsV: ImageView

    private lateinit var audioManager: AudioManager
    private lateinit var mediaPlayer: MediaPlayer

    private var team1CurrentName = ""
    private var team2CurrentName = ""
    private var team1CurrentScore = 0
    private var team2CurrentScore = 0
    private var team1CurrentColor = android.R.color.holo_blue_light
    private var team2CurrentColor = android.R.color.holo_orange_light
    private var isMuted = false
    private var currentTTSDelay = DEFAULT_DURATION_INCREASE_SCORE
    private var allowUpdateTTSDelay = true

    private var ttsQueue: LinkedList<String> = LinkedList<String>()
    private val handler = Handler()
    private var runnable: Runnable? = null

    private val focusRequest =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener { }
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupCoordinators()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaPlayer = MediaPlayer.create(this, R.raw.whistle)

        setListeners()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            return
        }

        runBlocking {
            getNamesFromData()
            getColorsFromData()
            getIsMutedFromData()
        }

        updateTeamsNames()
        updateTeamsScore()
        updateTeamsColors()
    }

    override fun onPause() {
        super.onPause()
        cancelPendingTTS()
    }

    override fun onResume() {
        super.onResume()
        scheduleTTS()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initViews() {
        team1Layout = findViewById(R.id.team1Layout)
        team2Layout = findViewById(R.id.team2Layout)
        team1ScoreV = findViewById(R.id.team1Score)
        team2ScoreV = findViewById(R.id.team2Score)
        team1ButtonsLayout = findViewById(R.id.team1ButtonsLayout)
        team2ButtonsLayout = findViewById(R.id.team2ButtonsLayout)
        team1NameV = findViewById(R.id.team1Name)
        team2NameV = findViewById(R.id.team2Name)
        team1MinusV = findViewById(R.id.team1MinusScore)
        team2MinusV = findViewById(R.id.team2MinusScore)

        resetV = findViewById(R.id.resetScore)
        announceV = findViewById(R.id.announceScore)
        swapV = findViewById(R.id.swapScore)
        resetV = findViewById(R.id.resetScore)
        whistleV = findViewById(R.id.whistle)
        highlightV = findViewById(R.id.highlight)
        settingsV = findViewById(R.id.settings)

        updateTeamsScore()
        updateTeamsNames()
        updateTeamsColors()
    }

    private fun setListeners() {
        team1ScoreV.setOnClickListener { incrementTeamScore(1) }
        team2ScoreV.setOnClickListener { incrementTeamScore(2) }
        team1MinusV.setOnClickListener { decreaseTeamScore(1) }
        team2MinusV.setOnClickListener { decreaseTeamScore(2) }

        mediaPlayer.setOnCompletionListener { audioManager.abandonAudioFocusRequest(focusRequest) }
        whistleV.setOnClickListener { playSound() }
        highlightV.setOnClickListener { highContrast() }
        resetV.setOnClickListener { resetScore() }
        resetV.setOnLongClickListener {
            resetTeamsNames()
            resetScore()
            return@setOnLongClickListener true
        }
        announceV.setOnClickListener {
            updateTTSDelay(DURATION_NOW)
            announceScore()
        }
        swapV.setOnClickListener { swapScores() }
        swapV.setOnLongClickListener {
            swapTeamsNames()
            swapScores()
            return@setOnLongClickListener true
        }
        settingsV.setOnClickListener { showSettingsDialog() }
    }

    private fun scheduleTTS() {
        runnable = Runnable {
            if (ttsQueue.isNotEmpty()) {
                TTS(this@MainActivity, ttsQueue.last, audioManager)
            }
            ttsQueue.clear()
            allowUpdateTTSDelay = true
        }
        handler.postDelayed(runnable!!, currentTTSDelay.toMillis())
    }

    private fun cancelPendingTTS() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }

    private fun playSound() {
        val result = audioManager.requestAudioFocus(focusRequest)
        whistleV.isEnabled = false
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.reset()
            }
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            whistleV.isEnabled = true
        }, DURATION_DISABLED_WHISTLE.toMillis())
    }

    private suspend fun getNamesFromData() {
        team1CurrentName = DataCoordinator.shared.getTeam1NameDataStore()
        team2CurrentName = DataCoordinator.shared.getTeam2NameDataStore()
    }

    private suspend fun getScoreFromData() {
        team1CurrentScore = DataCoordinator.shared.getTeam1ScoreDataStore()
        team2CurrentScore = DataCoordinator.shared.getTeam2ScoreDataStore()
    }

    private suspend fun getColorsFromData() {
        team1CurrentColor = DataCoordinator.shared.getTeam1ColorDataStore()
        team2CurrentColor = DataCoordinator.shared.getTeam2ColorDataStore()
    }

    private suspend fun getIsMutedFromData() {
        isMuted = DataCoordinator.shared.getIsMuted()
    }

    private fun updateScores() {
        DataCoordinator.shared.updateTeam1Score(team1CurrentScore)
        DataCoordinator.shared.updateTeam2Score(team2CurrentScore)

        updateTeamsScore()
        announceScore()
    }

    private fun updateTeamsNames() {
        team1NameV.text = team1CurrentName
        team2NameV.text = team2CurrentName
    }

    private fun updateTeamsScore() {
        team1ScoreV.text = team1CurrentScore.toString()
        team2ScoreV.text = team2CurrentScore.toString()
    }

    private fun updateTeamsColors() {
        team1Layout.setBackgroundTintList(
            ContextCompat.getColorStateList(
                baseContext,
                team1CurrentColor
            )
        )
        team2Layout.setBackgroundTintList(
            ContextCompat.getColorStateList(
                baseContext,
                team2CurrentColor
            )
        )
    }

    private fun incrementTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> team1CurrentScore++
            2 -> team2CurrentScore++
        }
        updateTTSDelay(DEFAULT_DURATION_INCREASE_SCORE)
        updateScores()
    }

    private fun decreaseTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> if (team1CurrentScore > 0) team1CurrentScore--
            2 -> if (team2CurrentScore > 0) team2CurrentScore--
        }
        updateTTSDelay(DEFAULT_DURATION_DECREASE_SCORE)
        updateScores()
    }

    private fun swapScores() {
        // No need for extra variable
        team1CurrentScore += team2CurrentScore
        team2CurrentScore = team1CurrentScore - team2CurrentScore
        team1CurrentScore -= team2CurrentScore
        updateScores()
    }

    private fun swapTeamsNames() {
        team1CurrentName = team2NameV.text.toString()
        team2CurrentName = team1NameV.text.toString()

        DataCoordinator.shared.updateTeam1Name(team2NameV.text.toString())
        DataCoordinator.shared.updateTeam2Name(team1NameV.text.toString())

        updateTeamsNames()
        updateTeamsColors()
    }

    private fun resetScore() {
        team1CurrentScore = 0
        team2CurrentScore = 0
        updateScores()
    }

    private fun announceScore() {
        if (isMuted && currentTTSDelay != DURATION_NOW) {
            return
        }

        val ttsPhrase = baseContext.getString(
            R.string.tts_default_phrase,
            team1CurrentScore,
            team1NameV.text,
            team2CurrentScore,
            team2NameV.text
        )

        ttsQueue.clear()
        ttsQueue.add(ttsPhrase)
        cancelPendingTTS()
        scheduleTTS()
    }

    private fun updateTTSDelay(newDelay: Duration) {
        if (!allowUpdateTTSDelay) {
            return
        }

        if (newDelay == DEFAULT_DURATION_DECREASE_SCORE) {
            allowUpdateTTSDelay = false
        }

        currentTTSDelay = newDelay
    }

    private fun showSettingsDialog() {
        val settingsFragment = SettingsFragment()
        settingsFragment.show(supportFragmentManager, settingsFragment.tag)
    }

    private fun resetTeamsNames() {
        runBlocking {
            DataCoordinator.shared.setTeam1NameDataStore("")
            DataCoordinator.shared.setTeam2NameDataStore("")
            getNamesFromData()
        }
        updateTeamsNames()
    }

    private fun highContrast() {
        val highlightedWeight = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,  // Width
            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
        )
        highlightedWeight.weight = 0.9f

        team1Layout.setBackgroundTintList(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team1ButtonsLayout.visibility = View.GONE
        team1ScoreV.textSize = 300f
        team1ScoreV.layoutParams = highlightedWeight
        team1NameV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.green_screen
            )
        )
        team1ScoreV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.green_screen
            )
        )
        team1ScoreV.isEnabled = false

        team2Layout.setBackgroundTintList(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team2ButtonsLayout.visibility = View.GONE
        team2ScoreV.textSize = 300f
        team2ScoreV.layoutParams = highlightedWeight
        team2NameV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.princess_peach
            )
        )
        team2ScoreV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.princess_peach
            )
        )
        team2ScoreV.isEnabled = false

        // Revert state
        Handler(Looper.getMainLooper()).postDelayed({
            revertHighlight()
        }, DURATION_HIGHLIGHT.toMillis())
    }

    private fun revertHighlight() {
        val normalWeight = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,  // Width
            LinearLayout.LayoutParams.WRAP_CONTENT   // Height
        )
        normalWeight.weight = 0.9f

        updateTeamsColors()
        team1ButtonsLayout.visibility = View.VISIBLE
        team1ScoreV.textSize = 150f
        team1ScoreV.layoutParams = normalWeight
        team1NameV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team1ScoreV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team1ScoreV.isEnabled = true

        team2ButtonsLayout.visibility = View.VISIBLE
        team2ScoreV.textSize = 150f
        team1ScoreV.layoutParams = normalWeight
        team2NameV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team2ScoreV.setTextColor(
            ContextCompat.getColorStateList(
                baseContext,
                R.color.black
            )
        )
        team2ScoreV.isEnabled = true
    }

    private fun setupCoordinators() {
        DataCoordinator.shared.initialize(
            context = this,
            onLoad = {
                runBlocking {
                    getScoreFromData()
                    getNamesFromData()
                    getColorsFromData()
                    getIsMutedFromData()
                }
            }
        )
    }
}