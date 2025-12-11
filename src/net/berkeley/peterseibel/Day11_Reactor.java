package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day11_Reactor extends Solution<List<String>, Long> {

  public Day11_Reactor() {
    super(11, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    Map<String, List<String>> m = loadMap(lines);
    return ways("you", "out", m, topoSort(m));
  }

  public Long part2(List<String> lines) {
    Map<String, List<String>> m = loadMap(lines);
    List<String> sorted = topoSort(m);

    long fft2dac = ways("fft", "dac", m, sorted);

    if (fft2dac > 0) {
      long svr2fft = ways("svr", "fft", m, sorted);
      long dac2out = ways("dac", "out", m, sorted);
      return svr2fft * fft2dac * dac2out;
    } else {
      long svr2dac = ways("svr", "dac", m, sorted);
      long dac2fft = ways("dac", "fft", m, sorted);
      long fft2out = ways("fft", "out", m, sorted);
      return svr2dac * dac2fft * fft2out;
    }
  }

  private Map<String, Integer> indegree(Map<String, List<String>> m) {
    Map<String, Integer> indegree = new HashMap<>();
    for (var v : m.values()) {
      for (var n : v) {
        indegree.merge(n, 1, (a, b) -> a + b);
      }
    }
    return indegree;
  }

  private List<String> topoSort(Map<String, List<String>> m) {
    var indegree = indegree(m);
    // Put starting points into queue to be sorted first.
    Queue<String> queue = new LinkedList<>();
    for (var n : m.keySet()) {
      if (indegree.getOrDefault(n, 0) == 0) {
        queue.add(n);
      }
    }

    // Pull items from queue to add to sorted list and decrement the indegree of
    // their neighbors.
    List<String> sorted = new ArrayList<>();
    while (!queue.isEmpty()) {
      String n = queue.remove();
      sorted.add(n);
      for (var o : m.getOrDefault(n, List.of())) {
        if (indegree.computeIfPresent(o, (_, v) -> v - 1) == 0) {
          queue.add(o);
        }
      }
    }
    return sorted;
  }

  private long ways(String start, String end, Map<String, List<String>> m, List<String> sorted) {
    Map<String, Long> ways = new HashMap<>();
    ways.put(start, 1L);

    for (var n : sorted) {
      for (var o : m.getOrDefault(n, List.of())) {
        ways.merge(o, ways.getOrDefault(n, 0L), (a, b) -> a + b);
      }
    }
    return ways.get(end);
  }

  private Map<String, List<String>> loadMap(List<String> lines) {
    Map<String, List<String>> m = new HashMap<>();
    for (var line : lines) {
      int colon = line.indexOf(":");
      String from = line.substring(0, colon).intern();
      List<String> to = stream(line.substring(colon + 2).split(" ")).map(String::intern).toList();
      m.put(from, to);
    }
    return m;
  }
}
