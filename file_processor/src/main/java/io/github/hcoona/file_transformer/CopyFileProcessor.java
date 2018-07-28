package io.github.hcoona.file_processor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;

public class CopyFileProcessor extends SingleInputOutputProcessor {
  public CopyFileProcessor(MessageDigest messageDigest,
      FileSystem fileSystem, Charset charset,
      String inputFile, String outputFile) {
    super(messageDigest, fileSystem, charset, inputFile, outputFile);
  }

  @Override
  protected void doProcess(Path inputFilePath, Path outputFilePath)
      throws IOException {
    Files.copy(inputFilePath, outputFilePath,
        StandardCopyOption.REPLACE_EXISTING);
  }
}
