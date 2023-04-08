package io.aesy.yamllint.startup

import com.intellij.execution.process.ProcessIOExecutorService
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.*
import com.intellij.ui.EditorNotificationProvider.CONST_NULL
import io.aesy.yamllint.settings.YamllintCache
import io.aesy.yamllint.settings.YamllintSettings
import io.aesy.yamllint.util.YamllintBundle
import io.aesy.yamllint.util.getLogger
import org.jetbrains.yaml.YAMLFileType
import java.util.function.Function
import javax.swing.JComponent

class YamllintActivationNotificationProvider: EditorNotificationProvider, DumbAware {
    companion object {
        private const val DONT_ASK_AGAIN_KEY = "io.aesy.yamllint.do.not.ask.to.enable"

        private val logger = getLogger()
    }

    override fun collectNotificationData(
        project: Project,
        file: VirtualFile
    ): Function<in FileEditor, out JComponent?> {
        if (file.fileType !== YAMLFileType.YML) {
            return CONST_NULL
        }

        if (PropertiesComponent.getInstance(project).getBoolean(DONT_ASK_AGAIN_KEY)) {
            return CONST_NULL
        }

        val settings = project.service<YamllintSettings>()

        if (settings.state.enabled && settings.state.binPath.isNotEmpty()) {
            return CONST_NULL
        }

        val cache = YamllintCache.getInstance()
        val executables = cache.foundExecutables
        val configFiles = YamllintConfigurationProvider.find(project)

        logger.info("Found ${configFiles.size} Yamllint config files in project")

        if (executables.isEmpty()) {
            ProcessIOExecutorService.INSTANCE.execute {
                val foundExecutables = YamllintExecutableProvider.find(project)

                logger.info("Found ${foundExecutables.size} Yamllint executables")

                if (foundExecutables.isNotEmpty()) {
                    cache.foundExecutables = foundExecutables

                    ApplicationManager.getApplication().invokeLater(
                        { EditorNotifications.getInstance(project).updateNotifications(file) },
                        project.disposed
                    )
                }
            }

            return CONST_NULL
        }

        val binPath = executables.first()
        val configPath = configFiles.firstOrNull() ?: ""

        return Function<FileEditor, JComponent?> { fileEditor -> createPanel(project, fileEditor, binPath, configPath) }
    }

    private fun createPanel(
        project: Project,
        editor: FileEditor,
        binPath: String,
        configPath: String
    ): EditorNotificationPanel {
        return EditorNotificationPanel(editor).apply {
            text = YamllintBundle.message("notification.yaml.linters.yamllint.activation-notifier.notification-text")
            createActionLabel(YamllintBundle.message("notification.yaml.linters.yamllint.activation-notifier.enable-label")) {
                logger.info("Enabling Yamllint")
                val settings = project.service<YamllintSettings>()
                settings.state.enabled = true
                settings.state.binPath = binPath
                settings.state.configPath = configPath
                EditorNotifications.updateAll()
            }
            createActionLabel(YamllintBundle.message("notification.yaml.linters.yamllint.activation-notifier.dismiss-label")) {
                logger.info("Disabling activation notification")
                PropertiesComponent.getInstance(project).setValue(DONT_ASK_AGAIN_KEY, true)
                EditorNotifications.updateAll()
            }
        }
    }
}
