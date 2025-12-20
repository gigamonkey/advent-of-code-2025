package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day10_Factory extends Solution<List<String>, Long> {

  private static final Pattern pat = Pattern.compile("\\[(.*)\\] (.*?) \\{(.*?)\\}");

  public record Machine(
      int goal,
      int[] buttons,
      List<List<Integer>> buttonsAsLists,
      List<Integer> joltages,
      String spec) {

    static Machine valueOf(String spec) {
      Matcher m = pat.matcher(spec);
      if (m.matches()) {
        String lights = m.group(1);
        String buttons = m.group(2);
        String joltages = m.group(3);
        return new Machine(
            parseLights(lights),
            parseButtons(buttons),
            parseButtonsAsLists(buttons),
            parseJoltages(joltages),
            spec);
      } else {
        throw new RuntimeException("Bad match against " + spec);
      }
    }

    private static int parseLights(String s) {
      int goal = 0;
      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == '#') goal |= (1 << i);
      }
      return goal;
    }

    private static int[] parseButtons(String buttons) {
      return stream(buttons.trim().split("\\s+"))
          .map(s -> s.substring(1, s.length() - 1))
          .map(s -> stream(s.split(",")).mapToInt(Integer::parseInt).toArray())
          .mapToInt(
              nums -> {
                int b = 0;
                for (int n : nums) {
                  b |= (1 << n);
                }
                return b;
              })
          .toArray();
    }

    private static List<List<Integer>> parseButtonsAsLists(String buttons) {
      return stream(buttons.trim().split("\\s+"))
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
    return machines(lines).stream().mapToLong(m -> minPresses(m.goal(), m.buttons())).sum();
  }

  public Long part2(List<String> lines) {
    return machines(lines).stream().mapToLong(Equations::answer).sum();
  }

  private int minPresses(int goal, int[] buttons) {
    return presses(buttons)
        .filter(p -> p.result() == goal)
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
