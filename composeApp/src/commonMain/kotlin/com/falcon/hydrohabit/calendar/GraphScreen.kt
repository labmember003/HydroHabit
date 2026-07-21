package com.falcon.hydrohabit.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.falcon.hydrohabit.ui.theme.backgroundColor2
import com.falcon.hydrohabit.ui.theme.primaryBlack
import com.falcon.hydrohabit.ui.theme.waterColor

data class GraphPoint(val x: Float, val y: Float)

@Composable
fun GraphScreen(
    modifier: Modifier = Modifier,
    points: List<GraphPoint> = listOf(
        GraphPoint(0f, 40f),
        GraphPoint(1f, 90f),
        GraphPoint(2f, 0f),
        GraphPoint(3f, 60f),
        GraphPoint(4f, 10f)
    ),
    ySteps: Int = 5,
    yMax: Float = 100f
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(fontSize = 10.sp, color = primaryBlack)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(backgroundColor2)
            .padding(start = 40.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
    ) {
        if (points.isEmpty()) return@Canvas

        val chartWidth = size.width
        val chartHeight = size.height
        val xMin = points.minOf { it.x }
        val xMax = points.maxOf { it.x }
        val xRange = if (xMax - xMin == 0f) 1f else xMax - xMin

        fun toCanvasX(x: Float) = ((x - xMin) / xRange) * chartWidth
        fun toCanvasY(y: Float) = chartHeight - (y / yMax) * chartHeight

        // Y-axis grid lines and labels
        for (i in 0..ySteps) {
            val yVal = (yMax / ySteps) * i
            val cy = toCanvasY(yVal)
            drawLine(
                color = Color(0xFFD1D1D6),
                start = Offset(0f, cy),
                end = Offset(chartWidth, cy),
                strokeWidth = 0.5f
            )
            val label = yVal.toInt().toString()
            val measured = textMeasurer.measure(label, labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(-measured.size.width.toFloat() - 8f, cy - measured.size.height / 2f)
            )
        }

        // X-axis labels
        points.forEach { point ->
            val cx = toCanvasX(point.x)
            val label = point.x.toInt().toString()
            val measured = textMeasurer.measure(label, labelStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(cx - measured.size.width / 2f, chartHeight + 8f)
            )
        }

        // Line path
        val path = Path()
        points.forEachIndexed { index, point ->
            val cx = toCanvasX(point.x)
            val cy = toCanvasY(point.y)
            if (index == 0) path.moveTo(cx, cy) else path.lineTo(cx, cy)
        }
        drawPath(path, primaryBlack, style = Stroke(width = 2.dp.toPx()))

        // Shadow under line
        val shadowPath = Path()
        points.forEachIndexed { index, point ->
            val cx = toCanvasX(point.x)
            val cy = toCanvasY(point.y)
            if (index == 0) shadowPath.moveTo(cx, cy) else shadowPath.lineTo(cx, cy)
        }
        shadowPath.lineTo(toCanvasX(points.last().x), chartHeight)
        shadowPath.lineTo(toCanvasX(points.first().x), chartHeight)
        shadowPath.close()
        drawPath(shadowPath, primaryBlack.copy(alpha = 0.05f))

        // Intersection dots
        points.forEach { point ->
            val cx = toCanvasX(point.x)
            val cy = toCanvasY(point.y)
            drawCircle(color = waterColor, radius = 6.dp.toPx(), center = Offset(cx, cy))
            drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(cx, cy))
        }
    }
}
