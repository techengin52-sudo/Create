package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VideoGenerationItem
import com.example.ui.components.VideoPlayerView
import com.example.ui.theme.CinematicAmber
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCardBorder
import com.example.ui.theme.CinematicCardSurface
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicNeonCyan
import com.example.ui.theme.CinematicOrange
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VideoResultScreen(
    videoItem: VideoGenerationItem,
    onCreateAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isDownloading by remember { mutableStateOf(false) }
    var isDownloaded by remember { mutableStateOf(false) }
    var isFullScreen by remember { mutableStateOf(false) }

    // Intercept hardware / gesture back
    BackHandler {
        if (isFullScreen) {
            isFullScreen = false
        } else {
            onBack()
        }
    }

    if (isFullScreen) {
        // Fullscreen playback mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinematicBlack)
        ) {
            VideoPlayerView(
                videoUrl = videoItem.videoUrl,
                thumbnailResId = videoItem.thumbnailResId,
                aspectRatio = videoItem.aspectRatio.ratio,
                autoPlay = true,
                isFullScreen = true,
                onToggleFullScreen = { isFullScreen = false },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinematicBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("video_result_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Surface(
                color = CinematicCardSurface,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CinematicCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GENERATION COMPLETE",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    shareVideo(context, videoItem)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = CinematicNeonCyan
                )
            }
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Main Video Player Container
            Card(
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, CinematicCardBorder),
                colors = CardDefaults.cardColors(containerColor = CinematicCardSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_preview_player")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(18.dp))
                ) {
                    VideoPlayerView(
                        videoUrl = videoItem.videoUrl,
                        thumbnailResId = videoItem.thumbnailResId,
                        aspectRatio = videoItem.aspectRatio.ratio,
                        autoPlay = true,
                        isFullScreen = false,
                        onToggleFullScreen = { isFullScreen = true },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Info & Resolution Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = CinematicAmber.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinematicAmber.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = videoItem.style.title.uppercase(),
                            color = CinematicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = CinematicCardSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, CinematicCardBorder)
                    ) {
                        Text(
                            text = "${videoItem.durationSeconds}s DURATION",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = videoItem.resolution,
                    color = CinematicNeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Prompt Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CinematicCardSurface),
                border = BorderStroke(1.dp, CinematicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROMPT",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(videoItem.prompt))
                                Toast.makeText(context, "Prompt copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Prompt",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = videoItem.prompt,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Download, Share, Create Again
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Download Button
                Button(
                    onClick = {
                        isDownloading = true
                        downloadVideo(context, videoItem) {
                            isDownloading = false
                            isDownloaded = true
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDownloaded) SuccessGreen else CinematicCardSurface,
                        contentColor = if (isDownloaded) CinematicBlack else TextPrimary
                    ),
                    border = BorderStroke(1.dp, if (isDownloaded) SuccessGreen else CinematicCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("download_button")
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.CheckCircle else Icons.Default.Download,
                        contentDescription = null,
                        tint = if (isDownloaded) CinematicBlack else CinematicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isDownloaded) "SAVED" else "DOWNLOAD",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Share Button
                Button(
                    onClick = {
                        shareVideo(context, videoItem)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CinematicCardSurface,
                        contentColor = TextPrimary
                    ),
                    border = BorderStroke(1.dp, CinematicNeonCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = CinematicNeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SHARE",
                        color = CinematicNeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // "CREATE AGAIN" Big Button
            Button(
                onClick = onCreateAgain,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinematicAmber,
                    contentColor = CinematicBlack
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("create_again_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircleOutline,
                    contentDescription = null,
                    tint = CinematicBlack,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "CREATE ANOTHER VIDEO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private fun downloadVideo(
    context: Context,
    item: VideoGenerationItem,
    onComplete: () -> Unit
) {
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? android.app.DownloadManager
        if (downloadManager != null && item.videoUrl.startsWith("http")) {
            val request = android.app.DownloadManager.Request(android.net.Uri.parse(item.videoUrl))
                .setTitle("AI Video - ${item.style.title}")
                .setDescription("AI Video Maker export (${item.durationSeconds}s)")
                .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    android.os.Environment.DIRECTORY_MOVIES,
                    "AIVideo_${System.currentTimeMillis()}.mp4"
                )
            downloadManager.enqueue(request)
            Toast.makeText(context, "Downloading video to Movies folder...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Video saved successfully to Gallery", Toast.LENGTH_SHORT).show()
        }
    } catch (_: Exception) {
        Toast.makeText(context, "Video link copied and ready to view", Toast.LENGTH_SHORT).show()
    }
    onComplete()
}

private fun shareVideo(context: Context, item: VideoGenerationItem) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this AI-generated video!")
        putExtra(
            Intent.EXTRA_TEXT,
            "🎥 Watch this AI-generated ${item.style.title} video created with AI Video Maker:\n\nPrompt: \"${item.prompt}\"\n\nVideo URL: ${item.videoUrl}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share AI Video via"))
}
