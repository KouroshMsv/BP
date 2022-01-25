plugins {
    id("com.android.application")
    kotlin("android")
}
val kotlinVersion: String by project
val minSdkVer: String  by project
val targetSdkVer: String  by project
val compileSdkVer: String   by project
val buildToolsVer: String  by project
val coroutines: String by project
val appcompat: String by project
val constraintLayout: String by project
val baseAndroid: String by project
val eventbus: String by project

android {
    compileSdk = compileSdkVer.toInt()
    buildToolsVersion = buildToolsVer
    defaultConfig {
        applicationId ="dev.kourosh.baseandroid"
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
        sourceCompatibility=JavaVersion.VERSION_1_8
        targetCompatibility=JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation ("androidx.appcompat:appcompat:${appcompat}")
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("org.greenrobot:eventbus:$eventbus")
    implementation ("androidx.constraintlayout:constraintlayout:$constraintLayout")
    implementation (project( ":baseapp"))
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")
    implementation ("com.google.android.material:material:1.5.0")
    implementation (project( ":basedomain"))
    implementation (project( ":basedata"))
    implementation (project( ":basedevice"))

    implementation (project(":basedevice"))
    implementation (project(":basedata"))
    implementation (project(":basedomain"))

    implementation ("com.github.kouroshmsv.baseandroid:baseapp:${baseAndroid}")
    implementation ("com.github.kouroshmsv.baseandroid:accountmanager:${baseAndroid}")
    implementation ("com.github.kouroshmsv.baseandroid:basedomain:${baseAndroid}")
    implementation ("com.github.samanzamani.persiandate:PersianDate:0.8")

}
