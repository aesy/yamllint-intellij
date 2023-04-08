package io.aesy.yamllint.intention

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import io.aesy.yamllint.settings.YamllintSettings
import io.aesy.yamllint.util.YamllintBundle

class DisableRuleIntention(
    private val rule: String
): BaseIntentionAction() {
    override fun getFamilyName(): String =
        YamllintBundle.message("intention.yaml.linters.yamllint.disable-rule.family-name")

    override fun getText(): String = YamllintBundle.message("intention.yaml.linters.yamllint.disable-rule.text", rule)

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        val settings = project.service<YamllintSettings>()
        val newRules = settings.disabledRules.toMutableSet()
        newRules.add(rule)
        settings.disabledRules = newRules

        DaemonCodeAnalyzer.getInstance(project).restart()
    }
}
