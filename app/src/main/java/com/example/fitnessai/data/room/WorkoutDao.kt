package com.example.fitnessai.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Query("SELECT * FROM workout_logs WHERE exercise = :exercise ORDER BY timestamp ASC")
    fun getLogsForExercise(exercise: String): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLogs(logs: List<WorkoutLogEntity>): List<Long>

    @Delete
    suspend fun deleteWorkoutLog(log: WorkoutLogEntity)

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkoutLogById(id: Long)

    @Query("SELECT DISTINCT exercise FROM workout_logs ORDER BY exercise ASC")
    fun getDistinctExercises(): Flow<List<String>>

    @Query("SELECT MAX(weightKg) FROM workout_logs WHERE exercise = :exercise")
    suspend fun getMaxWeightForExercise(exercise: String): Float?
}
