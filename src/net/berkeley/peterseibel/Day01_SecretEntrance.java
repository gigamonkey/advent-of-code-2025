package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;
import static java.nio.file.Files.*;

import module java.base;

public class Day01_SecretEntrance extends Solution<Stream<String>, Integer> {

  public Day01_SecretEntrance() {
    super(1);
  }

  public Integer part1(Stream<String> input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : nums(input)) {
      p = floorMod(p + n, 100);
      if (p == 0) count++;
    }
    return count;
  }

  public Integer part2(Stream<String> input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : nums(input)) {
      count += abs((p == 0 ? 0 : (n > 0 ? p : p - 100)) + n) / 100;
      p = floorMod(p + n, 100);
    }
    return count;
  }

  private int[] nums(Stream<String> lines) {
    return lines.mapToInt(this::parse).toArray();
  }

  private int parse(String line) {
    int n = parseInt(line.substring(1));
    return line.startsWith("R") ? n : -n;
  }

  protected Stream<String> input(Path p) {
    return asLines(p);
  }

  protected Integer expected(Path p) {
    return asInteger(p);
  }
}
