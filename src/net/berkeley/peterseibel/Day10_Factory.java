package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day10_Factory extends Solution<List<String>, Long> {

  private static final Pattern pat = Pattern.compile("\\[(.*)\\] (.*?) \\{(.*?)\\}");

  public record Machine(int lights, List<List<Integer>> buttons, List<Integer> joltages) {

    static Machine valueOf(String spec) {
      Matcher m = pat.matcher(spec);
      if (m.matches()) {
        String lights = m.group(1);
        String buttons = m.group(2);
        String joltages = m.group(3);
        return new Machine(parseLights(lights), parseButtons(buttons), parseJoltages(joltages));
      } else {
        throw new RuntimeException("Bad match against " + spec);
      }
    }

    public int[] buttonBits() {
      return buttons.stream().mapToInt(this::toBits).toArray();
    }

    private int toBits(List<Integer> nums) {
      return nums.stream().mapToInt(n -> n).reduce(0, (bits, n) -> bits | (1 << n));
    }

    private static int parseLights(String s) {
      int top = 1 << (s.length() - 1);
      return s.codePoints().map(c -> c == '#' ? top : 0).reduce(0, (bits, b) -> (bits >>> 1) | b);
    }

    private static List<List<Integer>> parseButtons(String buttons) {
      return stream(buttons.split("\\s+"))
          .map(s -> s.substring(1, s.length() - 1))
          .map(s -> stream(s.split(",")).map(Integer::valueOf).toList())
          .toList();
    }

    private static List<Integer> parseJoltages(String joltages) {
      return stream(joltages.split(",")).map(Integer::parseInt).toList();
    }
  }

  public Day10_Factory() {
    super(10, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return machines(lines).stream().mapToLong(m -> minPresses(m.lights(), m.buttonBits())).sum();
  }

  public Long part2(List<String> lines) {
    return machines(lines).stream().mapToLong(Equations::answer).sum();
  }

  private int minPresses(int lights, int[] buttons) {
    return presses(buttons)
        .filter(p -> p.result() == lights)
        .findFirst()
        .map(Presses::num)
        .orElseThrow();
  }

  record Presses(int num, int result) {}

  private Stream<Presses> presses(int[] buttons) {
    return IntStream.iterate(1, n -> n + 1)
        .boxed()
        .flatMap(n -> combos(buttons, n).mapToObj(v -> new Presses(n, v)));
  }

  private IntStream combos(int[] buttons, int n) {
    if (n == 1) {
      return stream(buttons);
    } else {
      return combos(buttons, n - 1).flatMap(a -> stream(buttons).map(b -> a ^ b));
    }
  }

  private List<Machine> machines(List<String> lines) {
    return lines.stream()
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(Machine::valueOf)
        .toList();
  }
}
