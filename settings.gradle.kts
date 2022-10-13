pluginManagement {
  repositories {
    mavenLocal()
    maven {
      setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenLocal()
    maven {
      setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    google()
    mavenCentral()
  }
}

rootProject.name = "safe-kotlinx-coroutines"
include(":example")
include(":safe-kotlinx-coroutines")
include(":safe-kotlinx-coroutines-plugin")
