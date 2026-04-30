package com.example.controlh.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

// LÍMITES EN MINUTOS (Pueden seguir siendo útiles como constantes)
const val DAILY_MAX_MINUTES = 480 // 8 horas
const val WEEKLY_MAX_MINUTES = 2400 // 40 horas

/**
 * Componente unificado para mostrar el progreso de uso contra cualquier límite.
 *
 * @param usedMinutes Tiempo de uso transcurrido en minutos.
 * @param maxMinutes Límite máximo (100%) en minutos (e.g., 480 para diario, 2400 para semanal).
 * @param label Etiqueta para el texto (e.g., "Diario" o "Semanal").
 * @param canvasSize Tamaño del componente.
 * @param foregroundIndicatorColor Color de la barra de progreso (por defecto: Azul para Diario, Verde para Semanal).
 */
@Composable
fun UsageLimitProgress(
    usedMinutes: Int,
    maxMinutes: Int,
    label: String,
    canvasSize: Dp = 150.dp,
    modifier: Modifier = Modifier,
    // Definimos el color por defecto basándonos en la etiqueta para replicar el comportamiento anterior
    foregroundIndicatorColor: Color = if (label == "Diario") MaterialTheme.colorScheme.primary else Color(0xFF00C853)
) {
    // Cálculo del tiempo restante y texto descriptivo
    val remainingMinutes = (maxMinutes - usedMinutes).coerceAtLeast(0)
    val maxTimeHours = maxMinutes / 60

    val smallText = if (remainingMinutes > 0) {
        "Restantes de ${maxTimeHours}h ($label)"
    } else {
        "Límite $label Alcanzado"
    }

    // Llamada al componente base de dibujo
    ComponentBar(
        modifier = modifier,
        canvasSize = canvasSize,
        indicatorValue = usedMinutes,
        maxIndicatorValue = maxMinutes,
        foregroundIndicatorColor = foregroundIndicatorColor,
        bigTextColor = foregroundIndicatorColor, // El color del texto grande sigue el color de la barra
        bigTextFontSize = MaterialTheme.typography.titleLarge.fontSize,
        centerValue = remainingMinutes,
        centerSuffix = "min",
        smallText = smallText,
    )
}


/**
 * Componente base de la barra de progreso reutilizable (Lógica de dibujo y animación).
 * Este componente permanece sin cambios para manejar la parte visual.
 */
@Composable
fun ComponentBar(
    modifier: Modifier = Modifier,
    canvasSize: Dp = 300.dp,
    indicatorValue: Int = 0,
    maxIndicatorValue: Int = 100,
    backgroundIndicatorColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    backgroundIndicatorStrokeWidth: Float = 100f,
    foregroundIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    foregroundIndicatorStrokeWidth: Float = 100f,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round,
    bigTextFontSize: TextUnit = MaterialTheme.typography.headlineLarge.fontSize,
    bigTextColor: Color = MaterialTheme.colorScheme.onSurface,
    centerValue: Int,
    centerSuffix: String,
    smallText: String = "Remaining",
    smallTextFontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize,
    smallTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
) {
    val allowedIndicatorValue = indicatorValue.coerceIn(0, maxIndicatorValue)

    var animatedIndicatorValue by remember { mutableStateOf(0f) }
    LaunchedEffect(key1 = allowedIndicatorValue) {
        animatedIndicatorValue = allowedIndicatorValue.toFloat()
    }

    // El porcentaje se calcula sobre el máximo
    val percentage = (animatedIndicatorValue / maxIndicatorValue) * 100

    // Animación para el arco (240 grados es el total visible, 2.4 * 100 = 240)
    val sweepAngle by animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),
        animationSpec = tween(1000)
    )

    // Animación para el valor numérico central (tiempo restante)
    val receivedValue by animateIntAsState(
        targetValue = centerValue,
        animationSpec = tween(1000)
    )

    val animatedBigTextColor by animateColorAsState(
        targetValue = if (indicatorValue >= maxIndicatorValue) Color.Red else bigTextColor,
        animationSpec = tween(1000)
    )

    Column(
        modifier = modifier
            .size(canvasSize)
            .drawBehind {
                val componentSize = size / 1.25f
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundIndicatorColor,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth,
                    indicatorStokeCap = indicatorStrokeCap
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth,
                    indicatorStokeCap = indicatorStrokeCap
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmbeddedElements(
            bigText = receivedValue,
            bigTextFontSize = bigTextFontSize,
            bigTextColor = animatedBigTextColor,
            bigTextSuffix = centerSuffix,
            smallText = smallText,
            smallTextColor = smallTextColor,
            smallTextFontSize = smallTextFontSize
        )
    }
}

fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float,
    indicatorStokeCap: StrokeCap
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(
    sweepAngle: Float,
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float,
    indicatorStokeCap: StrokeCap
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

@Composable
fun EmbeddedElements(
    bigText: Int,
    bigTextFontSize: TextUnit,
    bigTextColor: Color,
    bigTextSuffix: String,
    smallText: String,
    smallTextColor: Color,
    smallTextFontSize: TextUnit
) {
    Text(
        text = smallText,
        color = smallTextColor,
        fontSize = smallTextFontSize,
        textAlign = TextAlign.Center
    )
    Text(
        text = "$bigText $bigTextSuffix",
        color = bigTextColor,
        fontSize = bigTextFontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
}