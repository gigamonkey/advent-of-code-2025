package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;

import module java.base;

public class Day02_GiftShop extends Solution<String, Long> {

  record Range(long start, long end) {
    static Range valueOf(String s) {
      String[] parts = s.split("-");
      return new Range(parseLong(parts[0]), parseLong(parts[1]));
    }
  }

  public Day02_GiftShop() {
    super(2, Data::asString, Data::asLong);
  }

  public Long part1(String input) {
    return sumInvalid(input, n -> String.valueOf(n).matches("(.+?)\\1"));
  }

  public Long part2(String input) {
    return sumInvalid(input, n -> String.valueOf(n).matches("(.+?)\\1{1,}"));
  }

  private Long sumInvalid(String input, LongPredicate pred) {
    long sum = 0;
    for (var r : ranges(input)) {
      for (long n = r.start(); n <= r.end(); n++) {
        if (pred.test(n)) {
          sum += n;
        }
      }
    }
    return sum;
  }

  private List<Range> ranges(String input) {
    return Arrays.stream(input.trim().split(",")).map(Range::valueOf).toList();
  }
}
