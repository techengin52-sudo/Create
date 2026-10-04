package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.VideoDatabase
import com.example.data.VideoRepository
import com.example.model.GenerationUiState
import com.example.model.VideoAspectRatio
import com.example.model.VideoDuration
import com.example.model.VideoGenerationItem
import com.example.model.VideoStyle
import com.example.service.DefaultVideoGenerationService
import com.example.service.PromptEnhancer
import com.example.service.VideoGenerationRequest
import com.example.service.VideoGenerationService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VideoMakerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository
    private var videoService: VideoGenerationService = DefaultVideoGenerationService()

    // Form inputs
    val promptText = MutableStateFlow("A majestic golden eagle soaring above snow-covered alpine peaks at sunrise, cinematic lighting")
    val selectedDuration = MutableStateFlow(VideoDuration.SEC_10) // 10s selected by default as requested!
    val selectedStyle = MutableStateFlow(VideoStyle.CINEMATIC)
    val selectedAspectRatio = MutableStateFlow(VideoAspectRatio.WIDESCREEN)

    // Current Generation State
    private val _uiState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val uiState: StateFlow<GenerationUiState> = _uiState.asStateFlow()

    // API settings
    val customApiKey = MutableStateFlow("")
    val customApiEndpoint = MutableStateFlow("")

    private var activeGenerationJob: Job? = null

    // Room Database recent videos stream
    val recentVideos: StateFlow<List<VideoGenerationItem>>

    init {
        val db = VideoDatabase.getDatabase(application)
        repository = VideoRepository(db.videoDao())
        recentVideos = repository.recentVideos.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Pre-seed sample videos on first app launch
        viewModelScope.launch {
            val list = db.videoDao().getAllVideos().first()
            repository.seedInitialVideosIfEmpty(list)
        }
    }

    fun onPromptChange(newText: String) {
        promptText.value = newText
    }

    fun onDurationChange(duration: VideoDuration) {
        selectedDuration.value = duration
    }

    fun onStyleChange(style: VideoStyle) {
        selectedStyle.value = style
    }

    fun onAspectRatioChange(ratio: VideoAspectRatio) {
        selectedAspectRatio.value = ratio
    }

    fun setRandomPrompt() {
        promptText.value = PromptEnhancer.getRandomPrompt()
    }

    fun enhancePrompt() {
        val current = promptText.value
        promptText.value = PromptEnhancer.enhancePrompt(current, selectedStyle.value)
    }

    fun updateApiConfig(apiKey: String, endpoint: String) {
        customApiKey.value = apiKey
        customApiEndpoint.value = endpoint
    }

    fun generateVideo() {
        val prompt = promptText.value.trim()
        if (prompt.isEmpty()) return

        val style = selectedStyle.value
        val duration = selectedDuration.value.seconds
        val ratio = selectedAspectRatio.value

        activeGenerationJob?.cancel()
        activeGenerationJob = viewModelScope.launch {
            _uiState.value = GenerationUiState.Generating(
                prompt = prompt,
                style = style,
                duration = duration,
                progressPercent = 5,
                stageText = "Initializing neural video diffusion pipeline...",
                elapsedSeconds = 0
            )

            try {
                val request = VideoGenerationRequest(
                    prompt = prompt,
                    style = style,
                    durationSeconds = duration,
                    aspectRatio = ratio
                )

                val response = videoService.generateVideo(request) { progress, stage ->
                    _uiState.value = GenerationUiState.Generating(
                        prompt = prompt,
                        style = style,
                        duration = duration,
                        progressPercent = progress,
                        stageText = stage,
                        elapsedSeconds = (progress * duration) / 100
                    )
                }

                if (response.isSuccess) {
                    val newItem = VideoGenerationItem(
                        prompt = prompt,
                        durationSeconds = duration,
                        style = style,
                        aspectRatio = ratio,
                        videoUrl = response.videoUrl,
                        thumbnailResId = response.thumbnailResId,
                        resolution = response.resolution
                    )
                    // Persist to Room Database
                    val insertedId = repository.saveVideo(newItem)
                    val savedItem = newItem.copy(id = insertedId)

                    _uiState.value = GenerationUiState.Completed(savedItem)
                } else {
                    _uiState.value = GenerationUiState.Failed(response.errorMessage ?: "Video generation failed")
                }
            } catch (_: kotlinx.coroutines.CancellationException) {
                _uiState.value = GenerationUiState.Idle
            } catch (e: Exception) {
                _uiState.value = GenerationUiState.Failed(e.localizedMessage ?: "Unexpected error during rendering")
            }
        }
    }

    fun cancelGeneration() {
        activeGenerationJob?.cancel()
        activeGenerationJob = null
        _uiState.value = GenerationUiState.Idle
    }

    fun selectRecentVideo(item: VideoGenerationItem) {
        _uiState.value = GenerationUiState.Completed(item)
    }

    fun createAgain() {
        _uiState.value = GenerationUiState.Idle
    }

    fun resetToHome() {
        _uiState.value = GenerationUiState.Idle
    }

    fun deleteVideo(id: Long) {
        viewModelScope.launch {
            repository.deleteVideo(id)
        }
    }
}
