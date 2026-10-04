package com.example.data

import com.example.model.VideoAspectRatio
import com.example.model.VideoGenerationItem
import com.example.model.VideoStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VideoRepository(private val videoDao: VideoDao) {

    val recentVideos: Flow<List<VideoGenerationItem>> = videoDao.getAllVideos().map { entities ->
        entities.map { it.toItem() }
    }

    suspend fun saveVideo(item: VideoGenerationItem): Long {
        return videoDao.insertVideo(VideoEntity.fromItem(item))
    }

    suspend fun getVideoById(id: Long): VideoGenerationItem? {
        return videoDao.getVideoById(id)?.toItem()
    }

    suspend fun deleteVideo(id: Long) {
        videoDao.deleteVideoById(id)
    }

    suspend fun seedInitialVideosIfEmpty(existingList: List<VideoEntity>) {
        if (existingList.isEmpty()) {
            val sample1 = VideoEntity(
                prompt = "A high-speed cybernetic drone weaving through rainy neon skyscrapers in Neo-Kyoto 2088",
                durationSeconds = 10,
                styleName = VideoStyle.CINEMATIC.name,
                aspectRatioName = VideoAspectRatio.WIDESCREEN.name,
                videoUrl = VideoStyle.CINEMATIC.sampleVideoUrl,
                localFilePath = null,
                thumbnailResId = VideoStyle.CINEMATIC.drawableRes,
                timestamp = System.currentTimeMillis() - 3600_000,
                resolution = "4K 60fps HDR"
            )
            val sample2 = VideoEntity(
                prompt = "Crystalline floating islands with purple bioluminescent waterfalls drifting across celestial clouds",
                durationSeconds = 10,
                styleName = VideoStyle.FANTASY.name,
                aspectRatioName = VideoAspectRatio.WIDESCREEN.name,
                videoUrl = VideoStyle.FANTASY.sampleVideoUrl,
                localFilePath = null,
                thumbnailResId = VideoStyle.FANTASY.drawableRes,
                timestamp = System.currentTimeMillis() - 7200_000,
                resolution = "1080p 60fps HDR"
            )
            videoDao.insertVideo(sample1)
            videoDao.insertVideo(sample2)
        }
    }
}
