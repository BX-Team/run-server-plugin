dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}


pluginManagement {
    includeBuild("../plugin")
}

rootProject.name = "testing"
