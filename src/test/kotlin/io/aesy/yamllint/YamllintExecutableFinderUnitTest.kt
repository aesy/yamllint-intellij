package io.aesy.yamllint

import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.util.ResourceUtil
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.assertions.isNull
import java.net.URL

class YamllintExecutableFinderUnitTest : JUnit5PlatformTest() {
    @Test
    @DisplayName("It should be able to find the location of the Yamllint executable")
    fun testFindYamllintExecutable(
        @MockK project: Project,
        @MockK commandLineExecutor: CommandLineExecutor
    ) {
        val file = getFilePath("yamllint")
        val output = ProcessOutput(0)
        output.appendStdout(file.path)

        every { project.service<CommandLineExecutor>() } returns commandLineExecutor
        every { commandLineExecutor.execute(any(), any()) } returns output

        val finder = YamllintExecutableFinder(project)
        val result = finder.find()

        expectThat(result).isNotNull()
    }

    @Test
    @DisplayName("It should return null if no Yamllint executable exists")
    fun testExecutableNotFound(
        @MockK project: Project,
        @MockK commandLineExecutor: CommandLineExecutor
    ) {
        every { project.service<CommandLineExecutor>() } returns commandLineExecutor
        every { commandLineExecutor.execute(any(), any()) } returns ProcessOutput(-1)

        val finder = YamllintExecutableFinder(project)
        val result = finder.find()

        expectThat(result).isNull()
    }

    private fun getFilePath(filename: String): URL {
        return ResourceUtil.getResource(this.javaClass, "testData", filename)
    }
}
