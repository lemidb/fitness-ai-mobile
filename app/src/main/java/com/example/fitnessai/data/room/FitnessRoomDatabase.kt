package com.example.fitnessai.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WorkoutLogEntity::class], version = 1, exportSchema = false)
abstract class FitnessRoomDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessRoomDatabase? = null

        fun getDatabase(context: Context): FitnessRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessRoomDatabase::class.java,
                    "fitness_room.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
