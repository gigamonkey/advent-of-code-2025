package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import module java.base;

public class Day05_Cafeteria extends Solution<List<String>, Long> {

  private record Ingredients(List<Range> fresh, List<Long> available) {

    public static Ingredients from(List<String> lines) {
      Ingredients i = new Ingredients(new ArrayList<>(), new ArrayList<>());

      boolean inFresh = true;

      for (String line : lines) {
        if (inFresh) {
          if (line.trim().isEmpty()) {
            inFresh = false;
          } else {
            i.fresh.add(Range.valueOf(line));
          }
        } else {
          i.available.add(parseLong(line));
        }
      }
      return i;
    }

    public boolean isFresh(long id) {
      return fresh.stream().anyMatch(r -> r.contains(id));
    }

    public List<Range> sorted() {
      return fresh.stream().sorted().collect(toCollection(ArrayList::new));
    }
  }

  public Day05_Cafeteria() {
    super(5, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    var ingredients = Ingredients.from(lines);
    return ingredients.available().stream().filter(ingredients::isFresh).count();
  }

  public Long part2(List<String> lines) {
    long count = 0L;

    var fresh = Ingredients.from(lines).sorted();

    Range b = fresh.removeLast();
    while (!fresh.isEmpty()) {
      Range a = fresh.removeLast();
      if (a.overlaps(b)) {
        b = a.merge(b);
      } else {
        count += b.size();
        b = a;
      }
    }
    return count + b.size();
  }
}
