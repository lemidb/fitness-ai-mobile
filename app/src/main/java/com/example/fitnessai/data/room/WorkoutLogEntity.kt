package com.example.fitnessai.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exercise: String,
    val sets: Int,
    val reps: Int,
    val weightKg: Float,
    val rpe: Float = 7.5f,
    val muscleGroup: String = "Chest",
    val exerciseType: String = "strength",
    val notes: String = "",
    val volume: Float = sets * reps * weightKg,
    val oneRm: Float = if (reps > 0) weightKg * (1f + reps / 30f) else weightKg,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
    val timestamp: Long = System.currentTimeMillis(),
    val mood: Int = 8,
    val energy: Int = 8,
    val sleepQuality: Int = 8
)
