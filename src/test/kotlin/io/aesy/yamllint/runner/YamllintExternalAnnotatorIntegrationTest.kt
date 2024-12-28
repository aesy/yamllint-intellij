package io.aesy.yamllint.runner

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import io.aesy.yamllint.settings.YamllintSettings
import io.aesy.yamllint.startup.*
import org.jetbrains.yaml.psi.YAMLFile
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEmpty
import strikt.assertions.isNotEmpty

@Disabled("This test requires Yamllint to be accessible on the host to pass")
@ExtendWith(IntelliJExtension::class)
class YamllintExternalAnnotatorIntegrationTest {
    @IntelliJ
    private lateinit var fixture: CodeInsightTestFixture

    @IntelliJ
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        // Fixes assertion error due to missing access to yamllint executable when installed through a package manager
        VfsRootAccess.allowRootAccess({}, "/usr/bin/", "/usr/local/bin")

        val settings = project.service<YamllintSettings>()
        settings.state.enabled = true
        settings.state.binPath = YamllintExecutableProvider.find(project).first()
    }

    @Test
    @DisplayName("It should return an empty list when annotating a valid file")
    fun testAnnotateValidFile() {
        val results = annotateFile("valid.yml")

        expectThat(results).isEmpty()
    }

    @Test
    @DisplayName("It should return all problems when annotating an invalid file")
    fun testAnnotateInvalidFile() {
        val results = annotateFile("invalid.yml")

        expectThat(results).isNotEmpty()
    }

    private fun annotateFile(filePath: String): List<YamllintProblem> {
        val annotator = YamllintExternalAnnotator()
        val virtualFile = fixture.copyFileToProject(filePath)

        return WriteAction.computeAndWait<List<YamllintProblem>, Throwable> {
            val psiManager = fixture.psiManager
            val psiFile = psiManager.findFile(virtualFile) as YAMLFile

            annotator.doAnnotate(psiFile)
        }
    }
}
