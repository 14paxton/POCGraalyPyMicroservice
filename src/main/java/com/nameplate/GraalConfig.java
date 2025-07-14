package com.nameplate;

import io.micronaut.core.annotation.ReflectionConfig;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.core.annotation.TypeHint.AccessType;

@ReflectionConfig(type = com.nameplate.SentimentIntensityAnalyzer.class, accessType = TypeHint.AccessType.ALL_DECLARED_METHODS)
@ReflectionConfig(type = com.nameplate.SentimentIntensityAnalyzer.class, accessType = AccessType.ALL_DECLARED_CONSTRUCTORS)
@ReflectionConfig(type = com.nameplate.SentimentIntensityAnalyzer.class, accessType = AccessType.ALL_DECLARED_FIELDS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysisController.class, accessType = TypeHint.AccessType.ALL_DECLARED_METHODS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysisController.class, accessType = AccessType.ALL_DECLARED_CONSTRUCTORS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysisController.class, accessType = AccessType.ALL_DECLARED_FIELDS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysis.class, accessType = TypeHint.AccessType.ALL_DECLARED_METHODS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysis.class, accessType = AccessType.ALL_DECLARED_CONSTRUCTORS)
@ReflectionConfig(type = com.nameplate.SentimentAnalysis.class, accessType = AccessType.ALL_DECLARED_FIELDS)
public class GraalConfig {
}
