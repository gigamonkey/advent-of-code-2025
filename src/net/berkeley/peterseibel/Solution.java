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

  public int day() { return day; }

  public abstract R expected(String name, int part);

  public abstract T input(String name, int part);

  public void check(String name, int part) throws Exception {
    var input = input(name, part);
    var expected = expected(name, part);
    var ok = part == 1 ? part1(input).equals(expected) : part2(input).equals(expected);
    IO.println("Day %d, part %d - %s: %s".formatted(day, part, name, ok ? "pass" : "fail"));
  }

  public void checkAll() throws Exception {
    check("test", 1);
    check("puzzle", 1);
    check("test", 2);
    check("puzzle", 2);
  }

}
