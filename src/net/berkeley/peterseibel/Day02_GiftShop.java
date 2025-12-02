package net.berkeley.peterseibel;

import module java.base;

import static java.nio.file.Files.*;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.lang.Math.*;

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

  public Long part1(Path input) throws IOException {
    String text = Util.asString(input).trim();
    long sum = 0;
    for (Range r : Arrays.stream(text.split(",")).map(Range::of).toList()) {
      for (long n = r.start(); n <= r.end(); n++) {
        if (isInvalid(n)) {
          sum += n;
        }
      }
    }
    return sum;
  }

  public Long part2(Path input) throws IOException {
    String text = Util.asString(input).trim();
    long sum = 0;
    for (Range r : Arrays.stream(text.split(",")).map(Range::of).toList()) {
      for (long n = r.start(); n <= r.end(); n++) {
        if (isInvalid2(n)) {
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

  public Optional<Path> input(String name, int part) throws IOException {
    return Util.maybeInputPath(name, day(), part);
  }

  public Optional<Long> expected(String name, int part) throws IOException {
    return Util.maybeExpectedPath(name, day(), part).map(Util::asLong);
  }


}
