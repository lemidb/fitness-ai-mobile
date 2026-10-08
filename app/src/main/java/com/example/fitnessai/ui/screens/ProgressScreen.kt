package com.example.fitnessai.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessai.model.ExerciseLibrary
import com.example.fitnessai.ui.components.PRBadge
import com.example.fitnessai.ui.components.ProgressLineChart
import com.example.fitnessai.ui.components.StatTile
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessUiState
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(
    state: FitnessUiState,
    onSelectExercise: (String) -> Unit,
    onSelectDays: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = state.progressData
    val exerciseList = remember(state.history) {
        val fromHistory = state.history.map { it.exercise }.distinct()
        if (fromHistory.isNotEmpty()) fromHistory else listOf("Bench Press", "Squat", "Deadlift")
    }

    // Chart points
    val chartPoints = remember(state.history, state.selectedExercise, state.selectedDaysFilter) {
        state.history
            .filter { it.exercise.equals(state.selectedExercise, ignoreCase = true) }
            .sortedBy { it.date }
            .map { it.date to it.weightKg }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Header & Exercise Selection Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Performance Analytics",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Track weight progression, estimated 1RM, and PR breakthroughs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Exercise Selection Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(exerciseList) { exercise ->
                        FilterChip(
                            selected = state.selectedExercise.equals(exercise, ignoreCase = true),
                            onClick = { onSelectExercise(exercise) },
                            label = { Text(exercise) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                // Days Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val daysOptions = listOf(30 to "Last 30 Days", 90 to "Last 90 Days", 365 to "Full Year")
                    items(daysOptions) { (days, label) ->
                        FilterChip(
                            selected = state.selectedDaysFilter == days,
                            onClick = { onSelectDays(days) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyanDark,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Stats Row (Max Weight, Avg Weight, Sessions, Volume)
        if (progress != null && progress.totalWorkouts > 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatTile(
                            title = "Max Weight",
                            value = "${progress.maxWeight}",
                            unit = "kg",
                            icon = Icons.Default.FitnessCenter,
                            color = PrimaryCyan,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Avg Weight",
                            value = "${(progress.avgWeight * 10).roundToInt() / 10f}",
                            unit = "kg",
                            icon = Icons.Default.Scale,
                            color = PrimaryCyanLight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatTile(
                            title = "Total Sessions",
                            value = "${progress.totalWorkouts}",
                            unit = "logged",
                            icon = Icons.Default.EventRepeat,
                            color = AccentGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatTile(
                            title = "Max Volume",
                            value = "${(progress.maxVolume).roundToInt()}",
                            unit = "kg",
                            icon = Icons.Default.Equalizer,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Trend Indicator Card
            item {
                val trend = progress.trend
                val (trendColor, trendIcon, trendLabel) = when (trend.direction) {
                    "improving" -> Triple(AccentGreen, Icons.Default.TrendingUp, "Progression: Improving (\u2197\uFE0F +${trend.slope} kg/session)")
                    "declining" -> Triple(AccentRose, Icons.Default.TrendingDown, "Progression: Declining (\u2198\uFE0F ${trend.slope} kg/session)")
                    "maintaining" -> Triple(AccentAmber, Icons.Default.TrendingFlat, "Progression: Maintaining consistency")
                    else -> Triple(TextMuted, Icons.Default.Info, "Trend: Log 3+ sessions to unlock regression analysis")
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, trendColor.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(trendIcon, contentDescription = null, tint = trendColor, modifier = Modifier.size(24.dp))
                            Text(
                                text = trendLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = trendColor
                            )
                        }

                        if (trend.projectedWeight != null && trend.direction == "improving") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Projected 7 sessions ahead: ${trend.projectedWeight} kg target based on your current rate of strength gain.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Line Chart with Actual & Projected lines
            item {
                ProgressLineChart(
                    points = chartPoints,
                    projectedWeight = progress.trend.projectedWeight
                )
            }

            // Personal Records Badges
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Personal Records for ${state.selectedExercise}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (progress.prs.isEmpty()) {
                        Text("No PRs recorded yet.", color = TextMuted)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            progress.prs.forEach { pr ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            PRBadge(pr.type, pr.value)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Achieved on ${pr.date}" + (if (pr.reps != null) " (${pr.reps} reps)" else ""),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                            )
                                        }
                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
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
                            Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No data found for '${state.selectedExercise}' in this time window.",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Log a workout with this exercise to start charting analytics.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
