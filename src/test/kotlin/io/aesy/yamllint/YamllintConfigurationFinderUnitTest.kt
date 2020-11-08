package io.aesy.yamllint

import com.intellij.openapi.application.WriteAction
import com.intellij.psi.PsiFile
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class YamllintConfigurationFinderUnitTest : JUnit5PlatformTest() {
    @ParameterizedTest
    @DisplayName("It should be able to find the location of the Yamllint configuration in the given project")
    @ValueSource(strings = [".yamllint", ".yamllint.yaml", ".yamllint.yml"])
    fun testFindYamllintConfig(fileName: String) {
        myFixture.copyFileToProject(".yamllint", fileName)

        val finder = YamllintConfigurationFinder(project)
        val config = WriteAction.computeAndWait<PsiFile?, Throwable> { finder.find() }

        expectThat(config).isNotNull()
    }

    @Test
    @DisplayName("It should return null if no Yamllint configuration exists")
    fun testYamllintConfigNotFound() {
        val finder = YamllintConfigurationFinder(project)
        val config = WriteAction.computeAndWait<PsiFile?, Throwable> { finder.find() }

        expectThat(config).isNull()
    }
}
