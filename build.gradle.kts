// Top-level build file where you can add configuration options common to all sub-projects/modules.

subprojects {
  repositories {
    google()
    maven {
      setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    mavenCentral()
  }

  configurations.configureEach {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }

  if (name.startsWith("safe-kotlinx-coroutines")) {
    pluginManager.withPlugin("maven-publish") {

      val GROUP: String by project
      val VERSION: String by project

      val properties = java.util.Properties()
      properties.load(project.rootProject.file("local.properties").inputStream())
      project.ext["sonatypeUserName"] = properties.getProperty("sonatypeUserName")
      project.ext["sonatypePassword"] = properties.getProperty("sonatypePassword")
      project.ext["signing.keyId"] = properties.getProperty("signing.keyId")
      project.ext["signing.password"] = properties.getProperty("signing.password")
      project.ext["signing.secretKeyRingFile"] = properties.getProperty("signing.secretKeyRingFile")

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
            username = project.ext["sonatypeUserName"] as String
            password = project.ext["sonatypePassword"] as String
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
