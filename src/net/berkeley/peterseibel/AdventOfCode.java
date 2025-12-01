package net.berkeley.peterseibel;

import module java.base;

import static net.berkeley.peterseibel.Util.*;

public class AdventOfCode {

  public static <T, R> void check(Solution<T, R> solution, String name, int part) throws Exception {
    int day = solution.day();
    var input = solution.input(name, part);
    var expected = solution.expected(name, part);

    var ok = part == 1
      ? solution.part1(input).equals(expected)
      : solution.part2(input).equals(expected);
    IO.println("Day %d, part %d - %s: %s".formatted(day, part, name, ok ? "pass" : "fail"));
  }

  public static void main(String[] args) throws Exception {
    IO.println("Welcome to Advent of Code!");

    Solution<Path, Integer> day1 = new Day01_SecretEntrance();

    check(day1, "test", 1);
    check(day1, "test", 2);
    check(day1, "puzzle", 1);
    check(day1, "puzzle", 2);

  }
}
