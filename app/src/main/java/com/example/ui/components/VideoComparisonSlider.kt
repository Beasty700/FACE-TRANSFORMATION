package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun VideoComparisonSlider(
    modifier: Modifier = Modifier,
    originalDrawableRes: Int = R.drawable.sample_dancer,
    transformedDrawableRes: Int = R.drawable.hero_showcase,
    selectedDurationSec: Int = 10,
    onDurationChange: (Int) -> Unit = {},
    showControls: Boolean = true,
    initialSliderPosition: Float = 0.5f
) {
    var sliderFraction by remember { mutableFloatStateOf(initialSliderPosition) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlaybackSec by remember { mutableFloatStateOf(0f) }
    var showWireframeToggle by remember { mutableStateOf(false) }

    // Playback loop simulation
    LaunchedEffect(isPlaying, selectedDurationSec) {
        if (isPlaying) {
            while (isPlaying) {
                delay(100)
                currentPlaybackSec += 0.1f
                if (currentPlaybackSec >= selectedDurationSec) {
                    currentPlaybackSec = 0f
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_comparison_slider_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column {
            // Video / Image Split View Area
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color.Black)
            ) {
                val boxWidthPx = constraints.maxWidth.toFloat()
                val dividerX = (boxWidthPx * sliderFraction).coerceIn(0f, boxWidthPx)

                // Render Background Images with split clipping
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            // Draw Right Side (Transformed Identity)
                            clipRect(
                                left = dividerX,
                                top = 0f,
                                right = size.width,
                                bottom = size.height,
                                clipOp = ClipOp.Intersect
                            ) {
                                this@drawWithContent.drawContent()
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = transformedDrawableRes),
                        contentDescription = "Transformed Identity Video Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            // Draw Left Side (Original Source)
                            clipRect(
                                left = 0f,
                                top = 0f,
                                right = dividerX,
                                bottom = size.height,
                                clipOp = ClipOp.Intersect
                            ) {
                                this@drawWithContent.drawContent()
                            }
                        }
                ) {
                    Image(
                        painter = painterResource(id = originalDrawableRes),
                        contentDescription = "Original Source Video Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Decorative Badges
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = ObsidianBg.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                ) {
                    Text(
                        text = "ORIGINAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = ObsidianBg.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberCyan)
                        )
                        Text(
                            text = "AI TRANSFORMED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan
                        )
                    }
                }

                // Draggable Divider Line & Knob
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(48.dp)
                        .offset(x = with(androidx.compose.ui.platform.LocalDensity.current) { (dividerX - 24f).toDp() })
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newX = dividerX + dragAmount.x
                                sliderFraction = (newX / boxWidthPx).coerceIn(0.05f, 0.95f)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Vertical Neon Line
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(2.5.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(CyberCyan, NeonPurple, CyberCyan)
                                )
                            )
                    )

                    // Draggable Circular Handle
                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("split_slider_handle"),
                        shape = CircleShape,
                        color = ObsidianBg,
                        border = androidx.compose.foundation.BorderStroke(2.dp, CyberCyan),
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Drag comparison slider",
                                tint = CyberCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Time Code & Frame Stamp
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(6.dp),
                    color = ObsidianBg.copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "00:${String.format("%02d", currentPlaybackSec.toInt())}:18 / 00:${String.format("%02d", selectedDurationSec)}:00",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (showControls) {
                // Controls Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Playback Scrubber Slider
                    Slider(
                        value = (currentPlaybackSec / selectedDurationSec).coerceIn(0f, 1f),
                        onValueChange = { fraction ->
                            currentPlaybackSec = fraction * selectedDurationSec
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = ObsidianBorder
                        ),
                        modifier = Modifier.testTag("video_timeline_scrubber")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Play/Pause Button
                        FilledTonalIconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier.testTag("play_pause_button"),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = ObsidianSurfaceVariant,
                                contentColor = CyberCyan
                            )
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play"
                            )
                        }

                        // Duration Selector Buttons (5s, 10s, 15s)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Preview:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                            listOf(5, 10, 15).forEach { sec ->
                                val isSelected = selectedDurationSec == sec
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { onDurationChange(sec) },
                                    label = { Text("${sec}s", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                                        selectedLabelColor = CyberCyan
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = if (isSelected) CyberCyan else ObsidianBorder,
                                        enabled = true,
                                        selected = isSelected
                                    ),
                                    modifier = Modifier.testTag("duration_${sec}s_chip")
                                )
                            }
                        }

                        // Center reset split button
                        IconButton(
                            onClick = { sliderFraction = 0.5f },
                            modifier = Modifier.testTag("reset_split_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AlignHorizontalCenter,
                                contentDescription = "Center split",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
