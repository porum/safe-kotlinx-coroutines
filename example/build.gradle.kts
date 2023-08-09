plugins {
  id("com.android.application")
  id("kotlin-android")
  id("safe-kotlinx-coroutines")
}

android {
  compileSdk = 33

  defaultConfig {
    applicationId = "com.panda912.safecoroutines.example"
    minSdk = 21
    targetSdk = 33
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  namespace = "com.panda912.safecoroutines.example"
}

dependencies {
  // implementation(project(":safe-kotlinx-coroutines"))
  implementation("io.github.porum:safe-kotlinx-coroutines:0.0.6")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
  implementation("androidx.appcompat:appcompat:1.6.1")
  testImplementation("junit:junit:4.+")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}