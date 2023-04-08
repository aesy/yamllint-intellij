package io.aesy.yamllint.intention

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.util.nextLeaf
import io.aesy.yamllint.runner.YamllintProblem
import io.aesy.yamllint.util.YamlPsiElementFactory
import io.aesy.yamllint.util.YamllintBundle

class SuppressLineIntention(
    private val problem: YamllintProblem
): BaseIntentionAction() {
    override fun getFamilyName(): String =
        YamllintBundle.message("intention.yaml.linters.yamllint.suppress-line.family-name")

    override fun getText(): String =
        YamllintBundle.message("intention.yaml.linters.yamllint.suppress-line.text", problem.line)

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }

        val document = editor.document

        return problem.line < document.lineCount
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val document = editor.document
        val prevElement = file.findElementAt(document.getLineStartOffset(problem.line - 1))?.nextLeaf(true)
        val prevText = prevElement?.text?.trimStart('#')?.trimStart()

        if (prevElement is PsiComment && prevText != null && prevText.startsWith("yamllint disable-line")) {
            val comment = YamlPsiElementFactory.createComment(project, "$prevText rule:${problem.rule}")
            prevElement.replace(comment)
        } else {
            val errorElement = file.findElementAt(document.getLineStartOffset(problem.line) + problem.column) ?: return
            val comment = YamlPsiElementFactory.createComment(project, "yamllint disable-line rule:${problem.rule}")

            errorElement.parent.addBefore(comment, errorElement)
            errorElement.parent.addBefore(YamlPsiElementFactory.createNewLine(project), errorElement)
        }

        DaemonCodeAnalyzer.getInstance(project).restart(file)
    }
}
