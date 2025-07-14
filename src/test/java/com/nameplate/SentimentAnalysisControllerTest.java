package com.nameplate;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class SentimentAnalysisControllerTest {
  @Test
  void testAnalyzeResponse(@Client("/sentiment") HttpClient client) {
    HttpRequest<Object> requestHappy = HttpRequest.GET("/analyze?text=happy");
    HttpRequest<Object> requestSad   = HttpRequest.GET("/analyze?text=sad");

    Map<String, Double>
        response =
        client
            .toBlocking()
            .retrieve(requestHappy, Argument.mapOf(String.class, Double.class));

    assertTrue(response.get("compound") > 0.1);

    response =
        client
            .toBlocking()
            .retrieve(requestSad, Argument.mapOf(String.class, Double.class));

    assertTrue(response.get("compound") < -0.1);
  }
}
