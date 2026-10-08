package com.zxcbrod.codestep.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zxcbrod.codestep.Ul.BigPurpleButton
import com.zxcbrod.codestep.Ul.FloatingPoints
import com.zxcbrod.codestep.Ul.ProgressBar
import com.zxcbrod.codestep.Ul.appear
import com.zxcbrod.codestep.Ul.bounce
import com.zxcbrod.codestep.data.*
import com.zxcbrod.codestep.ui.*
import com.zxcbrod.codestep.Ul.pressEffect
import com.zxcbrod.codestep.Ul.shake
import com.zxcbrod.codestep.ui.theme.LightPurple
import com.zxcbrod.codestep.ui.theme.PurpleDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.zxcbrod.codestep.ui.theme.Bg
import com.zxcbrod.codestep.ui.theme.Green
import com.zxcbrod.codestep.ui.theme.Purple
import com.zxcbrod.codestep.ui.theme.Red
import com.zxcbrod.codestep.ui.theme.TextDark
import com.zxcbrod.codestep.ui.theme.TextGray
import androidx.compose.foundation.border


@Composable
fun LessonScreen(lesson: Lesson, onBack: () -> Unit, onFinished: (Int) -> Unit) {
    var step by remember { mutableStateOf(0) } // 0 теория, 1-3 задания
    var earned by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize().background(Bg)) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("←", fontSize = 22.sp, color = TextDark,
                modifier = Modifier.clickable { if (step == 0) onBack() else step -= 1 }.padding(8.dp))
            Spacer(Modifier.weight(1f))
            Text("Урок ${lesson.id} из ${Lessons.all.size}", color = TextGray, fontSize = 14.sp)
        }
        ProgressBar(step / 4f, Modifier.padding(horizontal = 16.dp))
        AnimatedContent(
            targetState = step,
            transitionSpec = {
                (slideInHorizontally(tween(350)) { it } + fadeIn(tween(350))) togetherWith
                        (slideOutHorizontally(tween(350)) { it / 3 } + fadeOut(tween(350)))
            },
            label = "step",
            modifier = Modifier.fillMaxSize()
        ) { st ->
            when (st) {
                0 -> TheoryBlock(lesson) { step = 1 }
                else -> TaskBlock(lesson.tasks[st - 1], st, lesson.id) {
                    earned += 10
                    if (st == 3) onFinished(earned) else step = st + 1
                }
            }
        }
    }
}

@Composable
private fun TaskBlock(task: Task, number: Int, seed: Int, onPassed: () -> Unit) {
    when (task) {
        is Task.Quiz -> QuizBlock(task.q, number, onPassed)
        is Task.FillBlank -> FillBlankBlock(task, number, onPassed)
        is Task.OrderCode -> OrderBlock(task, number, seed, onPassed)
        is Task.Robot -> RobotBlock(task.task, onPassed)
    }
}

@Composable
private fun TheoryBlock(lesson: Lesson, onNext: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text(lesson.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(0))
        Spacer(Modifier.height(12.dp))
        Text(lesson.theory, fontSize = 16.sp, color = TextDark, modifier = Modifier.appear(1))
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().appear(2).background(LightPurple, RoundedCornerShape(14.dp)).padding(16.dp)) {
            Text("Пример", fontWeight = FontWeight.Bold, color = PurpleDark)
            Spacer(Modifier.height(6.dp))
            Text(lesson.example, fontSize = 14.sp, color = TextDark)
        }
        Spacer(Modifier.height(24.dp))
        BigPurpleButton("Далее", onNext)
    }
}

@Composable
private fun QuizBlock(q: Question, number: Int, onPassed: () -> Unit) {
    var selected by remember { mutableStateOf(-1) }
    var wrong by remember { mutableStateOf(false) }
    var wrongCount by remember { mutableStateOf(0) }
    var correct by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(20.dp)) {
            Text("Задание $number из 3", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text(q.text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(0))
            Spacer(Modifier.height(16.dp))
            q.options.forEachIndexed { i, opt ->
                val isSelected = selected == i
                val bg = when {
                    correct && i == q.correct -> Green.copy(alpha = 0.25f)
                    isSelected && wrong -> Red.copy(alpha = 0.15f)
                    isSelected -> Purple.copy(alpha = 0.12f)
                    else -> Color.White
                }
                val borderColor = when {
                    correct && i == q.correct -> Green
                    isSelected && wrong -> Red
                    isSelected -> Purple
                    else -> Color.Transparent
                }
                Box(
                    Modifier.fillMaxWidth()
                        .appear(i + 1)
                        .pressEffect()
                        .shake(if (wrong && isSelected) wrongCount else 0)
                        .bounce(if (correct && i == q.correct) 1 else 0)
                        .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                        .background(bg, RoundedCornerShape(14.dp))
                        .clickable(enabled = !correct) { selected = i; wrong = false }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(opt, fontSize = 15.sp, color = TextDark, modifier = Modifier.weight(1f))
                        if (isSelected && !correct) Text("●", color = Purple, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.weight(1f))
            if (wrong) Text("❌ Неверно, попробуй ещё раз", color = Red, fontSize = 14.sp)
            if (correct) Text("✅ Верно!", color = Green, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            if (!correct) {
                BigPurpleButton(
                    text = "Проверить",
                    enabled = selected >= 0,
                    onClick = {
                        if (selected == q.correct) correct = true else { wrong = true; wrongCount++ }
                    }
                )
            } else BigPurpleButton("Далее", onPassed)
        }
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) { FloatingPoints(correct) }
    }
}

@Composable
private fun FillBlankBlock(t: Task.FillBlank, number: Int, onPassed: () -> Unit) {
    var chosen by remember { mutableStateOf(-1) }
    var wrong by remember { mutableStateOf(false) }
    var wrongCount by remember { mutableStateOf(0) }
    var correct by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Задание $number из 3", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("Вставь пропущенное слово в код", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(0))
            Spacer(Modifier.height(16.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFFE3E0F5))) { append(t.before) }
                    withStyle(SpanStyle(
                        color = if (correct) Green else Color(0xFFFFC94D), fontWeight = FontWeight.Bold)) {
                        append(if (chosen >= 0) t.options[chosen] else "___")
                    }
                    withStyle(SpanStyle(color = Color(0xFFE3E0F5))) { append(t.after) }
                },
                fontFamily = FontFamily.Monospace, fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth().appear(1)
                    .shake(if (wrong) wrongCount else 0)
                    .background(Color(0xFF2D2A3E), RoundedCornerShape(14.dp)).padding(16.dp)
            )
            Spacer(Modifier.height(16.dp))
            t.options.forEachIndexed { i, o ->
                val isSelected = chosen == i
                val bg = when {
                    correct && i == t.correct -> Green.copy(alpha = 0.25f)
                    isSelected && wrong -> Red.copy(alpha = 0.15f)
                    isSelected -> Purple.copy(alpha = 0.12f)
                    else -> Color.White
                }
                val borderColor = when {
                    correct && i == t.correct -> Green
                    isSelected && wrong -> Red
                    isSelected -> Purple
                    else -> Color.Transparent
                }
                Box(
                    Modifier.fillMaxWidth()
                        .appear(i + 2)
                        .pressEffect()
                        .bounce(if (correct && i == t.correct) 1 else 0)
                        .border(2.dp, borderColor, RoundedCornerShape(14.dp))
                        .background(bg, RoundedCornerShape(14.dp))
                        .clickable(enabled = !correct) { chosen = i; wrong = false }
                        .padding(14.dp)
                ) { Text(o, fontFamily = FontFamily.Monospace, fontSize = 15.sp, color = TextDark) }
                Spacer(Modifier.height(10.dp))
            }
            if (correct) Text("💡 " + t.explain, color = PurpleDark, fontSize = 13.sp, modifier = Modifier.appear(0))
            Spacer(Modifier.height(16.dp))
            if (!correct) {
                BigPurpleButton(
                    text = "Проверить",
                    enabled = chosen >= 0,
                    onClick = {
                        if (chosen == t.correct) correct = true else { wrong = true; wrongCount++ }
                    }
                )
            } else BigPurpleButton("Далее", onPassed)
        }
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) { FloatingPoints(correct) }
    }
}

@Composable
private fun OrderBlock(t: Task.OrderCode, number: Int, seed: Int, onPassed: () -> Unit) {
    val shuffled = remember {
        var s = t.correctLines.shuffled(Random(seed.toLong()))
        while (s == t.correctLines) s = s.shuffled(Random(seed.toLong() + 1))
        s
    }
    var remaining by remember { mutableStateOf(shuffled) }
    var answer by remember { mutableStateOf(listOf<String>()) }
    var wrongCount by remember { mutableStateOf(0) }
    var correct by remember { mutableStateOf(false) }

    fun removeFirst(list: List<String>, item: String): List<String> {
        val m = list.toMutableList(); m.remove(item); return m
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
            Text("Задание $number из 3", color = TextGray, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Text("Расставь строки по порядку", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(0))
            Spacer(Modifier.height(16.dp))
            Column(
                Modifier.fillMaxWidth().appear(1)
                    .shake(wrongCount)
                    .background(LightPurple, RoundedCornerShape(14.dp)).padding(12.dp)
            ) {
                if (answer.isEmpty()) Text("Нажимай на строки ниже 👇", color = TextGray, fontSize = 13.sp)
                answer.forEach { line ->
                    CodeLine(line, enabled = !correct) {
                        answer = removeFirst(answer, line)
                        remaining = remaining + line
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            remaining.forEachIndexed { i, line ->
                CodeLine(line, enabled = !correct) {
                    remaining = removeFirst(remaining, line)
                    answer = answer + line
                }
                Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(16.dp))
            if (correct) Text("✅ Код собран верно!", color = Green, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            if (!correct) {
                BigPurpleButton(
                    text = "Проверить",
                    enabled = answer.size == t.correctLines.size,
                    onClick = {
                        if (answer == t.correctLines) correct = true else wrongCount++
                    }
                )
            } else BigPurpleButton("Далее", onPassed)
        }
        Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) { FloatingPoints(correct) }
    }
}

@Composable
private fun CodeLine(line: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.fillMaxWidth()
            .pressEffect()
            .background(Color(0xFF2D2A3E), RoundedCornerShape(10.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) { Text(line, fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Color(0xFFE3E0F5)) }
}

@Composable
private fun RobotBlock(task: RobotTask, onPassed: () -> Unit) {
    var program by remember { mutableStateOf(listOf<Int>()) }
    var rx by remember { mutableStateOf(task.startX) }
    var ry by remember { mutableStateOf(task.startY) }
    var rd by remember { mutableStateOf(task.startDir) }
    var running by remember { mutableStateOf(false) }
    var passed by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Составь программу и нажми «Выполнить»") }
    val scope = rememberCoroutineScope()
    val dx = intArrayOf(0, 1, 0, -1)
    val dy = intArrayOf(-1, 0, 1, 0)
    val cell = 60

    val ax by animateDpAsState((rx * cell).dp, tween(300, easing = FastOutSlowInEasing), label = "rx")
    val ay by animateDpAsState((ry * cell).dp, tween(300, easing = FastOutSlowInEasing), label = "ry")
    val arot by animateFloatAsState(rd * 90f, tween(300), label = "rot")
    val inf = rememberInfiniteTransition(label = "star")
    val starScale by inf.animateFloat(1f, 1.2f,
        infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "star")

    fun run() {
        if (running || passed) return
        if (program.isEmpty()) { message = "Сначала добавь команды!"; return }
        running = true
        message = "Выполняю..."
        rx = task.startX; ry = task.startY; rd = task.startDir
        val prog = program
        scope.launch {
            var ok = true
            for (cmd in prog) {
                delay(450)
                when (cmd) {
                    Cmd.FORWARD -> {
                        val nx = rx + dx[rd]; val ny = ry + dy[rd]
                        if (nx < 0 || ny < 0 || nx >= task.cols || ny >= task.rows || (nx to ny) in task.walls) {
                            ok = false; message = "💥 Робот врезался в препятствие!"; break
                        }
                        rx = nx; ry = ny
                    }
                    Cmd.RIGHT -> rd = (rd + 1) % 4
                    Cmd.LEFT -> rd = (rd + 3) % 4
                }
            }
            running = false
            if (ok) {
                if (rx == task.starX && ry == task.starY) {
                    passed = true; message = "🎉 Робот дошёл до звезды!"
                } else message = "Робот не дошёл до звезды. Измени программу!"
            }
        }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Text("Задание 3 из 3", color = TextGray, fontSize = 14.sp)
        Spacer(Modifier.height(8.dp))
        Text("Собери алгоритм", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.appear(0))
        Spacer(Modifier.height(4.dp))
        Text("Робот должен дойти до звезды ⭐", color = TextGray, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CmdButton("Вперёд") { if (!running && !passed) program = program + Cmd.FORWARD }
            CmdButton("↻") { if (!running && !passed) program = program + Cmd.RIGHT }
            CmdButton("↺") { if (!running && !passed) program = program + Cmd.LEFT }
        }
        Spacer(Modifier.height(12.dp))

        if (program.isEmpty()) Text("Программа пуста", color = TextGray, fontSize = 13.sp)
        else Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            program.forEachIndexed { i, c ->
                Box(
                    Modifier.background(LightPurple, RoundedCornerShape(10.dp))
                        .clickable(enabled = !running && !passed) {
                            program = program.filterIndexed { idx, _ -> idx != i }
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) { Text("${i + 1}. ${cmdShort(c)}", color = PurpleDark, fontSize = 14.sp) }
            }
        }
        Spacer(Modifier.height(16.dp))

        Box(
            Modifier.align(Alignment.CenterHorizontally).appear(1)
                .background(Color.White, RoundedCornerShape(16.dp)).padding(8.dp)
        ) {
            Column {
                for (y in 0 until task.rows) {
                    Row {
                        for (x in 0 until task.cols) {
                            val isWall = (x to y) in task.walls
                            val isStar = x == task.starX && y == task.starY
                            Box(Modifier.size(cell.dp).padding(4.dp)
                                .background(if (isWall) Color(0xFFD9D4F0) else LightPurple, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center) {
                                if (isWall) Text("🧱", fontSize = 22.sp)
                                if (isStar) Text("⭐", fontSize = 22.sp,
                                    modifier = Modifier.graphicsLayer(scaleX = starScale, scaleY = starScale))
                            }
                        }
                    }
                }
            }
            // Робот поверх сетки с плавным перемещением и поворотом
            Box(Modifier.offset(ax, ay).size(cell.dp), contentAlignment = Alignment.Center) {
                RobotBody(arot)
            }
        }
        Spacer(Modifier.height(16.dp))

        Text(message, fontSize = 14.sp, color = when {
            passed -> Green
            message.contains("💥") || message.contains("не дошёл") -> Red
            else -> TextGray
        })
        Spacer(Modifier.height(12.dp))

        if (!passed) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button({ run() },
                    modifier = Modifier.weight(1f).height(48.dp).pressEffect(),
                    enabled = !running,
                    colors = ButtonDefaults.buttonColors(containerColor = Purple),
                    shape = RoundedCornerShape(14.dp)) { Text("▶ Выполнить", color = Color.White) }
                Button({
                    program = emptyList()
                    rx = task.startX; ry = task.startY; rd = task.startDir
                    message = "Составь программу и нажми «Выполнить»"
                },
                    modifier = Modifier.weight(1f).height(48.dp).pressEffect(),
                    enabled = !running,
                    colors = ButtonDefaults.buttonColors(containerColor = LightPurple, contentColor = PurpleDark),
                    shape = RoundedCornerShape(14.dp)) { Text("🗑 Сброс") }
            }
        } else BigPurpleButton("Далее", onPassed)
    }
}

@Composable
private fun RobotBody(rotation: Float) {
    Box(
        Modifier.size(44.dp)
            .graphicsLayer { rotationZ = rotation }
            .background(Purple, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("▲", color = Color.White, fontSize = 8.sp)
            Spacer(Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(8.dp).background(Color.White, CircleShape))
                Box(Modifier.size(8.dp).background(Color.White, CircleShape))
            }
            Spacer(Modifier.height(4.dp))
            Box(Modifier.size(14.dp, 4.dp).background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
private fun CmdButton(text: String, onClick: () -> Unit) {
    Button(onClick,
        modifier = Modifier.pressEffect(),
        colors = ButtonDefaults.buttonColors(containerColor = LightPurple, contentColor = PurpleDark),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text, fontSize = 13.sp)
    }
}