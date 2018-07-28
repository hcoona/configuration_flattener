package io.github.hcoona.cli;

import io.github.hcoona.file_processor.IFileProcessor;
import io.github.hcoona.file_processor.IniConfigurationFlattenProcessor;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

class Controller {
  private final FileSystem fileSystem;
  private final Charset charset;
  private final MessageDigest messageDigest;
  private final String inputFile;
  private final String outputFile;
  private final String outputManifestFile;
  private final Map<String, String> attributes;

  Controller(Properties config, FileSystem fileSystem,
      Map<String, String> attributes)
      throws NoSuchAlgorithmException {
    if (config == null) {
      throw new IllegalArgumentException("config cannot be null");
    }
    if (fileSystem == null) {
      throw new IllegalArgumentException("fileSystem cannot be null");
    }

    this.fileSystem = fileSystem;

    String charset = config.getProperty("charset", "utf-8");
    this.charset = Charset.forName(charset);

    String algorithm = config.getProperty("algorithm", "SHA-512");
    this.messageDigest = MessageDigest.getInstance(algorithm);

    String inputFile = config.getProperty("inputFile", "");
    if (inputFile.isEmpty()) {
      throw new IllegalArgumentException("inputFile cannot be empty");
    }
    this.inputFile = inputFile;

    String outputFile = config.getProperty("outputFile", "");
    if (outputFile.isEmpty()) {
      throw new IllegalArgumentException("outputFile cannot be empty");
    }
    this.outputFile = outputFile;
    this.outputManifestFile = outputFile + ".manifest";

    this.attributes = attributes;
  }

  public void run() throws Exception {
    IFileProcessor processor = new IniConfigurationFlattenProcessor(
        messageDigest, fileSystem, charset,
        inputFile, outputFile,
        attributes);

    Path outputManifestFilePath = fileSystem.getPath(outputManifestFile);
    String outputBaseDirectory = outputManifestFilePath.getParent().toString();
    if (Files.exists(outputManifestFilePath)) {
      Map<String, String> inputChecksum = new HashMap<>();
      Map<String, String> outputChecksum = new HashMap<>();
      parseManifestFile(outputManifestFilePath, inputChecksum, outputChecksum);

      if (processor.shouldSkipGeneration(
          outputBaseDirectory,
          inputChecksum, outputChecksum)) {
        return;
      }
    }

    Map<String, String> inputChecksum = new LinkedHashMap<>();
    Map<String, String> outputChecksum = new LinkedHashMap<>();
    processor.process(outputBaseDirectory, inputChecksum, outputChecksum);

    Files.write(outputManifestFilePath,
        formatManifestFile(inputChecksum, outputChecksum).getBytes(charset),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private String formatManifestFile(
      Map<String, String> inputChecksum,
      Map<String, String> outputChecksum) {
    // TODO: Implement it
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append("[").append("input_checksum").append("]")
        .append(System.lineSeparator());
    inputChecksum.forEach((key, value) -> {
      stringBuilder
          .append(key).append("=").append(value)
          .append(System.lineSeparator());
    });

    stringBuilder.append(System.lineSeparator());

    stringBuilder
        .append("[").append("output_checksum").append("]")
        .append(System.lineSeparator());
    outputChecksum.forEach((key, value) -> {
      stringBuilder
          .append(key).append("=").append(value)
          .append(System.lineSeparator());
    });

    return stringBuilder.toString();
  }

  private void parseManifestFile(Path outputManifestFilePath,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum) {
    // TODO: Implement it
  }
}
