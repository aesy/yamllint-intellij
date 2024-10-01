package io.aesy.yamllint.intention

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import io.aesy.yamllint.runner.YamllintProblem
import io.aesy.yamllint.util.YamlPsiElementFactory
import io.aesy.yamllint.util.YamllintBundle
import org.jetbrains.yaml.YAMLUtil

class SuppressLineIntention(
    private val problem: YamllintProblem
): BaseIntentionAction() {
    override fun getFamilyName(): String =
        YamllintBundle.message("intention.yaml.linters.yamllint.suppress-line.family-name")

    override fun getText(): String =
        YamllintBundle.message("intention.yaml.linters.yamllint.suppress-line.text", problem.rule)

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }

        val document = editor.document

        return problem.line < document.lineCount
    }

    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val document = editor.document

        if (problem.line > 0) {
            val offset = document.getLineStartOffset(problem.line - 1) + problem.column
            val prevElement = file.findElementAt(offset)

            if (prevElement is PsiComment) {
                val prevText = prevElement.text.trimStart('#').trimStart()

                if (prevText.startsWith("yamllint disable-line")) {
                    val comment = YamlPsiElementFactory.createComment(project, "$prevText rule:${problem.rule}")
                    prevElement.replace(comment)
                    DaemonCodeAnalyzer.getInstance(project).restart(file)
                    return
                }
            }
        }

        val lineStart = document.getLineStartOffset(problem.line)
        val element = file.findElementAt(lineStart) ?: return
        val indent = YAMLUtil.getIndentInThisLine(element)
        val comment = "yamllint disable-line rule:${problem.rule}"

        element.parent.addBefore(YamlPsiElementFactory.createComment(project, comment), element)
        element.parent.addBefore(YamlPsiElementFactory.createNewLine(project), element)
        element.parent.addBefore(YamlPsiElementFactory.createIndent(project, indent), element)

        DaemonCodeAnalyzer.getInstance(project).restart(file)
    }
}
