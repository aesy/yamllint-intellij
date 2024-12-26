import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.0"
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
}

group = "io.aesy.yamllint"
version = "0.5"

repositories {
    mavenCentral()
    mavenLocal()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.mockk:mockk:1.13.14")

    // https://youtrack.jetbrains.com/issue/IJPL-159134/JUnit5-Test-Framework-refers-to-JUnit4-java.lang.NoClassDefFoundError-junit-framework-TestCase
    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        // https://www.jetbrains.com/idea/download/other.html
        intellijIdeaCommunity("2024.3.1.1")

        // https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html#ids-of-bundled-plugins
        bundledPlugin("org.jetbrains.plugins.yaml")

        // https://github.com/JetBrains/intellij-platform-gradle-plugin/issues/1838
        bundledPlugin("com.intellij.llmInstaller")

        // https://plugins.jetbrains.com/plugin/631-python/versions
        plugin("PythonCore", "243.22562.145")

        testFramework(TestFrameworkType.Platform)
    }
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    pluginConfiguration {
        name = rootProject.name
    }
}

tasks {
    wrapper {
        gradleVersion = "8.12"
    }

    test {
        useJUnitPlatform()
    }

    publishPlugin {
        token = System.getenv("INTELLIJ_HUB_TOKEN")
    }
}
