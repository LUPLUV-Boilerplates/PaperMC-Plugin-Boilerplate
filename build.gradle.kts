import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.lupluv"
version = "1.0"

val exposedVersion = "0.41.1"
val ktorVersion = "2.2.2"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.flawcra.cc/mirrors")
    maven("https://nexus.coding-revolution.com/repository/maven-public/")
}

val shadowDependencies = listOf(
    "com.github.TheFruxz:Ascend:18.0.0",
    "com.github.TheFruxz:Stacked:4.0.0",
    "net.oneandone.reflections8:reflections8:0.11.7",
    "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4",
    "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1",
    "org.jetbrains.exposed:exposed-core:$exposedVersion",
    "org.jetbrains.exposed:exposed-dao:$exposedVersion",
    "org.jetbrains.exposed:exposed-jdbc:$exposedVersion",
    "org.jetbrains.exposed:exposed-java-time:$exposedVersion",
    "io.ktor:ktor-client-core:$ktorVersion",
    "io.ktor:ktor-client-okhttp:$ktorVersion",
    "io.ktor:ktor-client-websockets:$ktorVersion",
    "io.ktor:ktor-serialization-kotlinx-json:$ktorVersion",
    "io.ktor:ktor-client-content-negotiation:$ktorVersion",
    "org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5"
)

dependencies {

    // Minecraft PaperMC Dependencies
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:1.6.25")

    // ProtocolLib
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")


    shadowDependencies.forEach { dependency ->
        implementation(dependency)
        shadow(dependency)
    }

    // Eclipse collections
    listOf("-api", "").forEach { module ->
        ("org.eclipse.collections:eclipse-collections$module:11.1.0").let { dependency ->
            compileOnly(dependency)
            shadow(dependency)
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks {

    build {
        dependsOn("shadowJar")
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }

    withType<ProcessResources> {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    withType<ShadowJar> {
        mergeServiceFiles()
        configurations = listOf(project.configurations.shadow.get())
        archiveFileName.set("Plugin.jar")
    }

}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
