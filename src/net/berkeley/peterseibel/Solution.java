package net.berkeley.peterseibel;

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

  public int day() {
    return day;
  }

  public abstract Optional<R> expected(String name, int part) throws IOException;

  public abstract Optional<T> input(String name, int part) throws IOException;

  public void check(String name, int part) throws Exception {
    var input = input(name, part);
    var expected = expected(name, part);

    if (input.isPresent()) {
      if (expected.isPresent()) {
        var ok =
            part == 1
                ? part1(input.get()).equals(expected.get())
                : part2(input.get()).equals(expected.get());
        IO.println("Day %d, part %d - %s: %s".formatted(day, part, name, ok ? "pass" : "fail"));
      } else {
        var r = part == 1 ? part1(input.get()) : part2(input.get());
        IO.println("Day %d, part %d - %s: %s".formatted(day, part, name, r));
      }
    } else {
      IO.println("Day %d, part %d - No input".formatted(day, part));
    }
  }

  public void checkAll() throws Exception {
    check("test", 1);
    check("puzzle", 1);
    check("test", 2);
    check("puzzle", 2);
  }
}
