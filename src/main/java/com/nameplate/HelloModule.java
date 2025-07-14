package com.nameplate;

import io.micronaut.graal.graalpy.annotations.GraalPyModule;

@GraalPyModule("hello")
public interface HelloModule {
    String hello(String txt);
}