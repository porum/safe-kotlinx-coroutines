plugins {
  kotlin("jvm")
  `java-gradle-plugin`
  `kotlin-dsl`
  `maven-publish`
  signing
  id("org.jetbrains.dokka") version "1.6.0"
}

dependencies {
  implementation("org.ow2.asm:asm-tree:9.2")
  implementation(gradleApi())
  compileOnly("com.android.tools.build:gradle:7.2.0")
  compileOnly(kotlin("gradle-plugin", "1.6.10"))
}

gradlePlugin {
  plugins {
    create("safe-kotlinx-coroutines-plugin") {
      id = "safe-kotlinx-coroutines"
      implementationClass = "com.panda912.safecoroutines.plugin.SafeCoroutinePlugin"
    }
  }
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

val sourceJar by tasks.registering(Jar::class) {
  from(sourceSets.main.get().allSource)
  archiveClassifier.set("sources")
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
  dependsOn(tasks.dokkaJavadoc)
  from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
  archiveClassifier.set("javadoc")
}

publishing {
  publications {
    create<MavenPublication>("pluginMaven") {
      artifact(sourceJar)
      artifact(dokkaJavadocJar)
      pom {
        name.set("io.github.porum:safe-kotlinx-coroutines-plugin")
        description.set("safe-kotlinx-coroutines gradle plugin")
      }
    }
  }
}

signing {
  sign(extensions.getByType<PublishingExtension>().publications)
}