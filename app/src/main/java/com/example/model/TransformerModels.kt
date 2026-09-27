package com.example.model

enum class PerformanceMode(val label: String, val description: String, val speedFactor: String) {
    FAST("Fast", "Lower processing quality with faster iteration speeds.", "4x Speed"),
    BALANCED("Balanced", "Balanced quality and reasonable turnaround time.", "2x Speed"),
    HIGH_QUALITY("High Quality", "Prioritizes identity fidelity, clothing & temporal stability.", "1x Normal"),
    MAXIMUM_QUALITY("Maximum Quality", "Deepest neural models, multi-pass optical flow & 4K GPU pipeline.", "GPU Intensive")
}

enum class HairMode(val label: String) {
    PRESERVE_SOURCE("Preserve Source Hair"),
    USE_USER_HAIR("Use User Hair"),
    AUTOMATIC("Automatic Fit")
}

enum class ClothingOption(val label: String) {
    PRESERVE_SOURCE("Preserve Source Clothing"),
    USE_USER_CLOTHING("Use User Clothing"),
    AUTOMATIC("Automatic Fit")
}

enum class ClothingMode(val label: String) {
    REFERENCE_CLOTHING("Reference Clothing Image"),
    CLOTHING_DESCRIPTION("Text Prompt Description"),
    AUTOMATIC_FIT("Automatic Morph & Fit")
}

enum class OutputResolution(val label: String, val width: Int, val height: Int) {
    RES_720P("720p HD", 1280, 720),
    RES_1080P("1080p Full HD", 1920, 1080),
    RES_1440P("1440p / 2K", 2560, 1440),
    RES_4K("2160p / 4K UHD", 3840, 2160)
}

enum class CinematicLook(val label: String, val description: String) {
    NATURAL("Natural", "Authentic scene colors and balanced dynamics"),
    CLEAN("Clean", "Noise-reduced sharp commercial finish"),
    CINEMATIC("Cinematic", "Rich shadow depth and refined highlight roll-off"),
    HIGH_DETAIL("High Detail", "Maximum micro-contrast and fine texture retrieval"),
    FILM_LOOK("Film Look", "Organic grain structure and filmic color grading"),
    MAX_RESTORATION("Max Restoration", "Aggressive deblur, denoising, and reconstruction")
}

enum class CameraSimulation(val label: String) {
    CINEMATIC_DIGITAL("Cinematic Digital"),
    LARGE_SENSOR("Large-Sensor Look (Full Frame)"),
    FILMIC("Filmic 35mm"),
    HDR_EXPANDED("High Dynamic Range"),
    NATURAL_CINEMA("Natural Cinema"),
    DOCUMENTARY("Premium Documentary")
}

enum class FaceRestorationStrength(val label: String, val level: Float) {
    OFF("Off", 0f),
    LOW("Low", 0.25f),
    MEDIUM("Medium", 0.5f),
    HIGH("High", 0.75f),
    MAXIMUM("Maximum", 1.0f)
}

enum class VideoCodec(val label: String, val extension: String) {
    H264("H.264 / AVC", "mp4"),
    H265_HEVC("H.265 / HEVC", "mp4"),
    AV1("AV1 Next-Gen", "webm"),
    PRORES_MOV("Apple ProRes (MOV)", "mov")
}

enum class ProjectType {
    VIDEO_TRANSFORMATION,
    IMAGE_TRANSFORMATION,
    ENHANCE_AND_UPSCALE
}

enum class ProcessingStatus {
    DRAFT,
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}

data class VideoInfo(
    val title: String,
    val durationSec: Int,
    val resolution: String,
    val fps: Int,
    val fileSizeMb: Double,
    val aspectRatio: String,
    val detectedPeopleCount: Int = 1,
    val hasAudio: Boolean = true
)

data class PersonDetection(
    val id: Int,
    val label: String,
    val role: String,
    val confidence: Float,
    val xRatio: Float,
    val yRatio: Float,
    val widthRatio: Float,
    val heightRatio: Float,
    val isSelected: Boolean = false,
    val motionDescription: String = "Fast dynamic choreography"
)

data class QualityDiagnostics(
    val originalResolution: String,
    val fps: Int,
    val bitrate: String,
    val compressionLevel: String,
    val noiseLevel: String,
    val blurLevel: String,
    val faceQuality: String,
    val motionBlur: String,
    val dynamicRange: String,
    val recommendedPreset: String
)

data class ClothingReferenceProfile(
    val garmentType: String,
    val fabricType: String,
    val colorName: String,
    val patternType: String,
    val promptDescription: String = "",
    val hasZippersOrButtons: Boolean = true,
    val textureDetail: String = "Fine weave with metallic reflections"
)

data class PipelineStageInfo(
    val stepNumber: Int,
    val stageName: String,
    val description: String,
    val progressPercent: Int,
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

data class TransformationSettings(
    val faceIdentityStrength: Float = 0.92f,
    val bodyTransformationEnabled: Boolean = true,
    val bodyProportionMatch: Float = 0.88f,
    val hairMode: HairMode = HairMode.AUTOMATIC,
    val clothingOption: ClothingOption = ClothingOption.PRESERVE_SOURCE,
    val clothingMode: ClothingMode = ClothingMode.REFERENCE_CLOTHING,
    val clothingPrompt: String = "Designer black cyber techwear bomber jacket with silver zippers",
    val backgroundPreservation: Float = 0.98f,
    val motionPreservation: Float = 0.99f,
    val expressionPreservation: Float = 0.94f,
    val lightingMatching: Float = 0.95f,
    val temporalConsistencyStrength: Float = 0.95f,
    val performanceMode: PerformanceMode = PerformanceMode.HIGH_QUALITY,
    val outputResolution: OutputResolution = OutputResolution.RES_1080P,
    val cinematicLook: CinematicLook = CinematicLook.CINEMATIC,
    val cameraSimulation: CameraSimulation = CameraSimulation.CINEMATIC_DIGITAL,
    val faceRestoration: FaceRestorationStrength = FaceRestorationStrength.MEDIUM,
    val videoCodec: VideoCodec = VideoCodec.H264,
    val preserveAudio: Boolean = true,
    val chunkProcessing: Boolean = true
)
