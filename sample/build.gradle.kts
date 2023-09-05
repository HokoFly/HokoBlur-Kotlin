plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(CompileConfig.compileSdkVersion)
    defaultConfig {
        applicationId = "com.hoko.ktblur.demo"
        minSdk = CompileConfig.minSdkVersion
        targetSdk = CompileConfig.targetSdkVersion
        buildToolsVersion = CompileConfig.buildToolsVersion
        renderscriptTargetApi = CompileConfig.renderscriptTargetApi
        renderscriptSupportModeEnabled = CompileConfig.renderscriptSupportModeEnabled
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility(CompileConfig.javaVersion)
        targetCompatibility(CompileConfig.javaVersion)
    }
    kotlinOptions {
        jvmTarget = CompileConfig.jvmTarget
    }
    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            isJniDebuggable = true
        }
    }
    namespace = "com.hoko.ktblur.demo"
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
    implementation(Deps.kotlinCoroutine)
    implementation(Deps.appcompat)
    implementation(project(":library"))
}
