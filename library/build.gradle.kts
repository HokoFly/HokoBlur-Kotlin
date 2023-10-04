plugins {
    id("com.android.library")
    kotlin("android")
}

var isReleaseBuildType = false
gradle.startParameter.taskNames.forEach {
    val taskNameL = it.lowercase()
    if (taskNameL.contains("release")) {
        isReleaseBuildType = true
    }
    if (taskNameL.equals("uploadArchives", true)) {
        isReleaseBuildType = true
    }
}

android {
    compileSdk = CompileConfig.compileSdkVersion
    defaultConfig {
        minSdk = CompileConfig.minSdkVersion
        targetSdk = CompileConfig.targetSdkVersion
        buildToolsVersion = CompileConfig.buildToolsVersion
        renderscriptTargetApi = CompileConfig.renderscriptTargetApi
        renderscriptSupportModeEnabled = CompileConfig.renderscriptSupportModeEnabled
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_PLATFORM=android-13", "-DANDROID_TOOLCHAIN=clang")
                cppFlags("-std=c++11 -frtti -fexceptions")
            }
        }
        val abiFilters = if (isReleaseBuildType) arrayOf("armeabi-v7a", "arm64-v8a") else arrayOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        externalNativeBuild.cmake.abiFilters(*abiFilters)
        consumerProguardFiles("proguard-rules.pro")
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
            isMinifyEnabled = false
        }

        getByName("debug") {
            isJniDebuggable = true
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }
    namespace = "com.hoko.ktblur"

}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
    implementation(Deps.kotlinCoroutine)
}
