package io.aesy.yamllint.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.annotations.Tag

@State(
    name = "YamllintSettings",
    storages = [Storage(
        value = "yamllint.xml",
        roamingType = RoamingType.PER_OS,
    )],
    category = SettingsCategory.TOOLS,
)
class YamllintSettings: PersistentStateComponent<YamllintSettings> {
    companion object {
        fun getInstance(): YamllintSettings = service()
    }

    @Tag
    var enabled = false

    @Tag
    var binPath = ""

    @Tag
    var configPath = ""

    @Tag
    var disabledRules = emptySet<String>()

    override fun getState(): YamllintSettings = this

    override fun loadState(settings: YamllintSettings) {
        this.enabled = settings.enabled
        this.binPath = settings.binPath
        this.configPath = settings.configPath
        this.disabledRules = settings.disabledRules
    }
}
