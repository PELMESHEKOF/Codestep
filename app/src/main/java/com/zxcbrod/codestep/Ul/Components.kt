package com.zxcbrod.codestep.Ul

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.ui.theme.LightPurple
import com.zxcbrod.codestep.ui.theme.Purple

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val p by animateFloatAsState(progress.coerceIn(0f, 1f), tween(500), label = "p")
    Box(modifier.fillMaxWidth().height(8.dp)
        .clip(RoundedCornerShape(4.dp)).background(LightPurple)) {
        Box(Modifier.fillMaxHeight()
            .fillMaxWidth(p)
            .clip(RoundedCornerShape(4.dp)).background(Purple))
    }
}

@Composable
fun BigPurpleButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp).pressEffect(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Purple),
        shape = RoundedCornerShape(16.dp)) {
        Text(text, fontSize = 16.sp, color = Color.White)
    }
}