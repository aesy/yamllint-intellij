package io.aesy.yamllint.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import io.aesy.yamllint.IntelliJ
import io.aesy.yamllint.IntelliJExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isNotNull

@ExtendWith(IntelliJExtension::class)
class YamllintPathExecutableProviderIntegration {
    @IntelliJ
    private lateinit var project: Project

    @BeforeEach
    fun setup() {
        // Fixes assertion error due to missing access to yamllint executable when installed through a package manager
        VfsRootAccess.allowRootAccess({}, "/usr/bin/", "/usr/local/bin")
    }

    @Test
    @DisplayName("It should be able to find the location of the Yamllint executable")
    fun testFindYamllintExecutable() {
        val result = YamllintPathExecutableProvider().find(project)

        expectThat(result).isNotNull()
    }
}
