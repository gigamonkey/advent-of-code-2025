package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day08_Playground extends Solution<List<String>, Long> {

  record Box(long x, long y, long z) {

    public static List<Box> boxes(List<String> lines) {
      return lines.stream().map(Box::valueOf).toList();
    }

    public static Box valueOf(String line) {
      var parts = stream(line.split(",")).mapToLong(Long::parseLong).toArray();
      return new Box(parts[0], parts[1], parts[2]);
    }

    public double distance(Box other) {
      return sqrt(pow(x - other.x, 2) + pow(y - other.y, 2) + pow(z - other.z, 2));
    }
  }

  record Connection(Box a, Box b) {

    public double distance() { return a.distance(b); }
  }

  public Day08_Playground() {
    super(8, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    var boxes = Box.boxes(lines);

    // kludge
    var pairs = boxes.size() == 20 ? 10 : 1000;

    //IO.println("%d boxes making %d pairs".formatted(boxes.size(), pairs));

    Set<Connection> connections = new HashSet<>();
    Map<Box, Set<Box>> circuits = new HashMap<>();

    for (int p = 0; p < pairs; p++) {
      var c = closest(boxes, connections);
      //IO.println("Connecting %s".formatted(c));
      connections.add(c);
      var c1 = circuits.computeIfAbsent(c.a(), (k) -> new HashSet<>());
      c1.add(c.a());
      var c2 = circuits.computeIfAbsent(c.b(), (k) -> new HashSet<>());
      c2.add(c.b());
      if (c1 != c2) {
        for (var box : c2) {
          c1.add(box);
          circuits.put(box, c1);
        }
        //IO.println("Joining circuits of %s and %s size %d".formatted(c.a(), c.b(), c1.size()));
      } else {
        //IO.println("%s and %s already in same circuit size %d".formatted(c.a(), c.b(), c1.size()));
      }
      //IO.println("  %s".formatted(c1));
    }
    List<Set<Box>> allCircuits = new ArrayList<>(new HashSet<>(circuits.values()));
    //IO.println("%d circuits".formatted(allCircuits.size()));

    Collections.sort(allCircuits, (a, b) -> b.size() - a.size());

    // IO.println("all circuits");
    // for (var x : allCircuits) {
    //   IO.println("size: %d".formatted(x.size()));
    // }

    // IO.println();

    long prod = 1;
    for (var x : allCircuits.subList(0, 3)) {
      //IO.println("size: %d".formatted(x.size()));
      prod *= x.size();
    }
    return prod;
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }


  private static Connection closest(List<Box> boxes, Set<Connection> connected) {
    var closest = new Connection(boxes.get(0), boxes.get(1));
    for (int i = 0; i < boxes.size() - 1; i++) {
      for (int j = i + 1; j < boxes.size(); j++) {
        var c = new Connection(boxes.get(i), boxes.get(j));
        if (!connected.contains(c) && (c.distance() < closest.distance())) {
          closest = c;
        }
      }
    }
    return closest;
  }


}
