package com.zxcbrod.codestep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zxcbrod.codestep.data.Lessons
import com.zxcbrod.codestep.data.Prefs
import com.zxcbrod.codestep.screens.*

sealed class Screen {
    object Welcome : Screen()
    object HowTo : Screen()
    object Home : Screen()
    data class Lesson(val id: Int) : Screen()
    data class Success(val earned: Int) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CodeStepApp() }
    }
}

@Composable
fun CodeStepApp() {
    val context = LocalContext.current

    val prefs = remember { Prefs(context) }
    var screen by remember {
        mutableStateOf<Screen>(
            if (prefs.isOnboardingDone()) Screen.Home else Screen.Welcome
        )
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            (slideInHorizontally(tween(350)) { it } + fadeIn(tween(350))) togetherWith
                    (slideOutHorizontally(tween(350)) { -it / 3 } + fadeOut(tween(350)))
        },
        label = "screen",
        modifier = Modifier.fillMaxSize()
    ) { s ->
        when (s) {
            is Screen.Welcome -> WelcomeScreen {
                prefs.setOnboardingDone()   // больше Welcome не показываем
                screen = Screen.HowTo
            }
            is Screen.HowTo -> HowToScreen { screen = Screen.Home }
            is Screen.Home -> HomeScreen(prefs) { id -> screen = Screen.Lesson(id) }
            is Screen.Lesson -> LessonScreen(
                lesson = Lessons.byId(s.id),
                onBack = { screen = Screen.Home },
                onFinished = { earned ->
                    if (!prefs.isCompleted(s.id)) {
                        prefs.completeLesson(s.id)
                        prefs.addPoints(earned)
                    }
                    screen = Screen.Success(earned)
                }
            )
            is Screen.Success -> SuccessScreen(s.earned) { screen = Screen.Home }
        }
    }
}