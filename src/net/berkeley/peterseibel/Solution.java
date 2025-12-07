package net.berkeley.peterseibel;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;

import module java.base;

public abstract class Solution<I, R> {

  private record Result(boolean ok, String message) {}

  private class Checker {

    private final String name;
    private final int part;

    Checker(String name, int part) {
      this.name = name;
      this.part = part;
    }

    public Result check() {
      return maybeInput(name, part).map(this::checkInput).orElseGet(this::noInput);
    }

    private Result checkInput(I input) {
      try {
        var start = nanoTime();
        var result = part == 1 ? part1(input) : part2(input);
        var time = " (%d ms)".formatted(round((nanoTime() - start) / 1e6));

        return maybeExpected(name, part)
            .map(expected -> testResult(result, expected, time))
            .orElseGet(() -> showResult(result, time));

      } catch (IOException ioe) {
        return new Result(false, msg("💣", ": %s".formatted(ioe)));
      }
    }

    private Result testResult(R result, R expected, String time) {
      if (result.equals(expected)) {
        return new Result(true, msg("✅", time));
      } else {
        return new Result(
            false, msg("❌", "%s: got: %s; expected: %s ".formatted(time, result, expected)));
      }
    }

    private Result showResult(R result, String time) {
      return new Result(false, msg("🟡", "%s %s".formatted(time, result)));
    }

    private Result noInput() {
      return new Result(false, msg("❓", ": No input"));
    }

    private String msg(String emoji, String detail) {
      return "%s Day %d, part %d - %s%s".formatted(emoji, day, part, name, detail);
    }
  }

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
    List<Checker> checkers =
        List.of(
            new Checker("test", 1),
            new Checker("puzzle", 1),
            new Checker("test", 2),
            new Checker("puzzle", 2));
    for (var c : checkers) {
      var r = c.check();
      IO.println(r.message());
      if (!r.ok()) break;
    }
  }

  //////////////////////////////////////////////////////////////////////////////
  // Actual checking code

  private Optional<Path> maybePath(String name) {
    return Optional.of(Path.of("inputs/day-%02d/%s".formatted(day, name))).filter(Files::exists);
  }

  private Optional<I> maybeInput(String name, int part) {
    return maybePath("%s.data".formatted(name)).map(inputParser);
  }

  private Optional<R> maybeExpected(String name, int part) {
    return maybePath("%s.part%d.expected".formatted(name, part)).map(expectedParser);
  }
}
