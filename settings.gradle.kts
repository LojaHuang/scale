pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT) // 允许项目级仓库
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io")
    }
}
rootProject.name = "scale"
include(":sample")
include(":scale-image-viewer")
include(":scale-image-viewer-classic")
include(":scale-zoomable-view")
include(":scale-sampling-decoder")