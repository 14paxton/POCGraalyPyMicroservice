import PipInstall.PackageName.*
import PipInstall.wheelOsStandard

val graalPythonVersion: String by project

val paddlePaddleInstall = PADDLEPADDLE.getPackage(rootDir)
val paddleOcrInstall = PADDLEOCR.getPackage(rootDir)
val sciPyInstall = SCIPY.getPackage(rootDir)
val pandasInstall = PANDAS.getPackage(rootDir)
val sciKitLearnInstall = SCIKIT_LEARN.getPackage(rootDir)
val shapelyInstall = SHAPELY.getPackage(rootDir)
val tikTokenInstall = TIKTOKEN.getPackage(rootDir)

plugins {
  id("io.micronaut.application") version "4.5.4"
  id("org.graalvm.python") version "24.2.1"
  id("com.gradleup.shadow") version "8.3.8"
  id("io.micronaut.aot") version "4.5.3"
}

group = "com.nameplate"
version = "0.1"

repositories {
  mavenLocal()
  mavenCentral()
}

application {
  mainClass.set("com.nameplate.Application")
}

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
}


// *************************************************************************************************************************************
// PYTHON LIBRARIES Import *************************************************************************************************************

graalPy {

  // resourceDirectory.set("GRAALPY-VFS/com/nameplate")
  // resourceDirectory.set("org.graalvm.python.vfs")

  packages.set(
          setOf(
                  "--prefer-binary",
                  wheelOsStandard,
                  "numpy>=1.26.4",
                  "python-dotenv>=1.1.1",
                  "tqdm>=4.67.1",
                  "PyYAML>=6.0.2",
                  "pydantic>=2.11.7",
                  "pillow",
                  tikTokenInstall,
                  shapelyInstall,
                  sciKitLearnInstall,
                  pandasInstall,
                  sciPyInstall,
                  paddlePaddleInstall,
                  paddleOcrInstall,
               )
              )
}

// END PYTHON LIBRARIES Import *********************************************************************************************************
// *************************************************************************************************************************************


dependencies {
  compileOnly("io.micronaut:micronaut-http-client")

  implementation("org.graalvm.polyglot:polyglot:$graalPythonVersion")
  implementation("org.graalvm.polyglot:python:$graalPythonVersion")

  implementation("io.micronaut.views:micronaut-views-thymeleaf")
  implementation("io.micronaut:micronaut-http-server-netty")
  implementation("io.micronaut.graal-languages:micronaut-graalpy")
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


// *************************************************************************************************************************************
// Micronaut Gradle Plugin options : https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/#_micronaut_library_plugin ****

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

// END Micronaut ***********************************************************************************************************************
// *************************************************************************************************************************************


// *************************************************************************************************************************************
// GraalVM Gradle Plugin options : https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html ******************************

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

// END GraalVM options *****************************************************************************************************************
// *************************************************************************************************************************************


// *************************************************************************************************************************************
// Dockerfile.graalpy-vfs instructions *************************************************************************************************

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {
  jdkVersion.set("24")
  graalImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
  baseImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
  exposedPorts.set(setOf(8181))
}

// END Dockerfile.graalpy-vfs **********************************************************************************************************
// *************************************************************************************************************************************


tasks.withType<Jar> {
  isZip64 = true
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
  isZip64 = true
}


//*************************************************************************************************************************
// Python Resource Folder Management **************************************************************************************


tasks.register<Copy>("copyVenvResources") {
  group = "python"
  description = "Copies GraalPy venv resources from build directory to .venv"
  dependsOn("graalPyResources")

  from("build/generated/graalpy/resources/org.graalvm.python.vfs/venv") {
    include("**/*")
  }
  into(layout.projectDirectory.dir(".venv"))

  doLast {
    println("Copied GraalPy venv resources to .venv directory")
  }
}

tasks.named("graalPyResources") {
  finalizedBy("copyVenvResources")
}


// END Python Resource Folder Management ***********************************************************************************************
// *************************************************************************************************************************************