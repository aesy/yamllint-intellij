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
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.mockk:mockk:1.13.14")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(17)
}

intellij {
    pluginName = rootProject.name
    // https://www.jetbrains.com/idea/download/other.html
    version = "2024.3.1.1"
    type = "IC"
    updateSinceUntilBuild = false
    plugins = listOf(
        "org.jetbrains.plugins.yaml",
        // https://plugins.jetbrains.com/plugin/631-python/versions
        "PythonCore:243.22562.145"
    )
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
