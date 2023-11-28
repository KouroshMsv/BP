// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion: String by project
    repositories {
        maven {
            url = uri("http://192.168.19.11:8081/repository/maven-group/")
            isAllowInsecureProtocol = true
        }
        google()
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

allprojects {
    repositories {
        maven {
            url = uri("http://192.168.19.11:8081/repository/maven-group/")
            isAllowInsecureProtocol = true
        }
        google()
        mavenCentral()
        maven { url =uri("https://jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}