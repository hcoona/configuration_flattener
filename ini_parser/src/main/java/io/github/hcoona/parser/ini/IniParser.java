package io.github.hcoona.parser.ini;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.function.Supplier;

public class IniParser {
  private final Supplier<Map<String, Map<String, String>>> configMapSupplier;
  private final Supplier<Map<String, String>> optionMapSupplier;

  public IniParser(
      Supplier<Map<String, Map<String, String>>> configMapSupplier,
      Supplier<Map<String, String>> optionMapSupplier) {
    this.configMapSupplier = configMapSupplier;
    this.optionMapSupplier = optionMapSupplier;
  }

  public Map<String, Map<String, String>> parse(
      String filePath, Charset charset)
      throws IOException, IllegalIniFormatException {
    return parse(Paths.get(filePath), charset);
  }

  public Map<String, Map<String, String>> parse(
      Path filePath, Charset charset)
      throws IOException, IllegalIniFormatException {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(
            Files.newInputStream(filePath, StandardOpenOption.READ),
            charset))) {
      return parse(reader);
    }
  }

  public Map<String, Map<String, String>> parse(
      InputStream inputStream, Charset charset)
      throws IOException, IllegalIniFormatException {
    try (BufferedReader reader =
             new BufferedReader(new InputStreamReader(inputStream, charset))) {
      return parse(reader);
    }
  }

  public Map<String, Map<String, String>> parse(BufferedReader reader)
      throws IOException, IllegalIniFormatException {
    Map<String, Map<String, String>> result = configMapSupplier.get();
    int lineNumber = 0;

    String sectionHeader = null;
    Map<String, String> sectionOptions = null;
    while (true) {
      lineNumber++;
      String line = reader.readLine();
      if (line == null) {
        break;
      }

      line = line.trim();

      // Skip empty line
      if (line.isEmpty()) {
        continue;
      }

      // Skip comment line
      if (line.charAt(0) == '#' || line.charAt(0) == ';') {
        continue;
      }

      // Parse section header
      if (line.charAt(0) == '[') {
        if (line.charAt(line.length() - 1) == ']') {
          sectionHeader = line.substring(1, line.length() - 1);
          if (result.containsKey(sectionHeader)) {
            throw new IllegalIniFormatException(
                "Duplicated section header '" + sectionHeader + "'", lineNumber);
          } else {
            sectionOptions = optionMapSupplier.get();
            result.put(sectionHeader, sectionOptions);
          }
          continue;
        } else {
          throw new IllegalIniFormatException(
              "Expected ']' but got a '" + line.charAt(line.length() - 1) + "'",
              lineNumber);
        }
      }

      if (sectionOptions == null) {
        throw new IllegalIniFormatException(
            "Expected section header but got an option", lineNumber);
      }

      if (line.contains("=")) {
        String[] g = StringUtils.split(line, "=", 2);
        String key = g[0].trim();
        if (sectionOptions.containsKey(key)) {
          throw new IllegalIniFormatException(
              "Duplicated option key '" + key + "'", lineNumber);
        } else {
          String value = g.length == 2 ? g[1].trim() : "";
          sectionOptions.put(key, value);
        }
        continue;
      } else {
        throw new IllegalIniFormatException(
            "Expected '=' but got a '" + line.charAt(line.length() - 1) + "'",
            lineNumber);
      }
    }

    return result;
  }

  public String format(Map<String, Map<String, String>> config) {
    StringBuilder stringBuilder = new StringBuilder();
    format(stringBuilder, config);
    return stringBuilder.toString();
  }

  public void format(StringBuilder stringBuilder,
      Map<String, Map<String, String>> config) {
    config.forEach((sectionHeader, options) -> {
      stringBuilder.append("[").append(sectionHeader).append("]")
          .append(System.lineSeparator());
      options.forEach((key, value) -> {
        stringBuilder.append(key).append("=").append(value).append(System.lineSeparator());
      });
      stringBuilder.append(System.lineSeparator());
    });
  }
}
