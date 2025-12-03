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

  private void check(String name, int part) {
    maybeInput(name, part)
        .ifPresentOrElse(input -> checkInput(input, name, part), () -> noInput(name, part));
  }

  private Optional<Path> maybePath(String name) {
    return Optional.of(Path.of("inputs/day-%02d/%s".formatted(day, name))).filter(Files::exists);
  }

  private Optional<I> maybeInput(String name, int part) {
    return maybePath("%s.txt".formatted(name)).map(inputParser);
  }

  private Optional<R> maybeExpected(String name, int part) {
    return maybePath("%s.part%d.expected".formatted(name, part)).map(expectedParser);
  }

  private void checkInput(I input, String name, int part) {
    try {
      var start = nanoTime();
      var result = part == 1 ? part1(input) : part2(input);
      var time = " (%d ms)".formatted(round((nanoTime() - start) / 1e6));

      maybeExpected(name, part)
          .ifPresentOrElse(
              e -> showResult(result.equals(e), name, part, time),
              () -> showExpected(result, name, part, time));

    } catch (IOException ioe) {
      log("💣", name, part, ": %s".formatted(ioe));
    }
  }

  private void showResult(boolean ok, String name, int part, String time) {
    log(ok ? "✅" : "❌", name, part, time);
  }

  private void showExpected(R result, String name, int part, String time) {
    log("🟡", name, part, "%s%s".formatted(time, result));
  }

  private void noInput(String name, int part) {
    log("❓", name, part, ": No input");
  }

  private void log(String emoji, String name, int part, String detail) {
    IO.println("%s Day %d, part %d - %s%s".formatted(emoji, day, part, name, detail));
  }
}
