package com.example.fitnessai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessai.data.FitnessRepository
import com.example.fitnessai.data.room.WorkoutLogEntity
import com.example.fitnessai.data.room.WorkoutRoomRepository
import com.example.fitnessai.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FitnessUiState(
    val history: List<HistoryRecord> = emptyList(),
    val roomLogs: List<WorkoutLogEntity> = emptyList(),
    val schedule: List<CalendarEvent> = emptyList(),
    val activeEntries: List<WorkoutEntry> = listOf(
        WorkoutEntry(exercise = "Bench Press", sets = 4, reps = 8, weightKg = 75f, rpe = 7.5f)
    ),
    val sessionMetadata: SessionMetadata = SessionMetadata(mood = 8, energy = 8, sleepQuality = 8),
    val selectedExercise: String = "Bench Press",
    val selectedDaysFilter: Int = 90,
    val progressData: ProgressResponse? = null,
    val predictionData: PredictionResult? = null,
    val formAnalysisData: FormAnalysisResult? = null,
    val unitSystem: UnitSystem = UnitSystem.KG,
    val lastLogResponse: WorkoutLogResponse? = null,
    val isLogging: Boolean = false,
    val syncStatusMessage: String? = null,
    val userProfile: UserProfile = UserProfile()
)

class FitnessViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FitnessRepository(application)
    private val roomRepository = WorkoutRoomRepository(application)

    private val _uiState = MutableStateFlow(FitnessUiState())
    val uiState: StateFlow<FitnessUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            roomRepository.checkAndSeedIfEmpty()
            roomRepository.allWorkouts.collect { logs ->
                _uiState.value = _uiState.value.copy(roomLogs = logs)
            }
        }
        viewModelScope.launch {
            repository.historyFlow.collect { list ->
                _uiState.value = _uiState.value.copy(history = list)
                updateProgressData(_uiState.value.selectedExercise, _uiState.value.selectedDaysFilter)
            }
        }
        viewModelScope.launch {
            repository.scheduleFlow.collect { events ->
                _uiState.value = _uiState.value.copy(schedule = events)
            }
        }
        updateProgressData("Bench Press", 90)
        requestPrediction("Bench Press")
    }

    fun updateProgressData(exercise: String, days: Int = 90) {
        val progress = repository.getExerciseProgress(exercise, days)
        _uiState.value = _uiState.value.copy(
            selectedExercise = exercise,
            selectedDaysFilter = days,
            progressData = progress
        )
    }

    fun addWorkoutEntry(entry: WorkoutEntry = WorkoutEntry(exercise = "Squat", sets = 3, reps = 10, weightKg = 80f)) {
        val updated = _uiState.value.activeEntries + entry
        _uiState.value = _uiState.value.copy(activeEntries = updated)
    }

    fun updateWorkoutEntry(index: Int, updatedEntry: WorkoutEntry) {
        val list = _uiState.value.activeEntries.toMutableList()
        if (index in list.indices) {
            list[index] = updatedEntry
            _uiState.value = _uiState.value.copy(activeEntries = list)
        }
    }

    fun removeWorkoutEntry(index: Int) {
        val list = _uiState.value.activeEntries.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            if (list.isEmpty()) {
                list.add(WorkoutEntry(exercise = "Bench Press", sets = 3, reps = 10, weightKg = 60f))
            }
            _uiState.value = _uiState.value.copy(activeEntries = list)
        }
    }

    fun updateMetadata(metadata: SessionMetadata) {
        _uiState.value = _uiState.value.copy(sessionMetadata = metadata)
    }

    fun logSingleExerciseToRoom(
        exercise: String,
        sets: Int,
        reps: Int,
        weightKg: Float,
        rpe: Float = 7.5f,
        muscleGroup: String = "Chest",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val entity = WorkoutLogEntity(
                exercise = exercise,
                sets = sets,
                reps = reps,
                weightKg = weightKg,
                rpe = rpe,
                muscleGroup = muscleGroup,
                notes = notes,
                mood = _uiState.value.sessionMetadata.mood,
                energy = _uiState.value.sessionMetadata.energy,
                sleepQuality = _uiState.value.sessionMetadata.sleepQuality
            )
            roomRepository.insertWorkoutLog(entity)

            // Also synchronize with repository analytics
            repository.logWorkout(
                entries = listOf(
                    WorkoutEntry(
                        exercise = exercise,
                        sets = sets,
                        reps = reps,
                        weightKg = weightKg,
                        rpe = rpe,
                        muscleGroup = MuscleGroup.fromString(muscleGroup),
                        notes = notes
                    )
                ),
                metadata = _uiState.value.sessionMetadata
            )
        }
    }

    fun deleteRoomLog(id: Long) {
        viewModelScope.launch {
            roomRepository.deleteWorkoutLogById(id)
        }
    }

    fun submitWorkoutLog() {
        if (_uiState.value.activeEntries.isEmpty()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLogging = true)

            // Persist all active exercises locally into Room Database
            val roomEntities = _uiState.value.activeEntries.map { entry ->
                WorkoutLogEntity(
                    exercise = entry.exercise,
                    sets = entry.sets,
                    reps = entry.reps,
                    weightKg = entry.weightKg,
                    rpe = entry.rpe,
                    muscleGroup = entry.muscleGroup.displayName,
                    exerciseType = entry.exerciseType.value,
                    notes = entry.notes,
                    mood = _uiState.value.sessionMetadata.mood,
                    energy = _uiState.value.sessionMetadata.energy,
                    sleepQuality = _uiState.value.sessionMetadata.sleepQuality
                )
            }
            roomRepository.insertWorkoutLogs(roomEntities)

            val response = repository.logWorkout(
                entries = _uiState.value.activeEntries,
                metadata = _uiState.value.sessionMetadata
            )
            _uiState.value = _uiState.value.copy(
                isLogging = false,
                lastLogResponse = response,
                activeEntries = listOf(
                    WorkoutEntry(exercise = "Bench Press", sets = 3, reps = 10, weightKg = 70f, rpe = 7.5f)
                ),
                sessionMetadata = SessionMetadata(mood = 8, energy = 8, sleepQuality = 8)
            )
        }
    }

    fun dismissSummaryModal() {
        _uiState.value = _uiState.value.copy(lastLogResponse = null)
    }

    fun syncCalendar(daysAhead: Int = 7) {
        viewModelScope.launch {
            val response = repository.syncCalendarSchedule(daysAhead)
            _uiState.value = _uiState.value.copy(
                syncStatusMessage = "Synced ${response.totalWorkouts} workouts from Google Calendar"
            )
        }
    }

    fun clearSyncMessage() {
        _uiState.value = _uiState.value.copy(syncStatusMessage = null)
    }

    fun requestPrediction(exercise: String) {
        val result = repository.predictNextWorkout(exercise)
        _uiState.value = _uiState.value.copy(predictionData = result)
    }

    fun applyPredictionToWorkout() {
        val pred = _uiState.value.predictionData ?: return
        if (pred.predictedWeight <= 0f) return
        val entry = WorkoutEntry(
            exercise = pred.exercise,
            sets = 4,
            reps = pred.predictedReps,
            weightKg = pred.predictedWeight,
            rpe = pred.predictedRpe,
            notes = "From LSTM Prediction (${(pred.confidence * 100).toInt()}% confidence)"
        )
        _uiState.value = _uiState.value.copy(
            activeEntries = listOf(entry) + _uiState.value.activeEntries.filter { !it.exercise.equals(pred.exercise, ignoreCase = true) }
        )
    }

    fun runFormAnalysis(exercise: String, type: ExerciseType) {
        val result = repository.analyzeForm(exercise, type)
        _uiState.value = _uiState.value.copy(formAnalysisData = result)
    }

    fun applyFormAnalysisToWorkout() {
        val fa = _uiState.value.formAnalysisData ?: return
        val current = _uiState.value.activeEntries.toMutableList()
        val index = current.indexOfFirst { it.exercise.equals(fa.exercise, ignoreCase = true) }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(formScore = fa.formScore, rangeOfMotion = fa.rangeOfMotion)
        } else {
            current.add(0, WorkoutEntry(
                exercise = fa.exercise,
                sets = 3,
                reps = 8,
                weightKg = 60f,
                formScore = fa.formScore,
                rangeOfMotion = fa.rangeOfMotion,
                notes = "Form score: ${fa.formScore}/10"
            ))
        }
        _uiState.value = _uiState.value.copy(activeEntries = current)
    }

    fun prefillFromCalendarEvent(event: CalendarEvent) {
        val focusExercises = when {
            event.title.contains("Chest", ignoreCase = true) -> listOf("Bench Press", "Incline Bench Press", "Dumbbell Fly")
            event.title.contains("Back", ignoreCase = true) || event.title.contains("Deadlift", ignoreCase = true) -> listOf("Deadlift", "Barbell Row", "Pull-ups")
            event.title.contains("Leg", ignoreCase = true) || event.title.contains("Squat", ignoreCase = true) -> listOf("Squat", "Leg Press", "Romanian Deadlift")
            else -> listOf("Treadmill Run", "Plank")
        }

        val entries = focusExercises.map { name ->
            val def = ExerciseLibrary.findByName(name)
            WorkoutEntry(
                exercise = name,
                sets = def?.defaultSets ?: 3,
                reps = def?.defaultReps ?: 10,
                weightKg = def?.defaultWeightKg ?: 50f,
                exerciseType = def?.exerciseType ?: ExerciseType.STRENGTH,
                muscleGroup = def?.muscleGroup ?: MuscleGroup.FULL_BODY,
                notes = "Scheduled: ${event.title}"
            )
        }

        _uiState.value = _uiState.value.copy(
            activeEntries = entries,
            sessionMetadata = _uiState.value.sessionMetadata.copy(notes = "Session for ${event.title}")
        )
    }

    fun toggleUnitSystem() {
        val next = if (_uiState.value.unitSystem == UnitSystem.KG) UnitSystem.LBS else UnitSystem.KG
        _uiState.value = _uiState.value.copy(unitSystem = next)
    }

    fun getCsvExportData(): String {
        return repository.exportCsv(_uiState.value.history)
    }
}
