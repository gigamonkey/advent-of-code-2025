package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;
import static java.util.stream.IntStream.range;

import module java.base;

public class Day10_Factory extends Solution<List<String>, Long> {

  private static final Pattern pat = Pattern.compile("\\[(.*)\\] (.*?) \\{(.*?)\\}");

  public record Machine(
      int goal, int[] buttons, List<List<Integer>> buttonsAsLists, List<Integer> joltages) {

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
    return machines(lines).stream().peek(IO::println).mapToInt(Equations::answer).mapToLong(n -> n).sum();
  }

  record MemoKey(List<Integer> joltages, int numButtons) {}

  // Find solutions for each joltage level independently? Gets a List

  private int minimumForJoltages(
      List<Integer> joltages, List<List<Integer>> buttons, Map<MemoKey, Integer> memo) {
    var k = new MemoKey(joltages, buttons.size());
    if (!memo.containsKey(k)) {
      int result;

      if (zero(joltages)) {
        result = 0;

      } else if (negative(joltages) || buttons.isEmpty()) {
        result = -1;

      } else {

        int with = minimumForJoltages(subtract(joltages, buttons.get(0)), buttons, memo);
        int without = minimumForJoltages(joltages, buttons.subList(1, buttons.size()), memo);

        if (with == -1) {
          result = without;
        } else if (without == -1) {
          result = 1 + with;
        } else {
          result = min(1 + with, without);
        }
      }
      // IO.println("Memozing %s -> %d".formatted(k, result));
      memo.put(k, result);
    } else {
      // IO.println("Memo hit %s".formatted(k));
    }
    return memo.get(k);
  }

  private boolean zero(List<Integer> ints) {
    return ints.stream().allMatch(n -> n == 0);
  }

  private boolean negative(List<Integer> ints) {
    return ints.stream().anyMatch(n -> n < 0);
  }

  private List<Integer> subtract(List<Integer> ints, List<Integer> button) {
    List<Integer> r = new ArrayList<>(ints);
    for (int b : button) {
      r.set(b, r.get(b) - 1);
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

  private List<Integer> indexesByButtons(int size, List<List<Integer>> buttons) {
    List<Integer> idxs = new ArrayList<>(range(0, size).boxed().toList());
    // idxs.sort(comparingInt(i -> countButtons(i, buttons)));
    return idxs;
  }

  private int minimumWith(List<Integer> goal, List<List<Integer>> buttons) {
    IO.println("goal: %s".formatted(goal));
    List<Integer> indices = indexesByButtons(goal.size(), buttons);
    IO.println("sorted indexes: %s".formatted(indices));
    return minimumWith(indices, goal, Map.of(), buttons).orElseThrow();
  }

  private OptionalInt minimumWith(
      List<Integer> indices,
      List<Integer> goal,
      Map<Integer, Integer> assigned,
      List<List<Integer>> buttons) {
    if (indices.isEmpty()) {
      if (assigned.keySet().size() < buttons.size()) {
        IO.println(
            "Returning empty: indices empty but %d < %d"
                .formatted(assigned.keySet().size(), buttons.size()));
        return OptionalInt.empty();
      }

      var r = OptionalInt.of(sumAssignments(assigned));
      IO.println("Returning in base case: %s".formatted(r));
      return r;
    } else {
      int idx = indices.getFirst();
      IO.println("Processing idx %d from %s".formatted(idx, indices));
      int joltage = goal.get(idx);

      // All buttons that touch this joltage
      Set<Integer> relevantButtons = relevantButtons(idx, buttons);

      // Buttons for which we already have assigned a value, earlier in the recursion.
      Set<Integer> assignedButtons = new HashSet<>(relevantButtons);
      assignedButtons.retainAll(assigned.keySet());

      // How many clicks are needed to get this joltage to its goal. It can be
      // negitive if the assignments we've already made push this over.
      int left = joltage - assignedButtons.stream().mapToInt(b -> assigned.get(b)).sum();

      if (left < 0) {
        IO.println("Returning empty as required total is negative: %d".formatted(left));
        return OptionalInt.empty();
      } else if (left == 0) {
        int r = sumAssignments(assigned);
        IO.println(
            "Returning %d because joltage %d (%d) already exactly met".formatted(r, idx, joltage));
        return OptionalInt.of(r);
      } else {

        // Buttons for which we need to assign a value at this stage
        Set<Integer> withoutAssigned = new HashSet<>(relevantButtons);
        withoutAssigned.removeAll(assigned.keySet());
        List<Integer> toAssign = List.copyOf(withoutAssigned);

        // All possible permutations of how we can assigning clicks to the
        // unassigned buttons.
        if (toAssign.size() == 0) {
          IO.println("Returning empty because nothing to assign");
          // return sumAssignments(assigned);
          return OptionalInt.empty();
        }

        List<List<Integer>> possibleAssignments = sumTo(left, toAssign.size());

        // Find the minimum value for each of these assignmets
        OptionalInt min = OptionalInt.empty();

        for (var nums : possibleAssignments) {
          Map<Integer, Integer> next = newAssignments(assigned, nums, toAssign);
          // if (sumAssignments(next) <= min) {
          var sub = minimumWith(indices.subList(1, indices.size()), goal, next, buttons);

          if (sub.isPresent()) {
            // Gah, why doesn't OptionalInt have .map?!
            if (min.isPresent()) {
              min = OptionalInt.of(min(sub.getAsInt(), min.getAsInt()));
            } else {
              min = sub;
            }
          }
          // }
        }
        IO.println("Returning min %s from %s".formatted(min, indices));
        return min;
      }
    }
  }

  private int sumAssignments(Map<Integer, Integer> assigned) {
    return assigned.values().stream().mapToInt(n -> n).sum();
  }

  private Map<Integer, Integer> newAssignments(
      Map<Integer, Integer> assigned, List<Integer> nums, List<Integer> toAssign) {
    // Make a new assigned maps for our recursive call
    Map<Integer, Integer> next = new HashMap<>(assigned);
    for (int i = 0; i < toAssign.size(); i++) {
      next.put(toAssign.get(i), nums.get(i));
    }
    return next;
  }

  private Set<Integer> relevantButtons(int j, List<List<Integer>> buttons) {
    Set<Integer> bs = new HashSet<>();
    for (int i = 0; i < buttons.size(); i++) {
      if (buttons.get(i).contains(j)) {
        bs.add(i);
      }
    }
    return bs;
  }

  private int countButtons(int j, List<List<Integer>> buttons) {
    int count = 0;
    for (int i = 0; i < buttons.size(); i++) {
      if (buttons.get(i).contains(j)) {
        count++;
      }
    }
    return count;
  }

  private List<List<Integer>> sumTo(int total, int size) {
    return sumTo(total, size, new ArrayList<>(), new ArrayList<>());
  }

  private List<List<Integer>> sumTo(
      int left, int remainingSize, List<Integer> soFar, List<List<Integer>> results) {
    if (remainingSize == 0) {
      throw new Error("remaining size zero");
    }
    if (remainingSize == 1) {
      soFar.add(left);
      results.add(List.copyOf(soFar));
      soFar.removeLast();
    } else {
      for (int i = 0; i <= left; i++) {
        soFar.add(i);
        sumTo(left - i, remainingSize - 1, soFar, results);
        soFar.removeLast();
      }
    }
    return results;
  }

  private int semiStream(List<Integer> goal, List<List<Integer>> buttons) {
    // One-press results
    List<int[]> soFar =
        new ArrayList<>(
            buttons.stream()
                .map(b -> newJoltageCounts(b, goal.size()))
                .filter(j -> !tooHigh(j, goal))
                .toList());
    int n = 1;
    if (soFar.stream().anyMatch(j -> justRight(j, goal))) {
      return n;
    } else {
      return semiStream(goal, buttons, soFar, n + 1);
    }
  }

  private int semiStream(List<Integer> goal, List<List<Integer>> buttons, List<int[]> prev, int n) {
    List<int[]> soFar =
        new ArrayList<>(
            prev.stream()
                .flatMap(j -> buttons.stream().map(b -> combineJoltageCounts(j, b)))
                .filter(j -> !tooHigh(j, goal))
                .toList());
    prev.clear();
    IO.println("semiStream depth %d soFar.size: %d".formatted(n, soFar.size()));
    if (soFar.stream().anyMatch(j -> justRight(j, goal))) {
      return n;
    } else {
      return semiStream(goal, buttons, soFar, n + 1);
    }
  }

  private int minJoltagePresses(List<Integer> goal, List<List<Integer>> buttons) {
    return joltagePresses(buttons, goal)
        .filter(p -> justRight(p.joltages(), goal))
        .findFirst()
        .map(JoltagePresses::num)
        .orElseThrow();
  }

  record JoltagePresses(int num, int[] joltages) {}

  private Stream<JoltagePresses> joltagePresses(List<List<Integer>> buttons, List<Integer> goal) {
    return IntStream.iterate(1, n -> n + 1)
        .boxed()
        .flatMap(n -> combos2(buttons, goal, n).map(j -> new JoltagePresses(n, j)));
  }

  private Stream<int[]> combos2(List<List<Integer>> buttons, List<Integer> goal, int n) {
    IO.println("Checking %d press combos".formatted(n));
    if (n == 1) {
      return buttons.stream()
          .map(b -> newJoltageCounts(b, goal.size()))
          .filter(j -> !tooHigh(j, goal));
    } else {
      return combos2(buttons, goal, n - 1)
          .flatMap(
              counts ->
                  buttons.stream()
                      .map(b -> combineJoltageCounts(counts, b))
                      .filter(j -> !tooHigh(j, goal)));
    }
  }

  private boolean tooHigh(int[] joltages, List<Integer> goal) {
    return IntStream.range(0, joltages.length).anyMatch(i -> joltages[i] > goal.get(i));
  }

  private boolean justRight(int[] joltages, List<Integer> goal) {
    return IntStream.range(0, joltages.length).allMatch(i -> joltages[i] == goal.get(i));
  }

  private int[] newJoltageCounts(List<Integer> button, int size) {
    int[] joltages = new int[size];
    for (int b : button) {
      joltages[b]++;
    }
    return joltages;
  }

  private int[] combineJoltageCounts(int[] joltages, List<Integer> button) {
    int[] newJoltages = Arrays.copyOf(joltages, joltages.length);
    for (int b : button) {
      newJoltages[b]++;
    }
    return newJoltages;
  }

  private List<Machine> machines(List<String> lines) {
    return lines.stream().map(Machine::valueOf).toList();
  }
}
