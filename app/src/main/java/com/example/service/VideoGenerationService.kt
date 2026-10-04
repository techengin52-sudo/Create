package com.example.service

import com.example.model.VideoAspectRatio
import com.example.model.VideoStyle
import kotlinx.coroutines.delay

data class VideoGenerationRequest(
    val prompt: String,
    val style: VideoStyle,
    val durationSeconds: Int,
    val aspectRatio: VideoAspectRatio
)

data class VideoGenerationResponse(
    val isSuccess: Boolean,
    val videoUrl: String,
    val thumbnailResId: Int,
    val resolution: String = "1080p 60fps HDR",
    val errorMessage: String? = null
)

/**
 * Modular AI Video Generation Service interface.
 * Can be swapped with custom REST API implementations (e.g., Runway Gen-3, Luma Dream Machine, Google Veo, Replicate).
 */
interface VideoGenerationService {
    suspend fun generateVideo(
        request: VideoGenerationRequest,
        onProgress: suspend (progress: Int, stageText: String) -> Unit
    ): VideoGenerationResponse
}

class DefaultVideoGenerationService : VideoGenerationService {

    override suspend fun generateVideo(
        request: VideoGenerationRequest,
        onProgress: suspend (progress: Int, stageText: String) -> Unit
    ): VideoGenerationResponse {
        val stages = listOf(
            Pair(15, "Analyzing prompt semantic structure & camera choreography..."),
            Pair(35, "Synthesizing latent diffusion keyframes for ${request.style.title} style..."),
            Pair(60, "Simulating volumetric lighting & fluid motion coherence..."),
            Pair(82, "Synthesizing AI ambient audio & cinematic soundscape..."),
            Pair(95, "Mastering ${request.durationSeconds}s cut at 1080p 60fps HDR..."),
            Pair(100, "Finalizing render & encoding video stream...")
        )

        for (stage in stages) {
            // Realistic generation delay
            delay(900)
            onProgress(stage.first, stage.second)
        }

        // Return the video URL mapped to the chosen style or remote URL
        val targetUrl = request.style.sampleVideoUrl

        return VideoGenerationResponse(
            isSuccess = true,
            videoUrl = targetUrl,
            thumbnailResId = request.style.drawableRes,
            resolution = if (request.durationSeconds >= 30) "4K 60fps Cinema" else "1080p 60fps HDR"
        )
    }
}
