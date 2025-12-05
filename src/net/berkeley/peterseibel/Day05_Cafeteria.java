package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Comparator.*;

// Part 2 wrong:
// 371077840399614

import module java.base;

public class Day05_Cafeteria extends Solution<List<String>, Long> {

  record Ingredients(Set<Long> fresh, List<Long> available) {}

  record Range(long start, long end) implements Comparable<Range> {

    public boolean contains(long n) {
      return start <= n && n <= end;
    }

    public long size() {
      assert end >= start: "In order";
      return end - start + 1;
    }

    public boolean overlaps(Range other) {
      return (
        (end >= other.start && end <= other.end) ||
        (start >= other.start && start <= other.end) ||
        (start <= other.start && end >= other.end) ||
        (start >= other.start && end <= other.end));
    }

    public Range combineOverlapping(Range other) {
      return new Range(min(start, other.start), max(end, other.end));
    }

    public int compareTo(Range other) {
      return comparingLong(Range::start).thenComparing(comparingLong(Range::size)).compare(this, other);
    }

  }

  public Day05_Cafeteria() {
    super(5, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return countFresh(lines);
  }

  public Long part2(List<String> lines) {

    List<Range> fresh = new ArrayList<>();

    for (String line : lines) {
      if (line.trim().isEmpty()) break;
      String[] parts = line.split("-");
      long p1 = Long.parseLong(parts[0]);
      long p2 = Long.parseLong(parts[1]);
      long low = min(p1, p2);
      long high = max(p1, p2);
      fresh.add(new Range(low, high));
    }

    fresh.sort(null);

    long count = 0L;

    Range b = fresh.removeLast();
    while (!fresh.isEmpty()) {
      Range a = fresh.removeLast();
      if (a.overlaps(b)) {
        b = a.combineOverlapping(b);
      } else {
        count += b.size();
        if (count < 0) throw new Error("count: " + count);
        b = a;
      }
    }
    count += b.size();
    return count;
  }

  private long countFresh(List<String> lines) {

    List<Range> fresh = new ArrayList<>();

    long count = 0;

    boolean inFresh = true;

    for (String line : lines) {
      if (inFresh) {
        if (line.trim().isEmpty()) {
          inFresh = false;
        } else {
          String[] parts = line.split("-");
          fresh.add(new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
        }
      } else {
        long n = Long.parseLong(line);
        if (fresh.stream().anyMatch(r -> r.contains(n))) {
          count++;
        }
      }
    }
    return count;
  }

}
