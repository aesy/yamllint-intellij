package io.aesy.yamllint

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

@Service
class YamllintConfigurationFinder(
    private val project: Project
) {
    companion object {
        private val SEARCH_NAMES = arrayOf(".yamllint", ".yamllint.yaml", ".yamllint.yml")
    }

    fun find(): PsiFile? {
        val scope = GlobalSearchScope.projectScope(project)

        return SEARCH_NAMES
            .asSequence()
            .flatMap { name -> FilenameIndex.getFilesByName(project, name, scope).asSequence() }
            .firstOrNull()
    }
}
