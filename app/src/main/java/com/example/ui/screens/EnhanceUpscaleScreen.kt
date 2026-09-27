package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.TransformerViewModel
import com.example.ui.theme.*

@Composable
fun EnhanceUpscaleScreen(
    viewModel: TransformerViewModel,
    onNavigateBack: () -> Unit
) {
    val qualityDiag by viewModel.qualityDiagnostics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("enhance_upscale_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("enhance_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Column {
                        Text(
                            text = "4K Super-Res & Cinema",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "AI Video Enhancement & Optical Restoration",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberGlow
                        )
                    }
                }
            }
        }

        // SMART QUALITY ANALYSIS (Requirement 29)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("smart_quality_analysis_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGlow.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AmberGlow.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Analytics, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "Smart Source Quality Analysis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Badge(containerColor = AmberGlow.copy(alpha = 0.2f), contentColor = AmberGlow) {
                            Text("CALIBRATED", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }

                    // Metrics Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Resolution: ${qualityDiag.originalResolution}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Frame Rate: ${qualityDiag.fps} fps", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bitrate: ${qualityDiag.bitrate}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Compression: ${qualityDiag.compressionLevel}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Noise Floor: ${qualityDiag.noiseLevel}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Face Clarity: ${qualityDiag.faceQuality}", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dynamic Range: ${qualityDiag.dynamicRange}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Motion Blur: ${qualityDiag.motionBlur}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ObsidianSurfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "AI RECOMMENDED PIPELINE:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberGlow
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = qualityDiag.recommendedPreset,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // TARGET RESOLUTION & AI SUPER-RESOLUTION (Requirement 24)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("super_resolution_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "AI Video Upscaling (Super-Resolution)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Intelligently reconstructs fine textural details and edge contrast without artificial over-sharpening.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutputResolution.values().forEach { res ->
                            val isSelected = settings.outputResolution == res
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSettings { copy(outputResolution = res) } },
                                label = { Text(res.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberCyan
                                ),
                                modifier = Modifier.testTag("res_chip_${res.name}")
                            )
                        }
                    }
                }
            }
        }

        // CINEMATIC LOOK & CAMERA LOOK SIMULATION (Requirements 25 & 26)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cinematic_look_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Cinematic Quality Enhancement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Cinematic Look Options
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Color & Dynamic Grade", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                        CinematicLook.values().forEach { look ->
                            val isSelected = settings.cinematicLook == look
                            Surface(
                                onClick = { viewModel.updateSettings { copy(cinematicLook = look) } },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CyberCyan.copy(alpha = 0.12f) else ObsidianSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) CyberCyan else ObsidianBorder
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = look.label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) CyberCyan else TextPrimary
                                        )
                                        Text(
                                            text = look.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Camera Look Simulation (Section 26)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Professional Camera Look Simulation", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                        CameraSimulation.values().forEach { cam ->
                            val isSelected = settings.cameraSimulation == cam
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSettings { copy(cameraSimulation = cam) } },
                                label = { Text(cam.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberGlow.copy(alpha = 0.2f),
                                    selectedLabelColor = AmberGlow
                                ),
                                modifier = Modifier.testTag("cam_sim_${cam.name}")
                            )
                        }
                    }
                }
            }
        }

        // FACE RESTORATION MODULE (Requirement 27)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("face_restoration_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "AI Face Restoration Module",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Reconstructs eyes, eyebrows, nose, mouth contours and natural skin pores while strictly preserving the person's identity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // Strength Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FaceRestorationStrength.values().forEach { strength ->
                            val isSelected = settings.faceRestoration == strength
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSettings { copy(faceRestoration = strength) } },
                                label = { Text(strength.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = CyberCyan
                                )
                            )
                        }
                    }

                    // Warning on excessive restoration
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGlow.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGlow.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.WarningAmber, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Notice: Maximum restoration strength may introduce synthetic micro-details on heavily compressed videos.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = AmberGlow
                            )
                        }
                    }
                }
            }
        }

        // Launch Enhancement Pipeline Button
        item {
            Button(
                onClick = { viewModel.startTransformation("4K Cinematic Enhancement & Upscale") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("launch_enhancement_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberGlow,
                    contentColor = ObsidianBg
                )
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Process 4K Enhancement Pipeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
