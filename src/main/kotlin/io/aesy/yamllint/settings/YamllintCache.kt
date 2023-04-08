package io.aesy.yamllint.settings

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Tag

@State(
    name = "YamllintCache",
    storages = [Storage(StoragePathMacros.CACHE_FILE)]
)
class YamllintCache: PersistentStateComponent<YamllintCache> {
    companion object {
        fun getInstance(): YamllintCache = service()
    }

    @Tag
    var foundExecutables: Set<String> = emptySet()

    override fun getState(): YamllintCache = this

    override fun loadState(state: YamllintCache): Unit = XmlSerializerUtil.copyBean(state, this)
}
