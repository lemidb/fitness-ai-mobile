package com.example.fitnessai.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessai.data.room.WorkoutLogEntity
import com.example.fitnessai.model.*
import com.example.fitnessai.ui.components.ExercisePickerDialog
import com.example.fitnessai.ui.components.RpeIndicator
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessUiState
import kotlin.math.roundToInt

@Composable
fun LogWorkoutScreen(
    state: FitnessUiState,
    onAddExercise: (WorkoutEntry) -> Unit,
    onUpdateExercise: (Int, WorkoutEntry) -> Unit,
    onRemoveExercise: (Int) -> Unit,
    onUpdateMetadata: (SessionMetadata) -> Unit,
    onSubmitWorkout: () -> Unit,
    onLogSingleExerciseToRoom: (String, Int, Int, Float, Float, String, String) -> Unit,
    onDeleteRoomLog: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPickerForQuickLog by remember { mutableStateOf(false) }
    var showPickerIndex by remember { mutableStateOf<Int?>(null) }
    var showNewPicker by remember { mutableStateOf(false) }
    var showMetadataSection by remember { mutableStateOf(false) }

    // Quick Input State
    var inputExercise by remember { mutableStateOf("Bench Press") }
    var inputSets by remember { mutableIntStateOf(4) }
    var inputReps by remember { mutableIntStateOf(8) }
    var inputWeightStr by remember { mutableStateOf("75.0") }
    var inputRpe by remember { mutableFloatStateOf(7.5f) }
    var inputMuscleGroup by remember { mutableStateOf("Chest") }
    var inputNotes by remember { mutableStateOf("") }

    val parsedWeight = inputWeightStr.toFloatOrNull() ?: 0f
    val quickVolume = inputSets * inputReps * parsedWeight
    val quickOneRm = if (inputReps > 0) parsedWeight * (1f + inputReps / 30f) else parsedWeight

    val quickLifts = listOf(
        "Bench Press" to "Chest",
        "Squat" to "Legs",
        "Deadlift" to "Back",
        "Overhead Press" to "Shoulders",
        "Barbell Row" to "Back",
        "Pull-ups" to "Back",
        "Dips" to "Chest",
        "Leg Press" to "Legs"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
    ) {
        // Hero Header & Room Badge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("workout_logger_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = AccentGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "ROOM DATABASE",
                                        color = AccentGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "OFFLINE STORAGE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyanLight,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Workout Logger",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(PrimaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Input your exercise details below. Data is committed locally to Room SQLite.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Dedicated Direct Input Section (Exercise, Sets, Reps, Weight)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("exercise_input_form"),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Log Exercise Entry",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(
                            onClick = { showPickerForQuickLog = true },
                            modifier = Modifier.testTag("browse_exercise_library_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Browse All 50+", color = PrimaryCyan, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick-fill Chips
                    Text("Popular Lifts:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickLifts) { (name, group) ->
                            val isSelected = inputExercise.equals(name, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    inputExercise = name
                                    inputMuscleGroup = group
                                    val def = ExerciseLibrary.findByName(name)
                                    if (def != null) {
                                        inputSets = def.defaultSets
                                        inputReps = def.defaultReps
                                        inputWeightStr = "${def.defaultWeightKg}"
                                    }
                                },
                                label = { Text(name, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryCyan,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Exercise Name Input
                    OutlinedTextField(
                        value = inputExercise,
                        onValueChange = { inputExercise = it },
                        label = { Text("Exercise Name") },
                        placeholder = { Text("e.g. Incline Bench Press") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("exercise_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { showPickerForQuickLog = true }) {
                                Icon(Icons.Default.MenuBook, contentDescription = "Pick", tint = PrimaryCyan)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Volume & 1RM Computed Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Scale, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Est. Volume: ${(quickVolume).roundToInt()} kg",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Est. 1RM: ${(quickOneRm * 10).roundToInt() / 10f} kg",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sets, Reps, Weight 3-column input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Sets
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sets", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$inputSets",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        SmallStepperButton("-") { if (inputSets > 1) inputSets-- }
                                        SmallStepperButton("+") { inputSets++ }
                                    }
                                }
                            }
                        }

                        // Reps
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reps", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$inputReps",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        SmallStepperButton("-") { if (inputReps > 1) inputReps-- }
                                        SmallStepperButton("+") { inputReps++ }
                                    }
                                }
                            }
                        }

                        // Weight
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text("Weight (${state.unitSystem.label})", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    OutlinedTextField(
                                        value = inputWeightStr,
                                        onValueChange = { inputWeightStr = it },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("weight_input_field"),
                                        textStyle = LocalTextStyle.current.copy(
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = TextPrimary
                                        ),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent
                                        )
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        SmallStepperButton("-2.5") {
                                            val cur = inputWeightStr.toFloatOrNull() ?: 0f
                                            if (cur >= 2.5f) inputWeightStr = "%.1f".format(cur - 2.5f)
                                        }
                                        SmallStepperButton("+2.5") {
                                            val cur = inputWeightStr.toFloatOrNull() ?: 0f
                                            inputWeightStr = "%.1f".format(cur + 2.5f)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // RPE Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rate of Perceived Exertion (RPE)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        RpeIndicator(rpe = inputRpe)
                    }
                    Slider(
                        value = inputRpe,
                        onValueChange = { inputRpe = (it * 2).roundToInt() / 2f },
                        valueRange = 1f..10f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryCyan,
                            activeTrackColor = PrimaryCyan,
                            inactiveTrackColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notes
                    OutlinedTextField(
                        value = inputNotes,
                        onValueChange = { inputNotes = it },
                        placeholder = { Text("Exercise notes (e.g. paused at bottom, felt explosive)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Commit to Room Database Button
                    Button(
                        onClick = {
                            if (inputExercise.isBlank()) {
                                Toast.makeText(context, "Please enter an exercise name", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val weight = inputWeightStr.toFloatOrNull() ?: 0f
                            onLogSingleExerciseToRoom(
                                inputExercise.trim(),
                                inputSets,
                                inputReps,
                                weight,
                                inputRpe,
                                inputMuscleGroup,
                                inputNotes
                            )
                            Toast.makeText(context, "Saved $inputExercise to Room Database!", Toast.LENGTH_SHORT).show()
                            inputNotes = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_exercise_to_room_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Exercise to Room Database",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Saved Room Database Logs Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(18.dp))
                    Text(
                        text = "Saved in Room Database (${state.roomLogs.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        if (state.roomLogs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No logs in Room database yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Use the form above to log your first exercise set.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(state.roomLogs, key = { it.id }) { log ->
                RoomLogCard(
                    log = log,
                    onDelete = { onDeleteRoomLog(log.id) }
                )
            }
        }

        // Multi-Exercise Session Builder Section Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FULL SESSION BATCH LOG",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyanLight,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${state.activeEntries.size} Exercises in Workout Routine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        IconButton(onClick = { showMetadataSection = !showMetadataSection }) {
                            Icon(
                                imageVector = if (showMetadataSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = PrimaryCyan
                            )
                        }
                    }

                    AnimatedVisibility(visible = showMetadataSection) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetadataSlider(
                                label = "Mood (\uD83D\uDE1E → \uD83D\uDE0A)",
                                value = state.sessionMetadata.mood,
                                onValueChange = { onUpdateMetadata(state.sessionMetadata.copy(mood = it)) }
                            )
                            MetadataSlider(
                                label = "Energy (\uD83D\uDD0B → ⚡)",
                                value = state.sessionMetadata.energy,
                                onValueChange = { onUpdateMetadata(state.sessionMetadata.copy(energy = it)) }
                            )
                            MetadataSlider(
                                label = "Sleep Quality (\uD83D\uDE34 → ✨)",
                                value = state.sessionMetadata.sleepQuality,
                                onValueChange = { onUpdateMetadata(state.sessionMetadata.copy(sleepQuality = it)) }
                            )
                        }
                    }
                }
            }
        }

        // Multi-exercise entries
        itemsIndexed(state.activeEntries) { index, entry ->
            ExerciseEntryCard(
                entry = entry,
                index = index,
                onUpdate = { onUpdateExercise(index, it) },
                onRemove = { onRemoveExercise(index) },
                onChangeExercise = { showPickerIndex = index }
            )
        }

        // Add Another Exercise to session
        item {
            OutlinedButton(
                onClick = { showNewPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("add_exercise_to_session_button"),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Another Exercise to Session", color = PrimaryCyan, fontWeight = FontWeight.Bold)
            }
        }

        // Finish Entire Session
        item {
            Button(
                onClick = onSubmitWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("complete_entire_workout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(16.dp),
                enabled = !state.isLogging && state.activeEntries.isNotEmpty()
            ) {
                if (state.isLogging) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Save All Exercises to Room & Analyze",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // Dialog for Quick Log picker
    if (showPickerForQuickLog) {
        ExercisePickerDialog(
            onSelectExercise = { def ->
                inputExercise = def.name
                inputMuscleGroup = def.muscleGroup.displayName
                inputSets = def.defaultSets
                inputReps = def.defaultReps
                inputWeightStr = "${def.defaultWeightKg}"
            },
            onDismiss = { showPickerForQuickLog = false }
        )
    }

    // Dialog for Session list pickers
    if (showPickerIndex != null) {
        val idx = showPickerIndex!!
        ExercisePickerDialog(
            onSelectExercise = { def ->
                val old = state.activeEntries[idx]
                onUpdateExercise(
                    idx,
                    old.copy(
                        exercise = def.name,
                        muscleGroup = def.muscleGroup,
                        exerciseType = def.exerciseType,
                        weightKg = if (old.weightKg <= 0f) def.defaultWeightKg else old.weightKg
                    )
                )
            },
            onDismiss = { showPickerIndex = null }
        )
    }

    if (showNewPicker) {
        ExercisePickerDialog(
            onSelectExercise = { def ->
                onAddExercise(
                    WorkoutEntry(
                        exercise = def.name,
                        sets = def.defaultSets,
                        reps = def.defaultReps,
                        weightKg = def.defaultWeightKg,
                        muscleGroup = def.muscleGroup,
                        exerciseType = def.exerciseType
                    )
                )
            },
            onDismiss = { showNewPicker = false }
        )
    }
}

@Composable
fun RoomLogCard(
    log: WorkoutLogEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("room_log_item_${log.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = log.exercise,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(
                        color = PrimaryCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = log.muscleGroup,
                            color = PrimaryCyanLight,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete from Room",
                        tint = AccentRose,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${log.sets} sets × ${log.reps} reps @ ${log.weightKg} kg",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = AccentGreen
                )
                RpeIndicator(rpe = log.rpe)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Volume: ${(log.volume).roundToInt()} kg  ·  Est 1RM: ${(log.oneRm * 10).roundToInt() / 10f} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    text = log.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "“${log.notes}”",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun SmallStepperButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 34.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = PrimaryCyan, fontSize = 12.sp)
    }
}

@Composable
fun MetadataSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("$value / 10", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = 1f..10f,
            steps = 8,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryCyan,
                activeTrackColor = PrimaryCyan,
                inactiveTrackColor = DarkBorder
            )
        )
    }
}

@Composable
fun ExerciseEntryCard(
    entry: WorkoutEntry,
    index: Int,
    onUpdate: (WorkoutEntry) -> Unit,
    onRemove: () -> Unit,
    onChangeExercise: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exercise_entry_card_$index"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onChangeExercise() }
                ) {
                    Text(
                        text = if (entry.exercise.isNotBlank()) entry.exercise else "Tap to Select Exercise",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (entry.exercise.isNotBlank()) TextPrimary else PrimaryCyanLight
                    )
                    Text(
                        text = "${entry.muscleGroup.displayName} · ${entry.exerciseType.displayName} (Tap to change)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = AccentRose)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Live Volume: ${(entry.volume).roundToInt()} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryCyanLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Est. 1RM: ${(entry.estimated1RM * 10).roundToInt() / 10f} kg",
                    style = MaterialTheme.typography.labelSmall,
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ValueStepper(
                    label = "SETS",
                    value = entry.sets,
                    onIncrement = { onUpdate(entry.copy(sets = entry.sets + 1)) },
                    onDecrement = { if (entry.sets > 1) onUpdate(entry.copy(sets = entry.sets - 1)) },
                    modifier = Modifier.weight(1f)
                )

                ValueStepper(
                    label = "REPS",
                    value = entry.reps,
                    onIncrement = { onUpdate(entry.copy(reps = entry.reps + 1)) },
                    onDecrement = { if (entry.reps > 1) onUpdate(entry.copy(reps = entry.reps - 1)) },
                    modifier = Modifier.weight(1f)
                )

                WeightStepper(
                    label = "WEIGHT (kg)",
                    weight = entry.weightKg,
                    onIncrement = { onUpdate(entry.copy(weightKg = entry.weightKg + 2.5f)) },
                    onDecrement = { if (entry.weightKg >= 2.5f) onUpdate(entry.copy(weightKg = entry.weightKg - 2.5f)) },
                    modifier = Modifier.weight(1.3f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rate of Exertion", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                RpeIndicator(rpe = entry.rpe)
            }
            Slider(
                value = entry.rpe,
                onValueChange = { onUpdate(entry.copy(rpe = (it * 2).roundToInt() / 2f)) },
                valueRange = 1f..10f,
                steps = 17,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryCyan,
                    activeTrackColor = PrimaryCyan,
                    inactiveTrackColor = DarkBorder
                )
            )
        }
    }
}

@Composable
fun ValueStepper(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$value", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallStepperButton(text = "-", onClick = onDecrement)
                SmallStepperButton(text = "+", onClick = onIncrement)
            }
        }
    }
}

@Composable
fun WeightStepper(
    label: String,
    weight: Float,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (weight % 1f == 0f) "${weight.toInt()}" else "%.1f".format(weight),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                SmallStepperButton(text = "-2.5", onClick = onDecrement)
                SmallStepperButton(text = "+2.5", onClick = onIncrement)
            }
        }
    }
}
