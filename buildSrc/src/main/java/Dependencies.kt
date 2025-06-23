import Versions.activityKtxVersion
import Versions.appcompatVersion
import Versions.coroutineVersion
import Versions.lifecycleKtxVersion
import org.gradle.api.JavaVersion

object CompileConfig {
    const val minSdkVersion = 21
    const val compileSdkVersion = 36
    const val targetSdkVersion = 36
    const val buildToolsVersion = "36.0.0"
    const val renderscriptTargetApi = 36
    const val renderscriptSupportModeEnabled = true
    val javaVersion = JavaVersion.VERSION_17
    const val jvmTarget = "17"
    const val ndkVersion = "28.1.13356709"
}

internal object Versions {
    const val coroutineVersion = "1.3.2"
    const val appcompatVersion = "1.4.0"
    const val lifecycleKtxVersion = "2.5.1"
    const val activityKtxVersion = "1.6.0"
}

object Deps {
    const val kotlinCoroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"
    const val appcompat = "androidx.appcompat:appcompat:$appcompatVersion"
    const val lifecycleKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleKtxVersion"
    const val activityKtx = "androidx.activity:activity-ktx:$activityKtxVersion"
}
