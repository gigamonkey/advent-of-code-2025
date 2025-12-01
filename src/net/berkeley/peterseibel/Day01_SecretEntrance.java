package net.berkeley.peterseibel;

import module java.base;

import static java.nio.file.Files.lines;
import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

public class Day01_SecretEntrance implements Solution<Path, Integer> {

  public int day() { return 1; }

  public Integer part1(Path input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : lines(input).mapToInt(this::parse).toArray()) {
      p += n;
      if (floorMod(p, 100) == 0) count++;
    }
    return count;
  }

  public Integer part2(Path input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : lines(input).mapToInt(this::parse).toArray()) {
      if (n > 0 && (p + n) >= 100) {
        count += 1 + (n - (100 - p)) / 100;
      } else if (n < 0 && (p + n) <= 0) {
        int first = p > 0 ? 1 : 0;
        count += first + (abs(n) - p) / 100;
      }
      p = floorMod(p + n, 100);
    }
    return count;
  }

  public Path input(String name, int part) {
    return Util.inputPath(name, 1, part);
  }

  public Integer expected(String name, int part) {
    return Util.expectedInteger(name, 1, part);
  }

  private int parse(String line) {
    int n = parseInt(line.substring(1));
    return line.startsWith("R") ? n : -n;
  }

}
