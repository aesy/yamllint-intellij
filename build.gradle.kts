import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    kotlin("jvm") version "1.8.20"
}

group = "io.aesy.yamllint"
version = "0.3"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.mockk:mockk:1.13.4")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

intellij {
    pluginName.set(rootProject.name)
    version.set("IC-2022.1.1")
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("org.jetbrains.plugins.yaml", "PythonCore:221.5591.52"))
}

tasks {
    koverReport {
        dependsOn(withType<Test>())
    }

    withType<Wrapper> {
        gradleVersion = "7.5"
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        finalizedBy(koverReport)
    }

    withType<PublishPluginTask> {
        token.set(System.getenv("INTELLIJ_HUB_TOKEN"))
    }
}
