package io.aesy.yamllint

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

@Service
class YamllintConfigurationFinder(
    private val project: Project
) {
    companion object {
        private val SEARCH_NAMES = arrayOf(".yamllint", ".yamllint.yaml", ".yamllint.yml")
    }

    fun find(): VirtualFile? {
        val scope = GlobalSearchScope.projectScope(project)

        return SEARCH_NAMES
            .asSequence()
            .flatMap { name -> FilenameIndex.getVirtualFilesByName(name, scope).asSequence() }
            .firstOrNull()
    }
}
