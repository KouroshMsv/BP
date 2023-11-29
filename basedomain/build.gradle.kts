plugins {
    id("com.android.library")
    id("maven-publish")
    kotlin("android")
    kotlin("plugin.serialization") version "1.9.21"
}
group = "com.github.KouroshMsv"

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
val libVersion: String by project
afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {

                from(components.getByName("release"))
                groupId = "com.github.KouroshMsv"
                artifactId = "basedomain"
                version = libVersion
            }
        }
    }
}
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions{
        jvmTarget="17"
    }
    namespace = "com.parvanpajooh.basedomain"

}

dependencies {
    implementation((fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("com.github.kouroshmsv.baseandroid:basedomain:${baseAndroid}")
    implementation ("org.greenrobot:eventbus:$eventbus")
    implementation ("com.orhanobut:hawk:2.0.1")

}

