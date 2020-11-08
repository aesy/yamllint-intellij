package io.aesy.yamllint

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class YamllintProjectAnalyzerUnitTest : JUnit5PlatformTest() {
    @Test
    @DisplayName("It should suggest the path of a config if one is found")
    fun testFindConfiguration(
        @MockK project: Project,
        @MockK binFinder: YamllintExecutableFinder,
        @MockK confFinder: YamllintConfigurationFinder,
        @MockK conf: PsiFile
    ) {
        every { project.getService<YamllintExecutableFinder>() } returns binFinder
        every { project.getService<YamllintConfigurationFinder>() } returns confFinder
        every { binFinder.find() } returns null
        every { confFinder.find() } returns conf
        every { conf.virtualFile.path } returns ".yamllint"

        val analyzer = YamllintProjectAnalyzer(project)
        val settings = analyzer.getSuggestedSettings()

        expectThat(settings.configPath).isEqualTo(".yamllint")
    }

    @Test
    @DisplayName("It should suggest the plugin to be enabled if a config is found")
    fun testEnabled(
        @MockK project: Project,
        @MockK binFinder: YamllintExecutableFinder,
        @MockK confFinder: YamllintConfigurationFinder,
        @MockK conf: PsiFile
    ) {
        every { project.getService<YamllintExecutableFinder>() } returns binFinder
        every { project.getService<YamllintConfigurationFinder>() } returns confFinder
        every { binFinder.find() } returns null
        every { confFinder.find() } returns conf
        every { conf.virtualFile.path } returns ".yamllint"

        val analyzer = YamllintProjectAnalyzer(project)
        val settings = analyzer.getSuggestedSettings()

        expectThat(settings.enabled).isTrue()
    }

    @Test
    @DisplayName("It should suggest the path of a executable if one is found")
    fun testFindExecutable(
        @MockK project: Project,
        @MockK binFinder: YamllintExecutableFinder,
        @MockK confFinder: YamllintConfigurationFinder,
        @MockK executable: VirtualFile
    ) {
        every { project.getService<YamllintExecutableFinder>() } returns binFinder
        every { project.getService<YamllintConfigurationFinder>() } returns confFinder
        every { binFinder.find() } returns executable
        every { executable.path } returns "/usr/bin/yamllint"
        every { confFinder.find() } returns null

        val analyzer = YamllintProjectAnalyzer(project)
        val settings = analyzer.getSuggestedSettings()

        expectThat(settings.binPath).isEqualTo("/usr/bin/yamllint")
    }

    @Test
    @DisplayName("It should default to yamllint in path, no config, and not enabled")
    fun testDefaults(
        @MockK project: Project,
        @MockK binFinder: YamllintExecutableFinder,
        @MockK confFinder: YamllintConfigurationFinder
    ) {
        every { project.getService<YamllintExecutableFinder>() } returns binFinder
        every { project.getService<YamllintConfigurationFinder>() } returns confFinder
        every { binFinder.find() } returns null
        every { confFinder.find() } returns null

        val analyzer = YamllintProjectAnalyzer(project)
        val settings = analyzer.getSuggestedSettings()

        expectThat(settings.binPath).isEqualTo("yamllint")
        expectThat(settings.configPath).isEqualTo("")
        expectThat(settings.enabled).isFalse()
    }
}
