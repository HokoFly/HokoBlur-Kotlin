import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

fun Project.configureStripeAbi() {
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
        packaging {
            jniLibs {
                if (isReleaseBuild) {
                    excludes.add("lib/x86_64/*.so")
                    excludes.add("lib/x86/*.so")
                }
            }
        }
    }
}

internal fun Project.`android`(configure: Action<CommonExtension<*, *, *, *, *, *>>): Unit =
    (this as ExtensionAware).extensions.configure("android", configure)