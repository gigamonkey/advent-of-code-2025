package net.berkeley.peterseibel;

import static java.nio.file.Files.*;

import module java.base;

public abstract class Solution<T, R> {

  private final int day;

  public Solution(int day) {
    this.day = day;
  }

  /**
   * Solve part 1.
   */
  public abstract R part1(T input) throws IOException;

  /**
   * Solve part 2.
   */
  public R part2(T input) throws IOException {
    throw new Error("NYI");
  }

  /**
   * Get the input for test or puzzle, part 1 or 2.
   */
  public abstract Optional<T> input(String name, int part);

  /**
   * Get the expected value for test or puzzle, part 1 or 2.
   */
  public abstract Optional<R> expected(String name, int part);

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

  protected Integer asInteger(Path p) {
    return Integer.valueOf(asString(p).trim());
  }

  protected Long asLong(Path p) {
    return Long.valueOf(asString(p).trim());
  }

  private void check(String name, int part) {
    var input = input(name, part);
    var expected = expected(name, part);

    if (input.isPresent()) {
      try {
        if (expected.isPresent()) {
          var ok = checkExpected(part, input.get(), expected.get());
          var emoji = ok ? "✅" : "❌";
          var label = ok ? "pass" : "fail";
          IO.println("%s Day %d, part %d - %s: %s".formatted(emoji, day, part, name, label));
        } else {
          var r = part == 1 ? part1(input.get()) : part2(input.get());
          IO.println("🟡 Day %d, part %d - %s: %s".formatted(day, part, name, r));
        }
      } catch (IOException ioe) {
        IO.println("❌ Day %d, part %d - %s: Exception %s".formatted(day, part, name, ioe));
      }
    } else {
      IO.println("❓Day %d, part %d - No input".formatted(day, part));
    }
  }

  private boolean checkExpected(int part, T input, R expected) throws IOException {
    return (part == 1 ? part1(input) : part2(input)).equals(expected);
  }
}
