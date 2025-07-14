package com.nameplate;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class TestImageModuleTest {

  @Inject
  TestImageModule testImageModule;

  @Test
  void testModuleInjection() {
    Assertions.assertNotNull(testImageModule, "TestImageModule should be injected");
  }

  @Test
  void testCreateTestImage() {
    Assertions.assertDoesNotThrow(() -> {
      testImageModule.create_test_image();
    }, "create_test_image should execute without throwing exceptions");
  }
}