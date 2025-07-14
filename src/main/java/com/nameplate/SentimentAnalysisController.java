package com.nameplate;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.views.View;

import java.util.Map;

@Controller("/sentiment")
public class SentimentAnalysisController {
  private final SentimentAnalysis sentimentAnalysis;

  SentimentAnalysisController(SentimentAnalysis sentimentAnalysis) {
    this.sentimentAnalysis = sentimentAnalysis;
  }

  @Get
  @View("sentiment")
  public void index() {

  }

  @Get(value = "/analyze")
  @ExecuteOn(TaskExecutors.BLOCKING)
  public Map<String, Double> answer(String text) {
    return sentimentAnalysis.getPolarityScores(text);
  }
}
