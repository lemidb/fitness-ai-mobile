package com.example.fitnessai.model

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ExerciseType(val value: String, val displayName: String) {
    @SerializedName("strength") STRENGTH("strength", "Strength"),
    @SerializedName("cardio") CARDIO("cardio", "Cardio"),
    @SerializedName("flexibility") FLEXIBILITY("flexibility", "Flexibility"),
    @SerializedName("mixed") MIXED("mixed", "Mixed");

    companion object {
        fun fromString(type: String?): ExerciseType {
            return entries.firstOrNull { it.value.equals(type, ignoreCase = true) } ?: STRENGTH
        }
    }
}

enum class MuscleGroup(val value: String, val displayName: String) {
    @SerializedName("chest") CHEST("chest", "Chest"),
    @SerializedName("back") BACK("back", "Back"),
    @SerializedName("legs") LEGS("legs", "Legs"),
    @SerializedName("shoulders") SHOULDERS("shoulders", "Shoulders"),
    @SerializedName("arms") ARMS("arms", "Arms"),
    @SerializedName("core") CORE("core", "Core"),
    @SerializedName("full_body") FULL_BODY("full_body", "Full Body");

    companion object {
        fun fromString(group: String?): MuscleGroup {
            return entries.firstOrNull { it.value.equals(group, ignoreCase = true) } ?: FULL_BODY
        }
    }
}

enum class UnitSystem(val label: String, val conversionFactor: Float) {
    KG("kg", 1.0f),
    LBS("lbs", 2.20462f)
}

data class WorkoutEntry(
    val id: String = UUID.randomUUID().toString(),
    @SerializedName("exercise") val exercise: String = "",
    @SerializedName("sets") val sets: Int = 3,
    @SerializedName("reps") val reps: Int = 10,
    @SerializedName("weight_kg") val weightKg: Float = 60.0f,
    @SerializedName("rpe") val rpe: Float = 7.0f,
    @SerializedName("exercise_type") val exerciseType: ExerciseType = ExerciseType.STRENGTH,
    @SerializedName("muscle_group") val muscleGroup: MuscleGroup = MuscleGroup.CHEST,
    @SerializedName("notes") val notes: String = "",
    @SerializedName("form_score") val formScore: Int? = null,
    @SerializedName("range_of_motion") val rangeOfMotion: Int? = null,
    @SerializedName("device_data") val deviceData: String = ""
) {
    val volume: Float
        get() = sets * reps * weightKg

    val estimated1RM: Float
        get() = if (reps > 0) weightKg * (1f + reps / 30f) else weightKg
}

data class SessionMetadata(
    @SerializedName("mood") val mood: Int = 7,
    @SerializedName("energy") val energy: Int = 7,
    @SerializedName("sleep_quality") val sleepQuality: Int = 7,
    @SerializedName("bodyweight_kg") val bodyweightKg: Float? = null,
    @SerializedName("notes") val notes: String = ""
)

data class WorkoutLogRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("entries") val entries: List<WorkoutEntry>,
    @SerializedName("session_metadata") val sessionMetadata: SessionMetadata
)

data class EntryInsight(
    @SerializedName("exercise") val exercise: String,
    @SerializedName("is_pr") val isPr: Boolean = false,
    @SerializedName("prev_max_kg") val prevMaxKg: Float = 0f,
    @SerializedName("form_quality") val formQuality: String = "good",
    @SerializedName("suggestion") val suggestion: String = ""
)

data class WorkoutLogResponse(
    @SerializedName("status") val status: String = "success",
    @SerializedName("logged_entries") val loggedEntries: Int,
    @SerializedName("total_volume") val totalVolume: Float,
    @SerializedName("insights") val insights: List<EntryInsight>,
    @SerializedName("timestamp") val timestamp: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
)

data class HistoryRecord(
    val id: String = UUID.randomUUID().toString(),
    @SerializedName("date") val date: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("exercise") val exercise: String,
    @SerializedName("exercise_type") val exerciseType: String,
    @SerializedName("muscle_group") val muscleGroup: String,
    @SerializedName("sets") val sets: Int,
    @SerializedName("reps") val reps: Int,
    @SerializedName("weight_kg") val weightKg: Float,
    @SerializedName("rpe") val rpe: Float,
    @SerializedName("volume") val volume: Float,
    @SerializedName("one_rm") val oneRm: Float,
    @SerializedName("mood") val mood: Int,
    @SerializedName("energy") val energy: Int,
    @SerializedName("sleep_quality") val sleepQuality: Int,
    @SerializedName("notes") val notes: String = "",
    @SerializedName("form_score") val formScore: String = "",
    @SerializedName("range_of_motion") val rangeOfMotion: String = "",
    @SerializedName("device_data") val deviceData: String = "",
    @SerializedName("timestamp") val timestamp: String = ""
)

data class PersonalRecord(
    @SerializedName("exercise") val exercise: String,
    @SerializedName("type") val type: String, // weight_pr, volume_pr, one_rm_pr
    @SerializedName("value") val value: Float,
    @SerializedName("date") val date: String,
    @SerializedName("reps") val reps: Int? = null
)

data class TrendInfo(
    @SerializedName("direction") val direction: String, // improving, maintaining, declining, insufficient_data
    @SerializedName("slope") val slope: Float,
    @SerializedName("projected_weight") val projectedWeight: Float?
)

data class DateRange(
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String
)

data class ProgressResponse(
    @SerializedName("exercise") val exercise: String,
    @SerializedName("date_range") val dateRange: DateRange,
    @SerializedName("total_workouts") val totalWorkouts: Int,
    @SerializedName("max_weight") val maxWeight: Float,
    @SerializedName("avg_weight") val avgWeight: Float,
    @SerializedName("max_volume") val maxVolume: Float,
    @SerializedName("avg_volume") val avgVolume: Float,
    @SerializedName("trend") val trend: TrendInfo,
    @SerializedName("prs") val prs: List<PersonalRecord>
)

data class CalendarAiInsights(
    @SerializedName("focus_muscles") val focusMuscles: List<String> = emptyList(),
    @SerializedName("suggested_warmup") val suggestedWarmup: List<String> = emptyList(),
    @SerializedName("warm_up_time_min") val warmUpTimeMin: Int = 10,
    @SerializedName("estimated_duration_min") val estimatedDurationMin: Int = 60,
    @SerializedName("type") val type: String = "strength"
)

data class CalendarEvent(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("start") val start: String,
    @SerializedName("end") val end: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("location") val location: String = "",
    @SerializedName("type") val type: String = "strength",
    @SerializedName("estimated_duration_min") val estimatedDurationMin: Int = 60,
    @SerializedName("estimated_volume") val estimatedVolume: Int = 0,
    @SerializedName("ai_insights") val aiInsights: CalendarAiInsights = CalendarAiInsights()
)

data class ScheduleSyncResponse(
    @SerializedName("status") val status: String = "success",
    @SerializedName("schedule") val schedule: List<CalendarEvent> = emptyList(),
    @SerializedName("total_workouts") val totalWorkouts: Int = 0,
    @SerializedName("sync_timestamp") val syncTimestamp: String = ""
)

data class PredictionResult(
    @SerializedName("exercise") val exercise: String,
    @SerializedName("predicted_weight") val predictedWeight: Float,
    @SerializedName("predicted_reps") val predictedReps: Int,
    @SerializedName("predicted_rpe") val predictedRpe: Float,
    @SerializedName("confidence") val confidence: Float
)

data class FormAnalysisResult(
    @SerializedName("exercise") val exercise: String,
    @SerializedName("form_score") val formScore: Int,
    @SerializedName("range_of_motion") val rangeOfMotion: Int,
    @SerializedName("feedback") val feedback: String,
    @SerializedName("status") val status: String = "success"
)

data class UserProfile(
    @SerializedName("user_id") val userId: String = "user-demo-uuid",
    @SerializedName("username") val username: String = "GymAthlete",
    @SerializedName("email") val email: String = "athlete@fitness-ai.com"
)
