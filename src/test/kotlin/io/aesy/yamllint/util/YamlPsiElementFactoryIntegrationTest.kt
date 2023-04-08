package io.aesy.yamllint.util

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.*

@ExtendWith(IntelliJExtension::class)
class YamlPsiElementFactoryIntegrationTest {
    @IntelliJ
    private lateinit var project: Project

    @Test
    @DisplayName("It should create a new YAML comment PSI element")
    fun testCreateComment() {
        val comment = ReadAction.compute<PsiComment, Throwable> {
            YamlPsiElementFactory.createComment(project, "This is a comment")
        }

        expectThat(comment.text).isEqualTo("# This is a comment")
    }

    @Test
    @DisplayName("It should create a new line PSI element")
    fun testCreateNewLine() {
        val newLine = ReadAction.compute<PsiElement, Throwable> {
            YamlPsiElementFactory.createNewLine(project)
        }

        expectThat(newLine.text).isEqualTo("\n")
    }
}
