package com.zxcbrod.codestep.data

import android.content.Context

class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences("codestep", Context.MODE_PRIVATE)

    fun getPoints(): Int = prefs.getInt("points", 0)
    fun addPoints(p: Int) = prefs.edit().putInt("points", getPoints() + p).apply()
    fun isCompleted(id: Int): Boolean = prefs.getBoolean("lesson_$id", false)
    fun completeLesson(id: Int) = prefs.edit().putBoolean("lesson_$id", true).apply()
    fun completedCount(): Int = Lessons.all.count { isCompleted(it.id) }
    fun isUnlocked(id: Int): Boolean = id == 1 || isCompleted(id - 1)

    fun isOnboardingDone(): Boolean = prefs.getBoolean("onboarding", false)

    fun setOnboardingDone() = prefs.edit().putBoolean("onboarding", true).apply()
}