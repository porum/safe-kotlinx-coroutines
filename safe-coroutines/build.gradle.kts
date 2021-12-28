plugins {
  kotlin("jvm")
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

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.2")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}