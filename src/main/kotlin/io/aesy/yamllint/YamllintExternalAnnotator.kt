package io.aesy.yamllint

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile

@Service
class YamllintExternalAnnotator(
    project: Project
) : ExternalAnnotator<YAMLFile, List<YamllintProblem>>() {
    private val linter = project.getService<YamllintRunner>()
    private val documentManager = project.getService<PsiDocumentManager>()

    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        return file
    }

    override fun doAnnotate(file: YAMLFile): List<YamllintProblem> {
        return linter.run(files = arrayOf(file.name))
    }

    override fun apply(file: PsiFile, problems: List<YamllintProblem>, holder: AnnotationHolder) {
        val document = documentManager.getDocument(file) ?: return

        for ((_, line, column, level, message) in problems) {
            val startOffset = StringUtil.lineColToOffset(file.text, line, column)
            val endOffset = document.getLineEndOffset(line)
            val range = TextRange(startOffset, endOffset)
            val severity = when (level) {
                YamllintProblem.Level.WARNING -> HighlightSeverity.WARNING
                YamllintProblem.Level.ERROR -> HighlightSeverity.ERROR
            }

            holder.newAnnotation(severity, message)
                .range(range)
                .create()
        }
    }
}
