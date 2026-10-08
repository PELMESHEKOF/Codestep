package com.zxcbrod.codestep.Ul

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.ui.theme.Green
import kotlinx.coroutines.launch

// Плавное появление элемента (каскадом по index)
@Composable
fun Modifier.appear(index: Int = 0): Modifier {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(450, delayMillis = index * 110), label = "a")
    val off by animateFloatAsState(if (visible) 0f else 30f, tween(450, delayMillis = index * 110), label = "o")
    return this.alpha(alpha).offset(y = off.dp)
}

// Эффект нажатия (пружинка)
@Composable
fun Modifier.pressEffect(): Modifier {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (pressed) 0.94f else 1f,
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    return this.graphicsLayer(scaleX = scale, scaleY = scale)
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    val e = awaitPointerEvent()
                    pressed = e.changes.any { it.pressed }
                }
            }
        }
}

// Тряска при ошибке (trigger = счётчик ошибок)
@Composable
fun Modifier.shake(trigger: Int): Modifier {
    val ox = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            repeat(2) {
                ox.animateTo(14f, tween(60))
                ox.animateTo(-14f, tween(60))
            }
            ox.animateTo(0f, tween(60))
        }
    }
    return this.offset(x = ox.value.dp)
}

// Пружинный «bounce» при успехе
@Composable
fun Modifier.bounce(trigger: Int): Modifier {
    val s = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            s.animateTo(1.08f, tween(120))
            s.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }
    return this.graphicsLayer(scaleX = s.value, scaleY = s.value)
}

// Всплывающие «+10 баллов»
@Composable
fun FloatingPoints(show: Boolean) {
    val alpha = remember { Animatable(0f) }
    val y = remember { Animatable(0f) }
    LaunchedEffect(show) {
        if (show) {
            alpha.snapTo(1f); y.snapTo(0f)
            launch { alpha.animateTo(0f, tween(900, delayMillis = 250)) }
            y.animateTo(-120f, tween(1100))
        }
    }
    if (alpha.value > 0.01f) {
        Text("+10 баллов", color = Green, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(y = y.value.dp).alpha(alpha.value))
    }
}