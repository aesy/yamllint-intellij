package io.aesy.yamllint.intention

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import io.aesy.yamllint.settings.YamllintSettings
import io.aesy.yamllint.startup.YamllintExecutableProvider
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.containsExactly

@ExtendWith(IntelliJExtension::class)
class DisableRuleIntentionIntegrationTest {
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
    @DisplayName("It should be possible to disable a rule")
    fun testDisableRule() {
        val file = fixture.configureByText("woop.yml", "key: 'value'")
        val offset = StringUtil.lineColToOffset(file.text, 0, 2)

        WriteAction.runAndWait<Throwable> { fixture.editor.caretModel.moveToOffset(offset) }

        val intention = fixture.findSingleIntention("Disable 'document-start' for project")

        fixture.launchAction(intention)

        val settings = project.service<YamllintSettings>()

        expectThat(settings.disabledRules).containsExactly("document-start")
    }
}
