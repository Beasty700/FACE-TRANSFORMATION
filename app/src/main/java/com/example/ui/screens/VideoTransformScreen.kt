package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.*
import com.example.ui.TransformerViewModel
import com.example.ui.components.PoseOverlayCanvas
import com.example.ui.components.VideoComparisonSlider
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTransformScreen(
    viewModel: TransformerViewModel,
    onNavigateBack: () -> Unit
) {
    val currentVideo by viewModel.currentVideo.collectAsState()
    val detectedPeople by viewModel.detectedPeople.collectAsState()
    val selectedPersonId by viewModel.selectedPersonId.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val references by viewModel.identityReferences.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isConsentGranted by viewModel.isConsentGranted.collectAsState()

    var showPoseSkeleton by remember { mutableStateOf(true) }
    var previewDurationSec by remember { mutableIntStateOf(10) }
    var showAddReferenceSheet by remember { mutableStateOf(false) }
    var newRefLabel by remember { mutableStateOf("") }
    var newRefType by remember { mutableStateOf("SLIGHT_ANGLE") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("video_transform_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Bar
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
                        modifier = Modifier.testTag("transform_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Column {
                        Text(
                            text = "Video Identity Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Full Pipeline Transformation",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { viewModel.startTransformation() },
                    modifier = Modifier.testTag("top_generate_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = CyberCyan,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Transform", fontWeight = FontWeight.Bold)
                }
            }
        }

        // STEP 1: SOURCE VIDEO UPLOAD & TELEMETRY
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("source_video_card"),
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
                            Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                                Text("STEP 1", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Text(
                                text = "Source Video Upload",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        AssistChip(
                            onClick = {
                                // Simulate switching to another performance clip
                                viewModel.loadCustomSourceVideo(
                                    name = "Stage Performance - Hip-Hop Routine",
                                    durationSec = 35,
                                    resolution = "3840x2160 (4K)",
                                    fps = 60,
                                    sizeMb = 142.0
                                )
                            },
                            label = { Text("Switch Source", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = AssistChipDefaults.assistChipColors(labelColor = CyberCyan),
                            modifier = Modifier.testTag("switch_video_source_chip")
                        )
                    }

                    // Video Info Details Grid
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ObsidianSurfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = currentVideo.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Duration: ${currentVideo.durationSec}s", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Resolution: ${currentVideo.resolution}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("FPS: ${currentVideo.fps}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("File Size: ${currentVideo.fileSizeMb} MB", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Aspect Ratio: ${currentVideo.aspectRatio}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("Audio: Original Synced", style = MaterialTheme.typography.bodySmall, color = EmeraldSuccess)
                            }
                        }
                    }
                }
            }
        }

        // STEP 2: USER IDENTITY REFERENCES (3-10 PHOTOS + FULL BODY)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("identity_references_card"),
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
                            Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                                Text("STEP 2", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Text(
                                text = "Authorized Identity References",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        TextButton(
                            onClick = { showAddReferenceSheet = true },
                            modifier = Modifier.testTag("add_reference_photo_button")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Photo", color = CyberCyan, fontSize = 12.sp)
                        }
                    }

                    // Quality Recommendations Notice
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ObsidianSurface.copy(alpha = 0.8f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Recommendation: 3–10 high-quality photos with good lighting, no sunglasses, multiple viewing angles, and a full-body reference.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // References Horizontal Scroll
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(references) { ref ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ObsidianSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .width(130.dp)
                                    .testTag("ref_item_${ref.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.DarkGray)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.sample_identity),
                                            contentDescription = ref.label,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(4.dp)
                                                .size(14.dp)
                                        )
                                    }
                                    Text(
                                        text = ref.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = ref.qualityStatus,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // STEP 3: SOURCE VIDEO ANALYSIS & PERSON SELECTION
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                        Text("STEP 3", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                    Text(
                        text = "Person Selection & Motion Tracking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                PoseOverlayCanvas(
                    imageRes = R.drawable.sample_dancer,
                    detectedPeople = detectedPeople,
                    selectedPersonId = selectedPersonId,
                    onSelectPerson = { viewModel.selectPerson(it) },
                    showPoseLandmarks = showPoseSkeleton,
                    onToggleLandmarks = { showPoseSkeleton = it }
                )
            }
        }

        // STEP 4: TRANSFORMATION SETTINGS (FACE, BODY, HAIR, CLOTHING, TEMPORAL)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transformation_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                            Text("STEP 4", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Text(
                            text = "Transformation Controls",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // Face Identity Strength Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Face Identity Fidelity", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Text("${(settings.faceIdentityStrength * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        }
                        Slider(
                            value = settings.faceIdentityStrength,
                            onValueChange = { v -> viewModel.updateSettings { copy(faceIdentityStrength = v) } },
                            valueRange = 0.5f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan),
                            modifier = Modifier.testTag("face_identity_slider")
                        )
                        Text(
                            text = "Preserves facial expressions, gaze, mouth sync & natural skin texture.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Body Transformation Toggle & Proportion Match
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Body Proportion Morphing", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                                Text("Approximate shoulder width, arm & torso proportions", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = settings.bodyTransformationEnabled,
                                onCheckedChange = { v -> viewModel.updateSettings { copy(bodyTransformationEnabled = v) } },
                                colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan, checkedTrackColor = CyberCyan.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("body_transformation_switch")
                            )
                        }

                        if (settings.bodyTransformationEnabled) {
                            Slider(
                                value = settings.bodyProportionMatch,
                                onValueChange = { v -> viewModel.updateSettings { copy(bodyProportionMatch = v) } },
                                valueRange = 0.5f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = NeonPurple, activeTrackColor = NeonPurple),
                                modifier = Modifier.testTag("body_proportion_slider")
                            )
                        }
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Hair Mode Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Hairstyle & Appearance", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HairMode.values().forEach { mode ->
                                val isSelected = settings.hairMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateSettings { copy(hairMode = mode) } },
                                    label = { Text(mode.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                        selectedLabelColor = CyberCyan
                                    ),
                                    modifier = Modifier.testTag("hair_mode_${mode.name}")
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Clothing Option Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Garment & Clothing Option", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClothingOption.values().forEach { opt ->
                                val isSelected = settings.clothingOption == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateSettings { copy(clothingOption = opt) } },
                                    label = { Text(opt.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonPurple.copy(alpha = 0.2f),
                                        selectedLabelColor = NeonPurpleLight
                                    ),
                                    modifier = Modifier.testTag("clothing_opt_${opt.name}")
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Temporal Consistency Engine Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Temporal Consistency & Anti-Flicker", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Text("${(settings.temporalConsistencyStrength * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        }
                        Slider(
                            value = settings.temporalConsistencyStrength,
                            onValueChange = { v -> viewModel.updateSettings { copy(temporalConsistencyStrength = v) } },
                            valueRange = 0.7f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = CyberCyan, activeTrackColor = CyberCyan),
                            modifier = Modifier.testTag("temporal_consistency_slider")
                        )
                        Text(
                            text = "Multi-frame optical flow analysis prevents identity jitter across fast movements.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    HorizontalDivider(color = ObsidianBorder)

                    // Performance Mode
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Performance & Quality Mode", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PerformanceMode.values().forEach { mode ->
                                val isSelected = settings.performanceMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateSettings { copy(performanceMode = mode) } },
                                    label = { Text(mode.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AmberGlow.copy(alpha = 0.2f),
                                        selectedLabelColor = AmberGlow
                                    ),
                                    modifier = Modifier.testTag("perf_mode_${mode.name}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // STEP 5: PREVIEW SYSTEM & SIDE-BY-SIDE COMPARISON
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                            Text("STEP 5", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Text(
                            text = "Side-by-Side Comparison Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    TextButton(
                        onClick = { viewModel.startTransformation("Quick Preview Render") },
                        modifier = Modifier.testTag("regenerate_preview_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Regenerate", color = CyberCyan, fontSize = 12.sp)
                    }
                }

                VideoComparisonSlider(
                    originalDrawableRes = R.drawable.sample_dancer,
                    transformedDrawableRes = R.drawable.hero_showcase,
                    selectedDurationSec = previewDurationSec,
                    onDurationChange = { previewDurationSec = it },
                    showControls = true
                )
            }
        }

        // STEP 6: EXECUTE FULL TRANSFORMATION
        item {
            Button(
                onClick = { viewModel.startTransformation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_full_processing_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = ObsidianBg
                )
            ) {
                Icon(Icons.Default.MovieFilter, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Full Video Processing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Add Reference Dialog Sheet
    if (showAddReferenceSheet) {
        AlertDialog(
            onDismissRequest = { showAddReferenceSheet = false },
            title = {
                Text("Add Identity Reference", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Register an authorized reference photo of yourself to generate identity embeddings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = newRefLabel,
                        onValueChange = { newRefLabel = it },
                        label = { Text("Reference Label") },
                        placeholder = { Text("e.g. Neutral Face 30° Left") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ref_label_input")
                    )
                    Text("Angle / Type:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("FRONT", "LEFT", "RIGHT", "BODY").forEach { t ->
                            FilterChip(
                                selected = newRefType == t,
                                onClick = { newRefType = t },
                                label = { Text(t, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val label = if (newRefLabel.isNotBlank()) newRefLabel else "Authorized User Reference"
                        viewModel.addIdentityReference(label, newRefType, "sample_identity")
                        showAddReferenceSheet = false
                        newRefLabel = ""
                    },
                    modifier = Modifier.testTag("confirm_add_reference_button")
                ) {
                    Text("Save Reference")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReferenceSheet = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = ObsidianCard
        )
    }
}
