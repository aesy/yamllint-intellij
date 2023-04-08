package io.aesy.yamllint.settings

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.uiDesigner.core.GridConstraints
import io.aesy.yamllint.util.Yamllint
import io.aesy.yamllint.util.YamllintBundle
import java.awt.Desktop
import java.awt.Dimension
import java.net.URI
import javax.swing.*

class YamllintConfigurable(
    private val project: Project,
): SearchableConfigurable {
    private val settings = project.service<YamllintSettings>()

    private lateinit var mainPanel: JPanel
    private lateinit var disabledRulesContainer: JPanel
    private lateinit var enabled: JCheckBox
    private lateinit var binPath: TextFieldWithBrowseButton
    private lateinit var configPath: TextFieldWithBrowseButton
    private lateinit var disabledRules: TextFieldWithAutoCompletion<String>
    private lateinit var githubButton: JButton

    override fun getId(): String = "io.aesy.yamllint"

    override fun getDisplayName(): String = YamllintBundle.message("settings.yaml.linters.yamllint.configurable.title")

    override fun isModified(): Boolean {
        return settings.enabled != enabled.isSelected ||
            settings.binPath != binPath.text ||
            settings.configPath != configPath.text ||
            settings.disabledRules.joinToString(", ") != disabledRules.text
    }

    override fun apply() {
        settings.enabled = enabled.isSelected
        settings.binPath = binPath.text.trim()
        settings.configPath = configPath.text.trim()
        settings.disabledRules = disabledRules.text
            .split(",")
            .map(String::trim)
            .filter(String::isNotBlank)
            .toSet()

        DaemonCodeAnalyzer.getInstance(project).restart()
    }

    override fun reset() {
        enabled.isSelected = settings.enabled
        binPath.text = settings.binPath
        configPath.text = settings.configPath
        disabledRules.text = settings.disabledRules.joinToString(", ")
    }

    override fun createComponent(): JComponent {
        mainPanel.border =
            IdeBorderFactory.createTitledBorder(YamllintBundle.message("settings.yaml.linters.yamllint.configurable.title"))

        enabled.addChangeListener {
            for (field in arrayOf(
                disabledRules,
                binPath,
                configPath,
            )) {
                field.isEnabled = enabled.isSelected
            }
        }

        binPath.addBrowseFolderListener(
            YamllintBundle.message("settings.yaml.linters.yamllint.configurable.binpath-label"),
            YamllintBundle.message("settings.yaml.linters.yamllint.configurable.binpath-description"),
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
        )

        configPath.addBrowseFolderListener(
            YamllintBundle.message("settings.yaml.linters.yamllint.configurable.confpath-label"),
            YamllintBundle.message("settings.yaml.linters.yamllint.configurable.confpath-description"),
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
        )

        disabledRules = TextFieldWithAutoCompletion.create(project, Yamllint.RULES, true, "")
        disabledRules.toolTipText =
            YamllintBundle.message("settings.yaml.linters.yamllint.configurable.disabled-rules-tooltip")
        disabledRulesContainer.add(
            disabledRules,
            GridConstraints(
                0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW or GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                Dimension(-1, -1), Dimension(150, -1), Dimension(-1, -1),
            ),
        )

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            githubButton.addActionListener {
                Desktop.getDesktop().browse(URI("https://github.com/aesy/yamllint-intellij"))
            }
        } else {
            githubButton.isVisible = false
        }

        return mainPanel
    }
}
