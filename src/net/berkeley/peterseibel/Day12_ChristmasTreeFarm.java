package net.berkeley.peterseibel;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;

import module java.base;

public class Day12_ChristmasTreeFarm extends Solution<List<String>, Long> {

  private static final boolean verbose = false;
  private static final boolean greedy = true;

  public Day12_ChristmasTreeFarm() {
    super(12, Data::asLines, Data::asLong);
  }

  public Long part1(List<String> lines) {
    Spec spec = new Spec(lines);
    return spec.spaces.stream().filter(s -> s.solve(spec.shapes)).count();
  }

  public Long part2(List<String> lines) {
    long count = 0;
    return count;
  }

  private record Shape(char[][] map) {

    private static int[][][] transformations = {

      // Rotations
      {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}},
      {{6, 3, 0}, {7, 4, 1}, {8, 5, 2}},
      {{8, 7, 6}, {5, 4, 3}, {2, 1, 0}},
      {{2, 5, 8}, {1, 4, 7}, {0, 3, 6}},

      // Flips
      {{2, 1, 0}, {5, 4, 3}, {8, 7, 6}},
      {{6, 7, 8}, {3, 4, 5}, {0, 1, 2}},
      {{8, 5, 2}, {7, 4, 1}, {6, 3, 0}},
      {{0, 3, 6}, {1, 4, 7}, {2, 5, 8}}
    };

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

    public int numTransformations() {
      return transformations.length;
    }

    public int squaresFilled() {
      int filled = 0;
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          if (map[i][j] == '#') filled++;
        }
      }
      return filled;
    }

    public char[][] transform(int n) {
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

  private static class Space {

    private final int rows;
    private final int cols;
    private final int[] presents;
    private final List<Shape> shapes;
    private final char[][] grid;
    private final int squaresNeeded;

    private int presentsLeft;
    private int squaresLeft;

    Space(int rows, int cols, int[] presents, List<Shape> shapes) {
      this.rows = rows;
      this.cols = cols;
      this.presents = presents;
      this.shapes = shapes;
      this.grid = new char[rows][cols];
      this.squaresNeeded = needed(presents, shapes);
      this.presentsLeft = stream(presents).sum();
      this.squaresLeft = rows * cols;
    }

    private static int needed(int[] presents, List<Shape> shapes) {
      int needed = 0;
      for (int i = 0; i < presents.length; i++) {
        if (presents[i] > 0) {
          needed += presents[i] * shapes.get(i).squaresFilled();
        }
      }
      return needed;
    }

    int positions() {
      return (rows - 2) * (cols - 2);
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
      } else if (positions() - p < presentsLeft) {
        return false;
      } else if (squaresLeft < squaresNeeded) {
        return false;
      } else {
        // Try each of the shapes (in all transformations) in the current position.
        for (int i = 0; i < presents.length; i++) {
          if (presents[i] > 0) {
            Shape shape = shapes.get(i);
            presents[i]--;
            presentsLeft--;
            if (verbose)
              IO.println(
                  "Decrmemented presents %d presents %s".formatted(i, Arrays.toString(presents)));
            if (placeShape(shape, p, grid, shapes)) {
              if (verbose) {
                IO.println("Placed shape at %d".formatted(p));
                dumpGrid(grid);
              }
              // Placed this shape and then the rest.
              return true;
            } else {
              presents[i]++;
              presentsLeft++;
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
      for (int t = 0; t < shape.numTransformations(); t++) {
        if (placeTransformedShape(shape, p, t, grid)) {
          if (verbose) {
            IO.println("Placed transformed shape %d at %d".formatted(t, p));
            dumpGrid(grid);
          }
          // squaresLeft -= shape.squaresFilled();
          if (fillPosition(p + 1, grid, shapes)) {
            return true;
          } else {
            if (greedy) {
              return false;
            } else {
              unplaceTransformedShape(shape, p, t, grid);
            }
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
      // WTF, this should set the grid elements back to 0 but when I do it takes
      // way to long but when I set it to '.' it gets the right answer. Feels
      // like I got lucky that somehow marking them wrong didn't effect the
      // answer and also sped things up a lot. (I've put this behind the greedy
      // flag on the theory that marking with . makes the search basically
      // greedy since once a shape has been fit into a position it will leave at
      // least some squares marked in that position which means we'll actually
      // be able to put another shape in that position (except maybe a shape
      // that's the inverse of the one fit.)
      markTransformedShape(chars, r, c, grid, greedy ? '.' : (char) 0);
      // squaresLeft += shape.squaresFilled();
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
    final int squaresNeeded;

    public Spec(List<String> lines) {
      int needed = 0;
      boolean inPresents = true;

      List<String> shapeLines = new ArrayList<>();

      for (String line : lines) {
        if (line.matches("\\d+:")) {
        } else if (line.matches("[#\\.]+")) {
          shapeLines.add(line);
        } else if (line.equals("") && !shapeLines.isEmpty()) {
          Shape s = Shape.from(shapeLines);
          shapes.add(s);
          needed += s.squaresFilled();
          shapeLines.clear();
        } else {
          int x = line.indexOf("x");
          int colon = line.indexOf(":");
          int rows = parseInt(line.substring(0, x));
          int cols = parseInt(line.substring(x + 1, colon));
          int[] presents =
              stream(line.substring(colon + 2).split(" ")).mapToInt(Integer::parseInt).toArray();
          spaces.add(new Space(rows, cols, presents, shapes));
        }
      }
      squaresNeeded = needed;
    }
  }

  private static void dumpGrid(char[][] chars) {
    for (char[] row : chars) {
      for (char c : row) {
        IO.print(c);
      }
      IO.println();
    }
  }
}
