package com.nameplate;

import io.micronaut.graal.graalpy.annotations.GraalPyModule;

@GraalPyModule("create_test_image")
public interface TestImageModule {
    void create_test_image();
}
