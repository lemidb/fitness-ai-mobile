package com.example.fitnessai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessai.model.CalendarEvent
import com.example.fitnessai.ui.components.ExerciseTypeBadge
import com.example.fitnessai.ui.components.PRBadge
import com.example.fitnessai.ui.components.StatTile
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessUiState
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    state: FitnessUiState,
    onStartWorkout: () -> Unit,
    onStartEvent: (CalendarEvent) -> Unit,
    onViewProgress: (String) -> Unit,
    onViewHistory: () -> Unit,
    onViewSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalVolume = state.history.sumOf { it.volume.toDouble() }.toFloat()
    val totalWorkouts = state.history.map { it.date }.distinct().size
    val recentSession = state.history.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Hero Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PrimaryCyan.copy(alpha = 0.15f),
                                    Color.Transparent,
                                    AccentGreen.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "WELCOME BACK,",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyanLight,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = state.userProfile.username,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryCyan.copy(alpha = 0.2f))
                                    .border(1.dp, PrimaryCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = "Avatar",
                                    tint = PrimaryCyan,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onStartWorkout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("start_workout_hero_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryCyan
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                                Text(
                                    text = "Start Workout Now",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatTile(
                    title = "Total Volume",
                    value = "${(totalVolume / 1000).roundToInt()}k",
                    unit = "kg",
                    icon = Icons.Default.TrendingUp,
                    color = PrimaryCyan,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "Sessions",
                    value = "$totalWorkouts",
                    unit = "completed",
                    icon = Icons.Default.CheckCircle,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    title = "Recent PRs",
                    value = "${state.progressData?.prs?.size ?: 3}",
                    unit = "records",
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Upcoming Workouts Strip (Google Calendar)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Schedule",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    TextButton(onClick = onViewSchedule) {
                        Text("View all", color = PrimaryCyanLight)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (state.schedule.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "No scheduled workouts in Google Calendar. Tap Schedule to sync.",
                            color = TextSecondary,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.schedule.take(3)) { event ->
                            Card(
                                modifier = Modifier
                                    .width(280.dp)
                                    .clickable { onStartEvent(event) },
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ExerciseTypeBadge(event.type)
                                        Text(
                                            text = "${event.estimatedDurationMin} min",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = event.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )

                                    if (event.location.isNotBlank()) {
                                        Text(
                                            text = "\uD83D\uDCCD ${event.location}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Warm-up preview
                                    if (event.aiInsights.suggestedWarmup.isNotEmpty()) {
                                        Text(
                                            text = "Warm-up: ${event.aiInsights.suggestedWarmup.first()}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PrimaryCyanLight,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = { onStartEvent(event) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = DarkSurfaceVariant
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Start This Workout", color = TextPrimary, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = null,
                                            tint = PrimaryCyan,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Personal Records Showcase
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Top Personal Records",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Triple("Bench Press", "80.0 kg", "weight_pr"),
                            Triple("Squat", "95.0 kg", "weight_pr"),
                            Triple("Deadlift", "105.0 kg", "weight_pr")
                        ).forEach { (exercise, weight, type) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onViewProgress(exercise) }
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.EmojiEvents,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            exercise,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            "All-time personal record",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                                PRBadge(type, weight.replace(" kg", "").toFloat())
                            }
                            if (exercise != "Deadlift") {
                                Divider(color = DarkBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Recent Session Summary
        if (recentSession != null) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Last Completed Session",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        TextButton(onClick = onViewHistory) {
                            Text("Full History", color = PrimaryCyanLight)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    recentSession.date,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryCyanLight
                                )
                                Text(
                                    "Volume: ${(recentSession.volume).roundToInt()} kg",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${recentSession.exercise} · ${recentSession.sets} sets × ${recentSession.reps} reps @ ${recentSession.weightKg} kg",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "\uD83D\uDE0A Mood ${recentSession.mood}/10",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "⚡ Energy ${recentSession.energy}/10",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "\uD83D\uDE34 Sleep ${recentSession.sleepQuality}/10",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
