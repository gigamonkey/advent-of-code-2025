package net.berkeley.peterseibel;

import static java.nio.file.Files.*;

import module java.base;

/**
 * Utility methods to be used as arguments to Solution constructor that get data
 * from a path and parse it into various useful forms.
 */
class Data {

  /**
   * Contents of path as a single String.
   */
  public static String asString(Path p) {
    try {
      return readString(p);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Contents of path as a list containing the lines in the file.
   */
  public static List<String> asLines(Path p) {
    try {
      return lines(p).toList();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Contents of path as a two-dimensional arary of one-letter Strings.
   */
  public static String[][] asCharacterGrid(Path p) {
    try {
      return lines(p).map(s -> s.split("")).toArray(String[][]::new);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Contents of path as a two-dimensional arary of Strings trimmed and then
   * split on whitespace.
   */
  public static String[][] asStringGrid(Path p) {
    try {
      return lines(p).map(s -> s.trim().split("\\s+")).toArray(String[][]::new);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Contents of path parsed as an int.
   */
  public static Integer asInteger(Path p) {
    return Integer.valueOf(asString(p).trim());
  }

  /**
   * Contents of path parsed as a long.
   */
  public static Long asLong(Path p) {
    return Long.valueOf(asString(p).trim());
  }
}
