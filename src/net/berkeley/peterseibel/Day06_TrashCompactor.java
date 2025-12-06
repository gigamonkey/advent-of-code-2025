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
    return squidGrid(lines);
  }

  private String[][] simpleGrid(List<String> lines) {
    return lines.stream().map(s -> s.trim().split("\\s+")).toArray(String[][]::new);
  }

  private long squidGrid(List<String> lines) {
    // To find width of columns.
    List<String> all = new ArrayList<>(lines.stream().map(s -> s + " ").toList());
    String lastLine = all.removeLast();
    //// IO.println("lastLine: '%s'".formatted(lastLine));
    List<String> columns = new ArrayList<>(Arrays.asList(lastLine.splitWithDelimiters("\\s+", 0)));
    String[] numbers = all.toArray(new String[0]);

    // IO.println(columns);
    // IO.println(columns.stream().mapToInt(String::length).boxed().toList());

    long total = 0;

    while (!columns.isEmpty()) {
      var width = columns.removeLast().length() + 1;
      var symbol = columns.removeLast();
      var digits = width - 1;

      // IO.println("width: %d; symbol: %s".formatted(width, symbol));

      // Numbers in this column with padding
      List<String> ns = new ArrayList<>();
      for (int i = 0; i < numbers.length; i++) {
        String line = numbers[i];
        int colStart = line.length() - width;
        // last char is space
        String chunk = line.substring(colStart, line.length() - 1);
        // IO.println("lineLength: %d; colStart; %d; chunk: '%s'".formatted(line.length(), colStart,
        // chunk));
        ns.add(chunk);
        numbers[i] = line.substring(0, colStart);
      }

      List<Long> nums = new ArrayList<>();
      for (int i = 0; i < digits; i++) {
        long n = 0;
        for (String s : ns) {
          int idx = digits - 1 - i;
          char d = s.charAt(idx);
          if (d != ' ') {
            n *= 10;
            n += d - '0';
          }
        }
        nums.add(n);
      }

      if (symbol.equals("+")) {
        // IO.println("Adding " + nums);
        total += nums.stream().mapToLong(n -> n).sum();
      } else if (symbol.equals("*")) {
        // IO.println("Multiplying " + nums);
        total += nums.stream().mapToLong(n -> n).reduce(1, (acc, n) -> acc * n);
      }
    }
    return total;
  }

  // private List<Long> numsToCephalopod(List<Long> nums) {
  //   List<Long> newNums = new ArrayList<>();
  //   int maxDigits = nums.stream().mapToInt(n -> (int) ceil(log10(n)));
  //   List<String> padded = nums.stream().map(n -> {
  //       int d = (int) ceil(log10(n));
  //       return "" + d + "0".repeat(maxDigits - d);
  //     });

  //   for (int c = 0; c <
  //   while (true) {
  //     long n = 0;
  //     for (int i = 0; i < nums.size(); i++) {

  //     }
  //   }
  // }
}
