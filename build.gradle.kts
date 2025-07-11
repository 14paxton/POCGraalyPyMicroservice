import PipInstall.PackageName
import PipInstall.PackageName.*
import PipInstall.getPackageBinary
import PipInstall.wheelOsStandard

val numpyInstall = getPackageBinary(rootDir, NUMPY)
val paddlePaddleInstall = getPackageBinary(rootDir, PADDLEPADDLE)
val paddleOcrInstall = getPackageBinary(rootDir, PADDLEOCR)
val paddleXInstall = getPackageBinary(rootDir, PADDLEX)
val sciPyInstall = getPackageBinary(rootDir, SCIPY)
val pandasInstall = getPackageBinary(rootDir, PANDAS)
val skitLearnInstall = getPackageBinary(rootDir, SKITLEARN)
val pillowInstall = getPackageBinary(rootDir, PILLOW)
val shapelyInstall = getPackageBinary(rootDir, SHAPELY)
val tikTokenInstall = getPackageBinary(rootDir, TIKTOKEN)

plugins {
    id("io.micronaut.application") version "4.5.4"
    id("org.graalvm.python") version "24.2.1"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.3"
}

// **************************************************************************************************************************
// PYTHON LIBRARIES Import ***********************************************************************************************

graalPy {
    packages.set(
            setOf(
                    "--prefer-binary",
                    wheelOsStandard,
                    numpyInstall,
                    "python-dotenv==1.1.1",
                    "tqdm==4.67.1",
                    "PyYAML==6.0.2",
                    "pydantic==2.11.7",
                    tikTokenInstall,
                    pillowInstall,
                    shapelyInstall,
                    skitLearnInstall,
                    pandasInstall,
                    sciPyInstall,
                    paddlePaddleInstall,
                    paddleOcrInstall,
                    paddleXInstall,
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

    implementation("org.graalvm.polyglot:polyglot:24.2.1")
    implementation("org.graalvm.polyglot:python:24.2.1")
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

application {
    mainClass.set("com.nameplate.Application")
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
// Dockerfile.graalpy-vfs instructions ************************************************************************************************

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {
    jdkVersion.set("24")
    graalImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
    baseImage.set("container-registry.oracle.com/graalvm/native-image:24.0.1")
    exposedPorts.set(setOf(8181))
}

// END Dockerfile.graalpy-vfs **********************************************************************************************************
// *************************************************************************************************************************


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


// This explicitly tells Gradle that processResources and processTestResources tasks depend on the graalPyResources task, ensuring proper task ordering.

// tasks.named("processResources") {
//     dependsOn("graalPyResources")
// }
// tasks.named("processTestResources") {
//     dependsOn("graalPyResources")
// }
//
// tasks.named("test") {
//     dependsOn("graalPyResources")
// }


// This tells Gradle to include duplicate resources rather than failing the build when it encounters them.

// tasks.withType<ProcessResources> {
//     duplicatesStrategy = DuplicatesStrategy.INCLUDE
// }
//
// sourceSets {
//     main {
//         resources {
//             // Make sure main resources include the custom GraalPy VFS
//             srcDir("${layout.buildDirectory}/generated/graalpy/resources/GRAALPY-VFS/com/nameplate/nameplate-data-logger")
//         }
//     }
//     test {
//         resources {
//             // And your tests include it too
//             srcDir("${layout.buildDirectory}/generated/graalpy/resources/GRAALPY-VFS/com/nameplate/nameplate-data-logger")
//         }
//     }
// }


// END Python Resource Folder Management **********************************************************************************
//*************************************************************************************************************************
