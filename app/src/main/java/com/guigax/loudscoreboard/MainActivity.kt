package com.guigax.loudscoreboard

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.guigax.loudscoreboard.datacoordinator.DataCoordinator
import com.guigax.loudscoreboard.datacoordinator.getTeam1NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam1ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2NameDataStore
import com.guigax.loudscoreboard.datacoordinator.getTeam2ScoreDataStore
import com.guigax.loudscoreboard.datacoordinator.updateTeam1Score
import com.guigax.loudscoreboard.datacoordinator.updateTeam2Score
import com.guigax.loudscoreboard.fragment.BottomSheetDialogFragment
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.util.LinkedList


class MainActivity : AppCompatActivity() {
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
    private lateinit var mediaPlayer: MediaPlayer

    private var team1Score = 0
    private var team2Score = 0

    private var ttsQueue: LinkedList<String> = LinkedList<String>()
    lateinit var mainHandler: Handler

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

    private val readTTSFromQueue = object : Runnable {
        override fun run() {
            if (ttsQueue.isNotEmpty()) {
                TTS(this@MainActivity, ttsQueue.last, audioManager)
            }
            ttsQueue.clear()
            mainHandler.postDelayed(this, Duration.ofSeconds(2).toMillis())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
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

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mainHandler = Handler(Looper.getMainLooper())

        setupCoordinators()

        // Set click listeners
        team1ScoreV.setOnClickListener { incrementTeamScore(1) }
        team2ScoreV.setOnClickListener { incrementTeamScore(2) }
        team1MinusV.setOnClickListener { decreaseTeamScore(1) }
        team2MinusV.setOnClickListener { decreaseTeamScore(2) }

        mediaPlayer = MediaPlayer.create(this, R.raw.whistle)
        mediaPlayer.setOnCompletionListener { audioManager.abandonAudioFocusRequest(focusRequest) }
        whistleV.setOnClickListener { playSound() }
        resetV.setOnClickListener { resetScore() }
        announceV.setOnClickListener { announceScore() }
        swapV.setOnClickListener { swapScores() }
        settingsV.setOnClickListener { showSettingsDialog() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            return
        }

        runBlocking {
            getNamesFromData()
        }
    }

    private fun playSound() {
        val result = audioManager.requestAudioFocus(focusRequest)
        // Check if the MediaPlayer is playing, stop and reset it
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.reset()
            }
        }
        // Start playing the sound
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(readTTSFromQueue)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(readTTSFromQueue)
    }

    private suspend fun getNamesFromData() {
        team1NameV.text = DataCoordinator.shared.getTeam1NameDataStore()
        team2NameV.text = DataCoordinator.shared.getTeam2NameDataStore()
    }

    private suspend fun getScoreFromData() {
        team1Score = DataCoordinator.shared.getTeam1ScoreDataStore()
        team2Score = DataCoordinator.shared.getTeam2ScoreDataStore()

        team1ScoreV.text = team1Score.toString()
        team2ScoreV.text = team2Score.toString()
    }
    private fun updateScores() {
        DataCoordinator.shared.updateTeam1Score(team1Score)
        DataCoordinator.shared.updateTeam2Score(team2Score)

        team1ScoreV.text = team1Score.toString()
        team2ScoreV.text = team2Score.toString()

        ttsQueue.clear()
        ttsQueue.add("$team1Score para ${team1NameV.text}. A, $team2Score para ${team2NameV.text}")
    }

    private fun incrementTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> team1Score++
            2 -> team2Score++
        }
        updateScores()
    }

    private fun decreaseTeamScore(teamNumber: Int) {
        when (teamNumber) {
            1 -> if (team1Score > 0) team1Score--
            2 -> if (team2Score > 0) team2Score--
        }
        updateScores()
    }

    private fun swapScores() {
        // No need for extra variable
        team1Score += team2Score
        team2Score = team1Score - team2Score
        team1Score -= team2Score
        updateScores()
    }

    private fun resetScore() {
        team1Score = 0
        team2Score = 0
        updateScores()
    }

    private fun announceScore() {
        ttsQueue.clear()
        ttsQueue.add("$team1Score para ${team1NameV.text}. A, $team2Score para ${team2NameV.text}")
    }

    private fun showSettingsDialog() {
        val bottomSheetDialogFragment = BottomSheetDialogFragment()
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun setupCoordinators() {
        DataCoordinator.shared.initialize(
            context = baseContext,
            onLoad = {
                runBlocking {
                    getScoreFromData()
                    getNamesFromData()
                }
            }
        )
    }
}