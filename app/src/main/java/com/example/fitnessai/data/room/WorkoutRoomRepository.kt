package com.example.fitnessai.data.room

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class WorkoutRoomRepository(context: Context) {
    private val database = FitnessRoomDatabase.getDatabase(context)
    private val workoutDao = database.workoutDao()

    val allWorkouts: Flow<List<WorkoutLogEntity>> = workoutDao.getAllWorkoutLogs()
    val distinctExercises: Flow<List<String>> = workoutDao.getDistinctExercises()

    fun getLogsForExercise(exercise: String): Flow<List<WorkoutLogEntity>> {
        return workoutDao.getLogsForExercise(exercise)
    }

    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long = withContext(Dispatchers.IO) {
        workoutDao.insertWorkoutLog(log)
    }

    suspend fun insertWorkoutLogs(logs: List<WorkoutLogEntity>): List<Long> = withContext(Dispatchers.IO) {
        workoutDao.insertWorkoutLogs(logs)
    }

    suspend fun deleteWorkoutLogById(id: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteWorkoutLogById(id)
    }

    suspend fun getMaxWeightForExercise(exercise: String): Float? = withContext(Dispatchers.IO) {
        workoutDao.getMaxWeightForExercise(exercise)
    }

    suspend fun checkAndSeedIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val current = allWorkouts.first()
            if (current.isEmpty()) {
                val initial = listOf(
                    WorkoutLogEntity(
                        exercise = "Bench Press",
                        sets = 4,
                        reps = 8,
                        weightKg = 77.5f,
                        rpe = 7.5f,
                        muscleGroup = "Chest",
                        notes = "Explosive concentric phase",
                        mood = 8,
                        energy = 8,
                        sleepQuality = 8
                    ),
                    WorkoutLogEntity(
                        exercise = "Squat",
                        sets = 4,
                        reps = 6,
                        weightKg = 92.5f,
                        rpe = 8.0f,
                        muscleGroup = "Legs",
                        notes = "Hit depth comfortably",
                        mood = 8,
                        energy = 7,
                        sleepQuality = 8
                    ),
                    WorkoutLogEntity(
                        exercise = "Deadlift",
                        sets = 3,
                        reps = 5,
                        weightKg = 105f,
                        rpe = 8.5f,
                        muscleGroup = "Back",
                        notes = "Form solid throughout",
                        mood = 9,
                        energy = 8,
                        sleepQuality = 8
                    )
                )
                workoutDao.insertWorkoutLogs(initial)
            }
        } catch (_: Exception) {
            // Ignore seeding errors
        }
    }
}
