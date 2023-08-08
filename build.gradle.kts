// Top-level build file where you can add configuration options common to all sub-projects/modules.
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "7.3.0" apply false
  id("com.android.library") version "7.3.0" apply false
  id("org.jetbrains.kotlin.android") version "1.7.20" apply false
  id("org.jetbrains.dokka") version "1.7.20" apply false
}

buildscript {
  dependencies {
    classpath("io.github.porum:safe-kotlinx-coroutines-plugin:0.0.4")
  }
}

configurations.configureEach {
  resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

subprojects {
  configurations.configureEach {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }

  if (name.startsWith("safe-kotlinx-coroutines")) {
    pluginManager.withPlugin("maven-publish") {

      val GROUP: String by project
      val VERSION: String by project
      val sonatypeUserName: String by project
      val sonatypePassword: String by project

      val publishExtension = extensions.getByType<PublishingExtension>()
      publishExtension.repositories {
        mavenLocal()
        maven {
          val url = if (VERSION.endsWith("-SNAPSHOT")) {
            "https://s01.oss.sonatype.org/content/repositories/snapshots/"
          } else {
            "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
          }
          setUrl(url)
          credentials {
            username = sonatypeUserName
            password = sonatypePassword
          }
        }
      }
      publishExtension.publications.whenObjectAdded {
        check(this is MavenPublication) {
          "unexpected publication $this"
        }
        groupId = GROUP
        version = VERSION
        pom {
          url.set("https://github.com/porum/safe-kotlinx-coroutines")
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
          developers {
            developer {
              id.set("porum")
              name.set("guobao.sun")
              email.set("sunguobao12@gmail.com")
            }
          }
          scm {
            url.set("https://github.com/porum/safe-kotlinx-coroutines.git")
          }
        }
      }
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
