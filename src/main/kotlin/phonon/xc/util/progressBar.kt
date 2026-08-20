package phonon.xc.util.progressBar

fun createProgressBar(current: Int, max: Int, totalBars: Int = 10): String {
    if (max <= 0) return "[]"
    val percent = current.toDouble() / max.toDouble()
    val filledBars = (percent * totalBars).toInt().coerceIn(0, totalBars)
    val emptyBars = totalBars - filledBars
    return "[" + "|".repeat(filledBars) + " ".repeat(emptyBars) + "]"
}

fun createProgressBarWithText(current: Int, max: Int, totalBars: Int = 10, prefix: String = "", suffix: String = ""): String {
    val bar = createProgressBar(current, max, totalBars)
    return "$prefix$bar$suffix ($current/$max)"
}
