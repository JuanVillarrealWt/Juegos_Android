plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fisei.uta.ec.doodlz5"
    compileSdk = 36

    defaultConfig {
        applicationId = "fisei.uta.ec.doodlz5"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Librerías solicitadas en el Paso 2
    implementation("androidx.fragment:fragment:1.8.4")
    implementation("androidx.print:print:1.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}