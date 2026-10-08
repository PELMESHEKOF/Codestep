package com.zxcbrod.codestep.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.Ul.BigPurpleButton
import com.zxcbrod.codestep.Ul.appear
import com.zxcbrod.codestep.ui.*
import com.zxcbrod.codestep.ui.theme.Bg
import com.zxcbrod.codestep.ui.theme.TextDark
import com.zxcbrod.codestep.ui.theme.TextGray
import com.zxcbrod.codestep.R

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Bg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.robot),
            contentDescription = "Робот CodeStep",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(200.dp)
                .appear(0)
        )
        Spacer(Modifier.height(8.dp))
        Text("CodeStep", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(8.dp))
        Text("Первые шаги в программировании — это просто!",
            fontSize = 16.sp, color = TextGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))
        BigPurpleButton("Начать обучение", onStart)
    }
}