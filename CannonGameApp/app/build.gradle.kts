plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fisei.uta.edu.ec.cannongameapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "fisei.uta.edu.ec.cannongameapp"
        minSdk = 24
        targetSdk = 37
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
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":presentation"))
    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
