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

@Disabled("This test requires Yamllint to be accessible on the host to pass")
@ExtendWith(IntelliJExtension::class)
class SuppressLineIntentionIntegrationTest {
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
    @DisplayName("It should be able to suppress the document-start error")
    fun testSuppressDocumentStart() {
        val file = fixture.configureByText("woop.yml", "key: 'value'")
        val offset = StringUtil.lineColToOffset(file.text, 0, 2)

        WriteAction.runAndWait<Throwable> { fixture.editor.caretModel.moveToOffset(offset) }

        val intention = fixture.findSingleIntention("Disable 'document-start' for line")

        fixture.launchAction(intention)

        // TODO the indentation is wrong, but I don't know how to resolve this
        fixture.checkResult("# yamllint disable-line rule:document-start\n  key: 'value'")
    }

    @Test
    @DisplayName("It should be able to suppress the truthy error")
    fun testSuppressTruthy() {
        val file = fixture.configureByText("woop.yml", "---\nkey: yes")
        val offset = StringUtil.lineColToOffset(file.text, 1, 6)

        WriteAction.runAndWait<Throwable> { fixture.editor.caretModel.moveToOffset(offset) }

        val intention = fixture.findSingleIntention("Disable 'truthy' for line")

        fixture.launchAction(intention)

        // TODO the indentation is wrong, but I don't know how to resolve this
        fixture.checkResult("---\n# yamllint disable-line rule:truthy\n  key: yes")
    }

    @Test
    @DisplayName("It should be able to suppress multiple rules in one comment")
    fun testSuppressMultipleRules() {
        val file = fixture.configureByText("woop.yml", "# yamllint disable-line rule:document-start\nkey: yes")
        val offset = StringUtil.lineColToOffset(file.text, 1, 6)

        WriteAction.runAndWait<Throwable> { fixture.editor.caretModel.moveToOffset(offset) }

        val intention = fixture.findSingleIntention("Disable 'truthy' for line")

        fixture.launchAction(intention)

        fixture.checkResult("# yamllint disable-line rule:document-start rule:truthy\nkey: yes")
    }
}
