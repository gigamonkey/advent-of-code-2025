package net.berkeley.peterseibel;

import module java.base;

import static java.lang.Math.*;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;
import static java.util.stream.IntStream.*;
import static java.util.Arrays.stream;

public class Equations {

  private static final String[] alphabet = "abcdefghijqlmnopqrstuvwxyz".split("");

  static sealed interface Term permits Variable, Value {

    public default boolean isVariable() {
      return false;
    }

    public default boolean isVariable(String name) {
      return false;
    }

    public default boolean isValue() {
      return !isVariable();
    }

    public Term multiply(int amount);

    public default Term negated() {
      return multiply(-1);
    }

    public int coefficient();

    public int value(Map<String, Integer> bindings);

    public boolean isZero();

  }

  private static record Variable(String name, int coefficient)
      implements Term, Comparable<Variable> {

    private static Variable of(String name) {
      return new Variable(name, 1);
    }

    public boolean sameName(Variable other) {
      return name.equals(other.name);
    }

    public boolean isVariable() {
      return true;
    }

    public boolean isVariable(String name) {
      return this.name.equals(name);
    }

    public Term multiply(int amount) {
      return new Variable(name, coefficient * amount);
    }

    public int value(Map<String, Integer> bindings) {
      return bindings.get(name) * coefficient;
    }

    public Variable add(Variable other) {
      assert other.name.equals(name)
          : "Can only add variables of same name %s != %s".formatted(name, other.name);
      return new Variable(name, coefficient + other.coefficient);
    }

    public boolean isZero() {
      return coefficient == 0;
    }

    public int compareTo(Variable other) {
      return name.compareTo(other.name);
    }

    public static List<Term> combine(List<Variable> vars) {
      List<Term> combined = new ArrayList<>();
      Variable prev = null;
      for (var v : vars.stream().sorted().toList()) {
        if (prev == null) {
          prev = v;
        } else {
          if (v.sameName(prev)) {
            prev = prev.add(v);
          } else {
            if (!prev.isZero()) {
              combined.add(prev);
            }
            prev = v;
          }
        }
      }
      if (prev != null) combined.add(prev);

      return combined;
    }

    @Override public String toString() {
      if (coefficient == 1) {
        return "" + name;
      } else if (coefficient == -1) {
        return "-" + name;
      } else {
        return coefficient + "" + name;
      }
    }
  }

  private static record Value(int value) implements Term {

    public static Value ZERO = new Value(0);

    public Term multiply(int amount) {
      return new Value(value * amount);
    }


    public int value(Map<String, Integer> bindings) {
      return value;
    }

    public Value add(Value other) {
      return new Value(value + other.value);
    }

    // kludge?
    public int coefficient() {
      return value;
    }

    public boolean isZero() {
      return value == 0;
    }

    @Override public String toString() { return "" + value; }

  }

  private static record Equation(List<? extends Term> left, List<? extends Term> right) {

    public static Equation sum(Collection<String> vars, int total) {
      return new Equation(vars.stream().map(Variable::of).toList(), List.of(new Value(total)));
    }

    // Make a new equation with one variable isolated on the left
    public Equation isolate(String name) {
      List<Term> newLeft = new ArrayList<>();
      List<Term> newRight = new ArrayList<>();
      for (Term t : left) {
        if (t.isVariable(name)) {
          newLeft.add(t);
        } else {
          newRight.add(t.negated());
        }
      }
      for (Term t : right) {
        if (t.isVariable(name)) {
          newLeft.add(t.negated());
        } else {
          newRight.add(t);
        }
      }
      return new Equation(newLeft, newRight); // .simplify();
    }

    public Equation isolateFirst() {
      return firstVariable().map(v -> isolate(v.name())).orElse(this);
    }

    public Optional<Variable> firstVariable() {
      Optional<Variable> v = left.stream().filter(t -> t.isVariable()).findFirst().map(Variable.class::cast);
      return v.or(() -> right.stream().filter(t -> t.isVariable()).findFirst().map(Variable.class::cast));
    }

    // New equation with all variables on left and constant on right
    public Equation standardForm() {
      List<Term> newLeft =
          Stream.concat(
                  left.stream().filter(Term::isVariable),
                  right.stream().filter(Term::isVariable).map(Term::negated))
              .sorted()
              .toList();

      List<Term> newRight =
          Stream.concat(
                  left.stream().filter(Term::isValue).map(Term::negated),
                  right.stream().filter(Term::isVariable))
              .toList();

      return new Equation(newLeft, newRight).simplify();
    }

    public boolean isDefinition() {
      return left.size() == 1 && left.get(0).isVariable();
    }

    private Equation substitute(String name, List<? extends Term> defn) {
      return new Equation(substituted(left, name, defn), substituted(right, name, defn)).simplify();
    }


    private Equation simplify() {
      List<Term> newLeft = simplifyTerms(left);
      List<Term> newRight = simplifyTerms(right);

      Set<Term> common = new HashSet<>(newLeft);
      common.retainAll(newRight);

      newLeft.removeAll(common);
      newRight.removeAll(common);

      return new Equation(newLeft, newRight);
    }

    private static List<Term> substituted(List<? extends Term> terms, String name, List<? extends Term> defn) {
      return terms.stream()
        .flatMap(t -> {
            if (t.isVariable(name)) {
              return defn.stream().map(d -> d.multiply(t.coefficient()));
            } else {
              return Stream.of(t);
            }
          })
        .toList();
    }

    private static List<Term> simplifyTerms(List<? extends Term> terms) {
      List<Term> newTerms = new ArrayList<>();
      newTerms.addAll(simplifyVariables(terms));
      newTerms.add(simplifyConstants(terms));
      return newTerms.stream().filter(t -> !t.isZero()).collect(toCollection(ArrayList::new));
    }

    private static Term simplifyConstants(List<? extends Term> terms) {
      return terms.stream()
          .filter(Term::isValue)
          .map(Value.class::cast)
          .reduce(Value.ZERO, Value::add);
    }

    private static List<Term> simplifyVariables(List<? extends Term> terms) {
      return Variable.combine(
          terms.stream().filter(Term::isVariable).map(Variable.class::cast).toList());
    }

    @Override public String toString() {
      return left.stream().map(Object::toString).collect(joining(" + ")) + " = " + right.stream().map(Object::toString).collect(joining(" + "));
    }
  }

  private static class System {
    private Set<Equation> equations = new HashSet<>();

    // add equations to system

    // Boil the initial system of equations down to a system where for each
    // original variable there is one equation with it on the left side and a
    // right side consisting of only
    public System simplify() {
      return null;
    }

  }


  private Set<Variable> variables = new HashSet<>();
  private Map<Variable, Equation> bindings = new HashMap<>();


  private static Map<String, List<Integer>> buttonVariables(List<List<Integer>> buttons) {
    Map<String, List<Integer>> variables = new HashMap<>();
    for (int i = 0; i < buttons.size(); i++) {
      variables.put(alphabet[i], buttons.get(i));
    }
    return variables;
  }

  private static List<Equation> buttonSums(List<Integer> joltages, Map<String, List<Integer>> variables) {
    List<Equation> eqs = new ArrayList<>();
    for (int i = 0; i < joltages.size(); i++) {
      eqs.add(Equation.sum(touchesJoltage(i, variables), joltages.get(i)));
    }
    return eqs;
  }


  private static Set<String> touchesJoltage(int i, Map<String, List<Integer>> variables) {
    return variables.keySet().stream().filter(k -> variables.get(k).contains(i)).collect(toSet());
  }

  private static List<Equation> simplify(List<Equation> eqns, List<Equation> soFar) {
    if (eqns.isEmpty()) {
      return soFar;
    } else {
      Equation eq = eqns.removeLast().isolateFirst();

      List<Equation> newSoFar = new ArrayList<>();
      newSoFar.add(eq);

      if (eq.isDefinition()) {
        String var = eq.firstVariable().orElseThrow().name();
        var defn = eq.right();
        List<Equation> newEqns = new ArrayList<>(eqns.stream().map(e -> e.substitute(var, defn)).toList());
        newSoFar.addAll(soFar.stream().map(e -> e.substitute(var, defn)).toList());
        return simplify(newEqns, newSoFar);
      } else {
        newSoFar.addAll(soFar);
        return simplify(eqns, newSoFar);
      }
    }
  }



  public static void main() {
    String spec = "[.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}";

    Day10_Factory.Machine m = Day10_Factory.Machine.valueOf(spec);

    IO.println(m);

    Map<String, List<Integer>> variables = buttonVariables(m.buttonsAsLists());
    List<Equation> eqs = buttonSums(m.joltages(), variables);

    IO.println("Initial equations");
    eqs.forEach(IO::println);

    // 3. Remove an equation from the original set and isolate one variable.
    List<Equation> foo = eqs.stream().map(Equation::isolateFirst).toList();

    IO.println("Isolated equations");
    foo.forEach(IO::println);


    List<Equation> newEqns = simplify(eqs, List.of());
    IO.println("Simplified equations");
    newEqns.forEach(IO::println);

    Equation presses = new Equation(
      List.of(Variable.of("presses")),
      variables.keySet().stream().map(Variable::of).toList());

    IO.println(presses);


    // 4. Eliminate that variable from all other equations in original set and in new set.

    // 5. Put isolated equation in new set.

    // 6. Unless original set is empty, go to step 3.

    // At this point the new set should contain a bunch of isolated-variable
    // equations. And the variables on the left hand side will have been removed
    // from the right hand sides. However there will likely one or more
    // variables on the right hand side that don't have an isolated definition
    // (because otherwise they would have been eliminated from the right hand
    // sides.)

    // Put everything in standard form?



  }

}
