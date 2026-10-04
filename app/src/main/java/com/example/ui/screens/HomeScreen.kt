package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.VideoAspectRatio
import com.example.model.VideoGenerationItem
import com.example.ui.VideoMakerViewModel
import com.example.ui.components.CinematicButton
import com.example.ui.components.DurationSelector
import com.example.ui.components.RecentVideosList
import com.example.ui.components.StyleSelector
import com.example.ui.theme.CinematicAmber
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCardBorder
import com.example.ui.theme.CinematicCardSurface
import com.example.ui.theme.CinematicGold
import com.example.ui.theme.CinematicInputBg
import com.example.ui.theme.CinematicNeonCyan
import com.example.ui.theme.CinematicOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: VideoMakerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prompt by viewModel.promptText.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val selectedStyle by viewModel.selectedStyle.collectAsState()
    val selectedAspectRatio by viewModel.selectedAspectRatio.collectAsState()
    val recentVideos by viewModel.recentVideos.collectAsState()
    val apiKey by viewModel.customApiKey.collectAsState()
    val endpoint by viewModel.customApiEndpoint.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            currentApiKey = apiKey,
            currentEndpoint = endpoint,
            onSaveConfig = { newKey, newEnd ->
                viewModel.updateApiConfig(newKey, newEnd)
                Toast.makeText(context, "AI engine settings saved", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinematicBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(CinematicOrange, CinematicAmber)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "App Icon",
                        tint = CinematicBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "AI VIDEO MAKER",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Cinematic Generative Studio",
                        color = CinematicGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "API Settings",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Cinema Banner Showcase
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CinematicCardBorder),
                colors = CardDefaults.cardColors(containerColor = CinematicCardSurface)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_cinematic),
                        contentDescription = "Cinematic Showcase",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        CinematicBlack.copy(alpha = 0.9f),
                                        CinematicBlack.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = CinematicAmber.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, CinematicAmber.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "TEXT-TO-VIDEO ENGINE",
                                color = CinematicGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Turn Ideas Into Movies",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Ultra-realistic 4K AI generation in seconds",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Large Prompt Text Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = CinematicAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VIDEO PROMPT",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Enhance Prompt button
                        Surface(
                            color = CinematicCardSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CinematicCardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.enhancePrompt()
                                    Toast.makeText(context, "Cinematic camera tags applied!", Toast.LENGTH_SHORT).show()
                                }
                                .testTag("enhance_prompt_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CinematicNeonCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Enhance",
                                    color = CinematicNeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Random prompt button
                        Surface(
                            color = CinematicCardSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CinematicCardBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setRandomPrompt() }
                                .testTag("random_prompt_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = CinematicGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Surprise Me",
                                    color = CinematicGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The large text box
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.onPromptChange(it) },
                    placeholder = {
                        Text(
                            text = "Describe your scene in detail... e.g. A cyberpunk detective walking through neon rain in Tokyo, 35mm film, dramatic lighting",
                            color = TextMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("video_prompt_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CinematicInputBg,
                        unfocusedContainerColor = CinematicInputBg,
                        focusedBorderColor = CinematicAmber,
                        unfocusedBorderColor = CinematicCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = CinematicAmber
                    ),
                    trailingIcon = {
                        if (prompt.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.onPromptChange("") },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear prompt",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                )

                // Character count & quick tip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tip: Mention camera movement & lighting for best output",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${prompt.length} chars",
                        color = if (prompt.length > 300) CinematicAmber else TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Duration Selector (with 10s default)
            DurationSelector(
                selectedDuration = selectedDuration,
                onDurationSelected = { viewModel.onDurationChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Style Selector (Cinematic, Realistic, Historical, Fantasy)
            StyleSelector(
                selectedStyle = selectedStyle,
                onStyleSelected = { viewModel.onStyleChange(it) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Large "GENERATE VIDEO" Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                CinematicButton(
                    text = "GENERATE VIDEO",
                    enabled = prompt.trim().isNotEmpty(),
                    onClick = { viewModel.generateVideo() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Recent Generated Videos Section
            RecentVideosList(
                videos = recentVideos,
                onVideoSelected = { viewModel.selectRecentVideo(it) },
                onShareVideo = { video ->
                    shareVideo(context, video)
                },
                onDeleteVideo = { videoId ->
                    viewModel.deleteVideo(videoId)
                    Toast.makeText(context, "Video removed from history", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

private fun shareVideo(context: Context, item: VideoGenerationItem) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "AI Video: ${item.style.title}")
        putExtra(
            Intent.EXTRA_TEXT,
            "🎥 AI Generated Video (${item.style.title} style, ${item.durationSeconds}s):\n\"${item.prompt}\"\n\nWatch here: ${item.videoUrl}"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share AI Video"))
}
