package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.ProjectRepository
import com.example.data.local.AppDatabase
import com.example.data.local.IdentityReferenceEntity
import com.example.data.local.TransformationProject
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransformerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao())
        seedInitialReferencesIfEmpty()
    }

    // Projects list from Room
    val projects: StateFlow<List<TransformationProject>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Identity references from Room
    val identityReferences: StateFlow<List<IdentityReferenceEntity>> = repository.allReferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Source Video
    private val _currentVideo = MutableStateFlow(
        VideoInfo(
            title = "Studio Performance - Dynamic Solo Choreography",
            durationSec = 24,
            resolution = "1920x1080 (1080p)",
            fps = 60,
            fileSizeMb = 84.5,
            aspectRatio = "16:9",
            detectedPeopleCount = 2,
            hasAudio = true
        )
    )
    val currentVideo: StateFlow<VideoInfo> = _currentVideo.asStateFlow()

    // Quality Diagnostics
    private val _qualityDiagnostics = MutableStateFlow(
        QualityDiagnostics(
            originalResolution = "1080p FHD (1920x1080)",
            fps = 60,
            bitrate = "24.5 Mbps (High)",
            compressionLevel = "Moderate (H.264 Main@L4.2)",
            noiseLevel = "Low (ISO 400)",
            blurLevel = "Sharp (Fast Shutter 1/120s)",
            faceQuality = "Excellent (280x280px patch)",
            motionBlur = "Controlled Motion Vector",
            dynamicRange = "11.4 Stops (Studio Log)",
            recommendedPreset = "AI Identity Transfer + Temporal Consistency + 4K Super-Resolution"
        )
    )
    val qualityDiagnostics: StateFlow<QualityDiagnostics> = _qualityDiagnostics.asStateFlow()

    // Detected People in Video
    private val _detectedPeople = MutableStateFlow(
        listOf(
            PersonDetection(
                id = 1,
                label = "Dancer A [Lead Performer]",
                role = "Primary Subject • Center Stage",
                confidence = 0.985f,
                xRatio = 0.28f,
                yRatio = 0.12f,
                widthRatio = 0.44f,
                heightRatio = 0.82f,
                isSelected = true,
                motionDescription = "Complex footwork, 360-degree torso rotation, arm extensions"
            ),
            PersonDetection(
                id = 2,
                label = "Dancer B [Backing]",
                role = "Secondary Subject • Stage Left",
                confidence = 0.932f,
                xRatio = 0.05f,
                yRatio = 0.22f,
                widthRatio = 0.22f,
                heightRatio = 0.65f,
                isSelected = false,
                motionDescription = "Rhythmic background step"
            )
        )
    )
    val detectedPeople: StateFlow<List<PersonDetection>> = _detectedPeople.asStateFlow()

    private val _selectedPersonId = MutableStateFlow(1)
    val selectedPersonId: StateFlow<Int> = _selectedPersonId.asStateFlow()

    // Settings
    private val _settings = MutableStateFlow(TransformationSettings())
    val settings: StateFlow<TransformationSettings> = _settings.asStateFlow()

    // Clothing Reference Profile
    private val _clothingProfile = MutableStateFlow(
        ClothingReferenceProfile(
            garmentType = "Cyber Techwear Bomber Jacket",
            fabricType = "Water-resistant matte technical nylon with elastic ribbing",
            colorName = "Midnight Obsidian & Silver Chrome",
            patternType = "Solid paneling with metallic zipper hardware",
            promptDescription = "High-end black cyber techwear bomber jacket with silver zippers and structured collar",
            hasZippersOrButtons = true,
            textureDetail = "Fine weave with specular metallic reflections on seams"
        )
    )
    val clothingProfile: StateFlow<ClothingReferenceProfile> = _clothingProfile.asStateFlow()

    // Pipeline State
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _showPipelineDialog = MutableStateFlow(false)
    val showPipelineDialog: StateFlow<Boolean> = _showPipelineDialog.asStateFlow()

    private val _pipelineProgress = MutableStateFlow(0)
    val pipelineProgress: StateFlow<Int> = _pipelineProgress.asStateFlow()

    private val _currentStageTitle = MutableStateFlow("Initializing Pipeline")
    val currentStageTitle: StateFlow<String> = _currentStageTitle.asStateFlow()

    private val _currentStageDetail = MutableStateFlow("Preparing neural model weights on GPU...")
    val currentStageDetail: StateFlow<String> = _currentStageDetail.asStateFlow()

    private val _currentChunk = MutableStateFlow(1)
    val currentChunk: StateFlow<Int> = _currentChunk.asStateFlow()

    private val _totalChunks = MutableStateFlow(3)
    val totalChunks: StateFlow<Int> = _totalChunks.asStateFlow()

    private val _pipelineStages = MutableStateFlow(buildDefaultPipelineStages())
    val pipelineStages: StateFlow<List<PipelineStageInfo>> = _pipelineStages.asStateFlow()

    // Consent
    private val _isConsentGranted = MutableStateFlow(false)
    val isConsentGranted: StateFlow<Boolean> = _isConsentGranted.asStateFlow()

    private val _showConsentDialog = MutableStateFlow(false)
    val showConsentDialog: StateFlow<Boolean> = _showConsentDialog.asStateFlow()

    // Quality Control Evaluation results
    private val _qcStatus = MutableStateFlow("Passed • 98.6% Identity Fidelity")
    val qcStatus: StateFlow<String> = _qcStatus.asStateFlow()

    private var processingJob: Job? = null

    // ---------------- Actions ----------------

    fun selectPerson(personId: Int) {
        _selectedPersonId.value = personId
        _detectedPeople.update { list ->
            list.map { it.copy(isSelected = it.id == personId) }
        }
    }

    fun updateSettings(modifier: TransformationSettings.() -> TransformationSettings) {
        _settings.update { it.modifier() }
    }

    fun updateClothingProfile(modifier: ClothingReferenceProfile.() -> ClothingReferenceProfile) {
        _clothingProfile.update { it.modifier() }
    }

    fun openConsentDialog() {
        _showConsentDialog.value = true
    }

    fun closeConsentDialog() {
        _showConsentDialog.value = false
    }

    fun grantConsent() {
        _isConsentGranted.value = true
        _showConsentDialog.value = false
    }

    fun openPipelineDialog() {
        _showPipelineDialog.value = true
    }

    fun closePipelineDialog() {
        _showPipelineDialog.value = false
    }

    fun startTransformation(projectTitle: String = "Dance Identity Morph - Studio") {
        if (!_isConsentGranted.value) {
            _showConsentDialog.value = true
            return
        }

        _isProcessing.value = true
        _showPipelineDialog.value = true
        _pipelineProgress.value = 0
        _currentChunk.value = 1
        _totalChunks.value = 3

        processingJob?.cancel()
        processingJob = viewModelScope.launch {
            val stagesDef = buildDefaultPipelineStages()
            _pipelineStages.value = stagesDef

            for (i in stagesDef.indices) {
                val stage = stagesDef[i]
                _currentStageTitle.value = stage.stageName
                _currentStageDetail.value = stage.description

                // Update stages state
                _pipelineStages.update { list ->
                    list.mapIndexed { index, item ->
                        when {
                            index < i -> item.copy(isCompleted = true, isCurrent = false)
                            index == i -> item.copy(isCurrent = true, isCompleted = false)
                            else -> item.copy(isCurrent = false, isCompleted = false)
                        }
                    }
                }

                // Chunk handling simulation
                if (i in 6..11) {
                    val chunkIdx = (i - 6) / 2 + 1
                    _currentChunk.value = chunkIdx.coerceIn(1, 3)
                }

                // Smooth progress tick
                val targetProgress = ((i + 1).toFloat() / stagesDef.size * 100).toInt()
                val currentP = _pipelineProgress.value
                val step = (targetProgress - currentP) / 5
                for (t in 1..5) {
                    delay(120)
                    _pipelineProgress.value = (currentP + step * t).coerceAtMost(targetProgress)
                }
                _pipelineProgress.value = targetProgress
            }

            // Complete
            _pipelineStages.update { list -> list.map { it.copy(isCompleted = true, isCurrent = false) } }
            _currentStageTitle.value = "Transformation Complete"
            _currentStageDetail.value = "Final video rendered with audio synchronization and 98.6% identity consistency."
            _pipelineProgress.value = 100
            _isProcessing.value = false
            _qcStatus.value = "Passed Quality Check • Identity Stability: 99.1% • Temporal Jitter: 0.04%"

            // Save to Room Database
            val selectedPerson = _detectedPeople.value.firstOrNull { it.id == _selectedPersonId.value }?.label ?: "Lead Subject"
            val newProject = TransformationProject(
                title = projectTitle,
                projectType = ProjectType.VIDEO_TRANSFORMATION.name,
                sourceMediaUri = "sample_dancer",
                sourceMediaName = _currentVideo.value.title,
                selectedPersonLabel = selectedPerson,
                performanceMode = _settings.value.performanceMode.label,
                outputResolution = _settings.value.outputResolution.label,
                cinematicLook = _settings.value.cinematicLook.label,
                clothingOption = _settings.value.clothingOption.label,
                status = ProcessingStatus.COMPLETED.name,
                progress = 100,
                durationSec = _currentVideo.value.durationSec,
                originalFps = _currentVideo.value.fps,
                fileSizeMb = _currentVideo.value.fileSizeMb,
                hasOutput = true,
                outputMediaUri = "hero_showcase",
                outputCodec = _settings.value.videoCodec.label,
                qualityScore = 99
            )
            repository.saveProject(newProject)
        }
    }

    fun cancelTransformation() {
        processingJob?.cancel()
        _isProcessing.value = false
        _showPipelineDialog.value = false
        _pipelineProgress.value = 0
    }

    fun loadCustomSourceVideo(name: String, durationSec: Int, resolution: String, fps: Int, sizeMb: Double) {
        _currentVideo.value = VideoInfo(
            title = name,
            durationSec = durationSec,
            resolution = resolution,
            fps = fps,
            fileSizeMb = sizeMb,
            aspectRatio = "16:9",
            detectedPeopleCount = 1,
            hasAudio = true
        )
    }

    fun addIdentityReference(label: String, type: String, imageUri: String) {
        viewModelScope.launch {
            val entity = IdentityReferenceEntity(
                label = label,
                referenceType = type,
                imageUri = imageUri,
                qualityStatus = "EXCELLENT",
                resolutionInfo = "2048x2048 (High Res)",
                lightingCheck = true,
                facialObstructionFree = true
            )
            repository.addReference(entity)
        }
    }

    fun deleteReference(id: Long) {
        viewModelScope.launch {
            repository.deleteReference(id)
        }
    }

    fun deleteAllReferences() {
        viewModelScope.launch {
            repository.deleteAllReferences()
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllProjects()
            repository.deleteAllReferences()
            _isConsentGranted.value = false
        }
    }

    private fun seedInitialReferencesIfEmpty() {
        viewModelScope.launch {
            val existing = repository.allReferences.first()
            if (existing.isEmpty()) {
                val sampleRefs = listOf(
                    IdentityReferenceEntity(
                        label = "Authorized Face - Front Profile",
                        referenceType = "FRONT",
                        imageUri = "sample_identity",
                        qualityStatus = "EXCELLENT",
                        resolutionInfo = "2048x2048",
                        lightingCheck = true,
                        facialObstructionFree = true
                    ),
                    IdentityReferenceEntity(
                        label = "Authorized Face - Left 45° Angle",
                        referenceType = "LEFT_PROFILE",
                        imageUri = "sample_identity",
                        qualityStatus = "EXCELLENT",
                        resolutionInfo = "2048x2048",
                        lightingCheck = true,
                        facialObstructionFree = true
                    ),
                    IdentityReferenceEntity(
                        label = "Authorized Face - Right 45° Angle",
                        referenceType = "RIGHT_PROFILE",
                        imageUri = "sample_identity",
                        qualityStatus = "EXCELLENT",
                        resolutionInfo = "2048x2048",
                        lightingCheck = true,
                        facialObstructionFree = true
                    ),
                    IdentityReferenceEntity(
                        label = "Authorized Full-Body Silhouette",
                        referenceType = "FULL_BODY",
                        imageUri = "sample_identity",
                        qualityStatus = "GOOD",
                        resolutionInfo = "1920x2560",
                        lightingCheck = true,
                        facialObstructionFree = true
                    )
                )
                sampleRefs.forEach { repository.addReference(it) }
            }
        }
    }

    private fun buildDefaultPipelineStages(): List<PipelineStageInfo> {
        return listOf(
            PipelineStageInfo(1, "Source Video Ingestion", "Parsing video container, demuxing audio tracks and decoding frame rate.", 7),
            PipelineStageInfo(2, "Video Quality Diagnostics", "Analyzing dynamic range, noise floor, shutter blur & compression artifacts.", 14),
            PipelineStageInfo(3, "Person Detection & Segmentation", "Identifying distinct human subjects and segmenting lead dancer.", 21),
            PipelineStageInfo(4, "Pose & Motion Extraction", "Tracking 33 skeletal landmarks and choreography trajectory.", 28),
            PipelineStageInfo(5, "Face Landmark & Expression Analysis", "Mapping 468 dense facial mesh points, gaze direction and lip velocity.", 35),
            PipelineStageInfo(6, "Identity Reference Profiling", "Extracting high-dimensional facial biometric embedding from authorized photos.", 42),
            PipelineStageInfo(7, "Face Identity Transformation", "Synthesizing target facial structure while preserving performance expressions.", 50),
            PipelineStageInfo(8, "Body Silhouette & Proportion Morphing", "Adapting body proportions, shoulder width and posture lines.", 58),
            PipelineStageInfo(9, "Hair & Appearance Processing", "Synthesizing adapted hairstyle and fine hair strand dynamics.", 65),
            PipelineStageInfo(10, "Lighting, Shadows & Environment Matching", "Extracting spherical harmonic scene lighting and applying specular skin response.", 73),
            PipelineStageInfo(11, "Multi-Frame Temporal Consistency Engine", "Aligning neighboring frames with optical flow to eliminate identity flickering.", 81),
            PipelineStageInfo(12, "Artifact Removal & Flow Smoothing", "Eliminating seam boundaries, warping distortions and limb edge jitter.", 88),
            PipelineStageInfo(13, "Super-Resolution & Neural Upscaling", "Enhancing fine textures, skin micro-details and upscaling to target resolution.", 94),
            PipelineStageInfo(14, "Audio-Video Restoration & Export", "Synchronizing original uncompressed audio track and packaging final stream.", 100)
        )
    }
}
