import scale.compileSdk
import scale.minSdk
import scale.targetSdk

plugins {
    alias(libs.plugins.android.application)
    id("kotlin-android")
}

android {
    namespace = "com.jvziyaoyao.scale.sample"
    compileSdk = project.compileSdk

    defaultConfig {
        applicationId = "com.jvziyaoyao.scale.sample"
        minSdk = project.minSdk
        targetSdk = project.targetSdk
        versionCode = 1
        versionName = "1.1.0-alpha.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

}

dependencies {
    implementation(project(":scale-image-viewer"))
    //    implementation "com.jvziyaoyao.scaler:zoomable-view:1.1.0-test.3"
//    implementation "com.jvziyaoyao.scaler:image-viewer:1.1.0-test.3"

    implementation(libs.androidx.exif)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.jvziyaoyao.origeek.ui)

    implementation(libs.coil)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)
    implementation(libs.coil.compose)

    implementation(libs.google.accompanist.permissions)
    implementation(libs.google.accompanist.systemuicontroller)

    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.util)
    androidTestImplementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}