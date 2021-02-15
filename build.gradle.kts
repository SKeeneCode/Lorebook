import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.ksoftware"
version = "1.0-SNAPSHOT"

plugins {
    application
    java
    kotlin("jvm") version "1.4.30"
    kotlin("kapt") version "1.4.30"
    id("org.openjfx.javafxplugin") version "0.0.9"
}

application {
    mainClass.set("org.ksoftware.lorebook.main.MainKt")
    applicationDefaultJvmArgs = listOf(
            "--add-opens=javafx.graphics/javafx.css=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.util=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    )
}
kotlin {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "15"
            languageVersion = "1.5"
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
    mavenCentral()
    maven("https://dl.bintray.com/jerady/maven")
    maven("https://dl.bintray.com/micheljung/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    //implementation(fileTree("libs"))
    implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")
    implementation("de.jensd:fontawesomefx-commons:11.0")
    implementation("de.jensd:fontawesomefx-fontawesome:4.7.0-11")
    implementation("de.jensd:fontawesomefx-materialicons:2.2.0-11")
    implementation("org.fxmisc.richtext:richtextfx:0.10.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.2")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.jfoenix:jfoenix:9.0.10")
    implementation("org.controlsfx:controlsfx:11.0.3")
    implementation("org.fxmisc.flowless:flowless:0.6.2")
    implementation("org.fxmisc.wellbehaved:wellbehavedfx:0.3.3")

    // For copy + paste of rich text
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0")
    implementation("com.sun.xml.bind:jaxb-impl:3.0.0")

    implementation("ch.micheljung.fxstage:fxstage:0.7.4")

    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.11.0")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("org.hamcrest:hamcrest:2.1")
}