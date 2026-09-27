package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transformation_projects")
data class TransformationProject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val projectType: String,
    val sourceMediaUri: String,
    val sourceMediaName: String,
    val selectedPersonLabel: String,
    val performanceMode: String,
    val outputResolution: String,
    val cinematicLook: String,
    val clothingOption: String,
    val status: String,
    val progress: Int,
    val durationSec: Int,
    val originalFps: Int,
    val fileSizeMb: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val hasOutput: Boolean = false,
    val outputMediaUri: String = "",
    val outputCodec: String = "H.264 / AVC",
    val qualityScore: Int = 98
)
