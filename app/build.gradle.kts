import org.apache.tools.ant.util.JavaEnvUtils.VERSION_11

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.community"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.community"
        minSdk = 26 // Ensure this matches your app's requirements
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true // Enable multidex

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        renderscriptTargetApi = 19
        renderscriptSupportModeEnabled = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true // Ensure buildConfig is enabled
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dynamicFeatures += setOf(":busTrackingModule") // Include dynamic feature module
}

dependencies {
    // Core dependencies
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.7.1")
    implementation("com.google.firebase:firebase-analytics")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
//    implementation(libs.firebase.auth)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Other utilities
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.sothree.slidinguppanel:library:3.4.0")
    // Google Play Services Core
    implementation ("com.google.android.gms:play-services-base:18.3.0")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.maps.android:android-maps-utils:3.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation ("com.google.android.play:core:1.10.3")
    implementation ("com.google.j2objc:j2objc-annotations:2.8")
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.viewpager2)
    implementation(libs.rome)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Firebase Services (Ensure Proper Integration)
    implementation platform("com.google.firebase:firebase-bom:33.7.0")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-analytics")
}

secrets {
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
}
