plugins {
    id("com.android.library")
    kotlin("android")
}

var isReleaseBuild = false
gradle.startParameter.taskNames.forEach { taskName ->
    if (taskName.contains("release", true)) {
        isReleaseBuild = true
    }
    if (taskName.equals("uploadArchives", true)) {
        isReleaseBuild = true
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
            externalNativeBuild {
                cmake {
                    abiFilters("armeabi-v7a", "arm64-v8a")
                }
            }
        }

        getByName("debug") {
            isJniDebuggable = true
            externalNativeBuild {
                cmake {
                    abiFilters("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
                }
            }
        }
    }

    packaging {
        jniLibs {
            if (isReleaseBuild) {
                excludes.add("lib/x86_64/*.so")
                excludes.add("lib/x86/*.so")
            }
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
