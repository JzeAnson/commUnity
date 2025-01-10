plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

allprojects {
    configurations.all {
        resolutionStrategy {
            force("com.google.android.play:core:1.10.3")
            force("com.google.android.play:core-common:2.0.3")
        }
    }
}