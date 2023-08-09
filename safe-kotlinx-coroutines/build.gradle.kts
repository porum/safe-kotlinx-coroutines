plugins {
  kotlin("jvm")
  `maven-publish`
  signing
  id("org.jetbrains.dokka")
}

dependencies {
  compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")
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
    create<MavenPublication>("safe-kotlinx-coroutines") {
      from(components["java"])
      artifact(sourceJar)
      artifact(dokkaJavadocJar)
      pom {
        name.set("io.github.porum:safe-kotlinx-coroutines")
        description.set("safe-kotlinx-coroutines library")
      }
    }
  }
}

signing {
  sign(extensions.getByType<PublishingExtension>().publications)
}