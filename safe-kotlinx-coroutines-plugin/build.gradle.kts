plugins {
  kotlin("jvm")
  `java-gradle-plugin`
  `kotlin-dsl`
  id("com.gradle.plugin-publish") version "0.16.0"
  id("org.jetbrains.dokka") version "1.6.0"
}

// https://kotlin.github.io/dokka/1.6.0/user_guide/gradle/usage/#configuration-options
tasks.dokkaJavadoc.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
  offlineMode.set(true)
  dokkaSourceSets {
    configureEach {
      // Do not create index pages for empty packages
      skipEmptyPackages.set(true)
      // Disable linking to online kotlin-stdlib documentation
      noStdlibLink.set(false)
      // Disable linking to online Android documentation (only applicable for Android projects)
      noAndroidSdkLink.set(false)

      // Suppress a package
      perPackageOption {
        // will match all .internal packages and sub-packages
        matchingRegex.set(".*\\.internal.*")
        suppress.set(true)
      }
    }
  }
}

gradlePlugin {
  plugins {
    create("safe-kotlinx-coroutines-plugin") {
      id = "safe-kotlinx-coroutines"
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