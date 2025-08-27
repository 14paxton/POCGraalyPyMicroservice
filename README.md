# GraalPy

- > Using Micronaut with java and Gradle with Kotlin created an application that can handle http calls and
  > run Python libraries and code

# Install

1) install java GraalVM , if haven't install sdkman
   ```shell
    sdk install java 24.0.1-graal
   ```
2) can use sdk env (uses .sdkmanrc file) to set a java version
   ```shell
    sdk env
   ```

## MacOs

- May need for some libraries
    ```shell
        brew install jpeg;
        brew install libtiff libjpeg libpng freetype;
        brew install pkg-config;
    ```

# Adding Python Files

- Raw `*.py` files can be placed `resources/GRAALPY-VFS/com.nameplate.nameplate-data-logger/src`
- Create a java module and use annotation `@GraalPyModule("hello")`
- example `hello.py` -> `HelloModule.java` -> `HelloController.java`

## Adding wheels .whl

> if trouble installing, sometimes need to download wheel directly

- `python-resources` has `*.whl` files
- `buildSrc/src/main/kotlin/PipInstall.kt` kotlin script for dynamically choosing files based on OS

### Naming Convention When Adding binary `*.whl` files

- > Example : `paddlepaddle-3.0.0-graalpy311-graalpy242_311_native-macosx_14_0_arm64.whl`

  - `paddlepaddle`: package name
  - `3.0.0`: version
  - `graalpy311`: Python 3.11 for GraalPy
  - `graalpy242_311_native`: GraalPy 24.2, Python 3.11, native build
  - `macosx_14_0_arm64`: macOS 14.0 ARM64 platform

- > Why use this convention?

  - GraalPy is not binary-compatible with CPython, so wheels built for CPython may not work.
  - The naming helps pip and GraalPy identify compatible wheels and avoid installation errors.

    > Use the GraalPy naming convention for wheels to ensure they are recognized as compatible with the GraalPy runtime and your specific platform.

# Modifying VFS File Location

> When using` @GraalPyModule` with Micronaut's automatic context management, you don't directly initialize the Python context yourself. Here's where
> you would configure the VFS resource directory:

## Option 1: Global Configuration (Recommended)

- Create a CustomGraalPyContextBuilderFactory to set the VFS resource directory globally:

> - Example: `java/com/skeleton/CustomGraalPyContextBuilderFactory.java`

# Build

  ```shell
      ./gradlew build
  ```

# Test

- > `PygalControllerTest.java` tests the controller and Python functionality

    ```shell
        ./gradlew test
    ```

# Run

  ```shell
    ./gradlew run
  ```

- View At: [http://localhost:8181/ ](http://localhost:8181/ )

# NativeCompile

## Build

  ```shell
    ./gradlew nativeCompile
  ```

- output for native image will be in ```build/native/nativeCompile/```

## Run Native Image

  ```shell
    ./build/native/nativeCompile/nativeChangeMe
  ```

## Optimized Native Compile

### Build

```shell
    ./gradlew nativeOptimizedCompile
```

- output for native image will be in ```build/native/nativeOptimizedCompile/```

### Run

   ```shell
        ./build/native/nativeOptimizedCompile/optimizedNativeChangeMe
   ```

- View At: [http://localhost:8181/ ](http://localhost:8181/ )

### Create Docker File and Image from GraalVm Native Image

#### Build File and Image

   ```shell
     ./gradlew optimizedDockerfileNative
   ```

- Dockerfile for build will be `build/docker/native-optimized/DockerfileNative`
- Created image should be `changeMe:latest`

#### Run Image

   ```shell
        docker run  --name changeMe --rm -p 8181:8181 changeMe:latest
   ```

# Resources

- [Micronaut GraalPy Docs](https://micronaut-projects.github.io/micronaut-graal-languages/latest/guide/)
- [GraalPy Resource](https://www.graalvm.org/python/)
- [Oracle graalpython repo](https://github.com/oracle/graalpython?tab=readme-ov-file)
- [GraalPy Quick Reference Sheet](https://www.graalvm.org/uploads/quick-references/GraalPy_v1/quick-reference-graalpy-v1(eu_a4).pdf)
- [Overview Polygot Programming - injecting languages](https://www.graalvm.org/latest/reference-manual/polyglot-programming/)
- [Gradle plugin docs](https://github.com/oracle/graalpython/blob/master/docs/user/Embedding-Build-Tools.md#graalPy-gradle-plugin)

## Micronaut latest Documentation

- [User Guide](https://docs.micronaut.io/latest/guide/index.html)
- [API Reference](https://docs.micronaut.io/latest/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/latest/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)

---

- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Shadow Gradle Plugin](https://gradleup.com/shadow/)

## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)

## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

## Feature graalpy documentation

- [Micronaut GraalPy Extension documentation](https://micronaut-projects.github.io/micronaut-graal-languages/latest/guide/)

- [https://graalvm.org/python](https://graalvm.org/python)



