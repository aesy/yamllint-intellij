package io.aesy.yamllint

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull

// Note: This test require Yamllint to be installed locally to pass
class YamllintExecutableFinderTest : JUnit5PlatformTest() {
    @Test
    @DisplayName("It should be able to find the location of the Yamllint executable")
    fun testFindYamllintExecutable() {
        val finder = YamllintExecutableFinder(project)
        val result = finder.find()

        expectThat(result).isNotNull()
    }
}
