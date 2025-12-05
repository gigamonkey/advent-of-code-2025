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

  public Long part2(String[][] grid) {
    long total = 0L;
    long removed;
    do {
      removed = remove(grid);
      total += removed;
    } while (removed > 0);
    return total;
  }

  // This is more aggresive than described in the problem as it may remove rolls
  // in a pass due to their neighbors being cleared in the same pass. But it
  // doesn't seem to matter since they would get cleared in the next pass
  // anyway. But that means we can't use this or part1 because it removes more
  // than one pass's worth and thus the count is wrong.
  private long remove(String[][] grid) {
    long count = 0L;
    for (int r = 0; r < grid.length; r++) {
      for (int c = 0; c < grid[r].length; c++) {
        if (grid[r][c].equals("@") && neighboringRolls(grid, r, c) < 4) {
          grid[r][c] = ".";
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
    for (int r = row - 1; r <= row + 1; r++) {
      for (int c = col - 1; c <= col + 1; c++) {
        if (r != row || c != col) {
          if (inBounds(grid, r, c) && grid[r][c].equals("@")) {
            count++;
          }
        }
      }
    }
    return count;
  }
}
