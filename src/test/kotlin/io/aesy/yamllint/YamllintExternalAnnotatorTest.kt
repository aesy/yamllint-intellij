package io.aesy.yamllint

import com.intellij.openapi.application.WriteAction
import org.jetbrains.yaml.psi.YAMLFile
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isNotEmpty

// Note: These tests require Yamllint to be installed locally to pass
class YamllintExternalAnnotatorTest : JUnit5PlatformTest() {
    @Test
    @DisplayName("It should return an empty list when annotating a valid file")
    fun testAnnotatetValidFile() {
        val results = annotateFile("valid.yml")

        expectThat(results).isEmpty()
    }

    @Test
    @DisplayName("It should return all problems when annotating an invalid file")
    fun testAnnotatetInvalidFile() {
        val results = annotateFile("invalid.yml")

        expectThat(results).isNotEmpty()
    }

    private fun annotateFile(filePath: String): List<YamllintProblem> {
        val annotator = YamllintExternalAnnotator()
        val virtualFile = myFixture.copyFileToProject(filePath)

        return WriteAction.computeAndWait<List<YamllintProblem>, Throwable> {
            val psiFile = psiManager.findFile(virtualFile) as YAMLFile

            annotator.doAnnotate(psiFile)
        }
    }
}
