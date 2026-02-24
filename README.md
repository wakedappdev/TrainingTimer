# Cadence Interval Timer

A highly configurable interval timer app for Android, designed for training sessions that require alternating intensity periods.

## Features

- **Warm Up Phase:** Configurable warm-up period (in minutes) to prepare for your session.
- **Alternating Intervals:** Configure "Rep Time" and "Rest Time" (in seconds) that automatically cycle.
- **Flexible Stop Conditions:** Choose to end your session after a specific total time (minutes) or a set number of iterations.
- **Real-Time Tracking:** 
    - Large countdown display for the active interval or warm-up.
    - Status indicators like "Warming Up", "Let's Go", "Easy!!!", and "Ta-da!!!".
    - Total elapsed session time.
    - Iteration counter.
- **Audio Cues:** Distinct beeps signal the end of every interval and the warm-up phase.
- **Screen Management:** Keeps the screen on while the timer is active so you never lose track.
- **Simple Controls:** Pause, Resume, and Reset functionality at your fingertips.

## How to Use

1. **Configure Your Session:**
    - Enter the **Warm Up** time (minutes) to start with.
    - Enter the **Rep Time** (seconds) for your active interval.
    - Enter the **Rest Time** (seconds) for your recovery period.
    - Enter the **Stop after** value and select either **minutes** or **iterations** as your goal.
2. **Apply Settings:**
    - Tap **Save** to update the timer configuration. This will reset the current session.
3. **Control the Timer:**
    - Tap **Start** to begin the session (starting with the Warm Up phase).
    - Use the **Pause/Resume** button to temporarily halt the timer.
    - Tap **Stop** to reset the timer and session statistics.

## Technical Details

- Built with Kotlin and Android Jetpack.
- Uses `CountDownTimer` for precise interval management.
- Employs `ToneGenerator` for low-latency audio feedback.
- Persistent display state using `android:keepScreenOn`.
