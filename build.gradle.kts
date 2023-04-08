import org.jetbrains.intellij.tasks.PublishPluginTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
    kotlin("jvm") version "1.8.20"
}

group = "io.aesy.yamllint"
version = "0.2"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
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
    jacocoTestReport {
        reports {
            xml.required.set(true)
        }
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
        finalizedBy(jacocoTestReport)
    }

    withType<PublishPluginTask> {
        token.set(System.getenv("INTELLIJ_HUB_TOKEN"))
    }
}
