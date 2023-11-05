plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

object BuildVersions {
    const val kotlinVersion = "1.8.21"
    const val AGPVersion = "8.1.2"
}
object ClassPathDeps {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${BuildVersions.AGPVersion}"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${BuildVersions.kotlinVersion}"
}

dependencies {
    implementation(ClassPathDeps.androidGradlePlugin)
    implementation(ClassPathDeps.kotlinGradlePlugin)
}