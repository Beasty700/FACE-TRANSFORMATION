package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.model.*
import com.example.ui.TransformerViewModel
import com.example.ui.components.VideoComparisonSlider
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageTransformScreen(
    viewModel: TransformerViewModel,
    onNavigateBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val clothingProfile by viewModel.clothingProfile.collectAsState()
    val isConsentGranted by viewModel.isConsentGranted.collectAsState()

    var clothingPromptText by remember { mutableStateOf(clothingProfile.promptDescription) }
    var selectedClothingMode by remember { mutableStateOf(ClothingMode.REFERENCE_CLOTHING) }
    var selectedGarmentCategory by remember { mutableStateOf("Jacket") }

    val garmentCategories = listOf(
        "T-Shirt", "Shirt", "Jacket", "Suit", "Dress", "Hoodie",
        "Sweater", "Jeans", "Trousers", "Sportswear", "Formal", "Traditional"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("image_transform_screen_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
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
                        modifier = Modifier.testTag("image_transform_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Column {
                        Text(
                            text = "Photo & Clothing Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Identity Transfer & Virtual Garment Fitting",
                            style = MaterialTheme.typography.labelSmall,
                            color = NeonPurpleLight
                        )
                    }
                }

                FilledTonalButton(
                    onClick = { viewModel.startTransformation("Photo Identity & Garment Fitting") },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = NeonPurple,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("render_photo_transformation_button")
                ) {
                    Icon(Icons.Default.AutoFixNormal, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Render", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Split Comparison Area: Original vs Transformed Photo
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ORIGINAL PHOTO | EDITED PHOTO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                VideoComparisonSlider(
                    originalDrawableRes = R.drawable.sample_dancer,
                    transformedDrawableRes = R.drawable.hero_showcase,
                    showControls = false
                )
            }
        }

        // AI CLOTHING TRANSFORMATION SECTION (Section 22)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_clothing_transformation_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                                    .background(NeonPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Checkroom, contentDescription = null, tint = NeonPurpleLight, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                text = "AI Clothing Transformation",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Text(
                        text = "Realistically replace clothing in images or videos while preserving face, identity, body movement, and anatomical folds.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    // 3 Transformation Modes
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Clothing Mode", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClothingMode.values().forEach { mode ->
                                val isSelected = selectedClothingMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedClothingMode = mode },
                                    label = { Text(mode.label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonPurple.copy(alpha = 0.2f),
                                        selectedLabelColor = NeonPurpleLight
                                    ),
                                    modifier = Modifier.testTag("clothing_mode_${mode.name}")
                                )
                            }
                        }
                    }

                    // Mode 1: Reference Clothing Upload & Extraction Profile
                    if (selectedClothingMode == ClothingMode.REFERENCE_CLOTHING) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = ObsidianSurfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.sample_jacket),
                                            contentDescription = "Uploaded Clothing Reference",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = clothingProfile.garmentType,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Material: ${clothingProfile.fabricType}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Text(
                                            text = "Palette: ${clothingProfile.colorName}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberCyan,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ObsidianBg.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = "INTERNAL CLOTHING PROFILE: Isolated from background. Geometry, reflectivity, zipper metallic response, and dynamic fold vectors calibrated.",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Mode 2: Clothing Description
                    if (selectedClothingMode == ClothingMode.CLOTHING_DESCRIPTION) {
                        OutlinedTextField(
                            value = clothingPromptText,
                            onValueChange = {
                                clothingPromptText = it
                                viewModel.updateClothingProfile { copy(promptDescription = it) }
                            },
                            label = { Text("Garment Prompt Description") },
                            placeholder = { Text("e.g. Black premium leather jacket with silver zipper and realistic fabric texture") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clothing_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonPurple,
                                focusedLabelColor = NeonPurpleLight
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Garment Categories Horizontal Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Select Garment Silhouette", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextMuted)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(garmentCategories) { cat ->
                                val isSelected = selectedGarmentCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedGarmentCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.15f),
                                        selectedLabelColor = CyberCyan
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // IMAGE RESTORATION & FINISH
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_enhancements_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Facial Finish & Cinematic Look",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Face restoration
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("AI Face Detail Restoration", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                            Text("Reconstructs eye clarity, iris reflection & hairline", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = TextSecondary)
                        }
                        Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                            Text("ACTIVE", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    // Cinematic Look Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(CinematicLook.CINEMATIC, CinematicLook.FILM_LOOK, CinematicLook.NATURAL).forEach { look ->
                            val isSelected = settings.cinematicLook == look
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateSettings { copy(cinematicLook = look) } },
                                label = { Text(look.label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Render Action Button
        item {
            Button(
                onClick = { viewModel.startTransformation("Photo Identity & Garment Fitting") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("execute_photo_transform_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple,
                    contentColor = ObsidianBg
                )
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Process & Save Image", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
