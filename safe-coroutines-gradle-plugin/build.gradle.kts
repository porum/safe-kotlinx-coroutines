plugins {
  kotlin("jvm")
  `java-gradle-plugin`
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "0.16.0"
  id("org.jetbrains.dokka") version "1.5.30"
}

gradlePlugin {
  plugins {
    create("safe-coroutine-plugin") {
      id = "safe-coroutine"
      implementationClass = "com.panda912.safecoroutines.plugin.SafeCoroutinePlugin"
    }
  }
}

dependencies {
  implementation("org.ow2.asm:asm-tree:9.2")
  implementation(gradleApi())
  compileOnly("com.android.tools.build:gradle:7.0.4")
  compileOnly(kotlin("gradle-plugin", "1.6.10"))
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}