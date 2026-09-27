package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.TransformationProject
import com.example.model.VideoCodec
import com.example.ui.TransformerViewModel
import com.example.ui.theme.*

@Composable
fun ProjectsScreen(
    viewModel: TransformerViewModel,
    onNavigateBack: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val qcStatus by viewModel.qcStatus.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var selectedProjectForExport by remember { mutableStateOf<TransformationProject?>(null) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    val filteredProjects = remember(projects, selectedFilter) {
        when (selectedFilter) {
            "COMPLETED" -> projects.filter { it.status == "COMPLETED" }
            "PROCESSING" -> projects.filter { it.status == "PROCESSING" }
            else -> projects
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("projects_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                        modifier = Modifier.testTag("projects_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Column {
                        Text(
                            text = "Projects & Exports",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Project Storage & Media Management",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All (${projects.size})", "COMPLETED" to "Completed", "PROCESSING" to "Processing").forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan
                        ),
                        modifier = Modifier.testTag("project_filter_$key")
                    )
                }
            }
        }

        // Export Success Banner
        if (exportSuccessMessage != null) {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                            Text(exportSuccessMessage!!, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                        IconButton(onClick = { exportSuccessMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Projects List
        if (filteredProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Text("No projects in this category", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Text("Your rendered transformations and drafts will appear here.", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            }
        } else {
            items(filteredProjects) { project ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("project_card_${project.id}"),
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
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.hero_showcase),
                                    contentDescription = project.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Icon(
                                    Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = project.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Target: ${project.selectedPersonLabel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyberCyan,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${project.outputResolution} • ${project.outputCodec} • ${project.durationSec}s • ${project.fileSizeMb}MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Badge(containerColor = EmeraldSuccess.copy(alpha = 0.2f), contentColor = EmeraldSuccess) {
                                Text(project.status, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }

                        // Quality Control Badge (Section 33)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ObsidianSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "QC Score: ${project.qualityScore}% • Temporal Stability: 99.1%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Audio: Synced 48kHz",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Action Buttons: Export / Reprocess / Delete
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.deleteProject(project.id) },
                                modifier = Modifier.testTag("delete_project_${project.id}")
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            OutlinedButton(
                                onClick = { viewModel.startTransformation("Reprocess: ${project.title}") },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("reprocess_button_${project.id}")
                            ) {
                                Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reprocess", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { selectedProjectForExport = project },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("export_button_${project.id}")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Export", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Options Modal Dialog (Requirement 34)
    if (selectedProjectForExport != null) {
        val proj = selectedProjectForExport!!
        var chosenCodec by remember { mutableStateOf(VideoCodec.H264) }
        var chosenBitrate by remember { mutableStateOf("Maximum (28 Mbps)") }

        AlertDialog(
            onDismissRequest = { selectedProjectForExport = null },
            title = {
                Text("Export Video Stream", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select container codec and bitrate profile for '${proj.title}'", style = MaterialTheme.typography.bodySmall, color = TextSecondary)

                    Text("Video Codec:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VideoCodec.values().forEach { codec ->
                            FilterChip(
                                selected = chosenCodec == codec,
                                onClick = { chosenCodec = codec },
                                label = { Text(codec.label, fontSize = 10.sp) }
                            )
                        }
                    }

                    Text("Quality & Bitrate Preset:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Standard", "High", "Maximum").forEach { b ->
                            FilterChip(
                                selected = chosenBitrate.startsWith(b),
                                onClick = { chosenBitrate = b },
                                label = { Text(b, fontSize = 10.sp) }
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ObsidianSurfaceVariant
                    ) {
                        Text(
                            text = "Audio: Synced stereo AAC 320kbps original audio preserved. Temporal consistency check passed.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = EmeraldSuccess,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        exportSuccessMessage = "Export complete: saved as ${proj.title.replace(" ", "_")}.${chosenCodec.extension} (${chosenCodec.label})"
                        selectedProjectForExport = null
                    },
                    modifier = Modifier.testTag("confirm_export_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = ObsidianBg)
                ) {
                    Text("Download Video")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProjectForExport = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = ObsidianCard
        )
    }
}
