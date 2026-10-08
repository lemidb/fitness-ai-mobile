# Fitness AI — Android Mobile App

A modern, high-performance Android fitness tracker and intelligent coaching client built with **Kotlin** and **Jetpack Compose (Material 3)**.

Designed for real-time gym workout tracking, progress analytics, Google Calendar workout scheduling, and AI-powered lifting recommendations.

---

## Features

- **Real-Time Workout Logging:**
  - Fast, one-handed mobile entry between sets for 50+ pre-defined lifts and custom exercises.
  - Live computed Volume (`sets × reps × weight`) and Epley estimated 1RM (`weight × (1 + reps / 30)`).
  - Rate of Perceived Exertion (RPE 1–10) slider with dynamic color indicators.
  - Session readiness metadata: Mood, Energy, and Sleep Quality ratings with emoji gauges.
  - Instant post-workout celebration modal with Personal Record (PR) badges and AI coaching suggestions.

- **Performance Analytics & Interactive Charts:**
  - Linear regression trend analysis (`improving`, `maintaining`, `declining`) calculating progress slope (`kg/session`) and projected weights 7 sessions ahead.
  - Smooth custom Canvas line chart illustrating actual progression alongside dashed neural projection lines.
  - Personal Record (PR) tracking for Weight PR, Volume PR, and 1RM PR.

- **Workout History & CSV Export:**
  - Chronological workout logs with date, muscle group, and exercise filtering.
  - Expandable detail view displaying 1RM, volume, verified form scores, notes, and mood vitals.
  - One-tap CSV export matching the Google Sheets data warehouse format.

- **Google Calendar Schedule Synchronization:**
  - Automated detection of scheduled workouts via keyword filtering (`strength`, `cardio`, `flexibility`).
  - AI warm-up routines tailored by focus muscle groups with drill lists and duration.
  - "Start This Workout" button to instantly initialize logging with pre-filled exercises.

- **AI Intelligence Labs:**
  - **LSTM Predictions:** Next session targeted weight, reps, and RPE recommendations with confidence intervals.
  - **Computer Vision Form Analysis:** Pose kinematics overlay showing joint angles, form scores (0–10), and movement feedback.

- **Customization & Preferences:**
  - Metric (kg) and Imperial (lbs) unit toggling.
  - Custom backend API endpoint configuration for seamless local or remote server sync.

---

## Tech Stack & Architecture

- **Runtime & Language:** Kotlin 2.2.10, JDK 21, Android SDK 36 (targetSdk 36, minSdk 26)
- **UI Toolkit:** Jetpack Compose with Material Design 3
- **State Management:** MVVM architecture with `ViewModel` and `StateFlow`
- **Data Persistence:** SQLite database (`SQLiteOpenHelper`) with seed dataset and offline-first repository
- **Networking:** OkHttp 4.12.0 and Gson 2.11.0 for REST API communication
- **Iconography:** Material Icons Extended & Custom Adaptive Launcher Icon

---

## Project Structure

```
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/fitnessai/
│       │   ├── MainActivity.kt
│       │   ├── model/
│       │   │   ├── Models.kt
│       │   │   └── ExerciseLibrary.kt
│       │   ├── data/
│       │   │   ├── ApiClient.kt
│       │   │   ├── FitnessDatabaseHelper.kt
│       │   │   └── FitnessRepository.kt
│       │   ├── viewmodel/
│       │   │   └── FitnessViewModel.kt
│       │   └── ui/
│       │       ├── theme/
│       │       ├── components/
│       │       └── screens/
│       │           ├── DashboardScreen.kt
│       │           ├── LogWorkoutScreen.kt
│       │           ├── HistoryScreen.kt
│       │           ├── ProgressScreen.kt
│       │           ├── ScheduleScreen.kt
│       │           ├── AiLabsScreen.kt
│       │           └── ProfileScreen.kt
│       └── res/
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── metadata.json
```

---

## Building the App

Run the build via Gradle:

```bash
gradle assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
