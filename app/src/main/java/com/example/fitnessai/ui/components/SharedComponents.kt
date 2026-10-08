package com.example.fitnessai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fitnessai.model.*
import com.example.fitnessai.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun PRBadge(
    prType: String,
    value: Float,
    unit: String = "kg",
    modifier: Modifier = Modifier
) {
    val (label, bgGradient) = when (prType) {
        "weight_pr" -> "WEIGHT PR" to listOf(Color(0xFFF59E0B), Color(0xFFD97706))
        "volume_pr" -> "VOLUME PR" to listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
        "one_rm_pr" -> "1RM PR" to listOf(Color(0xFF10B981), Color(0xFF047857))
        else -> "PR" to listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(bgGradient))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Personal Record",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "$label: ${if (value % 1f == 0f) value.toInt().toString() else "%.1f".format(value)}$unit",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
fun RpeIndicator(rpe: Float, modifier: Modifier = Modifier) {
    val (color, label) = when {
        rpe <= 6f -> Color(0xFF38BDF8) to "Easy"
        rpe <= 8f -> Color(0xFFFBBF24) to "Moderate"
        else -> Color(0xFFFB7185) to "Hard"
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = "@ RPE $rpe ($label)",
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ExerciseTypeBadge(type: String, modifier: Modifier = Modifier) {
    val (color, icon) = when (type.lowercase()) {
        "strength" -> Color(0xFF10B981) to Icons.Default.FitnessCenter
        "cardio" -> Color(0xFF38BDF8) to Icons.Default.DirectionsRun
        "flexibility" -> Color(0xFFC084FC) to Icons.Default.SelfImprovement
        else -> Color(0xFFF59E0B) to Icons.Default.Bolt
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = type,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = type.replaceFirstChar { it.uppercase() },
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun StatTile(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    color: Color = PrimaryCyan,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressLineChart(
    points: List<Pair<String, Float>>,
    projectedWeight: Float? = null,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Not enough data to render chart", color = TextMuted)
        }
        return
    }

    val minVal = (points.minOfOrNull { it.second } ?: 0f) * 0.9f
    val maxVal = maxOf(
        (points.maxOfOrNull { it.second } ?: 100f) * 1.1f,
        (projectedWeight ?: 0f) * 1.05f
    )
    val valRange = if (maxVal - minVal > 0f) maxVal - minVal else 1f

    Card(
        modifier = modifier.fillMaxWidth(),
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
                Text(
                    text = "Weight Progression (kg)",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PrimaryCyan)
                        )
                        Text("Actual", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    if (projectedWeight != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen)
                            )
                            Text("Projected", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val width = size.width
                val height = size.height
                val paddingBottom = 24.dp.toPx()
                val chartHeight = height - paddingBottom

                // Grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = chartHeight * (i.toFloat() / gridLines)
                    drawLine(
                        color = DarkBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (points.size == 1) {
                    val y = chartHeight - ((points[0].second - minVal) / valRange) * chartHeight
                    drawCircle(
                        color = PrimaryCyan,
                        radius = 6.dp.toPx(),
                        center = Offset(width / 2f, y)
                    )
                    return@Canvas
                }

                val stepX = width / (points.size + (if (projectedWeight != null) 1 else 0) - 1).coerceAtLeast(1)
                val path = Path()
                val fillPath = Path()

                var lastX = 0f
                var lastY = 0f

                points.forEachIndexed { index, pair ->
                    val x = index * stepX
                    val y = chartHeight - ((pair.second - minVal) / valRange) * chartHeight

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, chartHeight)
                        fillPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                    lastX = x
                    lastY = y
                }

                fillPath.lineTo(lastX, chartHeight)
                fillPath.close()

                // Draw gradient under the curve
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryCyan.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = chartHeight
                    )
                )

                // Draw actual line
                drawPath(
                    path = path,
                    color = PrimaryCyan,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw data points
                points.forEachIndexed { index, pair ->
                    val x = index * stepX
                    val y = chartHeight - ((pair.second - minVal) / valRange) * chartHeight
                    drawCircle(
                        color = DarkSurface,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                    drawCircle(
                        color = PrimaryCyan,
                        radius = 4.dp.toPx(),
                        center = Offset(x, y)
                    )
                }

                // Draw projected extension line if present
                if (projectedWeight != null && points.isNotEmpty()) {
                    val projX = (points.size) * stepX
                    val projY = chartHeight - ((projectedWeight - minVal) / valRange) * chartHeight

                    val projPath = Path().apply {
                        moveTo(lastX, lastY)
                        lineTo(projX, projY)
                    }

                    drawPath(
                        path = projPath,
                        color = AccentGreen,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
                        )
                    )

                    drawCircle(
                        color = AccentGreen,
                        radius = 5.dp.toPx(),
                        center = Offset(projX, projY)
                    )
                }
            }
        }
    }
}

@Composable
fun InsightSummaryDialog(
    response: WorkoutLogResponse,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Workout Complete",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Workout Logged!",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "${response.loggedEntries} exercises · ${(response.totalVolume).roundToInt()} kg total volume",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = DarkBorder)

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "AI Coaching Insights",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryCyanLight,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(response.insights) { insight ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (insight.isPr) Color(0xFFF59E0B) else DarkBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = insight.exercise,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (insight.isPr) {
                                        Surface(
                                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Whatshot,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    "NEW PR \uD83C\uDFC6",
                                                    color = Color(0xFFF59E0B),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = insight.suggestion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_summary_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Awesome, Got It!", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ExercisePickerDialog(
    onSelectExercise: (ExerciseDef) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    var customExerciseName by remember { mutableStateOf("") }

    val filtered = remember(searchQuery, selectedMuscleGroup) {
        val base = ExerciseLibrary.search(searchQuery)
        if (selectedMuscleGroup != null) {
            base.filter { it.muscleGroup == selectedMuscleGroup }
        } else base
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Exercise Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 50+ lifts, cardio, mobility...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryCyan) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exercise_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceContainer,
                        unfocusedContainerColor = DarkSurfaceContainer
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Muscle Group Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedMuscleGroup == null,
                        onClick = { selectedMuscleGroup = null },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan,
                            selectedLabelColor = Color.Black
                        )
                    )
                    listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS).forEach { group ->
                        FilterChip(
                            selected = selectedMuscleGroup == group,
                            onClick = { selectedMuscleGroup = if (selectedMuscleGroup == group) null else group },
                            label = { Text(group.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered) { def ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectExercise(def)
                                    onDismiss()
                                },
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = def.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${def.muscleGroup.displayName} · ${def.exerciseType.displayName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                ExerciseTypeBadge(def.exerciseType.value)
                            }
                        }
                    }

                    if (filtered.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("No exercises matched.", color = TextMuted)
                                Spacer(modifier = Modifier.height(12.dp))
                                OutlinedTextField(
                                    value = customExerciseName,
                                    onValueChange = { customExerciseName = it },
                                    placeholder = { Text("Add custom exercise name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        if (customExerciseName.isNotBlank()) {
                                            onSelectExercise(
                                                ExerciseDef(
                                                    name = customExerciseName.trim(),
                                                    muscleGroup = MuscleGroup.FULL_BODY,
                                                    exerciseType = ExerciseType.STRENGTH
                                                )
                                            )
                                            onDismiss()
                                        }
                                    },
                                    enabled = customExerciseName.isNotBlank()
                                ) {
                                    Text("Add Custom Exercise")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
