package com.example.koreanclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.koreanclock.ui.theme.KoreanClockTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoreanClockTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Korean Clock Practice") }
                        )
                    }
                ) { innerPadding ->
                    ClockScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ClockScreen(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("What time is it?",
            modifier = Modifier,
        )

        Clock(0, 50, modifier = Modifier.fillMaxWidth())

        IconButton(
            onClick = { /*TODO*/ }) {
            Icon(Icons.Rounded.Refresh,
                modifier = Modifier.size(128.dp),
                contentDescription = "Clock icon",
                tint = Color.Green
            )
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter time") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        TextButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Text("Check your answer")
        }
    }
}

@Composable
fun Clock(currentHour: Int, currentMinutes: Int, modifier: Modifier = Modifier) {
    var minutes by remember { mutableStateOf(currentMinutes) }
    var hours by remember { mutableStateOf(currentHour) }
    Canvas(modifier = modifier
        .aspectRatio(1f)) {
        val radius = size.minDimension * 0.38f

        // Clock face
        drawCircle(
            color = Color.LightGray,
            radius = radius,
            center = center
        )
        drawCircle(
            color = Color.Black,
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
                color = Color.Black,
                start = start,
                end = end,
                strokeWidth = strokeWidth
            )
        }

        // Draw the hour clock hand
        val hourAngleDegrees = (hours % 12 + minutes / 60f) * 30f
        val hourAngle = (hourAngleDegrees - 90) * (PI / 180).toFloat()

        val hourHandLength = radius * 0.5f
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(center.x + hourHandLength * cos(hourAngle), center.y + hourHandLength * sin(hourAngle)),
            strokeWidth = 3.dp.toPx()
        )


        // Draw the minute clock hand
        val minutesAngleDegrees= minutes * 6f
        val minutesAngle = (minutesAngleDegrees - 90f) * (PI / 180).toFloat()
        val minuteHandLength = radius * 0.8f
        drawLine(
            color = Color.Black,
            start = center,
            end = Offset(center.x + minuteHandLength * cos(minutesAngle), center.y + minuteHandLength * sin(minutesAngle)),
            strokeWidth = 2.dp.toPx()
        )
        
        // Center circle
        drawCircle(
            color = Color.DarkGray,
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
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Korean Clock Practice") }
                )
            }
        ) { innerPadding ->
            ClockScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
