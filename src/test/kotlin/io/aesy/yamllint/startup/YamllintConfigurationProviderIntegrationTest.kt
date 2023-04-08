package io.aesy.yamllint.startup

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.*

@ExtendWith(IntelliJExtension::class)
class YamllintConfigurationProviderIntegrationTest {
    @IntelliJ
    private lateinit var fixture: CodeInsightTestFixture

    @IntelliJ
    private lateinit var project: Project

    @ParameterizedTest
    @DisplayName("It should be able to find the location of the Yamllint configurations in the given project")
    @ValueSource(strings = [".yamllint", ".yamllint.yaml", ".yamllint.yml"])
    fun testFindYamllintConfig(fileName: String) {
        fixture.copyFileToProject(".yamllint", fileName)

        val config = ReadAction.compute<Set<String>, Throwable> {
            YamllintConfigurationProvider.find(project)
        }

        expectThat(config).isNotEmpty()
    }

    @Test
    @DisplayName("It should return an empty set if no Yamllint configuration exists")
    fun testYamllintConfigNotFound() {
        val config = ReadAction.compute<Set<String>, Throwable> {
            YamllintConfigurationProvider.find(project)
        }

        expectThat(config).isEmpty()
    }
}
