package com.example.timerapp

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var intervalText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var startStopButton: Button
    private lateinit var restButton: Button
    private lateinit var interval1Input: EditText
    private lateinit var interval2Input: EditText
    private lateinit var totalDurationInput: EditText
    private lateinit var saveIntervalsButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var totalTimer: CountDownTimer? = null
    private var isRunning = false
    private var interval1 = 90
    private var interval2 = 60
    private var totalDuration = 25 // in minutes
    private var currentInterval = interval1
    private var lastIntervalBeforeRest = interval1
    private var remainingTime = interval1 * 1000L
    private var totalTimeElapsed = 0L

    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private var isSoundLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        intervalText = findViewById(R.id.intervalText)
        totalTimeText = findViewById(R.id.totalTimeText)
        startStopButton = findViewById(R.id.startStopButton)
        restButton = findViewById(R.id.restButton)
        interval1Input = findViewById(R.id.interval1Input)
        interval2Input = findViewById(R.id.interval2Input)
        totalDurationInput = findViewById(R.id.totalDurationInput)
        saveIntervalsButton = findViewById(R.id.saveIntervalsButton)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, _ ->
            isSoundLoaded = true
            startTimer()
        }

        soundId = soundPool.load(this, R.raw.beep, 1)

        updateDisplay()

        startStopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        restButton.setOnClickListener {
            stopTimer()
            lastIntervalBeforeRest = currentInterval
            currentInterval = 30
            remainingTime = currentInterval * 1000L
            updateDisplay()
            startTimer()
        }

        saveIntervalsButton.setOnClickListener {
            val newInterval1 = interval1Input.text.toString().toIntOrNull()
            val newInterval2 = interval2Input.text.toString().toIntOrNull()
            val newTotalDuration = totalDurationInput.text.toString().toIntOrNull()

            if (newInterval1 != null && newInterval2 != null && newTotalDuration != null) {
                interval1 = newInterval1
                interval2 = newInterval2
                totalDuration = newTotalDuration
                currentInterval = interval1
                remainingTime = currentInterval * 1000L
                stopTimer()
                updateDisplay()
                startTimer()
            }
        }
    }

    private fun startTimer() {
        if (!isSoundLoaded || isRunning) return
        isRunning = true
        startStopButton.text = "Stop"
        totalTimeElapsed = 0L

        createTimer(remainingTime)
        startTotalTimer()
    }

    private fun stopTimer() {
        isRunning = false
        startStopButton.text = "Start"
        countDownTimer?.cancel()
        totalTimer?.cancel()
        countDownTimer = null
        totalTimer = null
    }

    private fun createTimer(duration: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateDisplay()
            }

            override fun onFinish() {
                if (isSoundLoaded) {
                    soundPool.play(soundId, 1f, 1f, 1, 2, 1f)
                }
                if (currentInterval == 30) {
                    currentInterval = lastIntervalBeforeRest
                } else {
                    currentInterval = if (currentInterval == interval1) interval2 else interval1
                }

                remainingTime = currentInterval * 1000L
                updateDisplay()

                if (isRunning) {
                    createTimer(remainingTime)
                }
            }
        }.start()
    }

    private fun startTotalTimer() {
        totalTimer?.cancel()
        totalTimer = object : CountDownTimer(totalDuration * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                totalTimeElapsed += 1000
                updateTotalTimeDisplay()
            }

            override fun onFinish() {
                stopTimer()
            }
        }.start()
    }

    private fun updateDisplay() {
        val seconds = (remainingTime / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        timerText.text = String.format("%02d:%02d", minutes, secs)
        intervalText.text = "Interval: ${currentInterval}s"
    }

    private fun updateTotalTimeDisplay() {
        val seconds = (totalTimeElapsed / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        totalTimeText.text = String.format("Total Time: %02d:%02d", minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        totalTimer?.cancel()
        soundPool.release()
    }
}
