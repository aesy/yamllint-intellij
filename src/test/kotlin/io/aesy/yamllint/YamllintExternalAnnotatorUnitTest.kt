package io.aesy.yamllint

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.util.TextRange
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class YamllintExternalAnnotatorUnitTest : JUnit5PlatformTest() {
    @Test
    @DisplayName("It should apply error annotations")
    fun testApplyErrors(
        @MockK(relaxed = true) holder: AnnotationHolder
    ) {
        val annotator = YamllintExternalAnnotator()
        val psiFile = myFixture.configureByText("test.yml", "abc")
        val problem = YamllintProblem("test.yml", 1, 1, YamllintProblem.Level.ERROR, "message")
        val problems = listOf(problem)

        WriteAction.runAndWait<Throwable> {
            annotator.apply(psiFile, problems, holder)
        }

        verify(exactly = 1) {
            holder.newAnnotation(HighlightSeverity.ERROR, "message")
                .range(TextRange(0, 3))
                .needsUpdateOnTyping()
                .create()
        }
    }

    @Test
    @DisplayName("It should apply warning annotations")
    fun testApplyWarnings(
        @MockK(relaxed = true) holder: AnnotationHolder
    ) {
        val annotator = YamllintExternalAnnotator()
        val psiFile = myFixture.configureByText("test.yml", "abc")
        val problem = YamllintProblem("test.yml", 2, 2, YamllintProblem.Level.WARNING, "message")
        val problems = listOf(problem)

        WriteAction.runAndWait<Throwable> {
            annotator.apply(psiFile, problems, holder)
        }

        verify(exactly = 1) {
            holder.newAnnotation(HighlightSeverity.WARNING, "message")
                .range(TextRange(1, 3))
                .needsUpdateOnTyping()
                .create()
        }
    }
}
