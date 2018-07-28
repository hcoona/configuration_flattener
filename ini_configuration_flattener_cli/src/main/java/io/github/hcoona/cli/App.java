package io.github.hcoona.cli;

import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.Properties;

public class App {
  public static void main(String[] args) {
    Properties config = new Properties(System.getProperties());

    try {
      Controller controller = new Controller(
          config, FileSystems.getDefault(),
          Collections.singletonMap("Region", "cn"));
      controller.run();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
