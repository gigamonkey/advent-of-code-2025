package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

import module java.base;

public class Day01_SecretEntrance extends Solution<List<String>, Integer> {

  public Day01_SecretEntrance() {
    super(1, Data::asLines, Data::asInteger);
  }

  public Integer part1(List<String> input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : nums(input)) {
      p = floorMod(p + n, 100);
      if (p == 0) count++;
    }
    return count;
  }

  public Integer part2(List<String> input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : nums(input)) {
      count += abs((p == 0 ? 0 : (n > 0 ? p : p - 100)) + n) / 100;
      p = floorMod(p + n, 100);
    }
    return count;
  }

  private int[] nums(List<String> lines) {
    return lines.stream().mapToInt(this::parse).toArray();
  }

  private int parse(String line) {
    return (line.startsWith("R") ? 1 : -1) * parseInt(line.substring(1));
  }
}
