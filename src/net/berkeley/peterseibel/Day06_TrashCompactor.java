package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.Arrays.stream;

import module java.base;

public class Day06_TrashCompactor extends Solution<String[][], Long> {

  public Day06_TrashCompactor() {
    super(6, Data::asStringGrid, Data::asLong);
  }

  public Long part1(String[][] grid) {
    long total = 0;
    for (int c = 0; c < grid[0].length; c++) {
      List<Long> nums = new ArrayList<>();
      for (int r = 0; r < grid.length - 1; r++) {
        nums.add(parseLong(grid[r][c]));
      }
      //IO.println(nums);
      var symbol = grid[grid.length - 1][c];
      if (symbol.equals("+")) {
        total += nums.stream().mapToLong(n -> n).sum();
      } else if (symbol.equals("*")) {
        total += nums.stream().mapToLong(n -> n).reduce(1, (acc, n) -> acc * n);
      }
    }
    return total;
  }

  public Long part2(String[][] grid) {
    return 0L;
  }
}
