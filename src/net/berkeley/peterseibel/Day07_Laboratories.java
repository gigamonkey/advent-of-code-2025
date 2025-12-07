package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;

import module java.base;

public class Day07_Laboratories extends Solution<List<String>, Long> {

  public Day07_Laboratories() {
    super(7, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    long count = 0L;

    boolean[] beams = start(lines.getFirst());
    for (String line : lines.subList(1, lines.size())) {
      var splitters = splitters(line);
      for (int i = 0; i < splitters.length; i++) {
        if (beams[i] && splitters[i]) {
          count++;
          beams[i] = false;
          if (i > 0) beams[i - 1] = true;
          if (i < beams.length - 1) beams[i + 1] = true;
        }
      }
    }
    return count;
  }

  private boolean[] splitters(String line) {
    char[] chars = line.toCharArray();
    boolean[] r = new boolean[chars.length];
    for (int i = 0; i < chars.length; i++) {
      r[i] = (chars[i] == '^');

    }
    return r;
  }

  private boolean[] start(String line) {
    char[] chars = line.toCharArray();
    boolean[] r = new boolean[chars.length];
    for (int i = 0; i < chars.length; i++) {
      r[i] = (chars[i] == 'S');

    }
    return r;
  }



  public Long part2(List<String> banks) {
    return 0L;
  }
}
