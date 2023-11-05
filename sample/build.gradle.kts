plugins {
    id("com.android.application")
    kotlin("android")
}

configureStripeAbi()

android {
    compileSdk = CompileConfig.compileSdkVersion
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
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(Deps.lifecycleKtx)
    implementation(Deps.activityKtx)
    implementation(project(":library"))
}
