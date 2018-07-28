package io.github.hcoona.file_processor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Map;

public abstract class SingleInputOutputProcessor extends AbstractProcessor {
  protected final String inputFile;
  protected final String outputFile;

  public SingleInputOutputProcessor(MessageDigest messageDigest,
      FileSystem fileSystem, Charset charset,
      String inputFile, String outputFile) {
    super(messageDigest, fileSystem, charset);
    this.inputFile = inputFile;
    this.outputFile = outputFile;
  }

  @Override
  public boolean shouldSkipGeneration(String outputBaseDirectory,
      Map<String, String> recordInputChecksum,
      Map<String, String> recordOutputChecksum)
      throws IOException {
    Path inputFilePath = fileSystem.getPath(inputFile);
    Path outputFilePath = getOutputFilePath(outputBaseDirectory, outputFile);

    String recordInputFileChecksum =
        recordInputChecksum.get(getInputChecksumKey(outputFilePath));
    String recordOutputFileChecksum =
        recordOutputChecksum.get(getOutputChecksumKey(outputFilePath));

    return checkFileChecksum(inputFilePath, recordInputFileChecksum)
        && checkFileChecksum(outputFilePath, recordOutputFileChecksum);
  }

  @Override
  public void doProcess(String outputBaseDirectory)
      throws Exception {
    Path inputFilePath = fileSystem.getPath(inputFile);
    Path outputFilePath = getOutputFilePath(outputBaseDirectory, outputFile);

    doProcess(inputFilePath, outputFilePath);
  }

  @Override
  protected void fillChecksum(String outputBaseDirectory,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum)
      throws IOException {
    Path inputFilePath = fileSystem.getPath(inputFile);
    Path outputFilePath = getOutputFilePath(outputBaseDirectory, outputFile);

    inputChecksum.put(
        getInputChecksumKey(outputFilePath), getFileChecksum(inputFilePath));
    outputChecksum.put(
        getOutputChecksumKey(outputFilePath), getFileChecksum(outputFilePath));
  }

  protected abstract void doProcess(Path inputFilePath, Path outputFilePath)
      throws Exception;

  private String getInputChecksumKey(Path outputFilePath) {
    String outputFileName = outputFilePath.getFileName().toString();
    return outputFileName + ".inputFile." + digestAlgorithm;
  }

  private String getOutputChecksumKey(Path outputFilePath) {
    String outputFileName = outputFilePath.getFileName().toString();
    return outputFileName + "." + digestAlgorithm;
  }
}
