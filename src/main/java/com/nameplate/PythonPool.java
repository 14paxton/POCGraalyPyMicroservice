package com.nameplate;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.python.embedding.GraalPyResources;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

@io.micronaut.context.annotation.Context
public class PythonPool {
  private final Engine engine;
  private final BlockingDeque<Context> contexts;
  private int size;

  public PythonPool() {
    engine   = Engine.create();
    contexts = new LinkedBlockingDeque<>();
    size     = 0;
    setPoolSize(2);
  }

  public synchronized void setPoolSize(int newSize) {
    if (newSize <= 0) {
      throw new IllegalArgumentException();
    }
    while (size > newSize) {
      try {
        contexts.takeLast();
      }
      catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      size--;
    }
    while (size < newSize) {
      contexts.addLast(createContext(engine));
      size++;
    }
  }

  private static Context createContext(Engine engine) {
    //    var resourcesDir = Path.of("build","generated","graalpy","resources");
    //    if (!resourcesDir.toFile()
    //                     .isDirectory()) {
    //      var fs = VirtualFileSystem.create();
    //      try {
    //        GraalPyResources.extractVirtualFileSystemResources(fs, resourcesDir);
    //      }
    //      catch (IOException e) {
    //        throw new RuntimeException(e);
    //      }
    //    }
    var context = GraalPyResources.contextBuilder()
                                  .engine(engine)
                                  .allowNativeAccess(true)
                                  .allowCreateProcess(true)
                                  .allowExperimentalOptions(true)
                                  .option("python.IsolateNativeModules", "true")
                                  .build();
    context.initialize("python");
    return context;
  }

  public <T> T execute(Function<Context, T> action) {
    Context c;
    try {
      c = contexts.takeFirst();
    }
    catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    contexts.addLast(c);
    T result = action.apply(c);
    if (!(result instanceof Number || result instanceof String)) {
      throw new IllegalStateException("Instances must not leak out of PythonPool#execute. " + "Convert the value to a java.lang.Number or a java.lang.String, before returning " + "it" + ".");
    }
    return result;
  }
}
