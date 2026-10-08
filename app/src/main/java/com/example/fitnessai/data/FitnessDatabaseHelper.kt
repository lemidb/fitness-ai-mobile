package com.example.fitnessai.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fitnessai.model.CalendarAiInsights
import com.example.fitnessai.model.CalendarEvent
import com.example.fitnessai.model.HistoryRecord
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class FitnessDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val gson = Gson()

    companion object {
        const val DATABASE_NAME = "fitness_ai.db"
        const val DATABASE_VERSION = 1

        const val TABLE_HISTORY = "history_records"
        const val COL_ID = "id"
        const val COL_DATE = "date"
        const val COL_USER_ID = "user_id"
        const val COL_EXERCISE = "exercise"
        const val COL_EXERCISE_TYPE = "exercise_type"
        const val COL_MUSCLE_GROUP = "muscle_group"
        const val COL_SETS = "sets"
        const val COL_REPS = "reps"
        const val COL_WEIGHT_KG = "weight_kg"
        const val COL_RPE = "rpe"
        const val COL_VOLUME = "volume"
        const val COL_ONE_RM = "one_rm"
        const val COL_MOOD = "mood"
        const val COL_ENERGY = "energy"
        const val COL_SLEEP_QUALITY = "sleep_quality"
        const val COL_NOTES = "notes"
        const val COL_FORM_SCORE = "form_score"
        const val COL_RANGE_OF_MOTION = "range_of_motion"
        const val COL_DEVICE_DATA = "device_data"
        const val COL_TIMESTAMP = "timestamp"

        const val TABLE_CALENDAR = "calendar_events"
        const val COL_CAL_ID = "id"
        const val COL_CAL_TITLE = "title"
        const val COL_CAL_START = "start_time"
        const val COL_CAL_END = "end_time"
        const val COL_CAL_DESC = "description"
        const val COL_CAL_LOCATION = "location"
        const val COL_CAL_TYPE = "type"
        const val COL_CAL_DURATION = "duration_min"
        const val COL_CAL_VOLUME = "est_volume"
        const val COL_CAL_INSIGHTS_JSON = "insights_json"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createHistoryTable = """
            CREATE TABLE $TABLE_HISTORY (
                $COL_ID TEXT PRIMARY KEY,
                $COL_DATE TEXT,
                $COL_USER_ID TEXT,
                $COL_EXERCISE TEXT,
                $COL_EXERCISE_TYPE TEXT,
                $COL_MUSCLE_GROUP TEXT,
                $COL_SETS INTEGER,
                $COL_REPS INTEGER,
                $COL_WEIGHT_KG REAL,
                $COL_RPE REAL,
                $COL_VOLUME REAL,
                $COL_ONE_RM REAL,
                $COL_MOOD INTEGER,
                $COL_ENERGY INTEGER,
                $COL_SLEEP_QUALITY INTEGER,
                $COL_NOTES TEXT,
                $COL_FORM_SCORE TEXT,
                $COL_RANGE_OF_MOTION TEXT,
                $COL_DEVICE_DATA TEXT,
                $COL_TIMESTAMP TEXT
            )
        """.trimIndent()

        val createCalendarTable = """
            CREATE TABLE $TABLE_CALENDAR (
                $COL_CAL_ID TEXT PRIMARY KEY,
                $COL_CAL_TITLE TEXT,
                $COL_CAL_START TEXT,
                $COL_CAL_END TEXT,
                $COL_CAL_DESC TEXT,
                $COL_CAL_LOCATION TEXT,
                $COL_CAL_TYPE TEXT,
                $COL_CAL_DURATION INTEGER,
                $COL_CAL_VOLUME INTEGER,
                $COL_CAL_INSIGHTS_JSON TEXT
            )
        """.trimIndent()

        db.execSQL(createHistoryTable)
        db.execSQL(createCalendarTable)
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CALENDAR")
        onCreate(db)
    }

    fun insertHistoryRecord(record: HistoryRecord) {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID, record.id)
            put(COL_DATE, record.date)
            put(COL_USER_ID, record.userId)
            put(COL_EXERCISE, record.exercise)
            put(COL_EXERCISE_TYPE, record.exerciseType)
            put(COL_MUSCLE_GROUP, record.muscleGroup)
            put(COL_SETS, record.sets)
            put(COL_REPS, record.reps)
            put(COL_WEIGHT_KG, record.weightKg)
            put(COL_RPE, record.rpe)
            put(COL_VOLUME, record.volume)
            put(COL_ONE_RM, record.oneRm)
            put(COL_MOOD, record.mood)
            put(COL_ENERGY, record.energy)
            put(COL_SLEEP_QUALITY, record.sleepQuality)
            put(COL_NOTES, record.notes)
            put(COL_FORM_SCORE, record.formScore)
            put(COL_RANGE_OF_MOTION, record.rangeOfMotion)
            put(COL_DEVICE_DATA, record.deviceData)
            put(COL_TIMESTAMP, record.timestamp)
        }
        db.insertWithOnConflict(TABLE_HISTORY, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getAllHistory(): List<HistoryRecord> {
        val list = mutableListOf<HistoryRecord>()
        val db = readableDatabase
        val cursor = db.query(TABLE_HISTORY, null, null, null, null, null, "$COL_DATE DESC, $COL_TIMESTAMP DESC")
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    HistoryRecord(
                        id = it.getString(it.getColumnIndexOrThrow(COL_ID)),
                        date = it.getString(it.getColumnIndexOrThrow(COL_DATE)),
                        userId = it.getString(it.getColumnIndexOrThrow(COL_USER_ID)),
                        exercise = it.getString(it.getColumnIndexOrThrow(COL_EXERCISE)),
                        exerciseType = it.getString(it.getColumnIndexOrThrow(COL_EXERCISE_TYPE)),
                        muscleGroup = it.getString(it.getColumnIndexOrThrow(COL_MUSCLE_GROUP)),
                        sets = it.getInt(it.getColumnIndexOrThrow(COL_SETS)),
                        reps = it.getInt(it.getColumnIndexOrThrow(COL_REPS)),
                        weightKg = it.getFloat(it.getColumnIndexOrThrow(COL_WEIGHT_KG)),
                        rpe = it.getFloat(it.getColumnIndexOrThrow(COL_RPE)),
                        volume = it.getFloat(it.getColumnIndexOrThrow(COL_VOLUME)),
                        oneRm = it.getFloat(it.getColumnIndexOrThrow(COL_ONE_RM)),
                        mood = it.getInt(it.getColumnIndexOrThrow(COL_MOOD)),
                        energy = it.getInt(it.getColumnIndexOrThrow(COL_ENERGY)),
                        sleepQuality = it.getInt(it.getColumnIndexOrThrow(COL_SLEEP_QUALITY)),
                        notes = it.getString(it.getColumnIndexOrThrow(COL_NOTES)) ?: "",
                        formScore = it.getString(it.getColumnIndexOrThrow(COL_FORM_SCORE)) ?: "",
                        rangeOfMotion = it.getString(it.getColumnIndexOrThrow(COL_RANGE_OF_MOTION)) ?: "",
                        deviceData = it.getString(it.getColumnIndexOrThrow(COL_DEVICE_DATA)) ?: "",
                        timestamp = it.getString(it.getColumnIndexOrThrow(COL_TIMESTAMP)) ?: ""
                    )
                )
            }
        }
        return list
    }

    fun getCalendarEvents(): List<CalendarEvent> {
        val list = mutableListOf<CalendarEvent>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CALENDAR, null, null, null, null, null, "$COL_CAL_START ASC")
        cursor.use {
            while (it.moveToNext()) {
                val json = it.getString(it.getColumnIndexOrThrow(COL_CAL_INSIGHTS_JSON))
                val insights = try {
                    gson.fromJson(json, CalendarAiInsights::class.java) ?: CalendarAiInsights()
                } catch (_: Exception) {
                    CalendarAiInsights()
                }
                list.add(
                    CalendarEvent(
                        id = it.getString(it.getColumnIndexOrThrow(COL_CAL_ID)),
                        title = it.getString(it.getColumnIndexOrThrow(COL_CAL_TITLE)),
                        start = it.getString(it.getColumnIndexOrThrow(COL_CAL_START)),
                        end = it.getString(it.getColumnIndexOrThrow(COL_CAL_END)),
                        description = it.getString(it.getColumnIndexOrThrow(COL_CAL_DESC)) ?: "",
                        location = it.getString(it.getColumnIndexOrThrow(COL_CAL_LOCATION)) ?: "",
                        type = it.getString(it.getColumnIndexOrThrow(COL_CAL_TYPE)) ?: "strength",
                        estimatedDurationMin = it.getInt(it.getColumnIndexOrThrow(COL_CAL_DURATION)),
                        estimatedVolume = it.getInt(it.getColumnIndexOrThrow(COL_CAL_VOLUME)),
                        aiInsights = insights
                    )
                )
            }
        }
        return list
    }

    fun saveCalendarEvents(events: List<CalendarEvent>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CALENDAR, null, null)
            for (event in events) {
                val cv = ContentValues().apply {
                    put(COL_CAL_ID, event.id)
                    put(COL_CAL_TITLE, event.title)
                    put(COL_CAL_START, event.start)
                    put(COL_CAL_END, event.end)
                    put(COL_CAL_DESC, event.description)
                    put(COL_CAL_LOCATION, event.location)
                    put(COL_CAL_TYPE, event.type)
                    put(COL_CAL_DURATION, event.estimatedDurationMin)
                    put(COL_CAL_VOLUME, event.estimatedVolume)
                    put(COL_CAL_INSIGHTS_JSON, gson.toJson(event.aiInsights))
                }
                db.insertWithOnConflict(TABLE_CALENDAR, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val isoSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val cal = Calendar.getInstance()

        // Generate 12 past training records for Bench Press, Squat, Deadlift to showcase real charts
        val pastSessions = listOf(
            Triple(28, "Bench Press", Triple(70f, 4, 8)),
            Triple(24, "Squat", Triple(85f, 4, 6)),
            Triple(21, "Deadlift", Triple(95f, 3, 5)),
            Triple(18, "Bench Press", Triple(72.5f, 4, 8)),
            Triple(14, "Squat", Triple(90f, 4, 6)),
            Triple(12, "Bench Press", Triple(75f, 4, 7)),
            Triple(9, "Deadlift", Triple(100f, 4, 5)),
            Triple(7, "Bench Press", Triple(77.5f, 4, 8)),
            Triple(5, "Squat", Triple(95f, 4, 6)),
            Triple(3, "Deadlift", Triple(105f, 4, 5)),
            Triple(2, "Bench Press", Triple(80f, 4, 8)),
            Triple(1, "Overhead Press", Triple(47.5f, 4, 6))
        )

        for ((daysAgo, exercise, lift) in pastSessions) {
            val (weight, sets, reps) = lift
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dateStr = sdf.format(cal.time)
            val isoStr = isoSdf.format(cal.time)
            val volume = sets * reps * weight
            val oneRm = weight * (1f + reps / 30f)

            val cv = ContentValues().apply {
                put(COL_ID, UUID.randomUUID().toString())
                put(COL_DATE, dateStr)
                put(COL_USER_ID, "user-athlete-1")
                put(COL_EXERCISE, exercise)
                put(COL_EXERCISE_TYPE, "strength")
                put(COL_MUSCLE_GROUP, when (exercise) {
                    "Bench Press" -> "chest"
                    "Squat" -> "legs"
                    "Deadlift" -> "back"
                    else -> "shoulders"
                })
                put(COL_SETS, sets)
                put(COL_REPS, reps)
                put(COL_WEIGHT_KG, weight)
                put(COL_RPE, 7.5f)
                put(COL_VOLUME, volume)
                put(COL_ONE_RM, oneRm)
                put(COL_MOOD, 8)
                put(COL_ENERGY, 8)
                put(COL_SLEEP_QUALITY, 8)
                put(COL_NOTES, "Great barbell progression session")
                put(COL_FORM_SCORE, "8")
                put(COL_RANGE_OF_MOTION, "92")
                put(COL_DEVICE_DATA, "")
                put(COL_TIMESTAMP, isoStr)
            }
            db.insert(TABLE_HISTORY, null, cv)
        }

        // Seed 3 upcoming calendar workout events
        val calSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val futureDays = listOf(
            Triple(1, "Chest & Triceps Hypertrophy", "Gold's Gym"),
            Triple(3, "Leg Day Heavy Squats", "Metro Fitness"),
            Triple(5, "Deadlift & Back Strength", "Iron Athletic Club")
        )

        for ((daysAhead, title, location) in futureDays) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, daysAhead)
            c.set(Calendar.HOUR_OF_DAY, 8)
            c.set(Calendar.MINUTE, 0)
            val startStr = calSdf.format(c.time)
            c.set(Calendar.HOUR_OF_DAY, 9)
            c.set(Calendar.MINUTE, 30)
            val endStr = calSdf.format(c.time)

            val insights = when {
                title.contains("Chest", ignoreCase = true) -> CalendarAiInsights(
                    focusMuscles = listOf("chest", "anterior deltoid", "triceps"),
                    suggestedWarmup = listOf("Band pull-aparts x 20", "Shoulder rotations x 10", "Incline DB fly x 12 (light)"),
                    warmUpTimeMin = 10,
                    estimatedDurationMin = 90,
                    type = "strength"
                )
                title.contains("Leg", ignoreCase = true) -> CalendarAiInsights(
                    focusMuscles = listOf("quadriceps", "glutes", "hamstrings"),
                    suggestedWarmup = listOf("Goblet squats x 10 (light)", "Hip circles x 10", "Leg swings x 10"),
                    warmUpTimeMin = 12,
                    estimatedDurationMin = 90,
                    type = "strength"
                )
                else -> CalendarAiInsights(
                    focusMuscles = listOf("lats", "rhomboids", "biceps", "hamstrings"),
                    suggestedWarmup = listOf("Dead hangs x 20s", "Scapular pull-ups x 10", "Face pulls x 15"),
                    warmUpTimeMin = 10,
                    estimatedDurationMin = 75,
                    type = "strength"
                )
            }

            val cv = ContentValues().apply {
                put(COL_CAL_ID, "cal-event-$daysAhead")
                put(COL_CAL_TITLE, title)
                put(COL_CAL_START, startStr)
                put(COL_CAL_END, endStr)
                put(COL_CAL_DESC, "High intensity barbell training session")
                put(COL_CAL_LOCATION, location)
                put(COL_CAL_TYPE, "strength")
                put(COL_CAL_DURATION, 90)
                put(COL_CAL_VOLUME, 2400)
                put(COL_CAL_INSIGHTS_JSON, gson.toJson(insights))
            }
            db.insert(TABLE_CALENDAR, null, cv)
        }
    }
}
