package io.aesy.yamllint.startup

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

interface YamllintExecutableProvider {
    fun priority(): Int = 0
    fun find(project: Project): VirtualFile?

    companion object {
        private val EP_NAME: ExtensionPointName<YamllintExecutableProvider> =
            ExtensionPointName("io.aesy.yamllint.yamllintExecutableProvider")

        fun find(project: Project): Set<String> {
            return EP_NAME.extensionList.asSequence()
                .sortedByDescending(YamllintExecutableProvider::priority)
                .mapNotNull { extension -> extension.find(project) }
                .map { it.path }
                .toSet()
        }
    }
}
