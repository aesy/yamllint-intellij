package io.aesy.yamllint

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Paths

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockKExtension::class)
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
