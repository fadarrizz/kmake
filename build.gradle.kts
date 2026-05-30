import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    kotlin("jvm") version "2.3.21" // Kotlin/JVM support
    application // adds `run` + `installDist`
}

repositories {
    mavenCentral() // where dependencies are downloaded from
}

dependencies {
    val kotlinVersion = getKotlinPluginVersion()
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("com.github.ajalt.clikt:clikt:5.0.3") // CLI argument parsing

    testImplementation(kotlin("test")) // for unit tests
}

kotlin {
    jvmToolchain(21) // compile & run on a JDK 21 toolchain, regardless of machine default
}

application {
    mainClass = "kmake.MainKt" // entry point
}

tasks.test {
    useJUnitPlatform() // run tests on JUnit 5
}
