pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  plugins {
    id("io.micronaut.application") version "4.5.4"
    id("com.gradleup.shadow") version "8.3.8"
    id("io.micronaut.aot") version "4.5.3"
    id("org.jetbrains.kotlin.jvm") version "2.2.20-Beta2"
    id("org.graalvm.python") version "24.2.2"
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Enable build caching and parallel execution
// cache stored ~/.gradle/caches/build-cache-<version>/
// ~/Library/Caches/pip-graalpy
gradle.startParameter.isBuildCacheEnabled = true

rootProject.name = "nameplate-data-logger"

buildCache {
  local {
    isEnabled = true
  }
}