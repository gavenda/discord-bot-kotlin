import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.util.Properties

plugins {
    application
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

val botMainClass = "bot.MainKt"
val gitHash: String get() {
    if(File(".git").exists().not()) {
        return "SNAPSHOT"
    }

    return ByteArrayOutputStream()
        .use { outputStream ->
            project.exec {
                commandLine("git")
                args("rev-parse", "--short", "HEAD")
                standardOutput = outputStream
            }
            outputStream.toString().trim()
        }
}

group = "bot"
version = "1.0-$gitHash"

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // Dependency Injection
    implementation("org.kodein.di:kodein-di:7.7.0")

    // Discord API
    implementation("net.dv8tion:JDA:4.3.0_299") {
        exclude("club.minnced")
    }

    // Logging
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
}

java {                                      
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xuse-experimental=kotlinx.coroutines.DelicateCoroutinesApi"
            )
        }
    }

    val generateVersionProperties = register(name = "generateVersionProperties") {
        val resourcesDir = File("$buildDir/resources/main").apply {
            mkdirs()
        }
        val propertiesFilePath = File(resourcesDir, "/version.properties").apply {
            createNewFile()
        }
        Properties().apply {
            setProperty("version", version.toString())
            store(propertiesFilePath.outputStream(), null)
        }
    }

    classes.configure {
        finalizedBy(generateVersionProperties)
    }
}

application {
    mainClass.set(botMainClass)
}