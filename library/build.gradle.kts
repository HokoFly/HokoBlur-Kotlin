plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
}

var isReleaseBuildType = false
gradle.startParameter.taskNames.forEach {
    val taskNameL = it.toLowerCase()
    if (taskNameL.contains("release")) {
        isReleaseBuildType = true
    }
    if (taskNameL.equals("uploadArchives", true)) {
        isReleaseBuildType = true
    }
}

android {
    compileSdkVersion(CompileConfig.compileSdkVersion)
    defaultConfig {
        minSdkVersion(CompileConfig.minSdkVersion)
        targetSdkVersion(CompileConfig.targetSdkVersion)
        buildToolsVersion(CompileConfig.buildToolsVersion)
        renderscriptTargetApi = CompileConfig.renderscriptTargetApi
        renderscriptSupportModeEnabled(CompileConfig.renderscriptSupportModeEnabled)
        versionCode(1)
        versionName("1.0")
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_PLATFORM=android-13", "-DANDROID_TOOLCHAIN=clang")
                cppFlags("-std=c++11 -frtti -fexceptions")
            }
        }
        val abiFilters = if (isReleaseBuildType) arrayOf("armeabi-v7a", "arm64-v8a") else arrayOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        externalNativeBuild.cmake.abiFilters(*abiFilters)
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
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
        }
    }

}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to "*.jar"))
    implementation(Deps.kotlinStdlib)
    implementation(Deps.kotlinCoroutine)
}
