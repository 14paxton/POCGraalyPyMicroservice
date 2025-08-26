package com.nameplate;


import io.micronaut.core.annotation.ReflectionConfig;
import io.micronaut.core.annotation.TypeHint.AccessType;
import kotlin.coroutines.intrinsics.CoroutineSingletons;

@ReflectionConfig(type = CoroutineSingletons.class, accessType = AccessType.ALL_DECLARED_METHODS)
@ReflectionConfig(type = CoroutineSingletons.class, accessType = AccessType.ALL_DECLARED_CONSTRUCTORS)
@ReflectionConfig(type = CoroutineSingletons.class, accessType = AccessType.ALL_DECLARED_FIELDS)
public class GraalConfig {
}
