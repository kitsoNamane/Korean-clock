package com.example.koreanclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.koreanclock.data.GameTimer
import com.example.koreanclock.ui.theme.KoreanClockTheme
import com.example.koreanclock.ui.theme.onPrimaryContainerLight
import com.example.koreanclock.ui.theme.primaryContainerLight
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val TIME_TEMPLATE = "%s 시 %s 분 입니다"

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoreanClockTheme {
                ClockScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockScreen(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = AppViewModel(GameTimer(15000))
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("한국어 시계 연습",
                    fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                    color = MaterialTheme.colorScheme.primary
                ) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    viewModel.loadNextTurn()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.more_time_24px),
                    contentDescription = "Clock icon",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("시계를 보세요. 지금 몇 시예요?",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
                fontStyle = MaterialTheme.typography.titleLarge.fontStyle,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier
                    .padding(top = 16.dp)
            )

            Clock(uiState.clockHour!!, uiState.clockMinutes!!, modifier = Modifier.fillMaxWidth().padding(horizontal = 48.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    enabled = uiState.gameStarted,
                    value = uiState.playerTimeText,
                    onValueChange = { viewModel.updatePlayerTimeText(it) },
                    trailingIcon = { AnimatedVisibility(
                        visible = uiState.guessCorrect != null || uiState.guessWrong != null,
                    ) {
                        if (uiState.guessCorrect != null && uiState.guessCorrect!!) Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Correct answer indicator",
                            tint = Color.Green,
                        ) else if (uiState.guessWrong != null && uiState.guessWrong!!) Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Correct answer indicator",
                            tint = Color.Red,
                        )
                    }},
                    label = { Text("정답...",
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        color = MaterialTheme.colorScheme.primary,
                    ) },
                )

                OutlinedButton(
                    enabled = uiState.gameStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                        .padding(vertical = 4.dp),
                    onClick = {
                        viewModel.checkGuess()
                    },
                ) {
                    Text("답변 확인",
                        fontStyle = MaterialTheme.typography.bodyLarge.fontStyle,
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

            }
        }
    }
}

@Composable
fun Clock(currentHour: Int, currentMinutes: Int, modifier: Modifier = Modifier) {
    val primaryContainer = primaryContainerLight
    val onPrimaryContainer = onPrimaryContainerLight
    Canvas(modifier = modifier
        .aspectRatio(1f)
    ) {
        val radius = size.minDimension * 0.38f

        // Clock face
        drawCircle(
            color = primaryContainer,
            radius = radius,
            center = center
        )
        drawCircle(
            color = onPrimaryContainer,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw hour and minute lines
        val tickLength = radius * 0.1f
        val smallTickLength = radius * 0.05f
        for(i in 0 until 60) {
            val angle = i * 6f
            val angleRad = (angle - 90) * (PI / 180).toFloat()
            val isHour = i % 5 == 0
            val length = if (isHour) tickLength else smallTickLength
            val strokeWidth = if(isHour) 2.dp.toPx() else 1.dp.toPx()
            val start = Offset(
                x = center.x + cos(angleRad) * (radius - length),
                y = center.y + sin(angleRad) * (radius - length)
            )
            val end = Offset(
                x = center.x + cos(angleRad) * radius,
                y = center.y + sin(angleRad) * radius
            )
            drawLine(
                color = onPrimaryContainer,
                start = start,
                end = end,
                strokeWidth = strokeWidth
            )
        }

        // Draw the hour clock hand
        val hourAngleDegrees = (currentHour % 12) * 30 + (currentMinutes * 0.5)
        val hourAngle = ((hourAngleDegrees - 90) * (PI / 180))
        val hourHandLength = radius * 0.5f
        drawLine(
            color = onPrimaryContainer,
            start = center,
            end = Offset(center.x + hourHandLength * cos(hourAngle).toFloat(), center.y + hourHandLength * sin(hourAngle).toFloat()),
            strokeWidth = 2.dp.toPx()
        )

        // Draw the minute clock hand
        val minutesAngle= (((currentMinutes * 6) - 90F) * (PI / 180))
        val minuteHandLength = radius * 0.7f
        drawLine(
            color = onPrimaryContainer,
            start = center,
            end = Offset(center.x + minuteHandLength * cos(minutesAngle).toFloat(), center.y + minuteHandLength * sin(minutesAngle).toFloat()),
            strokeWidth = 2.dp.toPx()
        )

        // Center circle
        drawCircle(
            color = onPrimaryContainer,
            radius = radius * 0.05f,
            center = center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    KoreanClockTheme {
        ClockScreen()
    }
}
