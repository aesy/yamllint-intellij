package io.aesy.yamllint

import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull

// Note: This test require Yamllint to be installed locally to pass
class YamllintExecutableFinderTest : JUnit5PlatformTest() {
    @BeforeEach
    fun setup() {
        // Fixes assertion error due to missing access to yamllint executable when installed through a package manager
        VfsRootAccess.allowRootAccess(TestDisposable(), "/usr/bin/", "/usr/local/bin")
    }

    @Test
    @DisplayName("It should be able to find the location of the Yamllint executable")
    fun testFindYamllintExecutable() {
        val finder = YamllintExecutableFinder(project)
        val result = finder.find()

        expectThat(result).isNotNull()
    }
}
