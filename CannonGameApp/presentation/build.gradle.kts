plugins {
    id("com.android.library")
}

android {
    namespace = "fisei.uta.edu.ec.cannongameapp.presentation"
    compileSdk { version = release(37) }
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":application"))
    api(project(":domain"))
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
}
