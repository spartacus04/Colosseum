import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.1.10"

    id("org.jetbrains.dokka") version "2.0.0"

    `maven-publish`
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:2.0.0")
        classpath("com.guardsquare:proguard-gradle:7.6.1") {
            exclude("com.android.tools.build")
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
}

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }

    implementation("com.google.code.gson:gson:2.12.1")

    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
}

group = "me.spartacus04.colosseum"

version = System.getenv("version") ?: "dev"

description = "colosseum-api"
java.targetCompatibility = JavaVersion.VERSION_1_8
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

// publish

dokka {
    pluginsConfiguration.html {
        customStyleSheets.from(file("docsAssets/logo-styles.css"))
        customAssets.from(file("icon.webp"))
        footerMessage = "Colosseum is licensed under the <a href=\"https://github.com/spartacus04/Colosseum/blob/master/LICENSE\">MIT</a> License."

    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name.lowercase()
            version = "${rootProject.version}"

            from(components["kotlin"])
        }
    }
}