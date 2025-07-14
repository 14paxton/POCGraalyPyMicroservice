package com.nameplate;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Part;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.views.View;

import java.nio.charset.StandardCharsets;

@Controller
public class DataAnalysisController {
  private final PythonPool pool;

  public DataAnalysisController(PythonPool pool) {
    this.pool = pool;
  }

  @Get
  @View("index")
  public void index() {
  }

  @Post(value = "/data_analysis", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_PLAIN)
  @ExecuteOn(TaskExecutors.IO)
  String analyzeCsvMulti(StreamingFileUpload file, @Part("poolSize") String poolSizeString) {
    try {
      pool.setPoolSize(Integer.parseInt(poolSizeString));
      String csv = new String(file.asInputStream()
                                  .readAllBytes(), StandardCharsets.UTF_8);
      return pool.execute((c) -> {
        return c.eval("python", "import data_analysis; data_analysis")
                .invokeMember("mean", csv)
                .toString();
      });
    }
    catch (Exception e) {
      throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }
}
