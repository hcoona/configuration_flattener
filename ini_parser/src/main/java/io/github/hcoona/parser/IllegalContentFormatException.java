package io.github.hcoona.parser;

public class IllegalContentFormatException extends Exception {
  private final long lineNumber;

  public IllegalContentFormatException(String message, long lineNumber) {
    super(message + " lineNumber=" + lineNumber);
    this.lineNumber = lineNumber;
  }

  public long getLineNumber() {
    return lineNumber;
  }
}
