import Versions.AGPVersion
import Versions.appcompatVersion
import Versions.coroutineVersion
import Versions.kotlinVersion

object CompileConfig {
    const val minSdkVersion = 16
    const val compileSdkVersion = 32
    const val targetSdkVersion = 32
    const val buildToolsVersion = "32.0.0"
    const val renderscriptTargetApi = 31
    const val renderscriptSupportModeEnabled = true
}

internal object Versions {
    const val kotlinVersion = "1.5.10"
    const val coroutineVersion = "1.3.2"
    const val appcompatVersion = "1.4.0"
    const val AGPVersion = "7.0.4"
}

object Deps {
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
    const val kotlinCoroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"
    const val appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
}

object ClassPathDeps {
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val androidGradlePlugin = "com.android.tools.build:gradle:$AGPVersion"
}