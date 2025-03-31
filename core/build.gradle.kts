import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    kotlin("jvm") version "2.1.10"
}

group = "me.spartacus04.colosseum-core"
version = parent!!.version

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
        }
    }

    implementation("com.google.code.gson:gson:2.12.1")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib")
    compileOnly("com.mojang:brigadier:1.0.18")
}

java.targetCompatibility = JavaVersion.VERSION_1_8
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

tasks.withType<DokkaTaskPartial>().configureEach {
    dokkaSourceSets {

    }
}