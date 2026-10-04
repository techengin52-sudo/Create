package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.GenerationUiState
import com.example.ui.VideoMakerViewModel
import com.example.ui.screens.GeneratingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.VideoResultScreen
import com.example.ui.theme.CinematicAmber
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCardSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: VideoMakerViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CinematicBlack)
                ) {
                    AnimatedContent(
                        targetState = uiState,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { state ->
                        when (state) {
                            is GenerationUiState.Idle -> {
                                HomeScreen(viewModel = viewModel)
                            }

                            is GenerationUiState.Generating -> {
                                BackHandler {
                                    viewModel.cancelGeneration()
                                }
                                GeneratingScreen(
                                    state = state,
                                    onCancel = { viewModel.cancelGeneration() }
                                )
                            }

                            is GenerationUiState.Completed -> {
                                BackHandler {
                                    viewModel.resetToHome()
                                }
                                VideoResultScreen(
                                    videoItem = state.videoItem,
                                    onCreateAgain = { viewModel.createAgain() },
                                    onBack = { viewModel.resetToHome() }
                                )
                            }

                            is GenerationUiState.Failed -> {
                                BackHandler {
                                    viewModel.resetToHome()
                                }
                                AlertDialog(
                                    onDismissRequest = { viewModel.resetToHome() },
                                    title = {
                                        Text(text = "Generation Error", color = TextPrimary)
                                    },
                                    text = {
                                        Text(text = state.errorMessage, color = TextSecondary)
                                    },
                                    confirmButton = {
                                        Button(
                                            onClick = { viewModel.resetToHome() },
                                            colors = ButtonDefaults.buttonColors(containerColor = CinematicAmber)
                                        ) {
                                            Text(text = "Try Again", color = CinematicBlack)
                                        }
                                    },
                                    containerColor = CinematicCardSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
