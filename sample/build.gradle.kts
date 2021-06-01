plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(CompileConfig.compileSdkVersion)
    defaultConfig {
        applicationId("com.hoko.ktblur.demo")
        minSdkVersion(CompileConfig.minSdkVersion)
        targetSdkVersion(CompileConfig.targetSdkVersion)
        buildToolsVersion(CompileConfig.buildToolsVersion)
        renderscriptTargetApi = CompileConfig.renderscriptTargetApi
        renderscriptSupportModeEnabled(CompileConfig.renderscriptSupportModeEnabled)
        versionCode(1)
        versionName("1.0")
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
    implementation(Deps.kotlinStdlib)
    implementation(Deps.kotlinCoroutine)
    implementation(Deps.appcompat)
    implementation(project(":library"))
}
