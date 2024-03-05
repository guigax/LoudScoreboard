package com.guigax.loudscoreboard

import android.app.Activity
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.Locale


class TTS(
    private val activity: Activity,
    private val message: String,
    private val audioManager: AudioManager
) : TextToSpeech.OnInitListener {
    private val TAG: String = "TTS"

    private val tts: TextToSpeech = TextToSpeech(activity, this)
    private val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        .setAcceptsDelayedFocusGain(true)
        .setOnAudioFocusChangeListener { }
        .build()

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            val result: Int = tts.setLanguage(Locale.getDefault())

            tts.setSpeechRate(1.5f)
            tts.setPitch(0.8f)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(activity, "This Language is not supported", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.v(TAG, "before setOnUtteranceProgressListener")
                tts.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceID: String) {
                            //
                        }

                        override fun onDone(utteranceID: String) {
                            if (utteranceID == "messageId" ){
                                activity.runOnUiThread {
                                    releaseAudioFocus()
                                }
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceID: String) {
                            //
                        }
                    })

                speakOut(message)
            }
        } else {
            Toast.makeText(activity, "Initialization Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(message: String) {
        if (message.isBlank()) {
            return
        }

        val result = audioManager.requestAudioFocus(focusRequest)
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "messageId")
        }
    }

    private fun releaseAudioFocus() {
        audioManager.abandonAudioFocusRequest(focusRequest)
    }
}