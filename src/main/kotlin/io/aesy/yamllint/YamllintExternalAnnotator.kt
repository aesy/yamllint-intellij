package io.aesy.yamllint

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile

class YamllintExternalAnnotator : ExternalAnnotator<YAMLFile, List<YamllintProblem>>() {
    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        return file
    }

    override fun doAnnotate(file: YAMLFile): List<YamllintProblem> {
        val project = file.project
        val fileSystem = LocalFileSystem.getInstance()
        val linter = project.getService<YamllintRunner>()

        // basePath may be null in default project
        val basePath = project.basePath
            ?: return linter.run(null, file.text)

        // file may be null, unclear when
        val workspace = fileSystem.findFileByPath(basePath)
            ?: return linter.run(null, file.text)

        // relative path may be null if editing a fragment
        val relativePath = VfsUtil.getRelativePath(file.virtualFile, workspace)
            ?: return linter.run(null, file.text)

        return linter.run(null, workspace.path, arrayOf(relativePath))
    }

    override fun apply(file: PsiFile, problems: List<YamllintProblem>, holder: AnnotationHolder) {
        val documentManager = PsiDocumentManager.getInstance(file.project)
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
