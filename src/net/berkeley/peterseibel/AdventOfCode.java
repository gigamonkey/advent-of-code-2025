package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.*;

import module java.base;

public class AdventOfCode {

  private static final ZoneId TZ = ZoneId.of("America/New_York");
  private static final ZonedDateTime START = ZonedDateTime.of(2025, 12, 1, 0, 0, 0, 0, TZ);
  private static final int TODAY = (int) DAYS.between(START, ZonedDateTime.now(TZ)) + 1;

  private static List<Solution<?, ?>> solutions;

  static {
    try (var in = AdventOfCode.class.getResourceAsStream("solutions.txt")) {
      solutions = new String(in.readAllBytes()).lines().map(AdventOfCode::loadSolution).toList();
    } catch (IOException ioe) {
      throw new Error(ioe);
    }
  }

  private static Solution<?, ?> loadSolution(String name) {
    try {
      var pkg = AdventOfCode.class.getPackage().getName();
      var clazz = Class.forName(pkg + "." + name).asSubclass(Solution.class);
      return clazz.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException roe) {
      throw new Error(roe);
    }
  }

  private static Optional<Solution<?, ?>> solutionFor(int day) {
    if ((day - 1) < solutions.size()) {
      return Optional.of(solutions.get(day - 1));
    } else {
      return Optional.empty();
    }
  }

  private static void noCode(int day) {
    IO.println("No code for day %d.".formatted(day));
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equals("--all")) {
      solutions.forEach(Solution::check);
    } else {
      int day = args.length > 0 ? parseInt(args[0]) : TODAY;
      solutionFor(day).ifPresentOrElse(Solution::check, () -> noCode(day));
    }
  }
}
