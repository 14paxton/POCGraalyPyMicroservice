package com.nameplate.services;

import com.nameplate.TestImageModule;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TestImageService {

  private final TestImageModule testImageModule;

  @Inject
  public TestImageService(TestImageModule testImageModule) {
    this.testImageModule = testImageModule;
  }

  public void createTestImage(String output_path) {
    testImageModule.create_test_image(output_path);
  }
}
