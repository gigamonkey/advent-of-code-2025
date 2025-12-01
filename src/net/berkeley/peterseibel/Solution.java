package net.berkeley.peterseibel;

import module java.base;

public interface Solution<T, R> {

  /**
   * Solve part 1.
   */
  public R part1(T input) throws Exception;

  /**
   * Solve part 2.
   */
  public default R part2(T input) throws Exception {
    throw new Error("NYI");
  }
}
