plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenLocal()
  maven {
    setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
  mavenCentral()
}

configurations.configureEach {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {
  implementation(gradleApi())
  implementation("com.android.tools.build:gradle:7.0.4")
  implementation(kotlin("gradle-plugin", "1.6.10"))
  implementation("io.github.porum:safe-coroutines-gradle-plugin:1.0.0")
}