package io.aesy.yamllint.startup

import com.intellij.openapi.project.Project
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.concurrency.annotations.RequiresEdt

interface YamllintConfigurationProvider {
    companion object {
        private val SEARCH_NAMES = arrayOf(".yamllint", ".yamllint.yaml", ".yamllint.yml")

        @RequiresEdt
        fun find(project: Project): Set<String> {
            val scope = GlobalSearchScope.projectScope(project)

            return SEARCH_NAMES.asSequence()
                .flatMap { name -> FilenameIndex.getVirtualFilesByName(name, scope).asSequence() }
                .map { it.path }
                .toSet()
        }
    }
}
