package io.aesy.yamllint

data class YamllintProblem(
    val file: String,
    val line: Int,
    val column: Int,
    val level: Level,
    val message: String
) {
    enum class Level {
        WARNING, ERROR
    }
}
