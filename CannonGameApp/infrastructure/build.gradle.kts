plugins {
    id("com.android.library")
}

android {
    namespace = "fisei.uta.edu.ec.cannongameapp.infrastructure"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":application"))
}
