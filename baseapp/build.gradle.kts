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
                groupId = "com.github.kouroshmsv"
                artifactId = "baseapp"
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
        minSdk = minSdkVer.toInt()
        targetSdk = targetSdkVer.toInt()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    namespace = "com.parvanpajooh.baseapp"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("androidx.appcompat:appcompat:${appcompat}")
    implementation("org.greenrobot:eventbus:$eventbus")
    implementation("com.github.fede87:StatusBarAlert:2.0.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializer")

    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:${liveData}")
    implementation("androidx.lifecycle:lifecycle-common-java8:${liveData}")

    implementation("androidx.navigation:navigation-fragment-ktx:${navigationVer}")
    implementation("androidx.navigation:navigation-ui-ktx:${navigationVer}")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.android.material:material:1.9.0")
    implementation("io.github.inflationx:calligraphy3:3.1.1")
    implementation("io.github.inflationx:viewpump:2.0.3")

    implementation("androidx.constraintlayout:constraintlayout:${constraintLayout}")


    implementation(project(":basedevice"))
    implementation(project(":basedata"))
    implementation(project(":basedomain"))

    implementation("com.github.kouroshmsv.baseandroid:baseapp:${baseAndroid}")
    implementation("com.github.kouroshmsv.baseandroid:accountmanager:${baseAndroid}")
    implementation("com.github.kouroshmsv.baseandroid:basedomain:${baseAndroid}")
    implementation("com.github.kouroshmsv:metamorphosis:1.1.0")
    implementation("com.github.samanzamani:PersianDate:1.6.1")

    implementation("androidx.biometric:biometric:1.1.0")

}