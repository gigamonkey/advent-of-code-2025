package net.berkeley.peterseibel;

import module java.base;

public class AdventOfCode {

  public static void main(String[] args) throws Exception {
    IO.println("Welcome to Advent of Code!");

    Solution<Path, Integer> day1 = new Day01_SecretEntrance();
    //IO.println(day1.part1(Path.of("inputs/day-01/puzzle-1.txt")));
    IO.println(day1.part2(Path.of("inputs/day-01/test-1.txt")));
    IO.println(day1.part2(Path.of("inputs/day-01/puzzle-1.txt")));

  }
}
