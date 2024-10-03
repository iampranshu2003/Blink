plugins {

    id("com.google.gms.google-services")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.adminblink"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.adminblink"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //text dimension
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

// Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
//lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

// When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")


// Add the dependencies for any other desired Firebase products
// https://firebase.google.com/docs/android/setup#available-libraries

// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
//for authentication
    implementation ("com.google.firebase:firebase-auth-ktx")
//for google sign in using phone number
    implementation("com.google.android.gms:play-services-auth-api-phone:18.1.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")
//for storage
    implementation("com.google.firebase:firebase-storage-ktx")
//for database
    implementation("com.google.firebase:firebase-database-ktx")
//for notification
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.1")
//image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    //shimer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

}