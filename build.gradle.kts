plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin) apply false
    alias(libs.plugins.jetbrains.dokka)
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("Scale")
    outputDirectory.set(file("$rootDir/doc/page/reference"))
}