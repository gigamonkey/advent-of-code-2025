package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day11_Reactor extends Solution<List<String>, Long> {

  record Edges(String from, List<String> to) {
    static Edges valueOf(String line) {
      int colon = line.indexOf(":");
      String from = line.substring(0, colon);
      List<String> to = Arrays.asList(line.substring(colon + 2).split(" "));
      return new Edges(from, to);
    }
  }


  public Day11_Reactor() {
    super(11, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    List<Edges> edges = lines.stream().map(Edges::valueOf).toList();

    Map<String, List<String>> m = new HashMap<>();

    for (var e : edges) {
      m.put(e.from(), e.to());
    }

    return p1("you", "out", m);
  }

  private long p1(String from, String end, Map<String, List<String>> m) {
    if (from.equals(end)) {
      return 1;
    } else {
      long c = 0;
      for (var n : m.get(from)) {
        c += p1(n, end, m);
      }
      return c;
    }
  }

  public Long part2(List<String> lines) {
    Map<String, List<String>> m = new HashMap<>();
    for (var line: lines) {
      int colon = line.indexOf(":");
      String from = line.substring(0, colon);
      List<String> to = Arrays.asList(line.substring(colon + 2).split(" "));
      m.put(from, to);
    }

    return p2("svr", "out", m, new HashSet<String>(), false, false);
  }


  private long p2(String from, String end, Map<String, List<String>> m, Set<String> seen, boolean fft, boolean dac) {
    if (from.equals(end)) {
      return fft && dac ? 1 : 0;
    } else {
      long c = 0;
      for (var n : m.get(from)) {
        c += p2(n, end, m, seen, fft || n.equals("fft"), dac || n.equals("dac"));
      }
      return c;
    }
  }

}
