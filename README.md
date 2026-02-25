# Strider: 0 to 5K Trainer

Strider is a specialized training timer designed to help users progress through 0 to 5K running programs. It features a customizable warmup period, alternating running and resting intervals, and flexible stop conditions based on either total duration or number of iterations.

## Core Features

- **Customizable Warmup**: Set a dedicated warmup period in minutes before your intervals begin.
- **Dynamic Interval Training**: 
    - **Interval 1 (Running)**: Configure your high-intensity phase in seconds.
    - **Interval 2 (Rest/Walking)**: Configure your recovery phase in seconds.
- **Flexible Stop Conditions**: 
    - **Time-Based**: End your session automatically after a set number of total minutes.
    - **Iteration-Based**: End your session after completing a specific number of full run/walk cycles.
- **Real-time Progress Tracking**:
    - **Large Timer Display**: High visibility for your current interval's remaining time.
    - **Total Elapsed Time**: Keeps track of your overall workout duration.
    - **Iteration Counter**: Shows exactly how many intervals you've completed.
- **Audio Cues**: 
    - High-frequency beep alerts at the end of every interval.
    - Motivational "GO!", "REST", and "TA-DA!" text indicators.
- **User Interface**: 
    - Vibrant orange (`#FF9800`) action bar for high visibility.
    - Intuitive input fields and radio buttons for quick session configuration.

## How it Works

1.  **Configuration**: Enter your desired parameters in the input fields. 
2.  **Saving Settings**: Tapping the **SAVE SETTINGS** button resets the app to a clean state with your new values.
3.  **Warmup Phase**: When you tap **START**, the timer begins the countdown for your warmup period (if configured).
4.  **Interval Loops**: After the warmup, the app automatically transitions between the Running and Rest intervals.
5.  **Transitions**: A 2-second buffer occurs between interval changes, displaying "GO" or "REST" and sounding an alert to prepare you for the change in pace.
6.  **Completion**: Once the stop condition is met, the timer stops, sounds a final alert, and displays "TA-DA!" to celebrate your workout completion.

## Technical Details

- **Language**: Kotlin
- **Architecture**: Single Activity (MainActivity) using `CountDownTimer` for precise timekeeping.
- **UI Components**: Material Components (DayNight) with custom Action Bar styling.
- **Audio**: Uses Android `ToneGenerator` (DTMF tones) for lightweight, reliable feedback without requiring external sound files.

## Future Enhancements (Planned)

- **Vibration Alerts**: Haptic feedback for transitions in noisy environments.
- **Voice Navigation**: Spoken cues for current intervals.
- **Preset Programs**: Built-in 0 to 5K week-by-week schedules.
- **Workout History**: Local database to track and view past sessions.
- **Background Support**: Continued timer operation and notifications when the screen is off or app is minimized.
