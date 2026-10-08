package com.example.fitnessai.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitnessai.model.HistoryRecord
import com.example.fitnessai.model.MuscleGroup
import com.example.fitnessai.ui.components.ExerciseTypeBadge
import com.example.fitnessai.ui.components.RpeIndicator
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(
    state: FitnessUiState,
    onSelectExerciseForProgress: (String) -> Unit,
    onExportCsv: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedDays by remember { mutableIntStateOf(90) }
    var selectedGroup by remember { mutableStateOf<String?>(null) }
    var expandedRecordId by remember { mutableStateOf<String?>(null) }
    var showCsvDialog by remember { mutableStateOf(false) }

    val filteredRecords = remember(state.history, searchQuery, selectedDays, selectedGroup) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cutoffCal = Calendar.getInstance()
        cutoffCal.add(Calendar.DAY_OF_YEAR, -selectedDays)
        val cutoff = cutoffCal.time

        state.history.filter { record ->
            val matchesQuery = searchQuery.isBlank() || record.exercise.contains(searchQuery, ignoreCase = true)
            val matchesGroup = selectedGroup == null || record.muscleGroup.equals(selectedGroup, ignoreCase = true)
            val matchesDate = if (selectedDays >= 365) true else {
                try {
                    val d = sdf.parse(record.date)
                    d != null && d.after(cutoff)
                } catch (_: Exception) {
                    true
                }
            }
            matchesQuery && matchesGroup && matchesDate
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Search & Filter Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workout History",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(
                        onClick = { showCsvDialog = true },
                        modifier = Modifier.testTag("export_csv_button")
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Export CSV",
                            tint = PrimaryCyan
                        )
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter exercises...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceContainer,
                        unfocusedContainerColor = DarkSurfaceContainer
                    ),
                    singleLine = true
                )

                // Date Filters & Group Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val dayOptions = listOf(7 to "7 Days", 30 to "30 Days", 90 to "90 Days", 365 to "All Time")
                    items(dayOptions) { (days, label) ->
                        FilterChip(
                            selected = selectedDays == days,
                            onClick = { selectedDays = days },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedGroup == null,
                            onClick = { selectedGroup = null },
                            label = { Text("All Muscles") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyanDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(MuscleGroup.entries) { group ->
                        FilterChip(
                            selected = selectedGroup == group.value,
                            onClick = { selectedGroup = if (selectedGroup == group.value) null else group.value },
                            label = { Text(group.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyanDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Count Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${filteredRecords.size} records found",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Text(
                    text = "Tap row to expand details",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        if (filteredRecords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No workout logs match your filter.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(filteredRecords, key = { it.id }) { record ->
                val isExpanded = expandedRecordId == record.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedRecordId = if (isExpanded) null else record.id
                        }
                        .testTag("history_record_${record.id}"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isExpanded) PrimaryCyan else DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = record.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryCyanLight
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ExerciseTypeBadge(record.exerciseType)
                                RpeIndicator(record.rpe)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = record.exercise,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${record.sets}×${record.reps} @ ${record.weightKg} kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Volume: ${(record.volume).roundToInt()} kg",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = "Est. 1RM: ${(record.oneRm * 10).roundToInt() / 10f} kg",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Divider(color = DarkBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "\uD83D\uDE0A Mood: ${record.mood}/10",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "⚡ Energy: ${record.energy}/10",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Surface(
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "\uD83D\uDE34 Sleep: ${record.sleepQuality}/10",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (record.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Notes: ${record.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }

                                if (record.formScore.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Form Score: ${record.formScore}/10 (${record.rangeOfMotion}% ROM)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AccentGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = { onSelectExerciseForProgress(record.exercise) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyanDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("View Progress Chart for ${record.exercise}", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // CSV Export Dialog
    if (showCsvDialog) {
        val csvText = onExportCsv()
        Dialog(onDismissRequest = { showCsvDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Google Sheets CSV Export",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Formatted with all ${state.history.size} workout rows ready for Google Sheets or Excel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        color = DarkSurfaceContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = csvText,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 15
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCsvDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Close")
                        }
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(csvText))
                                Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
                                showCsvDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Copy CSV", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
