package net.berkeley.peterseibel;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;

import module java.base;

public abstract class Solution<I, R> {

  private final int day;
  private final Function<Path, I> inputParser;
  private final Function<Path, R> expectedParser;

  public Solution(int day, Function<Path, I> inputParser, Function<Path, R> expectedParser) {
    this.day = day;
    this.inputParser = inputParser;
    this.expectedParser = expectedParser;
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
   * Check all the parts we have inputs for.
   */
  public final void check() {
    check("test", 1);
    check("puzzle", 1);
    check("test", 2);
    check("puzzle", 2);
  }

  //////////////////////////////////////////////////////////////////////////////
  // Actual checking code

  private Optional<I> maybeInput(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.txt".formatted(day, name));
    return Optional.of(p).filter(Files::exists).map(inputParser);
  }

  private Optional<R> maybeExpected(String name, int part) {
    Path p = Path.of("inputs/day-%02d/%s.part%d.expected".formatted(day, name, part));
    return Optional.of(p).filter(Files::exists).map(expectedParser);
  }

  private void check(String name, int part) {
    maybeInput(name, part)
        .ifPresentOrElse(input -> checkInput(input, name, part), () -> noInput(name, part));
  }

  private void checkInput(I input, String name, int part) {
    try {
      var start = nanoTime();
      var result = part == 1 ? part1(input) : part2(input);
      var time = "(%d ms)".formatted(round((nanoTime() - start) / 1e6));

      maybeExpected(name, part)
          .ifPresentOrElse(
              expected -> showResult(result.equals(expected), name, part, time),
              () -> showExpected(result, name, part, time));
    } catch (IOException ioe) {
      IO.println("❌ Day %d, part %d - %s: Exception %s".formatted(day, part, name, ioe));
    }
  }

  private void showResult(boolean ok, String name, int part, String time) {
    IO.println("%s Day %d, part %d - %s %s".formatted(ok ? "✅" : "❌", day, part, name, time));
  }

  private void showExpected(R result, String name, int part, String time) {
    IO.println("🟡 Day %d, part %d - %s %s: %s".formatted(day, part, name, time, result));
  }

  private void noInput(String name, int part) {
    IO.println("❓Day %d, part %d - %s: No input".formatted(day, part, name));
  }
}
