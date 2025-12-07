package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

import module java.base;

public class Day07_Laboratories extends Solution<List<String>, Long> {

  public Day07_Laboratories() {
    super(7, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return solveBoth(lines).splits();
  }

  public Long part2(List<String> lines) {
    return solveBoth(lines).paths();
  }

  record Result(long splits, long paths) {}

  // Hat tip to Brad who pointed out that you can solve both parts in one pass
  public Result solveBoth(List<String> lines) {
    long count = 0;
    long[] paths = startPaths(lines.getFirst());
    for (String line : lines.subList(1, lines.size())) {
      for (int i = 0; i < line.length(); i++) {
        if (paths[i] > 0 && line.charAt(i) == '^') {
          if (i > 0) paths[i - 1] += paths[i];
          if (i < paths.length - 1) paths[i + 1] += paths[i];
          paths[i] = 0;
          count++;
        }
      }
    }
    return new Result(count, stream(paths).sum());
  }

  private long[] startPaths(String line) {
    long[] paths = new long[line.length()];
    paths[line.indexOf('S')] = 1;
    return paths;
  }
}
