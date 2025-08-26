package com.nameplate.services;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class TestImageServiceTest {
  private final Path projectRoot = Paths.get(System.getProperty("user.dir"));
  private final Path testOutputDir = projectRoot.resolve("testOutput");

  @Inject
  TestImageService testImageService;

  @Test
  void testModuleInjection() {
    assertNotNull(testImageService, "TestImageModule should be injected");
  }

  @Test
  void testCreateTestImage() throws IOException {
    String date = LocalDateTime.now()
                               .toString();
    Files.createDirectories(Paths.get("./testOutput"));
    String fileName = String.format("test_image_%s.png", date);
    Path testFilePath = testOutputDir.resolve(fileName);

    assertDoesNotThrow(() -> {
      testImageService.createTestImage(String.valueOf(testFilePath));
    }, "create_test_image should execute without throwing exceptions");

    boolean fileExists = Files.exists(testFilePath);
    assertTrue(fileExists, "Test file should exist in testOutput directory");
    
    Files.deleteIfExists(testFilePath);
  }

}