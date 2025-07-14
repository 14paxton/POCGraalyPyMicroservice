package com.nameplate;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class PygalControllerTest {

    @Test
    void testPygalResponse(@Client("/") HttpClient client) {
        String response = client
                .toBlocking()
                .retrieve(HttpRequest.GET("/pygal"));
        assertTrue(response.contains("<svg xmlns:xlink"));
        assertTrue(response.contains("<title>Pygal</title>"));
        assertTrue(response.contains("<g class=\"graph stackedbar-graph vertical\">"));
    }
}
