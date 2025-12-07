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
    long count = 0L;

    boolean[] beams = booleans(lines.getFirst(), 'S');
    for (String line : lines.subList(1, lines.size())) {
      var splitters = booleans(line, '^');
      for (int i = 0; i < splitters.length; i++) {
        if (beams[i] && splitters[i]) {
          count++;
          if (i > 0) beams[i - 1] = true;
          if (i < beams.length - 1) beams[i + 1] = true;
          beams[i] = false;
        }
      }
    }
    return count;
  }

  public Long part2(List<String> lines) {
    long[] paths = startPaths(lines.getFirst());
    for (String line : lines.subList(1, lines.size())) {
      var splitters = booleans(line, '^');
      for (int i = 0; i < splitters.length; i++) {
        if (paths[i] > 0 && splitters[i]) {
          if (i > 0) paths[i - 1] += paths[i];
          if (i < paths.length - 1) paths[i + 1] += paths[i];
          paths[i] = 0;
        }
      }
    }

    return stream(paths).sum();
  }

  private boolean[] booleans(String line, char c) {
    char[] chars = line.toCharArray();
    boolean[] r = new boolean[chars.length];
    for (int i = 0; i < chars.length; i++) {
      r[i] = chars[i] == c;
    }
    return r;
  }

  private long[] startPaths(String line) {
    char[] chars = line.toCharArray();
    long[] r = new long[chars.length];
    for (int i = 0; i < chars.length; i++) {
      r[i] = chars[i] == 'S' ? 1 : 0;
    }
    return r;
  }
}
