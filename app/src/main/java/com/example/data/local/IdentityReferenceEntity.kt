package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "identity_references")
data class IdentityReferenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String,
    val referenceType: String, // FRONT, LEFT_PROFILE, RIGHT_PROFILE, SLIGHT_ANGLE, FULL_BODY, VIDEO
    val imageUri: String,
    val qualityStatus: String, // EXCELLENT, GOOD, ADEQUATE, WARNING
    val resolutionInfo: String,
    val lightingCheck: Boolean = true,
    val facialObstructionFree: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
