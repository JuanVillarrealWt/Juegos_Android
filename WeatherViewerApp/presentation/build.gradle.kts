plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "fisei.uta.edu.ec.weatherviewerapp.presentation"
    compileSdk {
        version = release(37)
    }
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.appcompat)
}
