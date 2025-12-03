package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.time.temporal.ChronoUnit.*;

import module java.base;

public class AdventOfCode {

  private static final ZoneId TZ = ZoneId.of("America/New_York");
  private static final ZonedDateTime START = ZonedDateTime.of(2025, 12, 1, 0, 0, 0, 0, TZ);
  private static final int TODAY = (int) DAYS.between(START, ZonedDateTime.now(TZ)) + 1;

  private static final List<Solution<?, ?>> SOLUTIONS =
      List.of(new Day01_SecretEntrance(), new Day02_GiftShop(), new Day03_Lobby());

  private static Optional<Solution<?, ?>> solutionFor(int day) {
    if ((day - 1) < SOLUTIONS.size()) {
      return Optional.of(SOLUTIONS.get(day - 1));
    } else {
      return Optional.empty();
    }
  }

  private static void noCode(int day) {
    IO.println("No code for day %d.".formatted(day));
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equals("--all")) {
      SOLUTIONS.forEach(Solution::check);
    } else {
      int day = args.length > 0 ? parseInt(args[0]) : TODAY;
      solutionFor(day).ifPresentOrElse(Solution::check, () -> noCode(day));
    }
  }
}
