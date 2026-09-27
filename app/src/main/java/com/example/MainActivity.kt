package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TransformerViewModel
import com.example.ui.components.ConsentDialog
import com.example.ui.components.PipelineProgressDialog
import com.example.ui.screens.*
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.ObsidianSurface

enum class AppNavDestination(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    HOME("Home", Icons.Default.Home),
    VIDEO("Video AI", Icons.Default.MovieFilter),
    PHOTO_CLOTH("Photo & Cloth", Icons.Default.Checkroom),
    ENHANCE("4K Cinema", Icons.Default.HighQuality),
    PROJECTS("Projects", Icons.Default.VideoLibrary),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: TransformerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: TransformerViewModel) {
    var currentDestination by remember { mutableStateOf(AppNavDestination.HOME) }

    val showConsentDialog by viewModel.showConsentDialog.collectAsState()
    val showPipelineDialog by viewModel.showPipelineDialog.collectAsState()
    val pipelineProgress by viewModel.pipelineProgress.collectAsState()
    val currentStageTitle by viewModel.currentStageTitle.collectAsState()
    val currentStageDetail by viewModel.currentStageDetail.collectAsState()
    val currentChunk by viewModel.currentChunk.collectAsState()
    val totalChunks by viewModel.totalChunks.collectAsState()
    val pipelineStages by viewModel.pipelineStages.collectAsState()

    // Handle Android system back press
    BackHandler(enabled = currentDestination != AppNavDestination.HOME) {
        currentDestination = AppNavDestination.HOME
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ObsidianBg,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = ObsidianSurface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        windowInsets = WindowInsets.navigationBars,
                        modifier = Modifier.testTag("main_bottom_nav_bar")
                    ) {
                        AppNavDestination.values().forEach { destination ->
                            val isSelected = currentDestination == destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = destination.label,
                                        fontSize = 10.sp,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBg,
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.testTag("nav_item_${destination.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Navigation Rail for wide screens (Tablets / Foldables / ChromeOS)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = ObsidianSurface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .testTag("main_navigation_rail")
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppNavDestination.values().forEach { destination ->
                            val isSelected = currentDestination == destination
                            NavigationRailItem(
                                selected = isSelected,
                                onClick = { currentDestination = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label, fontSize = 11.sp) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianBg,
                                    selectedTextColor = CyberCyan,
                                    indicatorColor = CyberCyan
                                ),
                                modifier = Modifier.testTag("rail_item_${destination.name.lowercase()}")
                            )
                        }
                    }
                }

                // Main Screen Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ObsidianBg)
                ) {
                    when (currentDestination) {
                        AppNavDestination.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToVideoTransform = { currentDestination = AppNavDestination.VIDEO },
                            onNavigateToImageTransform = { currentDestination = AppNavDestination.PHOTO_CLOTH },
                            onNavigateToEnhance = { currentDestination = AppNavDestination.ENHANCE },
                            onNavigateToProjects = { currentDestination = AppNavDestination.PROJECTS }
                        )
                        AppNavDestination.VIDEO -> VideoTransformScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppNavDestination.HOME }
                        )
                        AppNavDestination.PHOTO_CLOTH -> ImageTransformScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppNavDestination.HOME }
                        )
                        AppNavDestination.ENHANCE -> EnhanceUpscaleScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppNavDestination.HOME }
                        )
                        AppNavDestination.PROJECTS -> ProjectsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppNavDestination.HOME }
                        )
                        AppNavDestination.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentDestination = AppNavDestination.HOME }
                        )
                    }
                }
            }
        }
    }

    // Consent Dialog
    ConsentDialog(
        isOpen = showConsentDialog,
        onConfirm = { viewModel.grantConsent() },
        onDismiss = { viewModel.closeConsentDialog() }
    )

    // 14-Stage Processing Pipeline Dialog
    PipelineProgressDialog(
        isOpen = showPipelineDialog,
        progressPercent = pipelineProgress,
        currentStageTitle = currentStageTitle,
        currentStageDetail = currentStageDetail,
        currentChunk = currentChunk,
        totalChunks = totalChunks,
        stages = pipelineStages,
        onCancel = { viewModel.cancelTransformation() },
        onMinimize = { viewModel.closePipelineDialog() }
    )
}
