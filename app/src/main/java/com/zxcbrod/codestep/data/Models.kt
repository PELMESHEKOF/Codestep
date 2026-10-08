package com.zxcbrod.codestep.data

data class Question(val text: String, val options: List<String>, val correct: Int)

object Cmd {
    const val FORWARD = 0
    const val RIGHT = 1
    const val LEFT = 2
}

fun cmdShort(c: Int) = when (c) {
    Cmd.FORWARD -> "⬆"
    Cmd.RIGHT -> "↻"
    else -> "↺"
}

data class RobotTask(
    val cols: Int, val rows: Int,
    val startX: Int, val startY: Int,
    val startDir: Int, // 0=↑ 1=→ 2=↓ 3=←
    val starX: Int, val starY: Int,
    val walls: List<Pair<Int, Int>> = emptyList()
)

sealed class Task {
    data class Quiz(val q: Question) : Task()
    data class FillBlank(
        val before: String, val after: String,
        val options: List<String>, val correct: Int, val explain: String
    ) : Task()
    data class OrderCode(val correctLines: List<String>) : Task()
    data class Robot(val task: RobotTask) : Task()
}

data class Lesson(
    val id: Int, val title: String,
    val theory: String, val example: String,
    val tasks: List<Task>
)

object Lessons {
    val all = listOf(
        Lesson(
            1, "Что такое алгоритм",
            "Алгоритм — это последовательность команд, которую компьютер выполняет шаг за шагом, чтобы решить задачу.",
            "Нужно приготовить бутерброд:\n1. Взять хлеб\n2. Намазать масло\n3. Положить сыр",
            listOf(
                Task.Quiz(Question("Что такое алгоритм?", listOf(
                    "Последовательность команд для решения задачи",
                    "Случайный набор символов",
                    "Язык программирования"), 0)),
                Task.OrderCode(listOf(
                    "Взять хлеб",
                    "Намазать масло",
                    "Положить сыр",
                    "Подать бутерброд")),
                Task.Robot(RobotTask(4, 4, 0, 3, 1, 3, 0))
            )
        ),
        Lesson(
            2, "Переменные в Kotlin",
            "Переменная — это именованная область памяти для хранения значения. В Kotlin: val — нельзя изменить, var — можно изменить.",
            "var score = 0\nscore = 10\nval name = \"КодСтёп\" // изменить нельзя",
            listOf(
                Task.Quiz(Question("Как объявить изменяемую переменную?", listOf("val", "var", "const"), 1)),
                Task.FillBlank(
                    before = "", after = " age = 18\nage = 19",
                    options = listOf("val", "var", "const"), correct = 1,
                    explain = "var — значение можно изменять, val — только для чтения."),
                Task.Robot(RobotTask(4, 4, 0, 0, 1, 2, 3, walls = listOf(1 to 1)))
            )
        ),
        Lesson(
            3, "Циклы в Kotlin",
            "Цикл — это повторение действий. В Kotlin repeat(n) { ... } повторяет блок n раз, а for перебирает элементы.",
            "repeat(3) {\n    println(\"Привет!\")\n} // выведет 3 раза",
            listOf(
                Task.Quiz(Question("Сколько раз выполнится тело цикла repeat(5) { }?", listOf("1", "3", "5"), 2)),
                Task.OrderCode(listOf(
                    "val sum = 0",
                    "repeat(3) {",
                    "    sum = sum + 2",
                    "}",
                    "println(sum)")),
                Task.Robot(RobotTask(4, 4, 0, 0, 2, 3, 3, walls = listOf(2 to 1)))
            )
        )
    )
    fun byId(id: Int) = all.first { it.id == id }
}