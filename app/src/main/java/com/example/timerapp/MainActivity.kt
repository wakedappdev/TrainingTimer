package com.example.timerapp

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    private lateinit var timerText: TextView
    private lateinit var intervalText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var totalIterationsText: TextView
    private lateinit var pauseResumeButton: Button
    private lateinit var stopButton: Button
    private lateinit var interval1Input: EditText
    private lateinit var interval2Input: EditText
    private lateinit var warmupInput: EditText
    private lateinit var totalDurationInput: EditText
    private lateinit var saveIntervalsButton: Button
    private lateinit var stopConditionType: RadioGroup

    private var countDownTimer: CountDownTimer? = null
    private var totalTimer: CountDownTimer? = null
    private var trackingTimer: CountDownTimer? = null
    private var isRunning = false
    private var isPaused = false
    private var isInChange = false
    private var isWarmup = true
    private var warmupDuration = 5 // Minutes
    private var interval1 = 60 // Seconds
    private var interval2 = 120 // Seconds
    private var totalDuration = 25 // Minutes
    private var currentInterval = interval1
    private var remainingTime = warmupDuration * 60 * 1000
    private var totalTimeRemaining = 0L
    private var totalTimeElapsed = 0L
    private var iterationCount = 0

    private lateinit var toneGenerator: ToneGenerator
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Set the full app name in the Action Bar/Title
        title = getString(R.string.full_app_name)

        timerText = findViewById(R.id.timerText)
        intervalText = findViewById(R.id.intervalText)
        totalTimeText = findViewById(R.id.totalTimeText)
        totalIterationsText = findViewById(R.id.totalIterationsText)
        pauseResumeButton = findViewById(R.id.pauseResumeButton)
        stopButton = findViewById(R.id.stopButton)
        interval1Input = findViewById(R.id.interval1Input)
        interval2Input = findViewById(R.id.interval2Input)
        warmupInput = findViewById(R.id.warmupInput)
        totalDurationInput = findViewById(R.id.totalDurationInput)
        saveIntervalsButton = findViewById(R.id.saveIntervalsButton)
        stopConditionType = findViewById(R.id.stopConditionType)

        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

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
            resetTimer()
        }

        saveIntervalsButton.setOnClickListener {
            val newWarmup = warmupInput.text.toString().toIntOrNull()
            val newInterval1 = interval1Input.text.toString().toIntOrNull()
            val newInterval2 = interval2Input.text.toString().toIntOrNull()
            val newTotalDuration = totalDurationInput.text.toString().toIntOrNull()

            if (newWarmup != null && newInterval1 != null && newInterval2 != null && newTotalDuration != null) {
                warmupDuration = newWarmup
                interval1 = newInterval1
                interval2 = newInterval2
                totalDuration = newTotalDuration
                currentInterval = warmupDuration
                remainingTime = currentInterval
                resetTimer()
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
        resetTimer()
        isRunning = true
        isPaused = false
        pauseResumeButton.text = getString(R.string.pause)
        totalTimeElapsed = 0L
        iterationCount = 0
        totalTimeRemaining = totalDuration * 60 * 1000L

        if (warmupDuration > 0) {
            isWarmup = true
            remainingTime = warmupDuration * 60 * 1000
        } else {
            isWarmup = false
            currentInterval = interval1
            remainingTime = currentInterval * 1000
        }
        
        updateDisplay()
        createTimer(remainingTime.toLong())
        startTrackingTimer()

        if (stopConditionType.checkedRadioButtonId == R.id.stopAfterMinutes) {
            startTotalTimer(totalTimeRemaining + (if (isWarmup) remainingTime else 0))
        }
    }

    private fun pauseTimer() {
        if (!isRunning || isPaused) return
        isPaused = true
        pauseResumeButton.text = getString(R.string.resume)
        countDownTimer?.cancel()
        trackingTimer?.cancel()
        totalTimer?.cancel()
        handler.removeCallbacksAndMessages(null)

        if (isInChange) {
            isInChange = false
            // We paused during change transition. Setup for the next phase.
            if (isWarmup) {
                isWarmup = false
                currentInterval = interval1
            } else {
                currentInterval = if (currentInterval == interval1) interval2 else interval1
                if (currentInterval == interval2) {
                    iterationCount++
                }
            }
            remainingTime = if (isWarmup) (warmupDuration * 60 * 1000) else (currentInterval * 1000)
            updateDisplay()
        }
    }

    private fun resumeTimer() {
        if (!isRunning || !isPaused) return
        isPaused = false
        pauseResumeButton.text = getString(R.string.pause)
        createTimer(remainingTime.toLong())
        startTrackingTimer()
        if (stopConditionType.checkedRadioButtonId == R.id.stopAfterMinutes) {
            startTotalTimer(totalTimeRemaining)
        }
    }

    private fun stopTimer() {
        isRunning = false
        isPaused = false
        isInChange = false
        isWarmup = false
        handler.removeCallbacksAndMessages(null)
        pauseResumeButton.text = getString(R.string.start)
        countDownTimer?.cancel()
        totalTimer?.cancel()
        trackingTimer?.cancel()
        countDownTimer = null
        isInChange = true
        timerText.text = getString(R.string.ta_da)
        updateDisplay()
    }

    private fun resetTimer() {
        isRunning = false
        isPaused = false
        isInChange = false
        isWarmup = false
        handler.removeCallbacksAndMessages(null)
        pauseResumeButton.text = getString(R.string.start)
        countDownTimer?.cancel()
        totalTimer?.cancel()
        trackingTimer?.cancel()
        countDownTimer = null
        totalTimer = null
        trackingTimer = null
        totalTimeElapsed = 0
        iterationCount = 0
        currentInterval = interval1
        remainingTime = if (warmupDuration > 0) (warmupDuration * 60 * 1000) else (interval1 * 1000)
        updateDisplay()
    }

    private fun createTimer(duration: Long) {
        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(duration, 100) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished.toInt()
                updateDisplay()
            }

            override fun onFinish() {
                playBeeps()

                if (!isWarmup && currentInterval == interval2
                    && stopConditionType.checkedRadioButtonId == R.id.stopAfterIterations
                    && iterationCount >= (totalDuration - 1)) {

                    iterationCount++
                    stopTimer()
                    return
                }

                isInChange = true
                if (isWarmup) {
                    timerText.text = getString(R.string.go)
                } else {
                    if (currentInterval == interval2) {
                        timerText.text = getString(R.string.go)
                    } else {
                        timerText.text = getString(R.string.rest)
                    }
                }
                updateDisplay()

                handler.postDelayed({
                    isInChange = false
                    if (isRunning && !isPaused) {
                        if (isWarmup) {
                            startIntervals()
                        } else {
                            startNextInterval()
                        }
                    }
                }, 2000)
            }
        }.start()
    }

    private fun startIntervals() {
        isWarmup = false
        currentInterval = interval1
        remainingTime = currentInterval * 1000
        updateDisplay()
        if (isRunning) {
            createTimer(remainingTime.toLong())
        }
    }

    private fun startNextInterval() {
        if (currentInterval == interval2) {
            iterationCount++
            currentInterval = interval1
        } else {
            currentInterval = interval2
        }
        remainingTime = currentInterval * 1000
        updateDisplay()

        if (isRunning) {
            createTimer(remainingTime.toLong())
        }
    }

    private fun playBeeps() {
        val beepTimer = object : CountDownTimer(1500, 500) {
            override fun onTick(millisUntilFinished: Long) {
                toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 200)
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
        if (!isInChange) {
            val seconds = ceil(remainingTime / 1000.0).toInt()
            val minutes = seconds / 60
            val secs = seconds % 60
            timerText.text = getString(R.string.time_format, minutes, secs)
        }
        
        if (isWarmup) {
            intervalText.text = getString(R.string.warmup)
        } else if (currentInterval == interval1){
            intervalText.text = getString(R.string.running_interval_label)
        } else {
            intervalText.text = getString(R.string.rest_interval_label)
        }

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
        handler.removeCallbacksAndMessages(null)
        toneGenerator.release()
    }
}
