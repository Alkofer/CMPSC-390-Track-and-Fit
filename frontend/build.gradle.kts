
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.google.services) apply false
    // id("com.android.application") version "8.2.1" apply false
    // id("com.android.library") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    // id("org.jetbrains.kotlin.android") version "2.1.0" apply false

    // id("com.google.gms.google-services") version "4.3.15" apply false
}



buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {

    }
}