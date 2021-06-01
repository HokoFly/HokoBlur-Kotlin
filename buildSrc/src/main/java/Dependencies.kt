import Versions.AGPVersion
import Versions.appcompatVersion
import Versions.coroutineVersion
import Versions.kotlinVersion

object CompileConfig {
    const val minSdkVersion = 16
    const val compileSdkVersion = 28
    const val targetSdkVersion = 28
    const val buildToolsVersion = "29.0.3"
    const val renderscriptTargetApi = 28
    const val renderscriptSupportModeEnabled = true
}

internal object Versions {
    const val kotlinVersion = "1.5.10"
    const val coroutineVersion = "1.3.2"
    const val appcompatVersion = "1.3.0"
    const val AGPVersion = "4.2.1"
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