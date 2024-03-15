package io.aesy.yamllint.runner

import com.intellij.lang.annotation.*
import com.intellij.openapi.application.WriteAction
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(IntelliJExtension::class, MockKExtension::class)
class YamllintExternalAnnotatorUnitTest {
    @IntelliJ
    private lateinit var fixture: CodeInsightTestFixture

    @Test
    @DisplayName("It should apply error annotations")
    fun testApplyErrors(
        @MockK holder: AnnotationHolder,
        @MockK(relaxed = true) builder: AnnotationBuilder
    ) {
        val annotator = YamllintExternalAnnotator()
        val psiFile = fixture.configureByText("test.yml", "abc")
        val problem = YamllintProblem("test.yml", 0, 0, YamllintProblem.Level.ERROR, "Message", "indentation")
        val problems = listOf(problem)

        every { holder.newAnnotation(any(), any()) } returns builder

        WriteAction.runAndWait<Throwable> {
            annotator.apply(psiFile, problems, holder)
        }

        // Can't verify more than this because Mockk is utterly broken when it comes to chaining
        verify { holder.newAnnotation(HighlightSeverity.ERROR, "indentation: Message") }
    }

    @Test
    @DisplayName("It should apply warning annotations")
    fun testApplyWarnings(
        @MockK holder: AnnotationHolder,
        @MockK(relaxed = true) builder: AnnotationBuilder
    ) {
        val annotator = YamllintExternalAnnotator()
        val psiFile = fixture.configureByText("test.yml", "abc")
        val problem = YamllintProblem("test.yml", 1, 1, YamllintProblem.Level.WARNING, "Message", "indentation")
        val problems = listOf(problem)

        every { holder.newAnnotation(any(), any()) } returns builder

        WriteAction.runAndWait<Throwable> {
            annotator.apply(psiFile, problems, holder)
        }

        verify { holder.newAnnotation(HighlightSeverity.WARNING, "indentation: Message") }
    }
}
