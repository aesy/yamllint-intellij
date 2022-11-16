import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    kotlin("jvm") version "1.6.21"
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "io.aesy.yamllint"
version = "0.2"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("io.strikt:strikt-core:0.34.1")
    testImplementation("io.mockk:mockk:1.13.2")
}

intellij {
    pluginName.set(rootProject.name)
    version.set("IC-2022.1.1")
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("org.jetbrains.plugins.yaml"))
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
}
