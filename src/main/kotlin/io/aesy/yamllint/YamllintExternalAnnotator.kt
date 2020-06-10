package io.aesy.yamllint

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.jetbrains.yaml.psi.YAMLFile
import kotlin.math.min

class YamllintExternalAnnotator : ExternalAnnotator<YAMLFile, List<YamllintProblem>>() {
    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        return file
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): YAMLFile? {
        return collectInformation(file)
    }

    override fun doAnnotate(file: YAMLFile): List<YamllintProblem> {
        val progressManager = ProgressManager.getInstance()
        val computable = Computable { lintFile(file) }
        val indicator = createIndicator(file)

        return progressManager.runProcess(computable, indicator)
    }

    override fun apply(file: PsiFile, problems: List<YamllintProblem>, holder: AnnotationHolder) {
        val documentManager = PsiDocumentManager.getInstance(file.project)
        val document = documentManager.getDocument(file) ?: return

        for ((_, line, column, level, message) in problems) {
            val annotationLine = if (document.lineCount < line) document.lineCount - 1 else line - 1
            val annotationColumn = column - 1
            val startOffset = document.getLineStartOffset(annotationLine) + annotationColumn
            val endOffset = document.getLineEndOffset(annotationLine)
            val range = TextRange.create(min(startOffset, endOffset), endOffset)
            val severity = level.toHighlightSeverity()
            val annotation = holder.newAnnotation(severity, message)
                .range(range)
                .needsUpdateOnTyping()

            if (startOffset !in 0 until endOffset) {
                annotation.afterEndOfLine()
            }

            annotation.create()
        }
    }

    private fun lintFile(file: PsiFile): List<YamllintProblem> {
        val project = file.project
        val fileManager = FileDocumentManager.getInstance()
        val fileSystem = LocalFileSystem.getInstance()
        val linter = project.getService<YamllintRunner>()

        try {
            if (fileManager.isFileModified(file.virtualFile)) {
                // We can't lint the file itself if there are changes in memory
                return linter.run(null, file.text)
            }

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
        } catch (e: YamllintException) {
            YamllintNotifications.error("Failed to execute yamllint\n$e\n${e.cause}").notify(project)

            return emptyList()
        }
    }

    private fun createIndicator(file: PsiFile): ProgressIndicator {
        return BackgroundableProcessIndicator(
            file.project,
            "Yamllint: Analyzing ${file.name}...",
            PerformInBackgroundOption.ALWAYS_BACKGROUND,
            "Stop",
            "Stop file analysis",
            false
        )
    }

    private fun YamllintProblem.Level.toHighlightSeverity(): HighlightSeverity = when (this) {
        YamllintProblem.Level.WARNING -> HighlightSeverity.WARNING
        YamllintProblem.Level.ERROR -> HighlightSeverity.ERROR
    }
}
