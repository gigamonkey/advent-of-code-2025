package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.nio.file.Files.*;

import module java.base;

public class Day02_GiftShop extends Solution<Path, Long> {

  record Range(long start, long end) {
    static Range of(String s) {
      String[] parts = s.split("-");
      return new Range(parseLong(parts[0]), parseLong(parts[1]));
    }
  }

  public Day02_GiftShop() {
    super(2);
  }

  public Long part1(Path input) {
    return sumInvalid(input, this::isInvalid);
  }

  public Long part2(Path input) {
    return sumInvalid(input, this::isInvalid2);
  }

  private Long sumInvalid(Path input, LongPredicate pred) {
    String text = asString(input).trim();
    long sum = 0;
    for (Range r : Arrays.stream(text.split(",")).map(Range::of).toList()) {
      for (long n = r.start(); n <= r.end(); n++) {
        if (pred.test(n)) {
          sum += n;
        }
      }
    }
    return sum;
  }

  private boolean isInvalid(long n) {
    return String.valueOf(n).matches("(.*)\\1");
  }

  private boolean isInvalid2(long n) {
    return String.valueOf(n).matches("(.*)\\1{1,}");
  }

  public Optional<Path> input(String name, int part) {
    return maybeInputPath(name, part);
  }

  public Optional<Long> expected(String name, int part) {
    return maybeExpectedPath(name, part).map(this::asLong);
  }
}
