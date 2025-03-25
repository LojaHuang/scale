import org.gradle.internal.extensions.core.extra
import scale.compileSdk
import scale.minSdk
import scale.versionName
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.jetbrains.dokka)
    id("maven-publish")
}

val aarName = "scale-image-viewer-${project.versionName}.aar"

android {
    namespace = "com.jvziyaoyao.scale.image"
    compileSdk = project.compileSdk

    defaultConfig {
        minSdk = project.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    libraryVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = aarName
            }
    }
    publishing {
        singleVariant("release"){
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing{
    repositories{
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lojahuang/scale")
            credentials {
                username = System.getenv("GPR_USER")
                password = System.getenv("GPR_KEY")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            // 配置信息，使用: classpath("groupId:artifactId:version") (不能有空格)
            groupId = "com.lojahuang"
            artifactId = "scale-image-viewer"
            version = project.versionName

            // 这条要加上，不然不会包含代码文件
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    api(project(":scale-zoomable-view"))
    implementation(libs.androidx.exif)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}