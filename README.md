# POC_GraalPyMicroservice

> proof of concept adding python to microservice

# GraalPy

- > Using Micronaut with java and Gradle with Kotlin created an application that can handle http calls and
  > run Python libraries and code

# ToDo

- Add name-plate-data-logger python logic to web application
- add unit tests

# Application Endpoints

- [localhost:8181/pygal](http://localhost:8181/pygal ) : PygalController imported python library, uses @GraalPyModule annotation, creates charts
- [localhost:8181/hello](http://localhost:8181/hello ) : HelloController local python script in resource folder
- [localhost:8181/sentiment](http://localhost:8181/sentiment) : SentimentAnalysisController manually creating Python context and getting a library

# Install

1) install java GraalVM , if haven't install sdkman
   ```shell
    sdk install java 24.0.1-graal
   ```
2) can use sdk env (uses .sdkmanrc file) to set a java version
   ```shell
    sdk env
   ```
3) with Gradle wrapper, no need to install anything else, wouldn't necessarily need python installed either

## MacOs

```shell
brew install jpeg;
brew install libtiff libjpeg libpng freetype;
brew install pkg-config;
```

# Adding Python Files

- Raw .py files can be placed `resources/org.graalvm.python.vfs/src/`
- Create a java module and use annotation `@GraalPyModule("hello")`
- example `hello.py` -> `HelloModule.java` -> `HelloController.java`

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

- view at [http://localhost:8181/ ](http://localhost:8181/ )

# NativeCompile

## Build

  ```shell
    ./gradlew nativeCompile
  ```

- output for native image will be in ```build/native/nativeCompile/```

## Run Native Image

  ```shell
    ./build/native/nativeCompile/nativeNameplateDataLogger
  ```

## Optimized Native Compile

### Build

```shell
    ./gradlew nativeOptimizedCompile
```

- output for native image will be in ```build/native/nativeOptimizedCompile/```

### Run

   ```shell
        ./build/native/nativeOptimizedCompile/optimizedNativeNameplateDataLogger
   ```

- view at [http://localhost:8181/ ](http://localhost:8181/ )

### Create Docker File and Image from GraalVm Native Image

#### Build File and Image

   ```shell
     ./gradlew optimizedDockerfileNative
   ```

- Dockerfile for build will be `build/docker/native-optimized/DockerfileNative`
- Created image should be `nameplate-data-logger:latest`

#### Run Image

   ```shell
        docker run  --name nameplatedatalogger --rm -p 8181:8181 nameplate-data-logger:latest
   ```

# Resources

- [Micronaut GraalPy Docs](https://micronaut-projects.github.io/micronaut-graal-languages/latest/guide/)
- [GraalPy Resource](https://www.graalvm.org/python/)
- [Oracle graalpython repo](https://github.com/oracle/graalpython?tab=readme-ov-file)
- [GraalPy Quick Reference Sheet](https://www.graalvm.org/uploads/quick-references/GraalPy_v1/quick-reference-graalpy-v1(eu_a4).pdf)
- [Overview Polygot Programming - injecting languages](https://www.graalvm.org/latest/reference-manual/polyglot-programming/)
- [Gradle plugin docs](https://github.com/oracle/graalpython/blob/master/docs/user/Embedding-Build-Tools.md#graalPy-gradle-plugin)

## Micronaut 4.8.3 Documentation

- [User Guide](https://docs.micronaut.io/4.8.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.8.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.8.3/guide/configurationreference.html)
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



