package net.berkeley.peterseibel;

import static java.lang.Math.*;

import module java.base;

public class Day03_Lobby extends Solution<List<String>, Long> {

  public Day03_Lobby() {
    super(3, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> banks) {
    return banks.stream().map(this::batteries).mapToLong(b -> maxJoltage(b, 2)).sum();
  }

  public Long part2(List<String> banks) {
    return banks.stream().map(this::batteries).mapToLong(b -> maxJoltage(b, 12)).sum();
  }

  private int[] batteries(String bank) {
    return Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
  }

  private long maxJoltage(int[] batteries, int n) {
    long j = 0L;
    int start = 0;
    while (n > 0) {
      int i = maxDigitIndex(batteries, start, n);
      j = j * 10 + batteries[i];
      start = i + 1;
      n--;
    }
    return j;
  }

  private int maxDigitIndex(int[] batteries, int start, int n) {
    int maxD = batteries[start];
    int maxI = start;
    for (int i = maxI; i <= batteries.length - n; i++) {
      if (batteries[i] > maxD) {
        maxD = batteries[i];
        maxI = i;
      }
      if (maxD == 9) break;
    }
    return maxI;
  }
}
