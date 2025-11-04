package com.example.koreanclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.koreanclock.ui.theme.KoreanClockTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoreanClockTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Clock(0, 50)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Clock(currentHour: Int, currentMinutes: Int) {
    var minutes by remember { mutableStateOf(currentMinutes) }
    var hours by remember { mutableStateOf(currentHour) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF11192E))
    ) {
        val boxWithConstraintsScope = this
        Canvas(modifier = Modifier) {
            val center = Offset(
                boxWithConstraintsScope.maxWidth.toPx() / 2,
                boxWithConstraintsScope.maxHeight.toPx() / 2,
            )

            val radius = boxWithConstraintsScope.maxWidth.toPx() / 2 * 2 / 3

            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    center.x,
                    center.y,
                    radius,
                    Paint().apply {
                        strokeWidth = 3.dp.toPx()
                        color = android.graphics.Color.parseColor("#040E25")
                        style = Paint.Style.FILL
                        setShadowLayer(150f, 0f, 0f, android.graphics.Color.argb(90, 255, 255, 255))
                    }
                )

                // Draw hour and minute lines
                var lineHeight: Float
                var strokeWidth: Float
                for(i in 0..360 step 6) {
                    if (i % 30 == 0) {
                        lineHeight = 60f
                        strokeWidth = 1.dp.toPx()
                    } else {
                        lineHeight = 40f
                        strokeWidth = Stroke.HairlineWidth
                    }

                    val angle = i.toFloat() * (PI / 180)
                    val startX = center.x + radius * cos(angle).toFloat()
                    val startY = center.y + radius * sin(angle).toFloat()
                    val endX = center.x + (radius - lineHeight) * cos(angle).toFloat()
                    val endY = center.y + (radius - lineHeight) * sin(angle).toFloat()

                    drawLine(
                        color = Color.Gray,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth
                    )
                }

                // Draw the hour clock hand
                val hour = (((hours * 30) - 90F) * (PI / 180))
                val startX = center.x
                val startY = center.y
                val endX = center.x + 100 * cos(hour).toFloat()
                val endY = center.y + 100 * sin(hour).toFloat()
                drawLine(
                    color = Color(0xFFF454FF),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx()
                )

                // Draw the minute clock hand
                val minutesAngle= (((minutes * 6) - 90F) * (PI / 180))
                val minuteStartX = center.x
                val minuteStartY = center.y
                val minuteEndX = center.x + 180 * cos(minutesAngle).toFloat()
                val minuteEndY = center.y + 180 * sin(minutesAngle).toFloat()
                drawLine(
                    color = Color(0xFFF454FF),
                    start = Offset(minuteStartX, minuteStartY),
                    end = Offset(minuteEndX, minuteEndY),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun ClockPreview() {
    KoreanClockTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
            Clock(0, 43)
        }
    }
}
