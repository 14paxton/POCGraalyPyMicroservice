import PipInstall.PackageName.*

plugins {
    id("io.micronaut.application")
    id("com.gradleup.shadow")
    id("io.micronaut.aot")
    id("org.jetbrains.kotlin.jvm")
    id("org.graalvm.python")
}

// *************************************************************************************************************************************
// Version Variables *******************************************************************************************************************

val graalPythonVersion: String by project
val graalVersion: String by project
val port: String by project
val jvmVersion: String by project

val javaVersion: JavaVersion = JavaVersion.toVersion(jvmVersion)
val javaLanguageVersion: JavaLanguageVersion = JavaLanguageVersion.of(jvmVersion)
val graalJvmVendor: JvmVendorSpec = JvmVendorSpec.GRAAL_VM

val groupName = "com.nameplate"
val mainClassName = "$groupName.Application"
val resourceFileLocation = "GRAALPY-VFS/$groupName.${rootProject.name}"

// END Version Variables ***************************************************************************************************************
// *************************************************************************************************************************************

group = groupName
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

application {
    mainClass.set(mainClassName)
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    toolchain {
        languageVersion.set(javaLanguageVersion)
        vendor.set(graalJvmVendor)
    }
}


dependencies {

    // Micronaut implementation
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.graal-languages:micronaut-graalpy")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")

    // Runtime dependencies
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("ch.qos.logback:logback-classic")

    // Test dependencies
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Annotation processors
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    compileOnly("io.micronaut:micronaut-http-client")

    implementation("org.graalvm.polyglot:polyglot:$graalPythonVersion")
    implementation("org.graalvm.polyglot:python:$graalPythonVersion")
}


// *************************************************************************************************************************************
// Micronaut Gradle Plugin options : https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/#_micronaut_library_plugin ****

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("$groupName.*")
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
    toolchainDetection.set(true)
    binaries {
        named("main") {
            configureNativeBinary("native-${rootProject.name}", fallbackEnabled = false)
        }

        named("optimized") {
            configureNativeBinary("optimized-${rootProject.name}", fallbackEnabled = true)
        }
    }
}

fun org.graalvm.buildtools.gradle.dsl.NativeImageOptions.configureNativeBinary(imageName: String, fallbackEnabled: Boolean) {
    println("*** Configuring Native Binary *** $imageName ***")

    this.imageName.set(imageName)
    richOutput.set(true)
    verbose.set(true)
    fallback.set(fallbackEnabled)
    mainClass.set(mainClassName)
    resources.autodetect()
    buildArgs.add("--verbose")
    buildArgs.add("--initialize-at-build-time=kotlin")
    buildArgs.add("-march=native")
    buildArgs.add("-Ob")
    // jvmArgs.add("-Xmx8g")
    // javaLauncher.set(javaToolchains.launcherFor {
    //   languageVersion.set(javaLanguageVersion)
    //   vendor.set(graalJvmVendor)
    // })
}

// END GraalVM options *****************************************************************************************************************
// *************************************************************************************************************************************


// *************************************************************************************************************************************
// Build Task Modifications ************************************************************************************************************

// tasks.named<org.graalvm.buildtools.gradle.tasks.BuildNativeImageTask>("nativeCompile") {
//
//   println("*** NativeCompile ***")
//
// }

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {
    jdkVersion.set(jvmVersion)
    graalImage.set("container-registry.oracle.com/graalvm/native-image:$graalVersion")
    baseImage.set("amazonlinux:2023")
    exposedPorts.set(setOf(port.toInt()))
    args("-XX:MaximumHeapSizePercent=80", "-Dio.netty.allocator.numDirectArenas=0", "-Dio.netty.noPreferDirect=true")
}

tasks.withType<Jar> {
    isZip64 = true
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    isZip64 = true
    archiveBaseName.set("shadow")
    archiveVersion.set(version.toString())
}

// END Build Task Modifications **********************************************************************************************************
// ***************************************************************************************************************************************


// *************************************************************************************************************************************
// PYTHON LIBRARIES Import *************************************************************************************************************

val localPackageInstallPathList: Set<String> = PipInstall.resolvePackages(rootDir, listOf(PADDLEPADDLE,
                                                                                          PADDLEOCR,
                                                                                          SCIPY,
                                                                                          PANDAS,
                                                                                          SCIKIT_LEARN,
                                                                                          SHAPELY,
                                                                                          TIKTOKEN,
                                                                                          OPENCV))

val packagesForPipToPull: Set<String> = setOf("python-dotenv>=1.1.1",
                                              "tqdm>=4.67.1",
                                              "PyYAML>=6.0.2",
                                              "pydantic>=2.11.7",
                                              "numpy>=1.26.4",
                                              "pillow>=11.3.0",
                                              "pygal>=3.0.5",
                                              "vader-sentiment==3.2.1.1",
                                              "requests>=2.26.0")

graalPy {
    // Default Resource Directory
    // resourceDirectory.set("org.graalvm.python.vfs")
    resourceDirectory.set(resourceFileLocation)
    packages.set(buildSet {
        add("--prefer-binary")
        add(PipInstall.wheelOsStandard)
        addAll(packagesForPipToPull)
        addAll(localPackageInstallPathList)
    })
}

// END PYTHON LIBRARIES Import *********************************************************************************************************
// *************************************************************************************************************************************


//**************************************************************************************************************************************
// Python Resources For Local .VENV ****************************************************************************************************

val cleanVenv by tasks.registering(Delete::class) {
    delete(layout.projectDirectory.dir(".venv"))

    doLast {
        println("deleted .venv directory")
    }
}

tasks.register<Copy>("copyVenvResources") {
    group = "python"
    description = "Cleans then copies GraalPy venv resources to .venv"
    dependsOn("graalPyResources", cleanVenv)

    from(layout.buildDirectory.dir("generated/graalpy/resources/$resourceFileLocation/venv")) {
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

// END Python Resources For Local .VENV ************************************************************************************************
// *************************************************************************************************************************************


// *************************************************************************************************************************************
// Build Performance Optimizations *****************************************************************************************************

// Enable build caching and parallel execution
// cache stored ~/.gradle/caches/build-cache-<version>/
// /Users/leroy/Library/Caches/pip-graalpy
// gradle.startParameter.isBuildCacheEnabled = true