package net.berkeley.peterseibel;

import static net.berkeley.peterseibel.GCD.gcd;
import static java.lang.Math.*;
import static java.util.Comparator.*;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.*;
import static java.util.stream.Gatherers.*;
import static java.util.stream.IntStream.*;

import module java.base;

public class Equations {

  // Names for variables. Assumes there are never more than 26 joltages.
  private static final String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");

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

    public Term divide(int amount);

    public default Term negated() {
      return multiply(-1);
    }

    public int coefficient();

    public int value(Map<String, Integer> bindings);

    public boolean isZero();

    public static int sum(List<? extends Term> terms, Map<String, Integer> bindings) {
      return terms.stream().mapToInt(t -> t.value(bindings)).sum();
    }
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

    public Term divide(int amount) {
      assert coefficient % amount == 0;
      return new Variable(name, coefficient / amount);
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

    @Override
    public String toString() {
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

    public Term divide(int amount) {
      assert value % amount == 0;
      return new Value(value / amount);
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

    @Override
    public String toString() {
      return "" + value;
    }
  }

  private static record Equation(List<? extends Term> left, List<? extends Term> right) {

    public static Equation sum(Collection<String> vars, int total) {
      return new Equation(vars.stream().map(Variable::of).toList(), List.of(new Value(total)));
    }

    // Make a new equation with one variable isolated on the left
    public Equation isolate(String name) {
      Map<Boolean, List<Term>> p = zeroForm().left.stream().collect(partitioningBy(t -> t.isVariable(name)));
      List<Term> right = p.get(false).stream().map(Term::negated).toList();
      Equation eq = new Equation(p.get(true), right);
      return signum(eq.left.get(0).coefficient()) == -1 ? eq.multiply(-1) : eq;
    }

    public Equation multiply(int amount) {
      return new Equation(
          left.stream().map(t -> t.multiply(amount)).toList(),
          right.stream().map(t -> t.multiply(amount)).toList());
    }

    public Equation divide(int amount) {
      return new Equation(
          left.stream().map(t -> t.divide(amount)).toList(),
          right.stream().map(t -> t.divide(amount)).toList());
    }

    public Equation isolateOne() {
      return firstVariable().map(v -> isolate(v.name())).orElse(this);
    }

    public Optional<Variable> firstVariable() {
      return aVariable(left).or(() -> aVariable(right));
    }

    private Optional<Variable> aVariable(List<? extends Term> terms) {
      return terms.stream()
          .filter(t -> t.isVariable() && abs(t.coefficient()) == 1)
          .findFirst()
          .map(Variable.class::cast);
    }

    // All variables on left and constant on right
    public Equation standardForm() {
      List<? extends Term> terms = zeroForm().left;
      return new Equation(terms.stream().filter(Term::isVariable).toList(),
                          terms.stream().filter(Term::isValue).map(Term::negated).toList());
    }

    // Right hand side is zero.
    public Equation zeroForm() {
      List<Term> terms = new ArrayList<>(left);
      terms.addAll(right.stream().map(Term::negated).toList());
      return new Equation(simplifyTerms(terms), List.of(Value.ZERO));
    }

    public boolean isDefinition() {
      return left.size() == 1 && left.get(0).isVariable() && left.get(0).coefficient() == 1;
    }

    public boolean isTautology() {
      // If after simplification there's nothing left it was a tautology.
      return left.isEmpty() && right.isEmpty();
    }

    public String definedVariable() {
      assert isDefinition();
      if (left.get(0) instanceof Variable v) {
        return v.name();
      } else {
        throw new Error("wat");
      }
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

      return new Equation(newLeft, newRight).divide(greatestCommonDivisor(newLeft, newRight));
    }

    private int greatestCommonDivisor(List<Term> left, List<Term> right) {
      return Stream.concat(left.stream(), right.stream()).mapToInt(Term::coefficient).map(Math::abs).reduce(GCD::gcd).orElse(1);
    }

    private static List<Term> substituted(
        List<? extends Term> terms, String name, List<? extends Term> defn) {
      return terms.stream()
          .flatMap(
              t -> {
                if (t.isVariable(name)) {
                  return defn.stream().map(d -> d.multiply(t.coefficient()));
                } else {
                  return Stream.of(t);
                }
              })
          .toList();
    }

    private static List<Term> simplifyTerms(List<? extends Term> terms) {
      List<Term> newTerms =
          Stream.concat(simplifyVariables(terms).stream(), Stream.of(simplifyConstants(terms)))
              .filter(t -> !t.isZero())
              .collect(toCollection(ArrayList::new));
      return newTerms.isEmpty() ? new ArrayList<>(List.of(Value.ZERO)) : newTerms;
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

    public boolean isTrue(Map<String, Integer> bindings) {
      return Term.sum(left, bindings) == Term.sum(right, bindings);
    }

    @Override
    public String toString() {
      return left.stream().map(Object::toString).collect(joining(" + "))
          + " = "
          + right.stream().map(Object::toString).collect(joining(" + "));
    }
  }

  private static Map<String, List<Integer>> buttonVariables(List<List<Integer>> buttons) {
    Map<String, List<Integer>> variables = new HashMap<>();
    for (int i = 0; i < buttons.size(); i++) {
      variables.put(alphabet[i], buttons.get(i));
    }
    return variables;
  }

  private static Set<Equation> buttonSums(
      List<Integer> joltages, Map<String, List<Integer>> variables) {
    Set<Equation> eqs = new HashSet<>();
    for (int i = 0; i < joltages.size(); i++) {
      eqs.add(Equation.sum(touchesJoltage(i, variables), joltages.get(i)));
    }
    return eqs;
  }

  private static Set<String> touchesJoltage(int i, Map<String, List<Integer>> variables) {
    return variables.keySet().stream().filter(k -> variables.get(k).contains(i)).collect(toSet());
  }

  /**
   * Simplify list of equations down to mostly isolated variables.
   */
  private static Set<Equation> simplify(Set<Equation> eqns, Set<Equation> soFar) {
    if (eqns.isEmpty()) {
      return soFar;
    } else {
      Equation origEq = eqns.iterator().next();
      eqns.remove(origEq);
      Equation eq = origEq.isolateOne();

      Set<Equation> newSoFar = new HashSet<>(Set.of(eq));

      if (eq.isDefinition()) {
        String var = eq.firstVariable().orElseThrow().name();
        var defn = eq.right();
        newSoFar.addAll(substituteVar(soFar, var, defn));
        return simplify(substituteVar(eqns, var, defn), newSoFar);

      } else {
        newSoFar.addAll(soFar);
        return simplify(eqns, newSoFar);
      }
    }
  }

  private static Set<Equation> substituteVar(
      Set<Equation> eqns, String var, List<? extends Term> defn) {
    return eqns.stream()
        .map(e -> e.substitute(var, defn))
        .filter(e -> !e.isTautology())
        .collect(toCollection(HashSet::new));
  }

  private static Map<String, List<? extends Term>> isolated(Set<Equation> eqns) {
    return eqns.stream()
        .filter(Equation::isDefinition)
        .collect(toMap(Equation::definedVariable, Equation::right));
  }

  private static Set<Equation> nonIsolated(Set<Equation> eqns) {
    return eqns.stream().filter(not(Equation::isDefinition)).collect(toSet());
  }

  private static Stream<List<Integer>> kTuplesWithSum(int k, int sum) {
    if (k == 1) {
      return Stream.of(List.of(sum));
    } else {
      return rangeClosed(0, sum)
          .boxed()
          .flatMap(
              first -> {
                return kTuplesWithSum(k - 1, sum - first)
                    .map(
                        t -> {
                          List<Integer> tuple = new ArrayList<>();
                          tuple.add(first);
                          tuple.addAll(t);
                          return tuple;
                        });
              });
    }
  }

  private static Stream<List<Integer>> kTuples(int k, int max) {
    return rangeClosed(0, max).boxed().flatMap(sum -> kTuplesWithSum(k, sum));
  }

  private static Stream<Map<String, Integer>> bindings(Set<String> vars, int max) {
    List<String> vs = List.copyOf(vars);
    return kTuples(vs.size(), max)
        .map(
            values -> {
              Map<String, Integer> bindings = new HashMap<>();
              for (int i = 0; i < values.size(); i++) {
                bindings.put(vs.get(i), values.get(i));
              }
              return bindings;
            });
  }

  private static boolean allNonNegative(
      Map<String, List<? extends Term>> isos, Map<String, Integer> bindings) {
    return isos.values().stream().allMatch(ts -> Term.sum(ts, bindings) >= 0);
  }

  private static boolean allTrue(Set<Equation> nonIsos, Map<String, Integer> bindings) {
    return nonIsos.stream().allMatch(e -> e.isTrue(bindings));
  }

  private static Equation presses(Set<Equation> eqns, Map<String, List<Integer>> variables) {
    var vars = variables.keySet().stream().map(Variable::of).toList();
    Equation presses = new Equation(List.of(Variable.of("presses")), vars);

    for (Equation eq : eqns) {
      if (eq.isDefinition()) {
        presses = presses.substitute(eq.definedVariable(), eq.right());
      }
    }
    return presses;
  }

  private static int maxPresses(
      Day10_Factory.Machine m, Set<String> freeVars, Map<String, List<Integer>> variables) {
    return freeVars.stream()
        .map(variables::get)
        .mapToInt(
            b -> b.stream().map(n -> m.joltages().get(n)).mapToInt(n -> n).min().orElseThrow())
        .sum();
  }

  public static int answer(Day10_Factory.Machine m) {

    var variables = buttonVariables(m.buttons());
    var eqns = simplify(buttonSums(m.joltages(), variables), Set.of());

    var presses = presses(eqns, variables);

    Map<String, List<? extends Term>> isos = isolated(eqns);
    Set<Equation> nonIsos = nonIsolated(eqns);

    if (!nonIsos.isEmpty()) {
      nonIsos.stream().map(Equation::standardForm).forEach(IO::println);
    }

    Set<String> freeVars = variables.keySet();
    freeVars.removeAll(isos.keySet());

    if (freeVars.isEmpty()) {
      assert presses.right().size() == 1;
      return Term.sum(presses.right(), Map.of());
    } else {

      var right = presses.right();
      return bindings(freeVars, maxPresses(m, freeVars, variables))
          .filter(b -> allNonNegative(isos, b))
          .filter(b -> allTrue(nonIsos, b))
          .mapToInt(b -> Term.sum(right, b))
          .min()
          .orElseThrow();
    }
  }
}
