plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

group = "io.aesy.yamllint"
version = "0.4"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.mockk:mockk:1.13.10")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(11)
}

intellij {
    pluginName = rootProject.name
    version = "2022.1.1"
    type = "IC"
    updateSinceUntilBuild = false
    plugins = listOf("org.jetbrains.plugins.yaml", "PythonCore:221.5591.52")
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }

    test {
        useJUnitPlatform()
    }

    publishPlugin {
        token = System.getenv("INTELLIJ_HUB_TOKEN")
    }
}
