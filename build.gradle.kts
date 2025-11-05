import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.2.21"

    id("org.jetbrains.dokka") version "2.1.0"

    `maven-publish`
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:2.0.0")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnly("com.github.retrooper:packetevents-spigot:2.9.5")
    compileOnly("dev.folia:folia-api:1.21.8-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }

    implementation("com.google.code.gson:gson:2.13.2")
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
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/spartacus04/Colosseum")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}