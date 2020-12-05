package io.aesy.yamllint

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.service
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JComponent

class YamllintConfigurable(
    private val project: Project
) : SearchableConfigurable {
    private val settings = project.service<YamllintSettingsProvider>()

    private lateinit var enabledCheckbox: JCheckBox
    private lateinit var binPathField: TextFieldWithBrowseButton
    private lateinit var confPathField: TextFieldWithBrowseButton

    override fun getId(): String = "io.aesy.yamllint"

    override fun getDisplayName(): String = YamllintBundle.message("settings.yaml.linters.yamllint.configurable.name")

    override fun isModified(): Boolean {
        return settings.state.enabled != enabledCheckbox.isSelected ||
                settings.state.binPath != binPathField.text ||
                settings.state.configPath != confPathField.text
    }

    override fun apply() {
        settings.state.enabled = enabledCheckbox.isSelected
        settings.state.binPath = binPathField.text
        settings.state.configPath = confPathField.text

        // Trigger recalculation of all current annotations
        DaemonCodeAnalyzer.getInstance(project).restart()
    }

    override fun reset() {
        enabledCheckbox.isSelected = settings.state.enabled
        binPathField.text = settings.state.binPath
        confPathField.text = settings.state.configPath
    }

    override fun createComponent(): JComponent {
        enabledCheckbox = JCheckBox()
        binPathField = TextFieldWithBrowseButton()
        confPathField = TextFieldWithBrowseButton()
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
        val title = YamllintBundle.message("settings.yaml.linters.yamllint.configurable.name")
        val binDescription = YamllintBundle.message("settings.yaml.linters.yamllint.configurable.confpath-description")
        val confDescription = YamllintBundle.message("settings.yaml.linters.yamllint.configurable.binpath-description")

        binPathField.addBrowseFolderListener(title, binDescription, project, descriptor)
        confPathField.addBrowseFolderListener(title, confDescription, project, descriptor)

        return FormBuilder.createFormBuilder()
            .addLabeledComponent("Enabled", enabledCheckbox)
            .addLabeledComponent(binDescription, binPathField)
            .addLabeledComponent(confDescription, confPathField)
            .addComponentFillVertically(Spacer(), 0)
            .panel
    }
}
