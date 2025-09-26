package com.nameplate;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Value;
import io.micronaut.graal.graalpy.GraalPyContextBuilderFactory;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.python.embedding.GraalPyResources;
import org.graalvm.python.embedding.VirtualFileSystem;

@io.micronaut.context.annotation.Context
@Replaces(GraalPyContextBuilderFactory.class)
public class CustomGraalPyContextBuilderFactory implements GraalPyContextBuilderFactory {
  private final static Engine engine = Engine.create();
  private final VirtualFileSystem vfs;


  public CustomGraalPyContextBuilderFactory(@Value("${graalpy.vfs.resource-directory}") String resourceDirectory) {
    this.vfs = VirtualFileSystem.newBuilder()
                                .resourceDirectory(resourceDirectory)
                                .build();
  }

  @Override
  public Context.Builder createBuilder() {

    return GraalPyResources.contextBuilder(vfs)
                           .engine(engine)
                           .allowNativeAccess(true)
                           .allowCreateProcess(true)
                           .allowExperimentalOptions(true)
                           .allowCreateThread(true)
                           .useSystemExit(true)
                           .option("python.VerboseFlag", "false")
                           .option("python.IsolateNativeModules", "false");
  }
}