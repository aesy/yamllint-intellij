package io.aesy.yamllint

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class JUnit5PlatformTest : BasePlatformTestCase() {
    override fun getTestDataPath(): String {
        val resourceDirectory = JUnit5PlatformTest::class.java.getResource("/testData").toURI()

        return Paths.get(resourceDirectory).toString()
    }

    @BeforeEach
    override fun setUp() {
        super.setUp()
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }
}
