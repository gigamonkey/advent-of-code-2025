package net.berkeley.peterseibel;

import static java.lang.Math.*;

import module java.base;

public class Day04_PrintingDepartment extends Solution<String[][], Long> {

  public Day04_PrintingDepartment() {
    super(4, Data::asCharacterGrid, Data::asLong);
  }

  public Long part1(String[][] grid) {
    long count = 0L;
    for (int r = 0; r < grid.length; r++) {
      for (int c = 0; c < grid[r].length; c++) {
        if (grid[r][c].equals("@") && neighboringRolls(grid, r, c) < 4) {
          count++;
        }
      }
    }
    return count;
  }

  private boolean inBounds(String[][] grid, int r, int c) {
    return 0 <= r && r < grid.length && 0 <= c && c < grid[r].length;
  }

  private int neighboringRolls(String[][] grid, int row, int col) {
    int count = 0;
    for (int r = -1; r <= 1; r++) {
      for (int c = -1; c <= 1; c++) {
        if (r != 0 || c != 0) {
          if (inBounds(grid, row + r, col + c) && grid[row + r][col + c].equals("@")) {
            count++;
          }
        }
      }
    }
    return count;
  }

  public Long part2(String[][] grid) {
    return 0L;
  }
}
