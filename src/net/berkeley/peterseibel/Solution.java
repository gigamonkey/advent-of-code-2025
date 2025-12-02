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

  protected String asString(Path p) {
    try {
      return readString(p);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  protected Stream<String> asLines(Path p) {
    try {
      return lines(p);
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

  private void check(String name, int part) {
    var input = maybeInputPath(name, part).map(this::input);
    var expected = maybeExpectedPath(name, part).map(this::expected);

    if (input.isPresent()) {
      try {
        var r = result(part, input.get());
        if (expected.isPresent()) {
          var ok = r.equals(expected.get());
          var emoji = ok ? "✅" : "❌";
          var label = ok ? "pass" : "fail";
          IO.println("%s Day %d, part %d - %s: %s".formatted(emoji, day, part, name, label));
        } else {
          IO.println("🟡 Day %d, part %d - %s: %s".formatted(day, part, name, r));
        }
      } catch (IOException ioe) {
        IO.println("❌ Day %d, part %d - %s: Exception %s".formatted(day, part, name, ioe));
      }
    } else {
      IO.println("❓Day %d, part %d - No input".formatted(day, part));
    }
  }

  private R result(int part, I input) throws IOException {
    return part == 1 ? part1(input) : part2(input);
  }
}
