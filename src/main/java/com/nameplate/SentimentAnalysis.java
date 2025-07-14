package com.nameplate;

import io.micronaut.context.annotation.Bean;
import org.graalvm.polyglot.Value;

import static com.nameplate.GraalPyContext.PYTHON;

@Bean
public class SentimentAnalysis {

  private final SentimentIntensityAnalyzer sentimentIntensityAnalyzer;

  public SentimentAnalysis(GraalPyContext context) {
    Value value = context.get()
                         .eval(PYTHON, """
                             from vader_sentiment.vader_sentiment import SentimentIntensityAnalyzer
                             SentimentIntensityAnalyzer()
                             """);
    sentimentIntensityAnalyzer = value.as(SentimentIntensityAnalyzer.class);
  }

  public java.util.Map<String, Double> getPolarityScores(String text) {
    return sentimentIntensityAnalyzer.polarity_scores(text);
  }
}
