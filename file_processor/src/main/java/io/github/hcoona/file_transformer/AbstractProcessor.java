package io.github.hcoona.file_processor;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Map;

public abstract class AbstractProcessor implements IFileProcessor {
  protected final DigestUtils digestUtils;
  protected final String digestAlgorithm;
  protected final FileSystem fileSystem;
  protected final Charset charset;

  public AbstractProcessor(MessageDigest messageDigest,
      FileSystem fileSystem, Charset charset) {
    if (messageDigest == null) {
      throw new IllegalArgumentException("messageDigest cannot be null");
    }
    if (fileSystem == null) {
      throw new IllegalArgumentException("fileSystem cannot be null");
    }
    if (charset == null) {
      throw new IllegalArgumentException("charset cannot be null");
    }

    this.digestUtils = new DigestUtils(messageDigest);
    digestAlgorithm = messageDigest.getAlgorithm();
    this.fileSystem = fileSystem;
    this.charset = charset;
  }

  @Override
  public final void process(String outputBaseDirectory,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum)
      throws Exception {
    doProcess(outputBaseDirectory);
    fillChecksum(outputBaseDirectory, inputChecksum, outputChecksum);
  }

  protected abstract void doProcess(String outputBaseDirectory)
      throws Exception;

  protected abstract void fillChecksum(String outputBaseDirectory,
      Map<String, String> inputChecksum, Map<String, String> outputChecksum)
      throws Exception;

  protected boolean checkFileChecksum(Path path, String recordFileChecksum)
      throws IOException {
    if (recordFileChecksum == null) {
      return false;
    } else {
      String realFileChecksum;
      try (InputStream is = Files.newInputStream(
          path, StandardOpenOption.READ)) {
        realFileChecksum = digestUtils.digestAsHex(is);
      }
      return realFileChecksum.equals(recordFileChecksum);
    }
  }

  protected Path getOutputFilePath(
      String outputBaseDirectory, String outputFile) {
    return fileSystem.getPath(outputBaseDirectory).resolve(outputFile);
  }

  protected String getFileChecksum(Path path) throws IOException {
    try (InputStream is = Files.newInputStream(path, StandardOpenOption.READ)) {
      return digestUtils.digestAsHex(is);
    }
  }
}
