package net.berkeley.peterseibel;

import static java.nio.file.Files.*;

import module java.base;

public abstract class Solution<I, R> {

  private final int day;

  public Solution(int day) {
    this.day = day;
  }

  /**
   * Solve part 1.
   */
  public abstract R part1(I input) throws IOException;

  /**
   * Solve part 2. (Default implementation so we don't have to write it right away.)
   */
  public R part2(I input) throws IOException {
    throw new Error("NYI");
  }

  /**
   * Get the input for test or puzzle from its Path.
   */
  protected abstract I input(Path p);

  /**
   * Get the expected value for test or puzzle from its Path.
   */
  protected abstract R expected(Path p);

  /**
   * Check all the parts.
   */
  public void check() {
    check("test", 1);
    check("puzzle", 1);
    check("test", 2);
    check("puzzle", 2);
  }

  protected Optional<Path> maybeInputPath(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.txt".formatted(day, name));
    return Optional.of(p).filter(Files::exists);
  }

  protected Optional<Path> maybeExpectedPath(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.part%d.expected".formatted(day, name, part));
    return Optional.of(p).filter(Files::exists);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Utility methods for extracting data from files.

  protected String asString(Path p) {
    try {
      return readString(p);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  protected List<String> asLines(Path p) {
    try {
      return lines(p).toList();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  protected Integer asInteger(Path p) {
    return Integer.valueOf(asString(p).trim());
  }

  protected Long asLong(Path p) {
    return Long.valueOf(asString(p).trim());
  }

  //////////////////////////////////////////////////////////////////////////////
  // Actual checking code

  private void check(String name, int part) {
    maybeInputPath(name, part)
        .map(this::input)
        .ifPresentOrElse(in -> checkInput(in, name, part), () -> noInput(name, part));
  }

  private void checkInput(I input, String name, int part) {
    try {
      var result = result(part, input);
      maybeExpectedPath(name, part)
          .map(this::expected)
          .ifPresentOrElse(
              exp -> checkExpected(result, exp, name, part),
              () -> showExpected(result, name, part));
    } catch (IOException ioe) {
      IO.println("❌ Day %d, part %d - %s: Exception %s".formatted(day, part, name, ioe));
    }
  }

  private void checkExpected(R result, R expected, String name, int part) {
    var ok = result.equals(expected);
    var emoji = ok ? "✅" : "❌";
    var label = ok ? "pass" : "fail";
    IO.println("%s Day %d, part %d - %s: %s".formatted(emoji, day, part, name, label));
  }
  ;

  private void showExpected(R result, String name, int part) {
    IO.println("🟡 Day %d, part %d - %s: %s".formatted(day, part, name, result));
  }

  private void noInput(String name, int part) {
    IO.println("❓Day %d, part %d - %s: No input".formatted(day, part, name));
  }

  private R result(int part, I input) throws IOException {
    return part == 1 ? part1(input) : part2(input);
  }
}
