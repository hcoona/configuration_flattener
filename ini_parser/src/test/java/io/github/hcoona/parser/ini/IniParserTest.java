package io.github.hcoona.parser.ini;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

public class IniParserTest {

  @Test
  public void testParse1() throws IOException, IllegalIniFormatException {
    IniParser parser = new IniParser(LinkedHashMap::new, LinkedHashMap::new);
    Map<String, Map<String, String>> options;

    ClassLoader classLoader = IniParserTest.class.getClassLoader();
    try (InputStream is = classLoader.getResourceAsStream("test_case1.ini")) {
      options = parser.parse(is, Charset.defaultCharset());
    }

    Assert.assertEquals(2, options.size());
    options.forEach((key, value) -> {
      Assert.assertEquals(2, value.size());
    });

    Assert.assertEquals("rm1,rm2",
        options.get("yarn-site").get("yarn.resourcemanager.ha.rm-ids"));
  }
}