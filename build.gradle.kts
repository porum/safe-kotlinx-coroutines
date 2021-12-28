// Top-level build file where you can add configuration options common to all sub-projects/modules.

//val publishScript = project.file(".script/publish.gradle.kts")

subprojects {
  repositories {
    google()
    mavenLocal()
    mavenCentral()
  }

  configurations.configureEach {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
  }

  if (name.startsWith("safe-kotlinx-coroutines")) {
//    apply(from = publishScript)
    apply<SigningPlugin>()
    apply<MavenPublishPlugin>()
    afterEvaluate {
      val sourceJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
        from(project.the<SourceSetContainer>()["main"].allSource)
      }
//      val dokkaJavadocJar by tasks.registering(Jar::class) {
//        dependsOn(tasks.named("dokkaJavadoc"))
//        archiveClassifier.set("javadoc")
//        from(dokkaJavadocTaskProvider.get().outputDirectory)
//      }

      val GROUP: String by project
      val VERSION: String by project

      configure<PublishingExtension> {
        publications {
          create<MavenPublication>("binary") {

            groupId = GROUP
            version = VERSION

            artifact(sourceJar.flatMap { it.archiveFile }) {
              classifier = "sources"
              extension = "jar"
              builtBy(sourceJar)
            }
//            artifact(dokkaJavadocJar)
            from(components["java"])
            pom {
              name.set("safe-kotlinx-coroutines")
              description.set("safe-kotlinx-coroutines")
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
                  name.set("sunguo.sun")
                  email.set("sunguobao12@gmail.com")
                }
              }
              scm {
                url.set("https://github.com/porum/safe-kotlinx-coroutines.git")
              }
            }
          }
        }

        val properties = java.util.Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        project.ext["sonatypeUserName"] = properties.getProperty("sonatypeUserName")
        project.ext["sonatypePassword"] = properties.getProperty("sonatypePassword")
        project.ext["signing.keyId"] = properties.getProperty("signing.keyId")
        project.ext["signing.password"] = properties.getProperty("signing.password")
        project.ext["signing.secretKeyRingFile"] =
          properties.getProperty("signing.secretKeyRingFile")

        repositories {
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
      }

    }

  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}