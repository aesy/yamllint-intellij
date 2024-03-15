package io.aesy.yamllint.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.yaml.YAMLElementGenerator

object YamlPsiElementFactory {
    fun createIndent(project: Project, size: Int): PsiElement {
        val generator = YAMLElementGenerator.getInstance(project)
        val file = generator.createDummyYamlWithText(StringUtil.repeatSymbol(' ', size))
        return PsiTreeUtil.getDeepestFirst(file)
    }

    fun createComment(project: Project, text: String): PsiComment {
        val generator = YAMLElementGenerator.getInstance(project)
        return generator.createDummyYamlWithText("# $text").firstChild as PsiComment
    }

    fun createNewLine(project: Project): PsiElement {
        val generator = YAMLElementGenerator.getInstance(project)
        return generator.createEol()
    }
}
