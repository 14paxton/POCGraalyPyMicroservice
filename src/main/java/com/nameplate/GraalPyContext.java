package com.nameplate;

import jakarta.annotation.PreDestroy;
import org.graalvm.polyglot.Context;
import org.graalvm.python.embedding.GraalPyResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@io.micronaut.context.annotation.Context
public final class GraalPyContext {
  static final String PYTHON = "python";
  private static final Logger LOG = LoggerFactory.getLogger(GraalPyContext.class);
  private final Context context;

  public GraalPyContext() {
    context = GraalPyResources.createContext();
    context.initialize(PYTHON);
  }

  Context get() {
    return context;
  }

  @PreDestroy
  void close() {
    try {
      context.close(true);
    }
    catch (Exception e) {
      LOG.error("Exception closing GraalPyContext: {}", e.getMessage());
    }
  }
}
