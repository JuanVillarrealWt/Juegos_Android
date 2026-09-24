plugins { alias(libs.plugins.android.library) }

android {
    namespace = "fisei.uta.edu.ec.addressbookapp.presentation"
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
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation(libs.recyclerview)
    implementation(libs.coordinatorlayout)
    implementation(libs.fragment)
}
