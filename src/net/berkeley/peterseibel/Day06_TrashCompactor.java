package net.berkeley.peterseibel;

import static java.lang.Long.parseLong;
import static java.lang.Math.*;
import static java.util.stream.Gatherers.scan;

import module java.base;

public class Day06_TrashCompactor extends Solution<List<String>, Long> {

  private static Pattern specPattern = Pattern.compile("(\\S\\s*)( |$)");

  private record Column(char symbol, int start, int width) {
    public static Column initial() {
      return new Column((char) 0, -1, 0);
    }

    public String extract(String line) {
      return line.substring(start, start + width);
    }

    public Column next(String s) {
      return new Column(s.charAt(0), start + width + 1, s.length());
    }
  }

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
    List<Column> specs = columnSpecs(lines.getLast());
    List<String> numberRows = lines.subList(0, lines.size() - 1);

    long total = 0;
    for (var spec : specs) {
      total += squidColumnValue(spec, numberRows);
    }
    return total;
  }

  private String[][] simpleGrid(List<String> lines) {
    return lines.stream().map(s -> s.trim().split("\\s+")).toArray(String[][]::new);
  }

  private List<Column> columnSpecs(String line) {
    return specPattern
        .matcher(line)
        .results()
        .map(m -> m.group(1))
        .gather(scan(Column::initial, Column::next))
        .toList();
  }

  private long squidColumnValue(Column spec, List<String> numberRows) {
    var column = numberRows.stream().map(spec::extract).toList();
    var nums = extractSquidNumbers(column, spec.width());

    if (spec.symbol() == '+') {
      return nums.stream().mapToLong(n -> n).sum();
    } else {
      return nums.stream().mapToLong(n -> n).reduce(1, (acc, n) -> acc * n);
    }
  }

  private List<Long> extractSquidNumbers(List<String> column, int width) {
    List<Long> nums = new ArrayList<>();
    for (int i = 0; i < width; i++) {
      long n = 0;
      for (String s : column) {
        char d = s.charAt(i);
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
