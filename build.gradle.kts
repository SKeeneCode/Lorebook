import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "org.ksoftware"
version = "1.0-SNAPSHOT"

plugins {
    application
    java
    kotlin("jvm") version "1.5.30"
    kotlin("kapt") version "1.5.30"
    id("org.openjfx.javafxplugin") version "0.0.9"
    id ("org.beryx.runtime") version "1.12.2"
}


application {
    mainClass.set("org.ksoftware.lorebook.main.MainInvokerKt")
    applicationDefaultJvmArgs = listOf(
            "--add-opens=javafx.graphics/javafx.css=ALL-UNNAMED",
            "--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.runtime=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
            "--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
            "--add-opens=javafx.base/com.sun.javafx.event=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.util=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
            "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
            "--add-opens=javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
    )
}
kotlin {
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "15"
            languageVersion = "1.4"
        }
    }
}

javafx {
    version = "17"
    modules = mutableListOf(
            "javafx.controls",
            "javafx.graphics",
            "javafx.fxml")
}

runtime {
    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    modules.set(listOf("java.desktop", "jdk.unsupported", "java.scripting", "java.logging", "java.xml"))
}

tasks["runtime"].doLast {
    copy {
        from("src/main/resources")
        into("$buildDir/image/bin")
    }
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation(fileTree("libs"))
    // logging
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    // implementation("no.tornado:tornadofx:2.0.0-SNAPSHOT")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.5.1")

    // fancy javafx controls
    implementation("com.jfoenix:jfoenix:9.0.10")
    implementation("org.controlsfx:controlsfx:11.1.0")

    // richtext
    implementation("org.fxmisc.richtext:richtextfx:0.10.6")
    implementation("org.fxmisc.flowless:flowless:0.6.6")
    implementation("org.fxmisc.wellbehaved:wellbehavedfx:0.3.3")

    // material design theme
    implementation("org.jfxtras:jmetro:11.6.15")

    // For copy + paste of rich text
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.0")
    implementation("com.sun.xml.bind:jaxb-impl:3.0.0")

    implementation("io.github.micheljung:fxstage:0.8.0")

    // For JSON serialisation
    implementation("com.squareup.moshi:moshi:1.12.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")
}