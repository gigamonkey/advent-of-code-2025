package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day09_MovieTheater extends Solution<List<String>, Long> {

  record Point(long col, long row) {
    static Point valueOf(String s) {
      long[] parts = stream(s.split(",")).mapToLong(Long::parseLong).toArray();
      return new Point(parts[0], parts[1]);
    }

    public long area(Point other) {
      long area = (1 + abs(row  - other.row)) * (1 + abs(col - other.col));
      //IO.println("Rectangle %s and %s area: %d".formatted(this, other, area));
      return area;
    }
  }

  public Day09_MovieTheater() {
    super(9, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    long max = 0;
    List<Point> points = points(lines);
    //IO.println(points);
    for (int i = 0; i < points.size() - 1; i++) {
      for (int j = i + 1; j < points.size(); j++) {
        max = Math.max(max, points.get(i).area(points.get(j)));
      }
    }
    return max;
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }


  private List<Point> points(List<String> lines) {
    return lines.stream().map(Point::valueOf).toList();
  }


}
