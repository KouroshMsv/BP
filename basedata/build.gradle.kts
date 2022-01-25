plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("plugin.serialization") version "1.6.10"
}
group = "com.github.KouroshMsv"
afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {

                from(components.getByName("release"))
                groupId = "com.github.KouroshMsv"
                artifactId = "basedata"
                version = "1.9.11"
            }
        }
    }
}
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation((fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$liveData")
    implementation ("androidx.lifecycle:lifecycle-livedata-core-ktx:$liveData")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializer")
    implementation ("com.github.samanzamani.persiandate:PersianDate:0.8")

    implementation (project(":basedomain"))

    implementation ("com.github.kouroshmsv.baseandroid:basedomain:${baseAndroid}")
}
