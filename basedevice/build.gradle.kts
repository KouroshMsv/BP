plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("plugin.serialization") version "1.9.21"
}
group = "com.github.KouroshMsv"
afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {

                from(components.getByName("release"))
                groupId = "com.github.KouroshMsv"
                artifactId = "basedevice"
                version = libVersion
            }
        }
    }
}
val libVersion: String by project
val kotlinVersion: String by project
val minSdkVer: String by project
val compileSdkVer: String by project
val buildToolsVer: String by project
val targetSdkVer: String by project
val coroutines: String by project
val appcompat: String by project
val liveData: String by project
val navigationVer: String by project
val constraintLayout: String by project
val baseAndroid: String by project
val kotlinSerializer: String by project
val eventbus: String by project

android {
    compileSdk = compileSdkVer.toInt()
    buildToolsVersion = buildToolsVer
    defaultConfig {
        minSdk=minSdkVer.toInt()
        targetSdk=targetSdkVer.toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions{
        jvmTarget="11"
    }
    namespace = "com.parvanpajooh.basedevice"
}

dependencies {
    implementation((fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))))
    implementation ("joda-time:joda-time:2.12.5")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation (project(":basedomain"))
    implementation ("com.github.kouroshmsv.baseandroid:basedomain:${baseAndroid}")
    implementation ("com.github.kouroshmsv.baseandroid:accountmanager:${baseAndroid}")
}
