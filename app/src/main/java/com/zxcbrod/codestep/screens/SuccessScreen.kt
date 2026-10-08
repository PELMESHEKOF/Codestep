package com.zxcbrod.codestep.screens


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.Ul.BigPurpleButton
import com.zxcbrod.codestep.Ul.appear
import com.zxcbrod.codestep.Ul.bounce
import com.zxcbrod.codestep.ui.*
import com.zxcbrod.codestep.ui.theme.Bg
import com.zxcbrod.codestep.ui.theme.Green
import com.zxcbrod.codestep.ui.theme.LightPurple
import com.zxcbrod.codestep.ui.theme.Purple
import com.zxcbrod.codestep.ui.theme.PurpleDark
import com.zxcbrod.codestep.ui.theme.Red
import com.zxcbrod.codestep.ui.theme.TextDark
import com.zxcbrod.codestep.ui.theme.TextGray
import kotlin.math.sin
import kotlin.random.Random

private data class Confetto(val x: Float, val speed: Float, val ci: Int)

@Composable
fun SuccessScreen(earned: Int, onNext: () -> Unit) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(Modifier.fillMaxSize().background(Bg)) {
        Confetti()
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎉", fontSize = 70.sp,
                modifier = Modifier.graphicsLayer(scaleX = scale.value, scaleY = scale.value))
            Spacer(Modifier.height(8.dp))
            Text("Отлично!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(1))
            Spacer(Modifier.height(8.dp))
            Text("Ты прошёл урок и заработал:", color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.appear(2))
            Spacer(Modifier.height(16.dp))
            Box(
                Modifier.fillMaxWidth().appear(3).bounce(1)
                    .background(LightPurple, RoundedCornerShape(16.dp)).padding(20.dp),
                contentAlignment = Alignment.Center
            ) { Text("⭐ +$earned баллов", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PurpleDark) }
            Spacer(Modifier.height(32.dp))
            BigPurpleButton("Далее", onNext)
        }
    }
}

@Composable
private fun Confetti() {
    val parts = remember {
        List(30) { Confetto(Random.nextFloat(), 0.5f + Random.nextFloat() * 0.8f, Random.nextInt(4)) }
    }
    val t = remember { Animatable(0f) }
    LaunchedEffect(Unit) { t.animateTo(1f, tween(3000, easing = LinearEasing)) }
    val colors = listOf(Purple, Green, androidx.compose.ui.graphics.Color(0xFFFFC94D), Red)
    Canvas(Modifier.fillMaxSize()) {
        parts.forEach { c ->
            val y = -60f + t.value * (size.height + 120f) * c.speed
            val x = c.x * size.width + sin(t.value * 6f + c.x * 12f) * 40f
            drawRect(colors[c.ci], androidx.compose.ui.geometry.Offset(x, y), androidx.compose.ui.geometry.Size(10f, 16f))
        }
    }
}