plugins {
  id("com.android.application")
  id("kotlin-android")
  id("safe-kotlinx-coroutines")
}

android {
  compileSdk = 32

  defaultConfig {
    applicationId = "com.panda912.safecoroutines.example"
    minSdk = 21
    targetSdk = 32
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(project(":safe-kotlinx-coroutines"))
  // implementation("io.github.porum:safe-kotlinx-coroutines:0.0.4")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
  implementation("androidx.appcompat:appcompat:1.5.0")
  testImplementation("junit:junit:4.+")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}