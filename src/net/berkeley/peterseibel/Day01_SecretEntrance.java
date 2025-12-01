package net.berkeley.peterseibel;

import module java.base;

import static java.nio.file.Files.lines;
import static java.lang.Integer.parseInt;
import static java.lang.Math.floorMod;

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


  private int parse(String line) {
    int n = parseInt(line.substring(1));
    return line.startsWith("R") ? n : -n;
  }

}
