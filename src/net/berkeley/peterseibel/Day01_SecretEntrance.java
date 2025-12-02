package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;
import static java.nio.file.Files.*;

import module java.base;

public class Day01_SecretEntrance extends Solution<Path, Integer> {

  public Day01_SecretEntrance() {
    super(1);
  }

  public Integer part1(Path input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : lines(input).mapToInt(this::parse).toArray()) {
      p = floorMod(p + n, 100);
      if (p == 0) count++;
    }
    return count;
  }

  public Integer part2(Path input) throws IOException {
    int p = 50;
    int count = 0;
    for (int n : lines(input).mapToInt(this::parse).toArray()) {
      count += abs((p == 0 ? 0 : (n > 0 ? p : p - 100)) + n) / 100;
      p = floorMod(p + n, 100);
    }
    return count;
  }

  public Optional<Path> input(String name, int part) throws IOException {
    return Util.maybeInputPath(name, day(), part);
  }

  public Optional<Integer> expected(String name, int part) throws IOException {
    return Util.maybeExpectedPath(name, day(), part).map(Util::asInteger);
  }

  private int parse(String line) {
    int n = parseInt(line.substring(1));
    return line.startsWith("R") ? n : -n;
  }
}
