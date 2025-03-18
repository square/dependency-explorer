pluginManagement {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.1.20-RC"
        id("com.google.devtools.ksp") version "2.0.21-1.0.27"
        id("org.jetbrains.kotlin.plugin.serialization") version "2.1.20-RC"
        id("com.github.johnrengelman.shadow") version "7.1.2"
    }
}

rootProject.name = "app"