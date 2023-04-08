package io.aesy.yamllint.runner

data class YamllintProblem(
    val file: String,
    val line: Int,
    val column: Int,
    val level: Level,
    val message: String,
    val rule: String,
) {
    enum class Level {
        WARNING, ERROR
    }
}
