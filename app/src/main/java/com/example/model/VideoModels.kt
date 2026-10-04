package com.example.model

import com.example.R

enum class VideoStyle(
    val title: String,
    val description: String,
    val drawableRes: Int,
    val sampleVideoUrl: String
) {
    CINEMATIC(
        title = "Cinematic",
        description = "Anamorphic widescreen, 35mm film grain, volumetric lighting",
        drawableRes = R.drawable.banner_cinematic,
        sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    ),
    REALISTIC(
        title = "Realistic",
        description = "Photorealistic 4K documentary texture, natural camera physics",
        drawableRes = R.drawable.thumb_realistic,
        sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    ),
    HISTORICAL(
        title = "Historical",
        description = "Period realism, warm golden hour palette, atmospheric dust",
        drawableRes = R.drawable.thumb_historical,
        sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4"
    ),
    FANTASY(
        title = "Fantasy",
        description = "Ethereal magical realms, mythical elements, glowing luminescence",
        drawableRes = R.drawable.thumb_fantasy,
        sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    )
}

enum class VideoDuration(
    val label: String,
    val seconds: Int,
    val badge: String
) {
    SEC_5("5s", 5, "Fast"),
    SEC_10("10s", 10, "Standard"),
    SEC_15("15s", 15, "Extended"),
    SEC_30("30s", 30, "HD Feature"),
    SEC_60("60s", 60, "Epic Cut")
}

enum class VideoAspectRatio(
    val label: String,
    val subtitle: String,
    val ratio: Float
) {
    WIDESCREEN("16:9", "Cinema / YT", 16f / 9f),
    PORTRAIT("9:16", "Reel / Shorts", 9f / 16f),
    SQUARE("1:1", "Square", 1f)
}

data class VideoGenerationItem(
    val id: Long = 0,
    val prompt: String,
    val durationSeconds: Int,
    val style: VideoStyle,
    val aspectRatio: VideoAspectRatio = VideoAspectRatio.WIDESCREEN,
    val videoUrl: String,
    val localFilePath: String? = null,
    val thumbnailResId: Int = style.drawableRes,
    val timestamp: Long = System.currentTimeMillis(),
    val resolution: String = "1080p 60fps HDR"
)

sealed interface GenerationUiState {
    data object Idle : GenerationUiState
    data class Generating(
        val prompt: String,
        val style: VideoStyle,
        val duration: Int,
        val progressPercent: Int,
        val stageText: String,
        val elapsedSeconds: Int
    ) : GenerationUiState
    data class Completed(
        val videoItem: VideoGenerationItem
    ) : GenerationUiState
    data class Failed(
        val errorMessage: String
    ) : GenerationUiState
}
