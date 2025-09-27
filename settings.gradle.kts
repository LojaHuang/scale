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
        maven {
            url = uri("https://maven.pkg.github.com/LojaHuang/scale")
            credentials {
                username =  System.getenv("GITHUB_PACKAGE_USER")
                password =  System.getenv("GITHUB_PACKAGE_TOKEN")
            }
        }
    }
}
rootProject.name = "scale"
include(":sample")
include(":scale-image-viewer")
include(":scale-image-viewer-classic")
include(":scale-zoomable-view")
include(":scale-sampling-decoder")