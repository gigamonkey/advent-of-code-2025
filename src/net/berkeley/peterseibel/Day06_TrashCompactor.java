package net.berkeley.peterseibel;

import static java.lang.Math.*;
import static java.util.stream.Gatherers.scan;
import static java.util.stream.IntStream.range;

import module java.base;

public class Day06_TrashCompactor extends Solution<List<String>, Long> {

  private static Pattern specPattern = Pattern.compile("([+*]\\s*)( |$)");

  private record Column(char symbol, int start, int width) {

    public static List<Column> specs(String line) {
      return specPattern
          .matcher(line)
          .results()
          .map(m -> m.group(1))
          .gather(scan(Column::initial, Column::next))
          .toList();
    }

    public long value(List<String> numberRows, BiFunction<List<String>, Column, List<Long>> fn) {
      var column = numberRows.stream().map(this::extract).toList();
      return fn.apply(column, this).stream().mapToLong(n -> n).reduce(zero(), reducer());
    }

    private static Column initial() {
      return new Column((char) 0, -1, 0);
    }

    private Column next(String s) {
      return new Column(s.charAt(0), start + width + 1, s.length());
    }

    private String extract(String line) {
      return line.substring(start, start + width);
    }

    private long zero() {
      return symbol == '+' ? 0 : 1;
    }

    private LongBinaryOperator reducer() {
      return symbol == '+' ? (acc, n) -> acc + n : (acc, n) -> acc * n;
    }
  }

  public Day06_TrashCompactor() {
    super(6, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return solve(lines, this::extractHumanNumbers);
  }

  public Long part2(List<String> lines) {
    return solve(lines, this::extractSquidNumbers);
  }

  public Long solve(List<String> lines, BiFunction<List<String>, Column, List<Long>> fn) {
    List<Column> specs = Column.specs(lines.getLast());
    List<String> numberRows = lines.subList(0, lines.size() - 1);
    return specs.stream().mapToLong(spec -> spec.value(numberRows, fn)).sum();
  }

  private List<Long> extractHumanNumbers(List<String> column, Column ignore) {
    return column.stream().map(String::trim).map(Long::parseLong).toList();
  }

  private List<Long> extractSquidNumbers(List<String> column, Column spec) {
    return range(0, spec.width()).mapToLong(i -> squid(i, column)).boxed().toList();
  }

  private long squid(int i, List<String> column) {
    long n = 0;
    for (String s : column) {
      char d = s.charAt(i);
      if (d != ' ') {
        n *= 10;
        n += d - '0';
      }
    }
    return n;
  }
}
