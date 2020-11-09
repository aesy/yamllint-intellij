package io.aesy.yamllint

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.jetbrains.yaml.psi.YAMLFile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isNotEmpty

@Disabled("This test requires Yamllint to be accessible on the host to pass")
class YamllintExternalAnnotatorIntegrationTest : JUnit5PlatformTest() {
    @BeforeEach
    fun setup() {
        // Fixes assertion error due to missing access to yamllint executable when installed through a package manager
        VfsRootAccess.allowRootAccess(TestDisposable(), "/usr/bin/", "/usr/local/bin")
    }

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
        val settings = project.service<YamllintSettingsProvider>()
        settings.state.enabled = true

        return WriteAction.computeAndWait<List<YamllintProblem>, Throwable> {
            val psiFile = psiManager.findFile(virtualFile) as YAMLFile

            annotator.doAnnotate(psiFile)
        }
    }
}
