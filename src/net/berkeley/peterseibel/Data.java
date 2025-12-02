package net.berkeley.peterseibel;

import static java.nio.file.Files.*;

import module java.base;

class Data {

  public static String asString(Path p) {
    try {
      return readString(p);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public static List<String> asLines(Path p) {
    try {
      return lines(p).toList();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public static Integer asInteger(Path p) {
    return Integer.valueOf(asString(p).trim());
  }

  public static Long asLong(Path p) {
    return Long.valueOf(asString(p).trim());
  }
}
