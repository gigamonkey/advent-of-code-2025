package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day10_Factory extends Solution<List<String>, Long> {

  private static final Pattern pat = Pattern.compile("\\[(.*)\\] (.*?) \\{(.*?)\\}");

  record Machine(int goal, int[] buttons, int[] joltages) {
    static Machine valueOf(String s) {
      Matcher m = pat.matcher(s);
      if (m.matches()) {
        String lights = m.group(1);
        String buttons = m.group(2);
        String joltages = m.group(3);
        return new Machine(parseLights(lights), parseButtons(buttons), parseJoltages(joltages));
      } else {
        throw new RuntimeException("Bad match against " + s);
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
        .mapToInt(nums -> {
            int b = 0;
            for (int n : nums) {
              b |= (1 << n);
            }
            return b;
          })
        .toArray();
    }

    private static int[] parseJoltages(String joltages) {
      return stream(joltages.split(",")).mapToInt(Integer::parseInt).toArray();
    }
  }

  public Day10_Factory() {
    super(10, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return machines(lines).stream().mapToLong(m -> minPresses(m.goal(), m.buttons())).sum();
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }


  private int minPresses(int goal, int[] buttons) {
    // try all the one button presses, then all the two button presses, etc.
    // until we get to the goal.
    return presses(buttons).filter(p -> p.result() == goal).findFirst().map(Presses::num).orElseThrow();
  }

  record Presses(int num, int result) {}

  private Stream<Presses> presses(int[] buttons) {
    return IntStream
      .iterate(1, n -> n + 1)
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
    return lines.stream().map(Machine::valueOf).toList();
  }
}
