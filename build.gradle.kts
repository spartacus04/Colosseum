import org.jetbrains.dokka.gradle.DokkaTaskPartial
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.dokka.DokkaConfiguration.Visibility

plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.dokka") version "2.0.0"
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

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/nexus/repository/public/")
        maven("https://libraries.minecraft.net")
    }
}

group = "me.spartacus04.colosseum"
version = System.getenv("version") ?: "dev"
description = "colosseum-api"

dependencies {
    implementation(project(":core"))
}

java.targetCompatibility = JavaVersion.VERSION_1_8
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}_${project.version}-shadowed.jar")
        val dependencyPackage = "${rootProject.group}.dependencies.${rootProject.name.lowercase()}"
        from(subprojects.map { it.sourceSets.main.get().output })

        relocate("kotlin", "${dependencyPackage}.kotlin")
        relocate("com/google/gson", "${dependencyPackage}.gson")
        relocate("org/jetbrains/annotations", "${dependencyPackage}.annotations")

        exclude("colors.bin")
        exclude("ScopeJVMKt.class")
        exclude("DebugProbesKt.bin")
        exclude("META-INF/**")

        minimize()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

// publish
subprojects {
    apply(plugin = "org.jetbrains.dokka")

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(setOf(
                Visibility.PUBLIC,
                Visibility.PROTECTED
            ))
        }
    }
}


allprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    if(project == rootProject) {
                        artifact(tasks.shadowJar)
                    } else {
                        from(components["kotlin"])
                    }

                    pom {
                        name = project.name
                        description = project.description
                        url = "https://github.com/spartacus04/Colosseum"

                        scm {
                            connection = "scm:git@github.com:spartacus04/Colosseum.git"
                            developerConnection = "scm:git@github.com:spartacus04/Colosseum.git"
                            url = "https://github.com/spartacus04/Colosseum"
                        }

                        licenses {
                            license {
                                name = "MIT License"
                                url = "https://github.com/spartacus04/Colosseum/blob/master/LICENSE"
                            }
                        }
                    }
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
    }
}