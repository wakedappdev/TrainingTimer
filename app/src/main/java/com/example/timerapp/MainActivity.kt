package com.example.timerapp

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var intervalText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var totalIterationsText: TextView
    private lateinit var pauseResumeButton: Button
    private lateinit var stopButton: Button
    private lateinit var interval1Input: EditText
    private lateinit var interval2Input: EditText
    private lateinit var totalDurationInput: EditText
    private lateinit var saveIntervalsButton: Button
    private lateinit var stopConditionType: RadioGroup

    private var countDownTimer: CountDownTimer? = null
    private var totalTimer: CountDownTimer? = null
    private var trackingTimer: CountDownTimer? = null
    private var isRunning = false
    private var isPaused = false
    private var interval1 = 90
    private var interval2 = 60
    private var totalDuration = 25
    private var currentInterval = interval1
    private var remainingTime = interval1 * 1000L
    private var totalTimeRemaining = 0L
    private var totalTimeElapsed = 0L
    private var iterationCount = 0

    private lateinit var toneGenerator: ToneGenerator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerText = findViewById(R.id.timerText)
        intervalText = findViewById(R.id.intervalText)
        totalTimeText = findViewById(R.id.totalTimeText)
        totalIterationsText = findViewById(R.id.totalIterationsText)
        pauseResumeButton = findViewById(R.id.pauseResumeButton)
        stopButton = findViewById(R.id.stopButton)
        interval1Input = findViewById(R.id.interval1Input)
        interval2Input = findViewById(R.id.interval2Input)
        totalDurationInput = findViewById(R.id.totalDurationInput)
        saveIntervalsButton = findViewById(R.id.saveIntervalsButton)
        stopConditionType = findViewById(R.id.stopConditionType)

        toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)

        updateDisplay()

        pauseResumeButton.setOnClickListener {
            if (isRunning) {
                if (isPaused) {
                    resumeTimer()
                } else {
                    pauseTimer()
                }
            } else {
                startTimer()
            }
        }

        stopButton.setOnClickListener {
            stopTimer()
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
            }
        }

        stopConditionType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.stopAfterMinutes) {
                totalDurationInput.setText(getString(R.string.minutes_default))
            } else {
                totalDurationInput.setText(getString(R.string.iterations_default))
            }
        }
    }

    private fun startTimer() {
        if (isRunning) return
        isRunning = true
        isPaused = false
        pauseResumeButton.text = getString(R.string.pause)
        totalTimeElapsed = 0L
        iterationCount = 0
        totalTimeRemaining = totalDuration * 60 * 1000L
        updateDisplay()

        createTimer(remainingTime)
        startTrackingTimer()

        if (stopConditionType.checkedRadioButtonId == R.id.stopAfterMinutes) {
            startTotalTimer(totalTimeRemaining)
        }
    }

    private fun pauseTimer() {
        if (!isRunning || isPaused) return
        isPaused = true
        pauseResumeButton.text = getString(R.string.resume)
        countDownTimer?.cancel()
        trackingTimer?.cancel()
        totalTimer?.cancel()
    }

    private fun resumeTimer() {
        if (!isRunning || !isPaused) return
        isPaused = false
        pauseResumeButton.text = getString(R.string.pause)
        createTimer(remainingTime)
        startTrackingTimer()
        if (stopConditionType.checkedRadioButtonId == R.id.stopAfterMinutes) {
            startTotalTimer(totalTimeRemaining)
        }
    }

    private fun stopTimer() {
        isRunning = false
        isPaused = false
        pauseResumeButton.text = getString(R.string.start)
        countDownTimer?.cancel()
        totalTimer?.cancel()
        trackingTimer?.cancel()
        countDownTimer = null
        totalTimer = null
        trackingTimer = null
        remainingTime = (interval1 * 1000).toLong()
        totalTimeElapsed = 0
        iterationCount = 0
        updateDisplay()
    }

    private fun createTimer(duration: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
            }

            override fun onFinish() {
                if (currentInterval == interval2) {
                    iterationCount++
                }
                playBeeps()

                if (stopConditionType.checkedRadioButtonId == R.id.stopAfterIterations && iterationCount >= totalDuration) {
                    stopTimer()
                    return
                }

                currentInterval = if (currentInterval == interval1) interval2 else interval1

                remainingTime = currentInterval * 1000L

                if (isRunning) {
                    createTimer(remainingTime)
                }
            }
        }.start()
    }

    private fun playBeeps() {
        val beepTimer = object : CountDownTimer(1500, 500) {
            override fun onTick(millisUntilFinished: Long) {
                toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
            }

            override fun onFinish() {}
        }
        beepTimer.start()
    }

    private fun startTrackingTimer() {
        trackingTimer?.cancel()
        trackingTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isRunning && !isPaused) {
                    totalTimeElapsed += 1000
                    updateDisplay()
                }
            }

            override fun onFinish() {}
        }.start()
    }

    private fun startTotalTimer(duration: Long) {
        totalTimer?.cancel()
        totalTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                totalTimeRemaining = millisUntilFinished
            }

            override fun onFinish() {
                playBeeps()
                stopTimer()
            }
        }.start()
    }

    private fun updateDisplay() {
        val seconds = (remainingTime / 1000).toInt()
        val minutes = seconds / 60
        val secs = seconds % 60
        timerText.text = getString(R.string.time_format, minutes, secs)
        intervalText.text = getString(R.string.interval_label, currentInterval)

        val totalSeconds = (totalTimeElapsed / 1000).toInt()
        val totalMinutes = totalSeconds / 60
        val totalSecs = totalSeconds % 60
        totalTimeText.text = getString(R.string.total_time_label, totalMinutes, totalSecs)
        totalIterationsText.text = getString(R.string.total_iterations_label, iterationCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        totalTimer?.cancel()
        trackingTimer?.cancel()
        toneGenerator.release()
    }
}
