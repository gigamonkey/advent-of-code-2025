package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Comparator.*;

import module java.base;

public class Day05_Cafeteria extends Solution<List<String>, Long> {

  record Ingredients(List<Range> fresh, List<Long> available) {}

  public Day05_Cafeteria() {
    super(5, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
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

  // private Ingredients ingredients(List<String> lines) {
  //   List<Range> fresh = new ArrayList<>();

  //   boolean inFresh = true;

  //   for (String line : lines) {
  //     if (inFresh) {
  //       if (line.trim().isEmpty()) {
  //         inFresh = false;
  //       } else {
  //         String[] parts = line.split("-");
  //         fresh.add(new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1])));
  //       }
  //     } else {
  //       long n = Long.parseLong(line);
  //       if (fresh.stream().anyMatch(r -> r.contains(n))) {
  //         count++;
  //       }
  //     }
  //   }
  //   return count;

}
