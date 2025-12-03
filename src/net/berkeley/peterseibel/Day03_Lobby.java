package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;

import module java.base;

public class Day03_Lobby extends Solution<List<String>, Integer> {

  public Day03_Lobby() {
    super(3, Data::asLines, Data::asInteger);
  }

  public Integer part1(List<String> banks) {
    return banks.stream().mapToInt(this::maxJoltage).sum();
  }

  public Long part2(String input) {
  }


  private int maxJoltage(String bank) {
    int[] batteries = Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
    int max = 0;
    for (int i = 0; i < batteries.length - 1; i++) {
      for (int j = i + 1; j < batteries.length; j++) {
        max = max(max, batteries[i] * 10 + batteries[j]);
      }
    }
    return max;
  }
}
