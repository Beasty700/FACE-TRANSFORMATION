package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.PersonDetection
import com.example.ui.theme.*

@Composable
fun PoseOverlayCanvas(
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.sample_dancer,
    detectedPeople: List<PersonDetection>,
    selectedPersonId: Int,
    onSelectPerson: (Int) -> Unit,
    showPoseLandmarks: Boolean = true,
    onToggleLandmarks: (Boolean) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("pose_overlay_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
    ) {
        Column {
            // Header with toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "PERSON & MOTION DETECTION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                    Text(
                        text = "${detectedPeople.size} subjects identified • Tap to select target",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onToggleLandmarks(!showPoseLandmarks) },
                        modifier = Modifier.testTag("toggle_pose_landmarks_button")
                    ) {
                        Icon(
                            imageVector = if (showPoseLandmarks) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle pose skeleton",
                            tint = if (showPoseLandmarks) CyberCyan else TextMuted
                        )
                    }
                }
            }

            // Interactive Frame Canvas
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(0.dp))
                    .background(Color.Black)
            ) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                // Video Frame Background
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Video analysis frame",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Pose Landmark Lines Canvas (Skeleton visualizer)
                if (showPoseLandmarks) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        detectedPeople.forEach { person ->
                            val color = if (person.id == selectedPersonId) CyberCyan else NeonPurple.copy(alpha = 0.6f)
                            val strokeWidth = if (person.id == selectedPersonId) 4f else 2.5f

                            // Approximate pose keypoints based on person bounding box
                            val cx = (person.xRatio + person.widthRatio / 2f) * w
                            val topY = person.yRatio * h
                            val bottomY = (person.yRatio + person.heightRatio) * h

                            val headPos = Offset(cx, topY + (bottomY - topY) * 0.12f)
                            val neckPos = Offset(cx, topY + (bottomY - topY) * 0.22f)
                            val leftShoulder = Offset(cx - (person.widthRatio * w * 0.28f), topY + (bottomY - topY) * 0.26f)
                            val rightShoulder = Offset(cx + (person.widthRatio * w * 0.28f), topY + (bottomY - topY) * 0.26f)

                            val leftElbow = Offset(cx - (person.widthRatio * w * 0.38f), topY + (bottomY - topY) * 0.42f)
                            val rightElbow = Offset(cx + (person.widthRatio * w * 0.38f), topY + (bottomY - topY) * 0.42f)
                            val leftWrist = Offset(cx - (person.widthRatio * w * 0.44f), topY + (bottomY - topY) * 0.55f)
                            val rightWrist = Offset(cx + (person.widthRatio * w * 0.44f), topY + (bottomY - topY) * 0.55f)

                            val pelvisPos = Offset(cx, topY + (bottomY - topY) * 0.56f)
                            val leftHip = Offset(cx - (person.widthRatio * w * 0.18f), topY + (bottomY - topY) * 0.56f)
                            val rightHip = Offset(cx + (person.widthRatio * w * 0.18f), topY + (bottomY - topY) * 0.56f)

                            val leftKnee = Offset(cx - (person.widthRatio * w * 0.20f), topY + (bottomY - topY) * 0.76f)
                            val rightKnee = Offset(cx + (person.widthRatio * w * 0.20f), topY + (bottomY - topY) * 0.76f)
                            val leftAnkle = Offset(cx - (person.widthRatio * w * 0.22f), bottomY - 10f)
                            val rightAnkle = Offset(cx + (person.widthRatio * w * 0.22f), bottomY - 10f)

                            // Head circle
                            drawCircle(color = color, radius = 10f, center = headPos, style = Stroke(width = strokeWidth))
                            // Torso spine
                            drawLine(color, headPos, neckPos, strokeWidth)
                            drawLine(color, neckPos, pelvisPos, strokeWidth)
                            drawLine(color, leftShoulder, rightShoulder, strokeWidth)
                            // Arms
                            drawLine(color, neckPos, leftShoulder, strokeWidth)
                            drawLine(color, leftShoulder, leftElbow, strokeWidth)
                            drawLine(color, leftElbow, leftWrist, strokeWidth)
                            drawLine(color, neckPos, rightShoulder, strokeWidth)
                            drawLine(color, rightShoulder, rightElbow, strokeWidth)
                            drawLine(color, rightElbow, rightWrist, strokeWidth)
                            // Hips & Legs
                            drawLine(color, pelvisPos, leftHip, strokeWidth)
                            drawLine(color, leftHip, leftKnee, strokeWidth)
                            drawLine(color, leftKnee, leftAnkle, strokeWidth)
                            drawLine(color, pelvisPos, rightHip, strokeWidth)
                            drawLine(color, rightHip, rightKnee, strokeWidth)
                            drawLine(color, rightKnee, rightAnkle, strokeWidth)

                            // Joints dots
                            listOf(headPos, neckPos, leftShoulder, rightShoulder, leftElbow, rightElbow, leftWrist, rightWrist, pelvisPos, leftHip, rightHip, leftKnee, rightKnee, leftAnkle, rightAnkle).forEach { joint ->
                                drawCircle(color = if (person.id == selectedPersonId) CyberCyan else Color.White, radius = 4f, center = joint)
                            }
                        }
                    }
                }

                // Interactive Bounding Boxes
                detectedPeople.forEach { person ->
                    val isSelected = person.id == selectedPersonId
                    val borderColor = if (isSelected) CyberCyan else Color.White.copy(alpha = 0.5f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = boxWidth * person.xRatio,
                                top = boxHeight * person.yRatio,
                                end = boxWidth * (1f - (person.xRatio + person.widthRatio)),
                                bottom = boxHeight * (1f - (person.yRatio + person.heightRatio))
                            )
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.12f) else Color.Transparent)
                            .clickable { onSelectPerson(person.id) }
                            .testTag("person_box_${person.id}")
                    ) {
                        // Tag Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(y = (-14).dp),
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) CyberCyan else ObsidianBg.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = ObsidianBg,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = if (isSelected) "TARGET: ${person.label}" else person.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) ObsidianBg else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Person Selector Radio Pills below frame
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Person to Transform:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    detectedPeople.forEach { person ->
                        val isSelected = person.id == selectedPersonId
                        OutlinedCard(
                            onClick = { onSelectPerson(person.id) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("person_selector_card_${person.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) CyberCyan.copy(alpha = 0.12f) else ObsidianSurfaceVariant
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyberCyan else ObsidianBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = person.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) CyberCyan else TextPrimary
                                    )
                                    if (isSelected) {
                                        Badge(containerColor = CyberCyan, contentColor = ObsidianBg) {
                                            Text("Active", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = person.role,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Confidence: ${(person.confidence * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
