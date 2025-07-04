import org.gradle.internal.os.OperatingSystem.current

val currentOS = current()

val wheelOsStandard = when {
    currentOS.isLinux -> "patchelf"
    currentOS.isMacOsX -> "delocate"
    currentOS.isWindows -> "delvewheel"
    else -> "patchelf"
}

data class PackageConfig(val path: String, val fallback: String)

val numpyConfig = when {
    currentOS.isLinux -> PackageConfig(
            "${rootDir}/python-resources/Linux/numpy-2.2.6-graalpy311-graalpy242_311_native-manylinux_2_27_aarch64" +
                    ".manylinux_2_28_aarch64.whl",
            "numpy==1.26.4"
                                      )

    currentOS.isMacOsX -> PackageConfig(
            "${rootDir}/python-resources/MacOS/numpy-2.2.6-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl",
            "numpy==1.26.4"
                                       )

    else -> PackageConfig("", "numpy==1.26.4")
}

val pillowConfig = when {
    currentOS.isLinux -> PackageConfig(
            "${rootDir}/python-resources/Linux/pillow-11.1.0-graalpy311-graalpy242_311_native-manylinux_2_27_aarch64.manylinux_2_28_aarch64.whl",
            "pillow<=11.2.1"
                                      )

    currentOS.isMacOsX -> PackageConfig(
            "${rootDir}/python-resources/MacOS/pillow-11.1.0-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl",
            "pillow<=11.2.1"
                                       )

    else -> PackageConfig("", "pillow<=11.2.1")
}

val shapelyConfig = when {
    currentOS.isLinux -> PackageConfig(
            "${rootDir}/python-resources/Linux/shapely-2.1.1-graalpy311-graalpy242_311_native-manylinux_2_24_aarch64.manylinux_2_28_aarch64.whl",
            "shapely==2.0.0"
                                      )

    currentOS.isMacOsX -> PackageConfig(
            "${rootDir}/python-resources/MacOS/shapely-2.1.1-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl",
            "shapely==2.0.0"
                                       )

    else -> PackageConfig("", "shapely==2.0.0")
}

val cythonConfig = PackageConfig(
        "${rootDir}/python-resources/any/Cython-3.0.11-py2.py3-none-any.whl", "cython==0.29.37"
                                )

val mesonPythonConfig = PackageConfig(
        "${rootDir}/python-resources/any/meson_python-0.16.0-py3-none-any.whl", "meson-python<= 0.16.0"
                                     )

val paddleOcrConfig = PackageConfig(
        "${rootDir}/python-resources/any/paddleocr-3.1.0-py3-none-any.whl", "paddleocr==2.7.0.3"
                                   )

val sciKitImageConfig = PackageConfig(
        "${rootDir}/python-resources/MacOS/scikit_image-0.25.0-cp312-cp312-macosx_15_0_arm64.whl", "scikit-image==0.25.0"
                                     )

val pandasConfig = PackageConfig(
        "${rootDir}/python-resources/MacOS/pandas-2.2.3-cp312-cp312-macosx_15_0_arm64.whl", "pandas==2.2.2"
                                )

fun createFileInstall(path: String, fallback: String): String {
    return if (path.isNotEmpty()) "file://${file(path).absolutePath}" else fallback
}

val numpyInstall = createFileInstall(numpyConfig.path, numpyConfig.fallback)
val pillowInstall = createFileInstall(pillowConfig.path, pillowConfig.fallback)
val shapelyInstall = createFileInstall(shapelyConfig.path, shapelyConfig.fallback)
val cythonInstall = createFileInstall(cythonConfig.path, cythonConfig.fallback)
val mesonPythonInstall = createFileInstall(mesonPythonConfig.path, mesonPythonConfig.fallback)
val paddleOcrInstall = createFileInstall(paddleOcrConfig.path, paddleOcrConfig.fallback)
val sciKitImageInstall = createFileInstall(sciKitImageConfig.path, sciKitImageConfig.fallback)
val pandasInstall = createFileInstall(pandasConfig.path, pandasConfig.fallback)

plugins {
    id("io.micronaut.application") version "4.5.3"
    id("org.graalvm.python") version "24.2.1"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.3"
}

/**************************************************************************************************************************
// PYTHON LIBRARIES Import ***********************************************************************************************
 *************************************************************************************************************************
- Python packages and their versions can be specified as if used with pip.
- Install and pin the numpy package to version 1.26.4.
 ****************************************************************************************************************************/

graalPy {

    // resourceDirectory.set("GRAALPY-VFS/com/nameplate/nameplate-data-logger")
    //    resourceDirectory.set("GRAALPY-VFS/com/nameplate")
    // resourceDirectory.set("org.graalvm.python.vfs")
    // externalDirectory.set(file("${rootDir}/python-resources"))
    // packages.set(setOf("--only-binary=:all:", "--prefer-binary", "cython", "pygal", "vader-sentiment==3.2.1.1", "requests", "numpy==1.26.4", "delocate==0.13.0"))
    packages.set(
            setOf(
                    "--prefer-binary",
                    wheelOsStandard,
                    // sciKitImageInstall,
                    cythonInstall,
                    mesonPythonInstall,
                    numpyInstall,
                    pillowInstall,
                    shapelyInstall,
                    "scikit_build_core==0.11.1",
                    "python-dotenv==0.21.0",
                    "tqdm==4.66.4",
                    "networkx==2.8.8",
                    "imageio==2.22.4",
                    "tifffile==2022.10.10",
                    "packaging==24.1",
                    pandasInstall
                    // "pandas==2.2.2",
                    // "paddleocr<=3.1.0"
                    // paddleOcrInstall

                    // leave comments
                    //            "--prefer-binary",
                    //            "cython",
                    //            "numpy==1.26.4",
                    //            "python-dotenv>=0.21.0",
                    //            "tqdm>=4.66.4",
                    //            "pillow<=11.2.1",
                    //            "paddlepaddle==3.0.0",
                    //            "paddleocr==2.7.0.3", // Known working version with Python 3.9+ and minimal deps
                 )
                )
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
    implementation("io.micronaut.graal-languages:micronaut-graalpy:1.2.0")
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
    applicationDefaultJvmArgs = listOf(
            "-Dpolyglot.engine.WarnInterpreterOnly=false",
            "-Dpolyglot.log.file=Log/truffle.log",
            "--enable-native-access=org.graalvm.truffle",
            "-Dpolyglot.engine.WarnVirtualThreadSupport=false"
                                      )
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
} // END Micronaut ***********************************************************************************************************
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
} // END GraalVM options ****************************************************************************************************
// *************************************************************************************************************************


//*************************************************************************************************************************
// Dockerfile.graalpy-vfs instructions ************************************************************************************************

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {
    jdkVersion.set("24")
    graalImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
    baseImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
    exposedPorts.set(setOf(8181))
} // END Dockerfile.graalpy-vfs **********************************************************************************************************
// *************************************************************************************************************************


//*************************************************************************************************************************
// Python Resource Folder Management **************************************************************************************

// This explicitly tells Gradle that processResources and processTestResources tasks depend on the graalPyResources task, ensuring proper task ordering.

/* tasks.named("processResources") {
     dependsOn("graalPyResources")
 }
 tasks.named("processTestResources") {
     dependsOn("graalPyResources")
 }

 tasks.named("test") {
     dependsOn("graalPyResources")
 }*/

// This tells Gradle to include duplicate resources rather than failing the build when it encounters them.

/* tasks.withType<ProcessResources> {
     duplicatesStrategy = DuplicatesStrategy.INCLUDE
 }

 sourceSets {
     main {
         resources {
             srcDir("org.graalvm.python.vfs")
         }
     }
     test {
         resources {
             srcDir("org.graalvm.python.vfs")
         }
     }
 }*/

// END Python Resource Folder Management **********************************************************************************
//*************************************************************************************************************************
