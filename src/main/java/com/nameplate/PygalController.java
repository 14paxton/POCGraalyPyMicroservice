package com.nameplate;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/pygal")
class PygalController {
  private static final Logger LOG = LoggerFactory.getLogger(PygalController.class);
  private final PygalModule pygal;

  PygalController(PygalModule pygal) {
    this.pygal = pygal;
  }

  @ExecuteOn(TaskExecutors.BLOCKING)
  @Get
  @Produces("image/svg+xml")
  public String index() {
    LOG.info("calling index");
    PygalModule.StackedBar stackedBar = pygal.StackedBar();
    stackedBar.add("Fibonacci", new int[]{0, 1, 1, 2, 3, 5, 8});
    PygalModule.Svg svg = stackedBar.render();
    return svg.decode();
  }

}
