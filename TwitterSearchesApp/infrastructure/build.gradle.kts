plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "fisei.uta.edu.ec.twittersearchesapp.infrastructure"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
	implementation(project(":application"))
	implementation(project(":domain"))
}
