package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day11_Reactor extends Solution<List<String>, Long> {

  static class Paths {
    private final Map<String, List<String>> graph;
    private final List<String> sorted;

    Paths(List<String> lines) {
      graph = loadGraph(lines);
      sorted = topoSort();
    }

    public long ways(String start, String end) {
      Map<String, Long> ways = new HashMap<>();
      ways.put(start, 1L);

      for (var n : sorted) {
        if (graph.containsKey(n)) {
          for (var o : graph.get(n)) {
            ways.merge(o, ways.getOrDefault(n, 0L), (a, b) -> a + b);
          }
        }
      }
      return ways.get(end);
    }

    private List<String> topoSort() {
      var indegree = indegree(graph);

      // Put starting points into queue to be sorted first.
      Queue<String> queue = new LinkedList<>();
      for (var n : graph.keySet()) {
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
        if (graph.containsKey(n)) {
          for (var o : graph.get(n)) {
            if (indegree.computeIfPresent(o, (_, v) -> v - 1) == 0) {
              queue.add(o);
            }
          }
        }
      }
      return sorted;
    }

    private Map<String, Integer> indegree(Map<String, List<String>> m) {
      Map<String, Integer> indegree = new HashMap<>();
      for (var v : graph.values()) {
        for (var n : v) {
          indegree.merge(n, 1, (a, b) -> a + b);
        }
      }
      return indegree;
    }
  }

  public Day11_Reactor() {
    super(11, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return new Paths(lines).ways("you", "out");
  }

  public Long part2(List<String> lines) {
    Paths p = new Paths(lines);
    long fft2dac = p.ways("fft", "dac");

    // Counting on this being a DAG
    if (fft2dac > 0) {
      return p.ways("svr", "fft") * fft2dac * p.ways("dac", "out");
    } else {
      return p.ways("svr", "dac") * p.ways("dac", "fft") * p.ways("fft", "out");
    }
  }

  private static Map<String, List<String>> loadGraph(List<String> lines) {
    Map<String, List<String>> graph = new HashMap<>();
    for (var line : lines) {
      int colon = line.indexOf(":");
      String from = line.substring(0, colon);
      List<String> to = Arrays.asList(line.substring(colon + 2).split(" "));
      graph.put(from, to);
    }
    return graph;
  }
}
