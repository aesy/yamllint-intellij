package io.aesy.yamllint.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import org.jetbrains.yaml.YAMLElementGenerator

object YamlPsiElementFactory {
    fun createComment(project: Project, text: String): PsiComment {
        val generator = YAMLElementGenerator.getInstance(project)
        return generator.createDummyYamlWithText("# $text").firstChild as PsiComment
    }

    fun createNewLine(project: Project): PsiElement {
        val generator = YAMLElementGenerator.getInstance(project)
        return generator.createEol()
    }
}
