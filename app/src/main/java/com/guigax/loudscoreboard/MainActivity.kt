package com.guigax.loudscoreboard

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.getTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam1ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.setTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Score
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Score
import com.guigax.loudscoreboard.fragment.ColorPickerDialog
import com.guigax.loudscoreboard.fragment.SettingsFragment
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.LinkedList


class MainActivity : AppCompatActivity(), ColorPickerDialog.ColorPickerListener {
    companion object {
        val DEFAULT_DURATION_INCREASE_SCORE: Duration = Duration.ofMillis(1000)
        val DEFAULT_DURATION_DECREASE_SCORE: Duration = Duration.ofMillis(4000)
        val DURATION_NOW: Duration = Duration.ZERO
    }

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
    private lateinit var settingsV: ImageView

    private lateinit var audioManager: AudioManager
    private lateinit var vibratorManager: VibratorManager

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    private var team1CurrentScore = 0
    private var team2CurrentScore = 0
    private var team1CurrentName = ""
    private var team2CurrentName = ""

    private var ttsQueue: LinkedList<String> = LinkedList<String>()
    private val handler = Handler()
    private var runnable: Runnable? = null

    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener { }
        .build()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupCoordinators()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator = vibratorManager.defaultVibrator
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
        }

        updateTeamsNames()
        updateTeamsScore()
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
        team1ScoreV = findViewById(R.id.team1Score)
        team2ScoreV = findViewById(R.id.team2Score)
        team1NameV = findViewById(R.id.team1Name)
        team2NameV = findViewById(R.id.team2Name)
        team1MinusV = findViewById(R.id.team1MinusScore)
        team2MinusV = findViewById(R.id.team2MinusScore)

        resetV = findViewById(R.id.resetScore)
        announceV = findViewById(R.id.announceScore)
        swapV = findViewById(R.id.swapScore)
        resetV = findViewById(R.id.resetScore)
        whistleV = findViewById(R.id.whistle)
        settingsV = findViewById(R.id.settings)

        updateTeamsScore()
        updateTeamsNames()
    }

    private fun setListeners() {
        team1ScoreV.setOnClickListener { incrementTeamScore(1) }
        team2ScoreV.setOnClickListener { incrementTeamScore(2) }
        team1MinusV.setOnClickListener { decreaseTeamScore(1) }
        team2MinusV.setOnClickListener { decreaseTeamScore(2) }

        mediaPlayer.setOnCompletionListener { audioManager.abandonAudioFocusRequest(focusRequest) }
        whistleV.setOnClickListener {
            playSound()
        }
        resetV.setOnClickListener { resetScore() }
        resetV.setOnLongClickListener {
            resetTeamsNames()
            resetScore()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            return@setOnLongClickListener true
        }
        announceV.setOnClickListener { announceScore(DURATION_NOW) }
        swapV.setOnClickListener { swapScores() }
        swapV.setOnLongClickListener {
            swapTeamsNames()
            swapScores()
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            return@setOnLongClickListener true
        }
        settingsV.setOnClickListener { showSettingsDialog() }
    }

    private fun scheduleTTS(delay: Duration = DEFAULT_DURATION_INCREASE_SCORE) {
        runnable = Runnable {
            if (ttsQueue.isNotEmpty()) {
                TTS(this@MainActivity, ttsQueue.last, audioManager)
            }
            ttsQueue.clear()
        }
        handler.postDelayed(runnable!!, delay.toMillis())
    }

    private fun cancelPendingTTS() {
        runnable?.let {
            handler.removeCallbacks(it)
            runnable = null
        }
    }

    private fun playSound() {
        val result = audioManager.requestAudioFocus(focusRequest)
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.reset()
            }
        }
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start()
        }
    }

    private suspend fun getNamesFromData() {
        team1CurrentName = DataCoordinator.shared.getTeam1NameDataStore()
        team2CurrentName = DataCoordinator.shared.getTeam2NameDataStore()
    }

    private suspend fun getScoreFromData() {
        team1CurrentScore = DataCoordinator.shared.getTeam1ScoreDataStore()
        team2CurrentScore = DataCoordinator.shared.getTeam2ScoreDataStore()
    }

    private fun updateScores(
        delay: Duration = DEFAULT_DURATION_INCREASE_SCORE
    ) {
        DataCoordinator.shared.updateTeam1Score(team1CurrentScore)
        DataCoordinator.shared.updateTeam2Score(team2CurrentScore)

        updateTeamsScore()
        announceScore(delay)
    }

    private fun updateTeamsNames() {
        team1NameV.text = team1CurrentName
        team2NameV.text = team2CurrentName
    }

    private fun updateTeamsScore() {
        team1ScoreV.text = team1CurrentScore.toString()
        team2ScoreV.text = team2CurrentScore.toString()
    }

    private fun incrementTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> team1CurrentScore++
            2 -> team2CurrentScore++
        }
        updateScores()
    }

    private fun decreaseTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> if (team1CurrentScore > 0) team1CurrentScore--
            2 -> if (team2CurrentScore > 0) team2CurrentScore--
        }
        updateScores(delay = DEFAULT_DURATION_DECREASE_SCORE)
    }

    private fun swapScores() {
        // No need for extra variable
        team1CurrentScore += team2CurrentScore
        team2CurrentScore = team1CurrentScore - team2CurrentScore
        team1CurrentScore -= team2CurrentScore
        updateScores()
    }

    private fun swapTeamsNames() {
        runBlocking {
            DataCoordinator.shared.setTeam1NameDataStore(team2NameV.text.toString())
            DataCoordinator.shared.setTeam2NameDataStore(team1NameV.text.toString())
            getNamesFromData()
        }
        updateTeamsNames()
    }

    private fun resetScore() {
        team1CurrentScore = 0
        team2CurrentScore = 0
        updateScores()
    }

    private fun announceScore(delay: Duration = DEFAULT_DURATION_INCREASE_SCORE) {
        ttsQueue.clear()
        ttsQueue.add("$team1CurrentScore para ${team1NameV.text}. A, $team2CurrentScore para ${team2NameV.text}")
        cancelPendingTTS()
        scheduleTTS(delay)
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

    private fun setupCoordinators() {
        DataCoordinator.shared.initialize(
            context = this,
            onLoad = {
                runBlocking {
                    getScoreFromData()
                    getNamesFromData()
                }
            }
        )
    }

    override fun onColorSelected(color: Int) {
        TODO("Not yet implemented")
        // get current color from data
        // set layout color
    }
}