package net.berkeley.peterseibel;

import static java.lang.Math.round;
import static java.lang.System.nanoTime;

import module java.base;

public abstract class Solution<I, R> {

  private static final boolean STOP = true;

  private static final List<String> names = List.of("test", "puzzle");
  private static final List<Integer> parts = List.of(1, 2);

  private final int day;
  private final Function<Path, I> inputParser;
  private final Function<Path, R> expectedParser;
  private final List<Checker> checkers;

  public Solution(int day, Function<Path, I> inputParser, Function<Path, R> expectedParser) {
    this.day = day;
    this.inputParser = inputParser;
    this.expectedParser = expectedParser;
    this.checkers = cross(parts, names, Checker::new);
  }

  /**
   * Solve part 1.
   */
  public abstract R part1(I input) throws IOException;

  /**
   * Solve part 2.
   */
  public abstract R part2(I input) throws IOException;

  /**
   * Check all the parts we have inputs for, stopping after the first step that
   * doesn't pass.
   */
  public final void check() {
    for (var c : checkers) {
      var r = c.check();
      IO.println(r.message());
      if (!r.ok()) break;
    }
  }

  private static <T, U, R> List<R> cross(List<T> ts, List<U> us, BiFunction<T, U, R> fn) {
    return ts.stream().flatMap(t -> us.stream().map(u -> fn.apply(t, u))).toList();
  }

  //////////////////////////////////////////////////////////////////////////////
  // Actual checking code

  private class Checker {

    private record Result(boolean ok, String message) {}

    private final int part;
    private final String name;

    Checker(int part, String name) {
      this.part = part;
      this.name = name;
    }

    public Result check() {
      return maybeInput(name, part).map(this::checkInput).orElseGet(() -> stop("❓", ": No input"));
    }

    private Result checkInput(I input) {
      try {
        var start = nanoTime();
        var result = part == 1 ? part1(input) : part2(input);
        var time = " (%d ms)".formatted(round((nanoTime() - start) / 1e6));

        return maybeExpected(name, part)
            .map(expected -> testResult(result, expected, time))
            .orElseGet(() -> stop("🟡", "%s %s".formatted(time, result)));

      } catch (IOException ioe) {
        return stop("💣", ": %s".formatted(ioe));
      }
    }

    private Result testResult(R result, R expected, String time) {
      return result.equals(expected)
          ? ok("✅", time)
          : stop("❌", "%s: got: %s; expected: %s ".formatted(time, result, expected));
    }

    private Result ok(String emoji, String detail) {
      return new Result(true, msg(emoji, detail));
    }

    private Result stop(String emoji, String detail) {
      return new Result(!STOP, msg(emoji, detail));
    }

    private String msg(String emoji, String detail) {
      return "%s Day %d, part %d - %s%s".formatted(emoji, day, part, name, detail);
    }

    private Optional<Path> maybePath(String name) {
      return Optional.of(Path.of("inputs/day-%02d/%s".formatted(day, name))).filter(Files::exists);
    }

    private Optional<I> maybeInput(String name, int part) {
      return maybePath("%s.%d.override.data".formatted(name, part))
        .or(() ->  part == 2 ? maybePath("%s.2.data".formatted(name)) : Optional.empty())
        .or(() -> maybePath("%s.data".formatted(name)))
        .map(x -> { IO.println(x); return x; })
        .map(inputParser);
    }

    private Optional<R> maybeExpected(String name, int part) {
      return maybePath("%s.part%d.expected".formatted(name, part)).map(expectedParser);
    }
  }
}
