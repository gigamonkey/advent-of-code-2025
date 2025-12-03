package net.berkeley.peterseibel;

import static java.lang.Math.*;

import module java.base;

public class Day03_Lobby extends Solution<List<String>, Long> {

  public Day03_Lobby() {
    super(3, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> banks) {
    return sumJoltages(banks, 2);
  }

  public Long part2(List<String> banks) {
    return sumJoltages(banks, 12);
  }

  private long sumJoltages(List<String> banks, int n) {
    return banks.stream().map(this::batteries).mapToLong(b -> maxJoltage(b, n)).sum();
  }

  private int[] batteries(String bank) {
    return Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
  }

  private long maxJoltage(int[] batteries, int n) {
    long j = 0L;
    int start = 0;
    while (n > 0) {
      int i = maxDigitIndex(batteries, start, n);
      j *= 10;
      j += batteries[i];
      start = i + 1;
      n--;
    }
    return j;
  }

  private int maxDigitIndex(int[] batteries, int start, int n) {
    int maxDigit = batteries[start];
    int maxIndex = start;
    for (int i = maxIndex; i <= batteries.length - n; i++) {
      if (batteries[i] > maxDigit) {
        maxDigit = batteries[i];
        maxIndex = i;
      }
      if (maxDigit == 9) break;
    }
    return maxIndex;
  }
}
