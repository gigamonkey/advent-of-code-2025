package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;
import static java.util.stream.Gatherers.*;
import static java.util.Comparator.*;

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
  }

  record Connection(Box a, Box b) {
    public double distance() {
      return hypot(hypot(a.x() - b.x(), a.y() - b.y()), a.z() - b.z());
    }
  }

  public Day08_Playground() {
    super(8, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    var boxes = Box.boxes(lines);
    var byDistance = closest(boxes);
    var pairs = boxes.size() == 20 ? 10 : 1000; // kludge

    Map<Box, Set<Box>> circuits = new HashMap<>();

    for (var c : byDistance.subList(0, pairs)) {
      var c1 = circuits.computeIfAbsent(c.a(), (k) -> new HashSet<>());
      c1.add(c.a());
      var c2 = circuits.computeIfAbsent(c.b(), (k) -> new HashSet<>());
      c2.add(c.b());
      if (c1 != c2) {
        for (var box : c2) {
          c1.add(box);
          circuits.put(box, c1);
        }
      }
    }

    List<Set<Box>> allCircuits = new ArrayList<>(new HashSet<>(circuits.values()));
    Collections.sort(allCircuits, (a, b) -> b.size() - a.size());

    return allCircuits.subList(0, 3).stream().mapToLong(Set::size).reduce(1, (acc, n) -> acc * n);

  }
  public Long part2(List<String> lines) {
    var boxes = Box.boxes(lines);
    var byDistance = closest(boxes);

    Map<Box, Set<Box>> circuits = new HashMap<>();

    for (var c : byDistance) {
      var c1 = circuits.computeIfAbsent(c.a(), (k) -> new HashSet<>());
      c1.add(c.a());
      var c2 = circuits.computeIfAbsent(c.b(), (k) -> new HashSet<>());
      c2.add(c.b());
      if (c1 != c2) {
        for (var box : c2) {
          c1.add(box);
          circuits.put(box, c1);
        }
      }
      if (c1.size() == boxes.size()) {
        return c.a().x() * c.b().x();
      }
    }
    throw new Error("wat");
  }

  private List<Connection> closest(List<Box> boxes) {
    List<Connection> all = new ArrayList<>();
    for (int i = 0; i < boxes.size() - 1; i++) {
      for (int j = i + 1; j < boxes.size(); j++) {
        all.add(new Connection(boxes.get(i), boxes.get(j)));
      }
    }
    all.sort(comparingDouble(Connection::distance));
    return all;
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
