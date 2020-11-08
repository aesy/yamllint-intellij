package io.aesy.yamllint

import com.intellij.openapi.components.Service
import com.intellij.util.LineSeparator
import java.text.ParseException

@Service
class YamllintOutputParser {
    companion object {
        private val pattern = Regex("(?<file>.+?):(?<line>\\d+):(?<column>\\d+): \\[(?<level>.+?)] (?<message>.+)")
    }

    @Throws(ParseException::class)
    fun parse(input: String): List<YamllintProblem> {
        return input
            .split(LineSeparator.LF.separatorString)
            .filter(String::isNotBlank)
            .mapIndexed(this::parseLine)
    }

    @Throws(ParseException::class)
    private fun parseLine(line: Int, text: String): YamllintProblem {
        val result = pattern.matchEntire(text)
            ?: throw ParseException("Line $line doesn't match excepected pattern", -1)

        return YamllintProblem(
            file = result.getString("file"),
            line = result.getInt("line"),
            column = result.getInt("column"),
            level = result.getLevel("level"),
            message = result.getString("message")
        )
    }

    @Throws(ParseException::class)
    private fun MatchResult.getString(name: String): String {
        return groups[name]?.value
            ?: throw ParseException("$name part is missing", -1)
    }

    @Throws(ParseException::class)
    private fun MatchResult.getInt(name: String): Int {
        val group = groups[name]
            ?: throw ParseException("$name part is missing", -1)

        try {
            return group.value.toInt()
        } catch (e: NumberFormatException) {
            throw ParseException(
                "Expected $name to be an integer, but got ${group.value}",
                group.range.first
            )
        }
    }

    @Throws(ParseException::class)
    private fun MatchResult.getLevel(name: String): YamllintProblem.Level {
        val group = groups[name]
            ?: throw ParseException("$name part is missing", -1)

        return when (group.value) {
            "warning" -> YamllintProblem.Level.WARNING
            "error" -> YamllintProblem.Level.ERROR
            else -> {
                val alternatives = YamllintProblem.Level.values().asList()

                throw ParseException(
                    "Expected $name to be one of $alternatives, but got ${group.value}",
                    group.range.first
                )
            }
        }
    }
}
