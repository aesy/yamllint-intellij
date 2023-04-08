package io.aesy.yamllint.runner

import com.intellij.lang.annotation.*
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import io.aesy.yamllint.YamllintException
import io.aesy.yamllint.intention.*
import io.aesy.yamllint.settings.YamllintSettings
import io.aesy.yamllint.util.YamllintBundle
import io.aesy.yamllint.util.YamllintNotifications
import org.jetbrains.yaml.psi.YAMLFile
import kotlin.math.min

class YamllintExternalAnnotator: ExternalAnnotator<YAMLFile, List<YamllintProblem>>() {
    override fun collectInformation(file: PsiFile): YAMLFile? {
        if (file !is YAMLFile) {
            return null
        }

        val settings = file.project.service<YamllintSettings>()

        if (!settings.enabled) {
            return null
        }

        return file
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

        for (problem in problems) {
            var (_, line, column, level, message, rule) = problem

            if (line >= document.lineCount) {
                line = document.lineCount - 1
            }

            val startOffset = document.getLineStartOffset(line) + column
            val endOffset = document.getLineEndOffset(line)
            val range = TextRange.create(min(startOffset, endOffset), endOffset)
            val severity = level.toHighlightSeverity()
            val annotation = holder.newAnnotation(severity, "$rule: $message")
                .range(range)
                .withFix(SuppressLineIntention(problem))
                .withFix(DisableRuleIntention(rule))
                .withFix(DisablePluginIntention())
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
        val linter = project.service<YamllintRunner>()

        try {
            // basePath may be null in default project
            val basePath = project.basePath
                ?: return linter.run(file.text)

            // file may be null, unclear when
            val workspace = fileSystem.findFileByPath(basePath)
                ?: return linter.run(file.text)

            if (fileManager.isFileModified(file.virtualFile)) {
                // We can't lint the file itself if there are changes in memory
                return linter.run(file.text, workspace.path)
            }

            // relative path may be null if editing a fragment
            val relativePath = VfsUtil.getRelativePath(file.virtualFile, workspace)
                ?: return linter.run(file.text, workspace.path)

            return linter.run(arrayOf(relativePath), workspace.path)
        } catch (e: YamllintException) {
            val message = YamllintBundle.message(
                "annotation.yaml.linters.yamllint.indicator.error",
                e.message ?: e.javaClass.simpleName,
                e.cause?.message ?: ""
            )
            YamllintNotifications.error(message).notify(project)
            return emptyList()
        }
    }

    private fun createIndicator(file: PsiFile): ProgressIndicator {
        return BackgroundableProcessIndicator(
            file.project,
            YamllintBundle.message("annotation.yaml.linters.yamllint.indicator.title", file.name),
            YamllintBundle.message("annotation.yaml.linters.yamllint.indicator.cancel-label"),
            YamllintBundle.message("annotation.yaml.linters.yamllint.indicator.cancel-tooltip"),
            false
        )
    }

    private fun YamllintProblem.Level.toHighlightSeverity(): HighlightSeverity = when (this) {
        YamllintProblem.Level.WARNING -> HighlightSeverity.WARNING
        YamllintProblem.Level.ERROR -> HighlightSeverity.ERROR
    }
}
