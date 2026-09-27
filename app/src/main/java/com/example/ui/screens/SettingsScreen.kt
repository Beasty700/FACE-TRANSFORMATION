package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.PerformanceMode
import com.example.ui.TransformerViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: TransformerViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val isConsentGranted by viewModel.isConsentGranted.collectAsState()
    val references by viewModel.identityReferences.collectAsState()

    var showClearDataConfirm by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Column {
                    Text(
                        text = "Engine & Privacy Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "GPU Acceleration & Biometric Governance",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                }
            }
        }

        // Notification Banner
        if (snackbarMessage != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(snackbarMessage!!, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        IconButton(onClick = { snackbarMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // PERFORMANCE MODES (Section 37)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("perf_mode_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(20.dp))
                        Text(
                            text = "AI Processing Performance Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "Choose model depth and GPU pass count. Maximum Quality utilizes multi-pass optical flow and full neural temporal consistency.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PerformanceMode.values().forEach { mode ->
                            val isSelected = settings.performanceMode == mode
                            Surface(
                                onClick = { viewModel.updateSettings { copy(performanceMode = mode) } },
                                shape = RoundedCornerShape(12.dp),
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = mode.label,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) CyberCyan else TextPrimary
                                            )
                                            Badge(
                                                containerColor = if (isSelected) CyberCyan else ObsidianBorder,
                                                contentColor = if (isSelected) ObsidianBg else TextMuted
                                            ) {
                                                Text(mode.speedFactor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = mode.description,
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
                }
            }
        }

        // GPU BACKEND TELEMETRY (Sections 18 & 35)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gpu_backend_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            Icon(Icons.Default.Memory, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                            Text(
                                text = "GPU Pipeline Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(EmeraldSuccess))
                            Text("Online", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ObsidianSurfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Acceleration Engine:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("NVIDIA CUDA & TensorRT", style = MaterialTheme.typography.bodySmall, color = CyberCyan, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Chunk Memory Buffer:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("2.4 GB / 16 GB VRAM Allocated", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Optical Flow Model:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("RAFT-Temporal v4 (Multi-Frame)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Audio Restorer:", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Phase-Aligned Demux / Pass-thru", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // PRIVACY, CONSENT & DATA DELETION (Requirement 16)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
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
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Privacy & Consent Governance",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        TextButton(onClick = { viewModel.openConsentDialog() }) {
                            Text(if (isConsentGranted) "Review Consent" else "Grant Consent", color = CyberCyan, fontSize = 12.sp)
                        }
                    }

                    Text(
                        text = "Because the application processes facial and body biometric data, strict user authorization is enforced. You maintain 100% control over local media and references.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    HorizontalDivider(color = ObsidianBorder)

                    // Delete Reference Photos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Delete Uploaded References", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Text("${references.size} biometric reference files registered", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = TextMuted)
                        }
                        OutlinedButton(
                            onClick = {
                                viewModel.deleteAllReferences()
                                snackbarMessage = "All uploaded identity references deleted."
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGlow),
                            modifier = Modifier.testTag("delete_references_button")
                        ) {
                            Text("Delete", fontSize = 11.sp)
                        }
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Clear All Project Data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Clear All Application Data", style = MaterialTheme.typography.labelMedium, color = CrimsonError)
                            Text("Removes all projects, cached frames, and references", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = TextMuted)
                        }
                        Button(
                            onClick = { showClearDataConfirm = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonError, contentColor = TextPrimary),
                            modifier = Modifier.testTag("clear_all_data_button")
                        ) {
                            Text("Clear All", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showClearDataConfirm) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirm = false },
            title = { Text("Confirm Data Deletion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to permanently erase all projects, identity references, and authorization records?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDataConfirm = false
                        snackbarMessage = "All application data and references have been permanently wiped."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonError, contentColor = TextPrimary)
                ) {
                    Text("Permanently Erase")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirm = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = ObsidianCard
        )
    }
}
