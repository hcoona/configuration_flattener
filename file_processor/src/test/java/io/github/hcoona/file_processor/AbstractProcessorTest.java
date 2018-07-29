package io.github.hcoona.file_processor;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;

public class AbstractProcessorTest {
  private static class MockProcessor extends AbstractProcessor {
    boolean proceed = false;
    boolean shouldSkip = false;

    public MockProcessor(MessageDigest messageDigest,
        FileSystem fileSystem, Charset charset) {
      super(messageDigest, fileSystem, charset);
    }

    @Override
    protected void doProcess(String outputBaseDirectory) {
      Assert.assertFalse(proceed);
      proceed = true;
    }

    @Override
    protected void fillChecksum(String outputBaseDirectory,
        Map<String, String> inputChecksum, Map<String, String> outputChecksum) {
      Assert.assertTrue(proceed);
    }

    @Override
    public boolean shouldSkipGeneration(String outputBaseDirectory,
        Map<String, String> recordInputChecksum,
        Map<String, String> recordOutputChecksum) {
      return shouldSkip;
    }
  }

  private static final String algorithm = "SHA-512";
  private static final FileSystem fileSystem =
      Jimfs.newFileSystem(Configuration.unix());
  private static final Charset charset = Charset.defaultCharset();

  private static MockProcessor createMockProcessorInstance()
      throws NoSuchAlgorithmException {
    return new MockProcessor(MessageDigest.getInstance(algorithm),
        fileSystem, charset);
  }

  @Test
  public void testCreation() throws NoSuchAlgorithmException {
    MockProcessor processor = createMockProcessorInstance();

    Assert.assertEquals(algorithm, processor.digestAlgorithm);
    Assert.assertEquals(algorithm,
        processor.digestUtils.getMessageDigest().getAlgorithm());
    Assert.assertEquals(fileSystem, processor.fileSystem);
    Assert.assertEquals(charset, processor.charset);
  }

  @Test
  public void testProcess() throws Exception {
    MockProcessor processor = createMockProcessorInstance();
    processor.process("",
        Collections.singletonMap("", ""),
        Collections.singletonMap("", ""));
    Assert.assertTrue(processor.proceed);
  }

  @Test
  public void testCheckFileChecksum()
      throws NoSuchAlgorithmException, IOException {
    final String filename = "test";
    final String content = RandomStringUtils.randomAlphanumeric(20);
    final String checksum = new DigestUtils(algorithm).digestAsHex(content);
    Files.write(fileSystem.getPath(filename), content.getBytes(charset));

    MockProcessor processor = createMockProcessorInstance();

    Assert.assertTrue(
        processor.checkFileChecksum(fileSystem.getPath(filename), checksum));
  }

  @Test
  public void testCheckFileChecksumMismatch()
      throws NoSuchAlgorithmException, IOException {
    final String filename = "test";
    final String content = RandomStringUtils.randomAlphanumeric(20);
    final String checksum = new DigestUtils(algorithm).digestAsHex(content);
    Files.write(fileSystem.getPath(filename),
        (content + "\n").getBytes(charset));

    MockProcessor processor = createMockProcessorInstance();

    Assert.assertFalse(
        processor.checkFileChecksum(fileSystem.getPath(filename), checksum));
  }

  @Test
  public void testGetFileChecksum()
      throws NoSuchAlgorithmException, IOException {
    final String filename = "test";
    final String content = RandomStringUtils.randomAlphanumeric(20);
    final String checksum = new DigestUtils(algorithm).digestAsHex(content);
    Files.write(fileSystem.getPath(filename), content.getBytes(charset));

    MockProcessor processor = createMockProcessorInstance();

    Assert.assertEquals(checksum,
        processor.getFileChecksum(fileSystem.getPath(filename)));
  }
}
