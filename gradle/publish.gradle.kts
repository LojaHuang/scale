// 为所有应用了 Android Library 插件的项目配置发布
plugins.withId("com.android.library") {
    apply(plugin = "maven-publish")
    afterEvaluate {
        // 检查是否配置了发布信息
        if (project.hasProperty("publishingGroupId") &&
            project.hasProperty("publishingArtifactId") &&
            project.hasProperty("publishingVersion")) {
            extensions.configure< PublishingExtension> {
                repositories {
                    maven {
                        name = "GitHubPackages"
                        url = uri("https://maven.pkg.github.com/LojaHuang")
                        credentials {
                            username = System.getenv("GITHUB_PACKAGE_USER")
                            password = System.getenv("GITHUB_PACKAGE_TOKEN")
                        }
                    }
                }

                publications {
                    register<MavenPublication>("gpr") {
                        groupId = project.property("publishingGroupId") as String
                        artifactId = project.property("publishingArtifactId") as String
                        version = project.property("publishingVersion") as String

                        // 对于 Android 库，使用 release 组件
                        if (project.plugins.hasPlugin("com.android.library")) {
                            from(components["release"])
                        }
                    }
                }
            }
        }
    }
}