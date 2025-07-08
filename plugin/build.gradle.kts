import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    alias(libs.plugins.gradle.plugin.publish)
}

group = "org.bxteam"
version = "1.2.0"
description = "Gradle plugin for running Minecraft server instances in your IDE"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.bxteam.org/releases")
}

dependencies {
    api(kotlin("stdlib"))
    implementation(libs.gson)
}

gradlePlugin {
    website = "https://bxteam.org/"
    vcsUrl = "https://github.com/BX-Team/run-server-plugin"

    plugins {
        create("runServerPlugin") {
            id = "org.bxteam.runserver"
            displayName = "RunServer"
            description = "Plugin for running Minecraft server instances in your IDE."
            tags = listOf("minecraft", "server", "run", "bxteam", "spigot", "paper", "purpur", "pufferfish", "canvas", "divinemc", "leaf", "leaves", "bungeecord", "waterfall", "velocity")
            implementationClass = "org.bxteam.runserver.RunServerPlugin"
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs = listOf("-Xjdk-release=17")
    }
}
