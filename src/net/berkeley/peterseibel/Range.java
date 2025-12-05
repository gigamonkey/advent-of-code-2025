package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Comparator.*;

import module java.base;

public record Range(long start, long end) implements Comparable<Range> {

  private static final Comparator<Range> cmp =
      comparingLong(Range::start).thenComparing(comparingLong(Range::size));

  public static Range valueOf(String s) {
    String[] parts = s.split("-");
    return new Range(parseLong(parts[0]), parseLong(parts[1]));
  }

  public boolean contains(long n) {
    return start <= n && n <= end;
  }

  public long size() {
    return end - start + 1;
  }

  public boolean overlaps(Range other) {
    return !(end < other.start || start > other.end);
  }

  public Range merge(Range other) {
    assert overlaps(other) : "Can only merge overlapping ranges";
    return new Range(min(start, other.start), max(end, other.end));
  }

  public int compareTo(Range other) {
    return cmp.compare(this, other);
  }
}
