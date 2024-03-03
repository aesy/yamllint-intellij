package io.aesy.yamllint

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.testFramework.fixtures.CodeInsightTestFixture
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.extension.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

annotation class IntelliJ

/**
 * JUnit 5 extension for IntelliJ tests
 */
class IntelliJExtension: BeforeEachCallback, AfterEachCallback, ParameterResolver {
    private lateinit var fixture: CodeInsightTestFixture

    override fun beforeEach(context: ExtensionContext) {
        initializeFixture(context.displayName)
        injectInstance(context.requiredTestInstance)
    }

    override fun afterEach(context: ExtensionContext) {
        cleanUp()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return resolveType(parameterContext.parameter.type) != null
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        val type = parameterContext.parameter.type

        return resolveType(type)
            ?: fail("Unknown type for injection: $type")
    }

    private fun initializeFixture(projectName: String) {
        val factory = IdeaTestFixtureFactory.getFixtureFactory()
        val projectFixture = factory.createLightFixtureBuilder(projectName).fixture
        val tempDirFixture = LightTempDirTestFixtureImpl(true)

        fixture = factory.createCodeInsightFixture(projectFixture, tempDirFixture)
        fixture.testDataPath = getTestDataPath().toString()
        fixture.setUp()
    }

    private fun cleanUp() {
        fixture.tearDown()

        ApplicationManager.getApplication().invokeAndWait {
            // BasePlatformTestCase doesn't normally clean up projects
            // between tests so we force deletion of the current project...
            LightPlatformTestCase.closeAndDeleteProject()
        }
    }

    private fun injectInstance(instance: Any) {
        val properties = instance::class.memberProperties
            .filter { it.findAnnotation<IntelliJ>() != null }

        for (property in properties) {
            if (property is KMutableProperty<*>) {
                val type = property.javaField!!.type
                val value = resolveType(type)
                    ?: fail("Unknown type for injection: $type")

                property.isAccessible = true
                property.setter.call(instance, value)
            } else {
                fail("Failed to set property: ${property.name}")
            }
        }
    }

    private fun resolveType(type: Class<*>): Any? {
        return when (type) {
            Project::class.java -> fixture.project
            Editor::class.java -> fixture.editor
            CodeInsightTestFixture::class.java -> fixture
            else -> null
        }
    }

    private fun getTestDataPath(): Path {
        val resourceDirectory = IntelliJExtension::class.java.getResource("/testData")!!.toURI()

        return Paths.get(resourceDirectory)
    }
}
