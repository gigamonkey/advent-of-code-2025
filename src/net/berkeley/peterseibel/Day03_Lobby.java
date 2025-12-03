package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;

import module java.base;

public class Day03_Lobby extends Solution<List<String>, Long> {

  public Day03_Lobby() {
    super(3, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> banks) {
    return banks.stream().mapToLong(this::maxJoltage2).sum();
  }

  public Long part2(List<String> banks) {
    return banks.stream().mapToLong(this::maxJoltageFaster).sum();
  }

  private long maxJoltage2(String bank) {
    int[] batteries = Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
    return maxJoltage(batteries, 0, 0, 2);
  }

  private long maxJoltage12(String bank) {
    int[] batteries = Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
    return maxJoltage(batteries, 0, 0, 12);
  }

  // Biggest digit less than n from the end.
  //

  private long maxJoltage(int[] batteries, long acc, int start, int n) {
    if (n == 0) {
      return acc;
    }

    if (start == batteries.length) {
      return -1;
    }

    long a = maxJoltage(batteries, acc * 10 + batteries[start], start + 1, n - 1);
    long b = maxJoltage(batteries, acc, start + 1, n);

    return max(a, b);
  }

  private long maxJoltageFaster(String bank) {
    int[] batteries = Arrays.stream(bank.split("")).mapToInt(Integer::parseInt).toArray();
    int start = 0;
    int n = 12;
    long j = 0L;
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
