package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;

import module java.base;

public class Day06_TrashCompactor extends Solution<List<String>, Long> {

  public Day06_TrashCompactor() {
    super(6, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    String[][] grid = simpleGrid(lines);
    long total = 0;
    for (int c = 0; c < grid[0].length; c++) {
      List<Long> nums = new ArrayList<>();
      for (int r = 0; r < grid.length - 1; r++) {
        nums.add(parseLong(grid[r][c]));
      }
      var symbol = grid[grid.length - 1][c];
      if (symbol.equals("+")) {
        total += nums.stream().mapToLong(n -> n).sum();
      } else if (symbol.equals("*")) {
        total += nums.stream().mapToLong(n -> n).reduce(1, (acc, n) -> acc * n);
      }
    }
    return total;
  }

  public Long part2(List<String> lines) {

    // Make mutable copies
    List<String> rows = new ArrayList<>(lines.stream().map(s -> s + " ").toList());
    List<String> columnSpecs = new ArrayList<>(Arrays.asList(rows.removeLast().splitWithDelimiters("\\s+", 0)));

    long total = 0;

    while (!columnSpecs.isEmpty()) {
      var width = columnSpecs.removeLast().length() + 1;
      var symbol = columnSpecs.removeLast();
      var digits = width - 1;

      List<String> column = extractColumn(rows, width);
      var nums = extractSquidNumbers(column, digits);

      if (symbol.equals("+")) {
        total += nums.stream().mapToLong(n -> n).sum();
      } else if (symbol.equals("*")) {
        total += nums.stream().mapToLong(n -> n).reduce(1, (acc, n) -> acc * n);
      }
    }
    return total;
  }

  private String[][] simpleGrid(List<String> lines) {
    return lines.stream().map(s -> s.trim().split("\\s+")).toArray(String[][]::new);
  }

  private List<String> extractColumn(List<String> rows, int width) {
    List<String> ns = new ArrayList<>();
    for (int i = 0; i < rows.size(); i++) {
      String line = rows.get(i);
      int colStart = line.length() - width;
      // last char is always space
      String chunk = line.substring(colStart, line.length() - 1);
      ns.add(chunk);
      // Mutate the line
      rows.set(i, line.substring(0, colStart));
    }
    return ns;
  }


  private List<Long> extractSquidNumbers(List<String> column, int digits ) {
    List<Long> nums = new ArrayList<>();
    for (int i = 0; i < digits; i++) {
      long n = 0;
      for (String s : column) {
        int idx = digits - 1 - i;
        char d = s.charAt(idx);
        if (d != ' ') {
          n *= 10;
          n += d - '0';
        }
      }
      nums.add(n);
    }
    return nums;
  }


}
