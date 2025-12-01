package net.berkeley.peterseibel;

import module java.base;

import static java.nio.file.Files.lines;
import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

public class Day01_SecretEntrance implements Solution<Path, Integer> {

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
      //IO.println("Rotating " + n);
      if (n > 0 && (p + n) >= 100) {
        count += 1 + (n - (100 - p)) / 100;
      } else if (n < 0 && (p + n) <= 0) {
        int first = p > 0 ? 1 : 0;
        count += first + (abs(n) - p) / 100;
      }
      // if n == 0 we didn't turn at all so it shouldn't change, I don't think
      p = floorMod(p + n, 100);
      //IO.println("p: %d; count: %d".formatted(p, count));
    }
    return count;
  }

  private int parse(String line) {
    int n = parseInt(line.substring(1));
    return line.startsWith("R") ? n : -n;
  }

}
