import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.ksoftware"
version = "1.0-SNAPSHOT"

plugins {
    application
    java
    kotlin("jvm") version "1.4.10"
    kotlin("kapt") version "1.4.10"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

application {
    mainClassName = "org.ksoftware.lorebook.main.MainKt"
}
kotlin {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "14"
        }
    }
}

javafx {
    version = "15"
    modules = mutableListOf(
            "javafx.controls",
            "javafx.graphics",
            "javafx.fxml")
}

repositories {
    maven {
        setUrl("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    mavenCentral()
}

dependencies {
    implementation ("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation("org.fxmisc.richtext:richtextfx:0.10.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.3.9")
    implementation("com.squareup.moshi:moshi:1.11.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.hamcrest:hamcrest:2.1")
}