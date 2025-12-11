package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
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

  public Long part2OLD(List<String> lines) {
    return p2("svr", "out", loadMap(lines), new HashSet<String>(), false, false);
  }

  public Long part2(List<String> lines) {
    Map<String, List<String>> m = loadMap(lines);
    List<String> sorted = topoSort(m);

    long svr2fft = ways("svr", "fft", m, sorted);
    long svr2dac = ways("svr", "dac", m, sorted);
    long fft2dac = ways("fft", "dac", m, sorted);
    long dac2fft = ways("dac", "fft", m, sorted);
    long fft2out = ways("fft", "out", m, sorted);
    long dac2out = ways("dac", "out", m, sorted);

    IO.println("svr -> fft: %d".formatted(ways("svr", "fft", m, sorted)));
    IO.println("svr -> dac: %d".formatted(ways("svr", "dac", m, sorted)));
    IO.println("fft -> dac: %d".formatted(ways("fft", "dac", m, sorted)));
    IO.println("dac -> fft: %d".formatted(ways("dac", "fft", m, sorted)));
    IO.println("fft -> out: %d".formatted(ways("fft", "out", m, sorted)));
    IO.println("dac -> out: %d".formatted(ways("dac", "out", m, sorted)));

    if (fft2dac > 0) {
      return svr2fft * fft2dac * dac2out;
    } else {
      return svr2dac * dac2fft * fft2out;
    }

    //return ways("svr", "out", m, sorted);
  }

  private long p2(String from, String end, Map<String, List<String>> m, Set<String> seen, boolean fft, boolean dac) {
    if (from == end) {
      return fft && dac ? 1 : 0;
    } else {
      long c = 0;
      for (var n : m.get(from)) {
        c += p2(n, end, m, seen, fft || n == "fft", dac || n == "dac");
      }
      return c;
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
    for (var line: lines) {
      int colon = line.indexOf(":");
      String from = line.substring(0, colon).intern();
      List<String> to = stream(line.substring(colon + 2).split(" ")).map(String::intern).toList();
      m.put(from, to);
    }
    return m;
  }


}

// svr -> fft = 1
// fft -> out = 4
// fft -> dac = 2
// dac -> fft = 0
// svr -> dac = 4
// dac -> out = 4


// if dac -> fft > 0: svr -> dac * dac -> fft * fft -> out
// else: svr -> fft * fft -> dac * dac -> out

// Ways from svr -> dac * dac ->
// Ways from
