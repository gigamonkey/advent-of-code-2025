package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day08_Playground extends Solution<List<String>, Long> {

  private record Box(long x, long y, long z) {
    public static Box valueOf(String line) {
      var parts = stream(line.split(",")).mapToLong(Long::parseLong).toArray();
      return new Box(parts[0], parts[1], parts[2]);
    }
  }

  private record Connection(Box a, Box b) {
    public double distance() {
      return hypot(hypot(a.x() - b.x(), a.y() - b.y()), a.z() - b.z());
    }
  }

  public Day08_Playground() {
    super(8, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    var boxes = boxes(lines);
    var byDistance = closest(boxes);

    var pairs = boxes.size() == 20 ? 10 : 1000; // kludge

    Map<Box, Set<Box>> circuits = new HashMap<>();

    for (var c : byDistance.subList(0, pairs)) {
      connectCircuits(circuits, c);
    }

    List<Set<Box>> allCircuits = new ArrayList<>(new HashSet<>(circuits.values()));
    Collections.sort(allCircuits, (a, b) -> b.size() - a.size());
    return allCircuits.subList(0, 3).stream().mapToLong(Set::size).reduce(1, (acc, n) -> acc * n);
  }

  public Long part2(List<String> lines) {
    var boxes = boxes(lines);
    var byDistance = closest(boxes);

    Map<Box, Set<Box>> circuits = new HashMap<>();

    for (var c : byDistance) {
      var circuit = connectCircuits(circuits, c);
      if (circuit.size() == boxes.size()) {
        return c.a().x() * c.b().x();
      }
    }
    throw new Error("wat");
  }

  public static List<Box> boxes(List<String> lines) {
    return lines.stream().map(Box::valueOf).toList();
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

  private Set<Box> connectCircuits(Map<Box, Set<Box>> circuits, Connection c) {
    var circuit1 = circuitFor(c.a(), circuits);
    var circuit2 = circuitFor(c.b(), circuits);

    if (circuit1 != circuit2) {
      for (var box : circuit2) {
        circuit1.add(box);
        circuits.put(box, circuit1);
      }
    }
    return circuit1;
  }

  private Set<Box> circuitFor(Box box, Map<Box, Set<Box>> circuits) {
    return circuits.computeIfAbsent(box, (k) -> new HashSet<>(List.of(box)));
  }
}
