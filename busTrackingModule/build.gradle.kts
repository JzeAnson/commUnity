plugins {
    alias(libs.plugins.android.dynamic.feature)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.bustrackingmodule"
    compileSdk = 34

    defaultConfig {
        minSdk = 26  // Match with app module
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":app")) // Ensure this is included to link with the base module
    // AndroidX and Material
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    implementation("androidx.fragment:fragment:1.6.1")
    implementation("androidx.activity:activity:1.8.0")

    // Google Maps Dependencies
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")


    // Maps and Location

    implementation(project(":app"))


    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("org.mobilitydata:gtfs-realtime-bindings:0.0.8")
    implementation(libs.fragment)

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}