package io.aesy.yamllint.runner

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import java.text.ParseException
import java.util.stream.Stream

class YamllintOutputParserUnitTest {
    @ParameterizedTest
    @DisplayName("It should return object representations of the given input")
    @MethodSource("provideValidInput")
    fun testValidInput(input: String, problems: List<YamllintProblem>) {
        val result = YamllintOutputParser.parse(input)

        expectThat(result).isEqualTo(problems)
    }

    @ParameterizedTest
    @DisplayName("It should ignore whitespace")
    @MethodSource("provideWhitespace")
    fun testEmptyInput(input: String) {
        val result = YamllintOutputParser.parse(input)

        expectThat(result).isEmpty()
    }

    @ParameterizedTest
    @DisplayName("It should throw a ParseException if given invalid input")
    @MethodSource("provideInvalidInput")
    fun testInvalidInput(input: String) {
        expectThrows<ParseException> {
            YamllintOutputParser.parse(input)
        }
    }

    companion object {
        @JvmStatic
        fun provideWhitespace(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(""),
                Arguments.of(" "),
                Arguments.of("\n")
            )
        }

        @JvmStatic
        fun provideValidInput(): Stream<Arguments> {
            val problem1 =
                YamllintProblem("file1", 0, 1, YamllintProblem.Level.WARNING, "message1", "line-length")
            val problem2 =
                YamllintProblem("file2", 2, 3, YamllintProblem.Level.ERROR, "message2", "line-length")
            val problem3 =
                YamllintProblem("file3", 0, 0, YamllintProblem.Level.ERROR, "message with ] bracket", "line-length")
            val problem4 =
                YamllintProblem("file4", 0, 0, YamllintProblem.Level.ERROR, "message with (parens)", "line-length")

            return Stream.of(
                Arguments.of(
                    "file1:1:2: [warning] message1 (line-length)",
                    listOf(problem1)
                ),
                Arguments.of(
                    "file1:1:2: [warning] message1 (line-length)\n",
                    listOf(problem1)
                ),
                Arguments.of(
                    "file1:1:2: [warning] message1 (line-length)\nfile2:3:4: [error] message2 (line-length)",
                    listOf(problem1, problem2)
                ),
                Arguments.of(
                    "file3:1:1: [error] message with ] bracket (line-length)",
                    listOf(problem3)
                ),
                Arguments.of(
                    "file4:1:1: [error] message with (parens) (line-length)",
                    listOf(problem4)
                )
            )
        }

        @JvmStatic
        fun provideInvalidInput(): Stream<Arguments> {
            return Stream.of(
                Arguments.of("-"),
                Arguments.of("invalid"),
                Arguments.of("file:1:1: [error]"),
                Arguments.of("file:1:1: [invalid] message")
            )
        }
    }
}
