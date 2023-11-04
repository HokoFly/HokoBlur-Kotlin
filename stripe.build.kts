var isReleaseBuild = false

gradle.startParameter.taskNames.forEach { taskName ->
    if (taskName.toLowerCase().contains("release")) {
        isReleaseBuild = true
    }
    if (taskName == "uploadArchives") {
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