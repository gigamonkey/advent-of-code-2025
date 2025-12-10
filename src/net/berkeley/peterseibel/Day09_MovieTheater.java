package net.berkeley.peterseibel;

import static java.lang.Long.signum;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

// Wrong, part 2

// 4588384997 - to high

public class Day09_MovieTheater extends Solution<List<String>, Long> {

  record Point(long column, long row) {
    static Point valueOf(String s) {
      long[] parts = stream(s.split(",")).mapToLong(Long::parseLong).toArray();
      return new Point(parts[0], parts[1]);
    }

    public long area(Point other) {
      return (1 + abs(row - other.row)) * (1 + abs(column - other.column));
    }

    public boolean northOf(Line line) {
      return line.isHorizontal()
          && row <= line.row()
          && between(line.a().column(), column, line.b().column());
    }

    public boolean eastOf(Line line) {
      return line.isVertical()
          && column <= line.column()
          && between(line.a().row(), row, line.b().row());
    }

    public boolean southOf(Line line) {
      return line.isHorizontal()
          && row >= line.row()
          && between(line.a().column(), column, line.b().column());
    }

    public boolean westOf(Line line) {
      return line.isVertical()
          && column >= line.column()
          && between(line.a().row(), row, line.b().row());
    }

    public Point toward(Point other) {
      long dr = signum(other.row - row);
      long dc = signum(other.column - column);
      return new Point(column + dc, row + dr);
    }
  }

  private static boolean between(long a, long b, long c) {
    return min(a, c) <= b && b <= max(a, c);
  }

  record Line(Point a, Point b) {

    boolean isVertical() {
      return a.column() == b.column();
    }

    boolean isHorizontal() {
      return !isVertical();
    }

    long column() {
      assert isVertical() : "Only vertical lines have column";
      return a.column();
    }

    long row() {
      assert !isVertical() : "Only horizontal lines have row";
      return a.row();
    }
  }

  public Day09_MovieTheater() {
    super(9, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    long max = 0;
    List<Point> points = points(lines);
    for (int i = 0; i < points.size() - 1; i++) {
      for (int j = i + 1; j < points.size(); j++) {
        max = Math.max(max, points.get(i).area(points.get(j)));
      }
    }
    return max;
  }

  public Long part2(List<String> input) {
    long max = 0;

    List<Point> points = points(input);
    IO.println(points.stream().mapToLong(Point::row).summaryStatistics());
    IO.println(points.stream().mapToLong(Point::column).summaryStatistics());
    List<Line> lines = lines(points);
    for (int i = 0; i < points.size() - 1; i++) {
      for (int j = i + 1; j < points.size(); j++) {
        Point a = points.get(i);
        Point b = points.get(j);
        if (cornersInside(a, b, lines) && linesInside(a, b, lines)) {
          max = Math.max(max, points.get(i).area(points.get(j)));
        }
      }
    }
    return max;
  }

  private List<Point> points(List<String> lines) {
    return lines.stream().map(Point::valueOf).toList();
  }

  private List<Line> lines(List<Point> points) {
    List<Line> lines = new ArrayList<>();
    for (int i = 0; i < points.size(); i++) {
      lines.add(new Line(points.get(i), points.get((i + 1) % points.size())));
    }
    return lines;
  }

  private boolean cornersInside(Point a, Point b, List<Line> lines) {
    // IO.println("Checking corners of %s, %s".formatted(a, b));
    Point c = new Point(a.column(), b.row());
    Point d = new Point(b.column(), a.row());
    return inside(c, lines) && inside(d, lines);
  }

  private boolean cornersNeighborsInside(Point a, Point b, List<Line> lines) {
    // IO.println("Checking corners of %s, %s".formatted(a, b));
    Point c = new Point(a.column(), b.row());
    Point d = new Point(b.column(), a.row());
    return (inside(c, lines)
        && inside(d, lines)
        && inside(c.toward(a), lines)
        && inside(c.toward(b), lines)
        && inside(d.toward(a), lines)
        && inside(d.toward(b), lines));
  }

  private boolean linesInside(Point a, Point b, List<Line> lines) {
    // IO.println("Checking corners of %s, %s".formatted(a, b));
    Point c = new Point(a.column(), b.row());
    Point d = new Point(b.column(), a.row());

    return (lineInside(new Line(a, c), lines)
        && lineInside(new Line(c, b), lines)
        && lineInside(new Line(b, d), lines)
        && lineInside(new Line(d, a), lines));
  }

  private boolean lineInside(Line line, List<Line> lines) {
    long dr = signum(line.b().row() - line.a().row());
    long dc = signum(line.b().column() - line.a().column());
    Point p = line.a();
    Point end = line.b();
    do {
      if (!inside(p, lines)) {
        return false;
      }
      p = new Point(p.column() + dc, p.row() + dr);
    } while (!p.equals(end));
    return true;
  }

  private boolean inside(Point p, List<Line> lines) {
    boolean x =
        (lines.stream().anyMatch(line -> p.northOf(line))
            && lines.stream().anyMatch(line -> p.eastOf(line))
            && lines.stream().anyMatch(line -> p.southOf(line))
            && lines.stream().anyMatch(line -> p.westOf(line)));
    // IO.println("checking if %s inside: %s".formatted(p, x));
    return x;
  }
}
