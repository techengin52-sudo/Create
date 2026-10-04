package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.VideoAspectRatio
import com.example.model.VideoGenerationItem
import com.example.model.VideoStyle

@Entity(tableName = "generated_videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val durationSeconds: Int,
    val styleName: String,
    val aspectRatioName: String,
    val videoUrl: String,
    val localFilePath: String?,
    val thumbnailResId: Int,
    val timestamp: Long,
    val resolution: String
) {
    fun toItem(): VideoGenerationItem {
        val style = try {
            VideoStyle.valueOf(styleName)
        } catch (_: Exception) {
            VideoStyle.CINEMATIC
        }
        val ratio = try {
            VideoAspectRatio.valueOf(aspectRatioName)
        } catch (_: Exception) {
            VideoAspectRatio.WIDESCREEN
        }

        return VideoGenerationItem(
            id = id,
            prompt = prompt,
            durationSeconds = durationSeconds,
            style = style,
            aspectRatio = ratio,
            videoUrl = videoUrl,
            localFilePath = localFilePath,
            thumbnailResId = if (thumbnailResId != 0) thumbnailResId else style.drawableRes,
            timestamp = timestamp,
            resolution = resolution
        )
    }

    companion object {
        fun fromItem(item: VideoGenerationItem): VideoEntity {
            return VideoEntity(
                id = item.id,
                prompt = item.prompt,
                durationSeconds = item.durationSeconds,
                styleName = item.style.name,
                aspectRatioName = item.aspectRatio.name,
                videoUrl = item.videoUrl,
                localFilePath = item.localFilePath,
                thumbnailResId = item.thumbnailResId,
                timestamp = item.timestamp,
                resolution = item.resolution
            )
        }
    }
}
