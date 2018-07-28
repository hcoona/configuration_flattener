package io.github.hcoona.file_processor;

import java.io.IOException;
import java.util.Map;

public interface IFileProcessor {
  boolean shouldSkipGeneration(String outputBaseDirectory,
      Map<String, String> recordInputChecksum,
      Map<String, String> recordOutputChecksum) throws IOException;

  void process(String outputBaseDirectory,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum)
          throws Exception;
}
