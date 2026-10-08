package com.zxcbrod.codestep.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.Ul.ProgressBar
import com.zxcbrod.codestep.Ul.appear
import com.zxcbrod.codestep.Ul.pressEffect
import com.zxcbrod.codestep.data.Lessons
import com.zxcbrod.codestep.data.Prefs
import com.zxcbrod.codestep.ui.*
import com.zxcbrod.codestep.ui.theme.Bg
import com.zxcbrod.codestep.ui.theme.LightPurple
import com.zxcbrod.codestep.ui.theme.Purple
import com.zxcbrod.codestep.ui.theme.PurpleDark
import com.zxcbrod.codestep.ui.theme.TextDark
import com.zxcbrod.codestep.ui.theme.TextGray

@Composable
fun HomeScreen(prefs: Prefs, onLesson: (Int) -> Unit) {
    var tab by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize().background(Bg)) {
        Box(Modifier.weight(1f)) {
            if (tab == 0) LessonsList(prefs, onLesson) else ProfileContent(prefs)
        }
        Row(
            Modifier.fillMaxWidth().background(Color.White).padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TabItem("🏠", "Главная", tab == 0) { tab = 0 }
            TabItem("👤", "Профиль", tab == 1) { tab = 1 }
        }
    }
}

@Composable
private fun LessonsList(prefs: Prefs, onLesson: (Int) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Модуль 1", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Основы Kotlin", color = TextGray, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))
        ProgressBar(prefs.completedCount() / Lessons.all.size.toFloat())
        Spacer(Modifier.height(6.dp))
        Text("${prefs.completedCount()} из ${Lessons.all.size} уроков", color = TextGray, fontSize = 13.sp)
        Spacer(Modifier.height(20.dp))
        Lessons.all.forEachIndexed { i, lesson ->
            val unlocked = prefs.isUnlocked(lesson.id)
            val done = prefs.isCompleted(lesson.id)
            Row(
                Modifier.fillMaxWidth()
                    .appear(i)
                    .pressEffect()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .clickable(enabled = unlocked) { onLesson(lesson.id) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            )
         {
                Box(
                    Modifier.size(40.dp).clip(CircleShape)
                        .background(if (done) Purple else LightPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${lesson.id}", color = if (done) Color.White else PurpleDark, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(lesson.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                    Text(
                        when { done -> "Пройден ✅"; unlocked -> "Доступен"; else -> "Закрыт 🔒" },
                        fontSize = 12.sp, color = TextGray
                    )
                }
                Text(if (done) "⭐" else if (unlocked) "›" else "🔒", fontSize = 18.sp, color = TextGray)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun ProfileContent(prefs: Prefs) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier.size(90.dp).clip(CircleShape).background(LightPurple),
            contentAlignment = Alignment.Center
        ) { Text("🧑", fontSize = 44.sp) }
        Spacer(Modifier.height(12.dp))
        Text("Привет!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Начинающий программист", color = TextGray, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("⭐", "${prefs.getPoints()}", "баллов")
            StatCard("📚", "${prefs.completedCount()}", "уроков")
        }
    }
}

@Composable
private fun StatCard(icon: String, value: String, label: String) {
    Column(
        modifier = Modifier // Начинаем с большой буквы 'Modifier'
           // .weight(1f)     // Указываем 1f (Float) вместо 1 (Int)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 22.sp)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(label, fontSize = 12.sp, color = TextGray)
    }
}


@Composable
private fun TabItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 20.sp)
        Text(label, fontSize = 12.sp, color = if (selected) Purple else TextGray)
    }
}