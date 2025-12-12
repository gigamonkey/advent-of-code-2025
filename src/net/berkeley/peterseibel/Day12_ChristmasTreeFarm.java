package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day12_ChristmasTreeFarm extends Solution<List<String>, Long> {

  private static final boolean verbose = false;

  public static int[][][] transformations = {

    // Rotations
    {
      {0, 1, 2},
      {3, 4, 5},
      {6, 7, 8},
    },
    {
      {6, 3, 0},
      {7, 4, 1},
      {8, 5, 2}
    },
    {
      {8, 7, 6},
      {5, 4, 3},
      {2, 1, 0}
    },
    {
      {2, 5, 8},
      {1, 4, 7},
      {0, 3, 6}
    },

    // Flips
    {
      {2, 1, 0},
      {5, 4, 3},
      {8, 7, 6}
    },
    {
      {6, 7, 8},
      {3, 4, 5},
      {0, 1, 2}
    },
    {
      {8, 5, 2},
      {7, 4, 1},
      {6, 3, 0}
    },
    {
      {0, 3, 6},
      {1, 4, 7},
      {2, 5, 8}
    }
  };

  public Day12_ChristmasTreeFarm() {
    super(12, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    return parseInput(lines).spaces.stream()
        .peek(IO::println)
        .filter(space -> space.solve(spec.shapes))
        .count();
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }

  private static void dump(char[][] chars) {
    for (char[] row : chars) {
      for (char c : row) {
        IO.print(c);
      }
      IO.println();
    }
  }

  private record Shape(char[][] map) {

    static Shape from(List<String> lines) {
      return new Shape(
          lines.stream()
              .map(
                  line -> {
                    char[] row = new char[line.length()];
                    for (int i = 0; i < row.length; i++) {
                      row[i] = line.charAt(i);
                    }
                    return row;
                  })
              .toArray(char[][]::new));
    }

    char[][] transform(int n) {
      int[][] t = transformations[n];
      char[][] transformed = new char[3][3];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          int tr = t[i][j];
          transformed[i][j] = map[tr / 3][tr % 3];
        }
      }
      return transformed;
    }
  }

  private record Space(int rows, int cols, int[] presents) {

    int positions() {
      return (rows() - 2) * (cols - 2);
    }

    boolean solve(List<Shape> shapes) {
      if (verbose)
        IO.println(
            "Solving %dx%d space with presents: %s"
                .formatted(rows, cols, Arrays.toString(presents)));
      char[][] grid = new char[rows][cols];
      return fillPosition(0, grid, shapes);
    }

    boolean fillPosition(int p, char[][] grid, List<Shape> shapes) {
      if (verbose)
        IO.println("Fill position %d presents %s".formatted(p, Arrays.toString(presents)));
      if (stream(presents).allMatch(n -> n == 0)) {
        return true;
      } else if (p >= positions()) {
        return false;
      } else {
        // Try each of the shapes (in all transformations) in the current position.
        for (int i = 0; i < presents.length; i++) {
          if (presents[i] > 0) {
            Shape shape = shapes.get(i);
            presents[i]--;
            if (verbose)
              IO.println(
                  "Decrmemented presents %d presents %s".formatted(i, Arrays.toString(presents)));
            if (placeShape(shape, p, grid, shapes)) {
              if (verbose) {
                IO.println("Placed shape at %d".formatted(p));
                dump(grid);
              }
              // Placed this shape and then the rest.
              return true;
            } else {
              presents[i]++;
              if (verbose)
                IO.println(
                    "Incremented presents %d presents %s".formatted(i, Arrays.toString(presents)));
            }
          }
        }
        // Didn't find a solution with anything in the current position so maybe
        // we can solve it with nothing in this position.
        return fillPosition(p + 1, grid, shapes);
      }
    }

    boolean placeShape(Shape shape, int p, char[][] grid, List<Shape> shapes) {
      for (int t = 0; t < transformations.length; t++) {
        if (placeTransformedShape(shape, p, t, grid)) {
          if (verbose) IO.println("Placed transformed shape %d at %d".formatted(t, p));
          if (verbose) dump(grid);
          if (fillPosition(p + 1, grid, shapes)) {
            return true;
          } else {
            unplaceTransformedShape(shape, p, t, grid);
          }
        }
      }
      return false;
    }

    boolean placeTransformedShape(Shape shape, int p, int t, char[][] grid) {
      char[][] chars = shape.transform(t);
      int r = p / (cols - 2);
      int c = p % (cols - 2);
      if (fits(chars, r, c, grid)) {
        if (verbose) IO.println("Shape fits");
        markTransformedShape(chars, r, c, grid, '#');
        return true;
      } else {
        if (verbose) IO.println("Shape does not fit");
        return false;
      }
    }

    void unplaceTransformedShape(Shape shape, int p, int t, char[][] grid) {
      char[][] chars = shape.transform(t);
      int r = p / (cols - 2);
      int c = p % (cols - 2);
      markTransformedShape(chars, r, c, grid, '.');
    }

    boolean fits(char[][] chars, int r, int c, char[][] grid) {
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (chars[i][j] == '#' && grid[r + i][c + j] != 0) {
            return false;
          }
        }
      }
      return true;
    }

    void markTransformedShape(char[][] chars, int r, int c, char[][] grid, char mark) {
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (chars[i][j] == '#') {
            grid[r + i][c + j] = mark;
          }
        }
      }
    }
  }

  private static class Spec {
    List<Shape> shapes = new ArrayList<>();
    List<Space> spaces = new ArrayList<>();
  }

  private static Spec parseInput(List<String> lines) {
    Spec spec = new Spec();

    boolean inPresents = true;

    List<String> shapeLines = new ArrayList<>();

    for (String line : lines) {
      if (line.matches("\\d+:")) {
      } else if (line.matches("[#\\.]+")) {
        shapeLines.add(line);
      } else if (line.equals("") && !shapeLines.isEmpty()) {
        spec.shapes.add(Shape.from(shapeLines));
        shapeLines.clear();
      } else {
        int x = line.indexOf("x");
        int colon = line.indexOf(":");
        int rows = parseInt(line.substring(0, x));
        int cols = parseInt(line.substring(x + 1, colon));
        int[] presents =
            stream(line.substring(colon + 2).split(" ")).mapToInt(Integer::parseInt).toArray();
        spec.spaces.add(new Space(rows, cols, presents));
      }
    }
    return spec;
  }
}
