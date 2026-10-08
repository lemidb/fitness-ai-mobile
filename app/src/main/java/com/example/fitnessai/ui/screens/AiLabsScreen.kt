package com.example.fitnessai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessai.model.ExerciseType
import com.example.fitnessai.ui.components.RpeIndicator
import com.example.fitnessai.ui.components.StatTile
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessUiState

@Composable
fun AiLabsScreen(
    state: FitnessUiState,
    onRequestPrediction: (String) -> Unit,
    onApplyPrediction: () -> Unit,
    onRunFormAnalysis: (String, ExerciseType) -> Unit,
    onApplyFormAnalysis: () -> Unit,
    onNavigateToLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: LSTM Predict, 1: Form Analysis
    var selectedExercise by remember { mutableStateOf("Bench Press") }

    val exerciseOptions = listOf("Bench Press", "Squat", "Deadlift", "Overhead Press")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // AI Labs Header & Sub-Tabs
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "AI Intelligence Labs",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = "Neural network workout predictions & computer vision pose tracking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurface,
                    contentColor = PrimaryCyan,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("LSTM Predictions", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            onRunFormAnalysis(selectedExercise, ExerciseType.STRENGTH)
                        },
                        text = { Text("Form Analysis", fontWeight = FontWeight.SemiBold) },
                        icon = { Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }

        // Exercise Picker Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Exercise:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(exerciseOptions) { ex ->
                        FilterChip(
                            selected = selectedExercise == ex,
                            onClick = {
                                selectedExercise = ex
                                if (selectedTab == 0) onRequestPrediction(ex)
                                else onRunFormAnalysis(ex, ExerciseType.STRENGTH)
                            },
                            label = { Text(ex) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryCyan,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Tab 0: LSTM Prediction
            val pred = state.predictionData
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prediction_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Next Session Recommendation",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Surface(
                                color = AccentPurple.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "LSTM Model",
                                    color = AccentPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (pred != null && pred.predictedWeight > 0f) {
                            Text(
                                text = "${pred.predictedWeight} kg",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCyan
                            )
                            Text(
                                text = "${pred.predictedReps} reps targeted @ RPE ${pred.predictedRpe}",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Confidence Bar
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Model Confidence", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text("${(pred.confidence * 100).toInt()}%", fontWeight = FontWeight.Bold, color = AccentGreen)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { pred.confidence },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = AccentGreen,
                                    trackColor = DarkBorder
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    onApplyPrediction()
                                    onNavigateToLog()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("use_prediction_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Use This Prediction in Workout", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Log at least 3 sessions for $selectedExercise to unlock neural network predictions.",
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Tab 1: Form Analysis (MediaPipe pose overlay)
            val fa = state.formAnalysisData
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceContainer),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Computer Vision Pose Tracking",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Surface(
                                color = AccentGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Active",
                                    color = AccentGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Pose Skeleton Canvas Simulation
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val w = size.width
                                val h = size.height
                                val head = Offset(w * 0.5f, h * 0.22f)
                                val shoulderL = Offset(w * 0.42f, h * 0.35f)
                                val shoulderR = Offset(w * 0.58f, h * 0.35f)
                                val elbowL = Offset(w * 0.35f, h * 0.48f)
                                val elbowR = Offset(w * 0.65f, h * 0.48f)
                                val wristL = Offset(w * 0.38f, h * 0.38f)
                                val wristR = Offset(w * 0.62f, h * 0.38f)
                                val hipL = Offset(w * 0.45f, h * 0.58f)
                                val hipR = Offset(w * 0.55f, h * 0.58f)
                                val kneeL = Offset(w * 0.42f, h * 0.75f)
                                val kneeR = Offset(w * 0.58f, h * 0.75f)
                                val ankleL = Offset(w * 0.40f, h * 0.92f)
                                val ankleR = Offset(w * 0.60f, h * 0.92f)

                                val boneColor = PrimaryCyan
                                val jointColor = AccentGreen

                                fun drawBone(p1: Offset, p2: Offset) {
                                    drawLine(boneColor, p1, p2, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                                }
                                fun drawJoint(p: Offset) {
                                    drawCircle(jointColor, radius = 5.dp.toPx(), center = p)
                                }

                                drawCircle(PrimaryCyanLight, radius = 14.dp.toPx(), center = head)
                                drawBone(head, Offset(w * 0.5f, h * 0.35f))
                                drawBone(shoulderL, shoulderR)
                                drawBone(shoulderL, elbowL)
                                drawBone(elbowL, wristL)
                                drawBone(shoulderR, elbowR)
                                drawBone(elbowR, wristR)
                                drawBone(Offset(w * 0.5f, h * 0.35f), Offset(w * 0.5f, h * 0.58f))
                                drawBone(hipL, hipR)
                                drawBone(hipL, kneeL)
                                drawBone(kneeL, ankleL)
                                drawBone(hipR, kneeR)
                                drawBone(kneeR, ankleR)

                                listOf(shoulderL, shoulderR, elbowL, elbowR, wristL, wristR, hipL, hipR, kneeL, kneeR, ankleL, ankleR).forEach {
                                    drawJoint(it)
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp),
                                color = DarkSurface.copy(alpha = 0.85f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "Joint Kinematics: 95% Match",
                                    color = AccentGreen,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (fa != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                StatTile(
                                    title = "Form Score",
                                    value = "${fa.formScore}/10",
                                    icon = Icons.Default.Verified,
                                    color = AccentGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                StatTile(
                                    title = "Range of Motion",
                                    value = "${fa.rangeOfMotion}",
                                    unit = "%",
                                    icon = Icons.Default.Straighten,
                                    color = PrimaryCyan,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Coaching Feedback:",
                                style = MaterialTheme.typography.labelLarge,
                                color = PrimaryCyanLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = fa.feedback,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    onApplyFormAnalysis()
                                    onNavigateToLog()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save Scores to Active Workout", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
