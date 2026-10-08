package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessai.ui.components.InsightSummaryDialog
import com.example.fitnessai.ui.screens.*
import com.example.fitnessai.ui.theme.*
import com.example.fitnessai.viewmodel.FitnessViewModel

enum class NavDestination(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Home", Icons.Default.Home, "nav_dashboard"),
    LOG("Log", Icons.Default.Add, "nav_log"),
    PROGRESS("Progress", Icons.Default.ShowChart, "nav_progress"),
    HISTORY("History", Icons.Default.History, "nav_history"),
    SCHEDULE("Schedule", Icons.Default.CalendarMonth, "nav_schedule"),
    AI_LABS("AI Labs", Icons.Default.Psychology, "nav_ai_labs"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    private val viewModel: FitnessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FitnessAITheme {
                FitnessApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessApp(viewModel: FitnessViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }

    // Predictive back / BackHandler to return to Dashboard if on a subscreen
    BackHandler(enabled = currentDestination != NavDestination.DASHBOARD) {
        currentDestination = NavDestination.DASHBOARD
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Fitness AI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Real-time Tracker & Coach",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentDestination = NavDestination.AI_LABS },
                        modifier = Modifier.testTag("top_ai_labs_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Labs",
                            tint = if (currentDestination == NavDestination.AI_LABS) PrimaryCyan else TextSecondary
                        )
                    }
                    IconButton(
                        onClick = { currentDestination = NavDestination.PROFILE },
                        modifier = Modifier.testTag("top_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = if (currentDestination == NavDestination.PROFILE) PrimaryCyan else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                val primaryNavs = listOf(
                    NavDestination.DASHBOARD,
                    NavDestination.LOG,
                    NavDestination.PROGRESS,
                    NavDestination.HISTORY,
                    NavDestination.SCHEDULE
                )

                primaryNavs.forEach { dest ->
                    val selected = currentDestination == dest
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentDestination = dest },
                        icon = {
                            if (dest == NavDestination.LOG) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) PrimaryCyan else DarkSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        dest.icon,
                                        contentDescription = dest.title,
                                        tint = if (selected) Color.Black else PrimaryCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    dest.icon,
                                    contentDescription = dest.title,
                                    tint = if (selected) PrimaryCyan else TextSecondary
                                )
                            }
                        },
                        label = {
                            Text(
                                text = dest.title,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) PrimaryCyan else TextSecondary
                            )
                        },
                        modifier = Modifier.testTag(dest.tag),
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (currentDestination) {
                NavDestination.DASHBOARD -> {
                    DashboardScreen(
                        state = uiState,
                        onStartWorkout = { currentDestination = NavDestination.LOG },
                        onStartEvent = { event ->
                            viewModel.prefillFromCalendarEvent(event)
                            currentDestination = NavDestination.LOG
                        },
                        onViewProgress = { exercise ->
                            viewModel.updateProgressData(exercise)
                            currentDestination = NavDestination.PROGRESS
                        },
                        onViewHistory = { currentDestination = NavDestination.HISTORY },
                        onViewSchedule = { currentDestination = NavDestination.SCHEDULE }
                    )
                }
                NavDestination.LOG -> {
                    LogWorkoutScreen(
                        state = uiState,
                        onAddExercise = { viewModel.addWorkoutEntry(it) },
                        onUpdateExercise = { idx, entry -> viewModel.updateWorkoutEntry(idx, entry) },
                        onRemoveExercise = { idx -> viewModel.removeWorkoutEntry(idx) },
                        onUpdateMetadata = { viewModel.updateMetadata(it) },
                        onSubmitWorkout = { viewModel.submitWorkoutLog() },
                        onLogSingleExerciseToRoom = { exercise, sets, reps, weight, rpe, group, notes ->
                            viewModel.logSingleExerciseToRoom(exercise, sets, reps, weight, rpe, group, notes)
                        },
                        onDeleteRoomLog = { id -> viewModel.deleteRoomLog(id) }
                    )
                }
                NavDestination.PROGRESS -> {
                    ProgressScreen(
                        state = uiState,
                        onSelectExercise = { viewModel.updateProgressData(it, uiState.selectedDaysFilter) },
                        onSelectDays = { viewModel.updateProgressData(uiState.selectedExercise, it) }
                    )
                }
                NavDestination.HISTORY -> {
                    HistoryScreen(
                        state = uiState,
                        onSelectExerciseForProgress = { ex ->
                            viewModel.updateProgressData(ex)
                            currentDestination = NavDestination.PROGRESS
                        },
                        onExportCsv = { viewModel.getCsvExportData() }
                    )
                }
                NavDestination.SCHEDULE -> {
                    ScheduleScreen(
                        state = uiState,
                        onSyncCalendar = { viewModel.syncCalendar(it) },
                        onStartEvent = { event ->
                            viewModel.prefillFromCalendarEvent(event)
                            currentDestination = NavDestination.LOG
                        },
                        onDismissSyncMessage = { viewModel.clearSyncMessage() }
                    )
                }
                NavDestination.AI_LABS -> {
                    AiLabsScreen(
                        state = uiState,
                        onRequestPrediction = { viewModel.requestPrediction(it) },
                        onApplyPrediction = { viewModel.applyPredictionToWorkout() },
                        onRunFormAnalysis = { ex, type -> viewModel.runFormAnalysis(ex, type) },
                        onApplyFormAnalysis = { viewModel.applyFormAnalysisToWorkout() },
                        onNavigateToLog = { currentDestination = NavDestination.LOG }
                    )
                }
                NavDestination.PROFILE -> {
                    ProfileScreen(
                        state = uiState,
                        onToggleUnit = { viewModel.toggleUnitSystem() }
                    )
                }
            }

            // Summary Dialog on Workout Submission
            if (uiState.lastLogResponse != null) {
                InsightSummaryDialog(
                    response = uiState.lastLogResponse!!,
                    onDismiss = { viewModel.dismissSummaryModal() }
                )
            }
        }
    }
}
