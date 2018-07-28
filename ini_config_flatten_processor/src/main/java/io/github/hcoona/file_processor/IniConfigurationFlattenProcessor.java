package io.github.hcoona.file_processor;

import io.github.hcoona.config_flattener.ConfigurationFlattener;
import io.github.hcoona.parser.ini.IniParser;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class IniConfigurationFlattenProcessor extends SingleInputOutputProcessor {
  private final Map<String, String> attributes;

  public IniConfigurationFlattenProcessor(MessageDigest messageDigest,
      FileSystem fileSystem, Charset charset,
      String inputFile, String outputFile,
      Map<String, String> attributes) {
    super(messageDigest, fileSystem, charset, inputFile, outputFile);
    this.attributes = attributes;
  }

  @Override
  public boolean shouldSkipGeneration(String outputBaseDirectory,
      Map<String, String> recordInputChecksum,
      Map<String, String> recordOutputChecksum) throws IOException {
    boolean shouldSkipGeneration = super.shouldSkipGeneration(
        outputBaseDirectory,
        recordInputChecksum, recordOutputChecksum);
    if (!shouldSkipGeneration) {
      return false;
    }

    Path outputFilePath = getOutputFilePath(outputBaseDirectory, outputFile);
    String recordedAttributesChecksum =
        recordInputChecksum.get(getAttributeChecksumKey(outputFilePath));
    return recordedAttributesChecksum != null
        && recordedAttributesChecksum.equals(getAttributeChecksumValue());
  }

  @Override
  protected void fillChecksum(String outputBaseDirectory,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum)
      throws IOException {
    super.fillChecksum(outputBaseDirectory, inputChecksum, outputChecksum);

    Path outputFilePath = getOutputFilePath(outputBaseDirectory, outputFile);
    inputChecksum.put(
        getAttributeChecksumKey(outputFilePath),
        getAttributeChecksumValue());
  }

  @Override
  protected void doProcess(Path inputFilePath, Path outputFilePath)
      throws Exception {
    IniParser parser = new IniParser(
        LinkedCaseInsensitiveMap::new, LinkedCaseInsensitiveMap::new);
    ConfigurationFlattener flattener = new ConfigurationFlattener(
        attributes, LinkedCaseInsensitiveMap::new);

    Map<String, Map<String, String>> config =
        parser.parse(inputFilePath, charset);
    Map<String, Map<String, String>> flattenedConfig =
        config.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, entry -> flattener.flatten(entry.getValue()),
                (key, value) -> {
                  throw new IllegalStateException("Duplicate key " + key);
                },
                LinkedCaseInsensitiveMap::new));

    Files.write(outputFilePath,
        parser.format(flattenedConfig).getBytes(charset),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING);
  }

  private String getAttributeChecksumKey(Path outputFilePath) {
    String outputFileName = outputFilePath.getFileName().toString();
    return outputFileName + ".attributes." + digestAlgorithm;
  }

  private String getAttributeChecksumValue() {
    String attributesStr = this.attributes.entrySet().stream()
        .sorted(Comparator.comparing(entry -> entry.getKey().toLowerCase()))
        .map(entry ->
            entry.getKey().toLowerCase() + "=" + entry.getValue().toLowerCase())
        .collect(Collectors.toList())
        .toString();

    return digestUtils.digestAsHex(attributesStr);
  }
}