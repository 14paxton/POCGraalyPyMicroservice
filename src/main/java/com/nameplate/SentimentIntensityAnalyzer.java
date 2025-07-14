package com.nameplate;

public interface SentimentIntensityAnalyzer {
    java.util.Map<String, Double> polarity_scores(String text);
}
