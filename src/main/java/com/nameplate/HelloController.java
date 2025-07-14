package com.nameplate;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/hello")
class HelloController {

    private final HelloModule hello;

    HelloController(HelloModule hello) {
        this.hello = hello;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    String index() {
        return hello.hello("World");
    }
}
