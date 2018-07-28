package io.github.hcoona.parser.ini;

import io.github.hcoona.parser.IllegalContentFormatException;

public class IllegalIniFormatException extends IllegalContentFormatException {
  public IllegalIniFormatException(String message, long lineNumber) {
    super(message, lineNumber);
  }
}
