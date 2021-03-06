import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    jacoco
    kotlin("jvm") version "1.3.61"
    id("org.jetbrains.intellij") version "0.4.21"
}

group = "io.aesy.yamllint"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
    testImplementation("io.strikt:strikt-core:0.26.1")
    testImplementation("io.mockk:mockk:1.10.0")
}

intellij {
    version = "IC-2020.1"
    pluginName = rootProject.name
    updateSinceUntilBuild = false
    setPlugins("org.jetbrains.plugins.yaml")
}

tasks {
    jacocoTestReport {
        reports {
            xml.isEnabled = true
        }
    }

    withType<Wrapper> {
        gradleVersion = "6.5"
    }

    withType<KotlinCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"

        kotlinOptions {
            jvmTarget = "1.8"
            apiVersion = "1.3"
            languageVersion = "1.3"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }
}
