plugins {
  id("io.micronaut.application") version "4.5.3"
  id("org.graalvm.python") version "24.2.1"
  id("com.gradleup.shadow") version "8.3.6"
  id("io.micronaut.aot") version "4.5.3"
}

/**************************************************************************************************************************
// PYTHON LIBRARIES Import ***********************************************************************************************
 *************************************************************************************************************************
- Python packages and their versions can be specified as if used with pip. Install and pin the numpy package to version 1.26.4.
- Currently, the support for isolating native modules and loading them multiple times relies on packages built from source on the target system.
Until this limitation is lifted, we must force the plugins to build numpy from source.
- The implementation of native module isolation uses platform-specific helper packages at runtime.
These are selected here depending on the operating system.
 ****************************************************************************************************************************/

graalPy {
  // TODO should set up unique venv for python but can never get to work right,
  // resourceDirectory.set("GRAALPY-VFS/com/nameplate")

  // packages.set(setOf("--only-binary=:all:", "--prefer-binary", "cython", "pygal", "vader-sentiment==3.2.1.1", "requests", "numpy==1.26.4",
    // "delocate==0.13.0"))
  packages.set(setOf(
          "pygal",
          "Pillow",
          "cython",
          "vader-sentiment==3.2.1.1",
          "requests",
          "numpy==1.26.4",
          "delocate==0.13.0"))
}

// END PYTHON LIBRARIES Import *********************************************************************************************
// *************************************************************************************************************************

group = "com.nameplate"
version = "0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  compileOnly("io.micronaut:micronaut-http-client")

  implementation("io.micronaut.views:micronaut-views-thymeleaf")
  implementation("io.micronaut:micronaut-http-server-netty")
  implementation("io.micronaut.graal-languages:micronaut-graalpy:1.1.0")
  implementation("io.micronaut.serde:micronaut-serde-jackson")

  runtimeOnly("org.yaml:snakeyaml")
  runtimeOnly("ch.qos.logback:logback-classic")

  testImplementation("io.micronaut:micronaut-http-client")
  testImplementation("io.micronaut.test:micronaut-test-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter-api")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

  annotationProcessor("io.micronaut:micronaut-http-validation")
  annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
}

application {
  mainClass.set("com.nameplate.Application")
  applicationDefaultJvmArgs = listOf("-Dpolyglot.engine.WarnInterpreterOnly=false",
                                     "-Dpolyglot.log.file=Log/truffle.log",
                                     "--enable-native-access=org.graalvm.truffle",
                                     "-Dpolyglot.engine.WarnVirtualThreadSupport=false")
}

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
}


// ********************************************************************************************************************************
// Micronaut Gradle Plugin options : https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/#_micronaut_library_plugin

micronaut {
  runtime("netty")
  testRuntime("junit5")
  processing {
    incremental(true)
    annotations("com.nameplate.*")
  }
  aot {
    configFile = file("gradle/micronaut-aot.properties")
  }
}

// END Micronaut ***********************************************************************************************************
// *************************************************************************************************************************


// *************************************************************************************************************************
// GraalVM Gradle Plugin options : https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html ******************

graalvmNative {
  toolchainDetection = false
  binaries {
    named("main") {
      imageName.set("nativeNameplateDataLogger")
      richOutput.set(true)
      verbose.set(true)
      fallback.set(false)
      mainClass.set("com.nameplate.Application")
      resources.autodetect()
      javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(24))
        vendor.set(JvmVendorSpec.ORACLE)
      })
    }

    named("optimized") {
      imageName.set("optimizedNativeNameplateDataLogger")
      richOutput.set(true)
      verbose.set(true)
      resources.autodetect()
      mainClass.set("com.nameplate.Application")
      resources.autodetect()
      javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(24))
        vendor.set(JvmVendorSpec.ORACLE)
      })
    }
  }
}

// END GraalVM options ****************************************************************************************************
// *************************************************************************************************************************


//*************************************************************************************************************************
// Dockerfile instructions ************************************************************************************************

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {
  jdkVersion.set("24")
  graalImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
  baseImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
  exposedPorts.set(setOf(8181))
}

// END Dockerfile **********************************************************************************************************
// *************************************************************************************************************************


//*************************************************************************************************************************
// Python Resource Folder Management **************************************************************************************

// This explicitly tells Gradle that processResources and processTestResources tasks depend on the graalPyResources task, ensuring proper task
// ordering.
tasks.named("processResources") {
  dependsOn("graalPyResources")
}
tasks.named("processTestResources") {
  dependsOn("graalPyResources")
}

tasks.named("test") {
  dependsOn("graalPyResources")
}

// This tells Gradle to include duplicate resources rather than failing the build when it encounters them.
tasks.withType<ProcessResources> {
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
  main {
    resources {
      srcDir("build/generated/graalpy/resources")
    }
  }
  test {
    resources {
      srcDir("build/generated/graalpy/resources")
    }
  }
}

// END Python Resource Folder Management **********************************************************************************
//*************************************************************************************************************************