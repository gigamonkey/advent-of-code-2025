package net.berkeley.peterseibel;

import module java.base;

import static java.nio.file.Files.*;

public class Util {

  private static final Path inputs = Path.of("inputs");

  public static Optional<Path> maybeInputPath(String name, int day, int part) {
    Path p = inputPath(name, day, part);
    return exists(p) ? Optional.of(p) : Optional.empty();
  }

  public static Optional<Path> maybeExpectedPath(String name, int day, int part) {
    Path p = expectedPath(name, day, part);
    return exists(p) ? Optional.of(p) : Optional.empty();
  }

  public static Path inputPath(String name, int day, int part) {
    return Path.of("inputs/day-%02d/%s.txt".formatted(day, name));
  }

  public static Path expectedPath(String name, int day, int part) {
    return Path.of("inputs/day-%02d/%s.part%d.expected".formatted(day, name, part));
  }

  public static String expectedString(String name, int day, int part) {
    try {
      return readString(expectedPath(name, day, part));
    } catch (IOException ioe) {
      throw new SolverException(ioe);
    }
  }

  public static String asString(Path p) {
    try {
      return readString(p);
    } catch (IOException ioe) {
      throw new SolverException(ioe);
    }
  }

  public static Integer asInteger(Path p) {
    return Integer.valueOf(asString(p).trim());
  }


  public static Integer expectedInteger(String name, int day, int part) {
    return Integer.valueOf(expectedString(name, day, part).trim());
  }
}
