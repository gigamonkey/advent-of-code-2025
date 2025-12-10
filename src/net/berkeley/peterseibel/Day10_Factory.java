package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day10_Factory extends Solution<List<String>, Long> {

  private static final Pattern pat = Pattern.compile("\\[(.*)\\] (.*?) \\{(.*?)\\}");

  record Machine(int goal, int[] buttons, List<List<Integer>> buttonsAsLists, int[] joltages) {
    static Machine valueOf(String s) {
      Matcher m = pat.matcher(s);
      if (m.matches()) {
        String lights = m.group(1);
        String buttons = m.group(2);
        String joltages = m.group(3);
        return new Machine(
          parseLights(lights),
          parseButtons(buttons),
          parseButtonsAsLists(buttons),
          parseJoltages(joltages));
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
    return machines(lines).stream().mapToLong(this::minimumFor).sum();
  }

  private int minimumFor(Machine m) {
    int x = minimumFor(m.joltages(), m.buttonsAsLists());
    //IO.println("Got minimum %d for %s".formatted(x, m));
    return x;
  }

  private int minimumFor(int[] joltages, List<List<Integer>> buttons) {
    if (zero(joltages)) {
      return 0;
    } else if (negative(joltages) || buttons.isEmpty()) {
      return -1;
    } else {
      int with = minimumFor(subtract(joltages, buttons.get(0)), buttons);
      int without = minimumFor(joltages, buttons.subList(1, buttons.size()));

      if (with == -1) {
        return without;
      } else if (without == -1) {
        return 1 + with;
      } else {
        return min(1 + with, without);
      }
    }
  }

  private boolean zero(int[] ints) {
    return stream(ints).allMatch(n -> n == 0);
  }

  private boolean negative(int[] ints) {
    return stream(ints).anyMatch(n -> n < 0);
  }

  private int[] subtract(int[] ints, List<Integer> button) {
    int[] r = Arrays.copyOf(ints, ints.length);
    for (int b : button) {
      r[b]--;
    }
    return r;
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
    return lines.stream().map(Machine::valueOf).toList();
  }
}
