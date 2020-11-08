package io.aesy.yamllint

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service
@State(
    name = "YamllintSettings",
    storages = [Storage(StoragePathMacros.WORKSPACE_FILE, roamingType = RoamingType.DISABLED)]
)
class YamllintSettingsProvider(
    project: Project
) : PersistentStateComponent<YamllintSettings> {
    private val analyzer = project.getService<YamllintProjectAnalyzer>()

    private lateinit var settings: YamllintSettings

    override fun getState(): YamllintSettings = settings

    override fun loadState(settings: YamllintSettings) {
        this.settings = settings
    }

    override fun noStateLoaded() {
        this.settings = analyzer.getSuggestedSettings()
    }
}
