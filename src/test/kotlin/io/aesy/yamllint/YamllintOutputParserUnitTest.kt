package io.aesy.yamllint

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

class YamllintOutputParserUnitTest : JUnit5PlatformTest() {
    @ParameterizedTest
    @DisplayName("It should return object representations of the given input")
    @MethodSource("provideValidInput")
    fun testValidInput(input: String, problems: List<YamllintProblem>) {
        val parser = YamllintOutputParser()
        val result = parser.parse(input)

        expectThat(result).isEqualTo(problems)
    }

    @ParameterizedTest
    @DisplayName("It should ignore whitespace")
    @MethodSource("provideWhitespace")
    fun testEmptyInput(input: String) {
        val parser = YamllintOutputParser()
        val result = parser.parse(input)

        expectThat(result).isEmpty()
    }

    @ParameterizedTest
    @DisplayName("It should throw a ParseException if given invalid input")
    @MethodSource("provideInvalidInput")
    fun testInvalidInput(input: String) {
        val parser = YamllintOutputParser()

        expectThrows<ParseException> {
            parser.parse(input)
        }
    }

    @Suppress("unused")
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
            val problem1 = YamllintProblem("file1", 1, 2, YamllintProblem.Level.WARNING, "message1")
            val problem2 = YamllintProblem("file2", 3, 4, YamllintProblem.Level.ERROR, "message2")
            val problem3 = YamllintProblem("file", 1, 1, YamllintProblem.Level.ERROR, "message with ] bracket")

            return Stream.of(
                Arguments.of("file1:1:2: [warning] message1", listOf(problem1)),
                Arguments.of("file1:1:2: [warning] message1\n", listOf(problem1)),
                Arguments.of("file1:1:2: [warning] message1\nfile2:3:4: [error] message2", listOf(problem1, problem2)),
                Arguments.of("file:1:1: [error] message with ] bracket", listOf(problem3))
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
