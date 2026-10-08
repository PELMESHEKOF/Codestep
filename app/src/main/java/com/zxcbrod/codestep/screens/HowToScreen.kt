package com.zxcbrod.codestep.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.Ul.BigPurpleButton
import com.zxcbrod.codestep.ui.*
import com.zxcbrod.codestep.ui.theme.Bg
import com.zxcbrod.codestep.ui.theme.TextDark
import com.zxcbrod.codestep.ui.theme.TextGray

@Composable
fun HowToScreen(onNext: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg).padding(24.dp)) {
        Spacer(Modifier.height(24.dp))
        Text("Как это работает?", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(24.dp))
        HowItem("📖", "1. Читай", "Короткие и понятные объяснения без сложных терминов.")
        Spacer(Modifier.height(12.dp))
        HowItem("💻", "2. Выполняй задания", "Отвечай на вопросы и собирай алгоритмы для робота.")
        Spacer(Modifier.height(12.dp))
        HowItem("⭐", "3. Получай награды", "Зарабатывай баллы за каждый успешный шаг!")
        Spacer(Modifier.weight(1f))
        BigPurpleButton("Понятно", onNext)
    }
}

@Composable
private fun HowItem(icon: String, title: String, desc: String) {
    Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Text(icon, fontSize = 26.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color =  TextDark, fontSize = 16.sp)
            Text(desc, color = TextGray, fontSize = 13.sp)
        }
    }
}