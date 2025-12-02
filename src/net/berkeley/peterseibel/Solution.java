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
  public abstract R part1(T input) throws Exception;

  /**
   * Solve part 2.
   */
  public R part2(T input) throws Exception {
    throw new Error("NYI");
  }

  public abstract Optional<R> expected(String name, int part) throws IOException;

  public abstract Optional<T> input(String name, int part) throws IOException;

  protected Optional<Path> maybeInputPath(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.txt".formatted(day, name));
    return exists(p) ? Optional.of(p) : Optional.empty();
  }

  protected Optional<Path> maybeExpectedPath(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.part%d.expected".formatted(day, name, part));
    return exists(p) ? Optional.of(p) : Optional.empty();
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

  public void check() throws Exception {
    check("test", 1);
    check("puzzle", 1);
    check("test", 2);
    check("puzzle", 2);
  }

  private void check(String name, int part) throws Exception {
    var input = input(name, part);
    var expected = expected(name, part);

    if (input.isPresent()) {
      if (expected.isPresent()) {
        var ok = checkExpected(part, input.get(), expected.get());
        var emoji = ok ? "✅" : "❌";
        var label = ok ? "pass" : "fail";
        IO.println("%s Day %d, part %d - %s: %s".formatted(emoji, day, part, name, label));
      } else {
        var r = part == 1 ? part1(input.get()) : part2(input.get());
        IO.println("🟡 Day %d, part %d - %s: %s".formatted(day, part, name, r));
      }
    } else {
      IO.println("❓Day %d, part %d - No input".formatted(day, part));
    }
  }

  private boolean checkExpected(int part, T input, R expected) throws Exception {
    return part == 1
      ? part1(input).equals(expected)
      : part2(input).equals(expected);
  }

}
