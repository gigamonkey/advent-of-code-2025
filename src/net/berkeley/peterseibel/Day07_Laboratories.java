package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.fold;

import module java.base;

public class Day07_Laboratories extends Solution<List<String>, Long> {

  public Day07_Laboratories() {
    super(7, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return Solver.solve(lines).splits();
  }

  public Long part2(List<String> lines) {
    return Solver.solve(lines).paths();
  }

  // Hat tip to Brad for the idea to solve both parts in one pass
  private static class Solver {

    private long splits = 0;
    private long[] paths = null;

    public static Solver solve(List<String> lines) {
      return lines.stream().gather(fold(Solver::new, Solver::update)).findFirst().get();
    }

    public long splits() {
      return splits;
    }

    public long paths() {
      return stream(paths).sum();
    }

    private Solver update(String line) {
      if (paths == null) {
        paths = new long[line.length()];
        paths[line.indexOf('S')] = 1;
      } else {
        for (int i = 0; i < line.length(); i++) {
          if (paths[i] > 0 && line.charAt(i) == '^') {
            if (i > 0) paths[i - 1] += paths[i];
            if (i < paths.length - 1) paths[i + 1] += paths[i];
            paths[i] = 0;
            splits++;
          }
        }
      }
      return this;
    }
  }
}
