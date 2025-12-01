package net.berkeley.peterseibel;

import module java.base;

public class AdventOfCode {

  public static void main(String[] args) throws Exception {
    IO.println("Welcome to Advent of Code!");
    // Look at the inputs directory and find the most recent inputs.
    // Default to running solution for the latest input if there is one.

    Solution<Path, Integer> day1 = new Day01_SecretEntrance();
    IO.println(day1.part1(Path.of("inputs/day-01/puzzle-1.txt")));

  }
}
