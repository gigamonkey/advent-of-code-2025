package net.berkeley.peterseibel;

public class GCD {

  // Compute the GCD of two non-negative numbers using the Binary GCD algorithm
  // Based on Rust code from https://en.wikipedia.org/wiki/Binary_GCD_algorithm
  public static int gcd(int a, int b) {

    if (a == 0) return b;
    if (b == 0) return a;

    // Number of twos in prime factorization of gcd
    int twos = Integer.numberOfTrailingZeros(a | b);

    // Make each number odd.
    a >>= Integer.numberOfTrailingZeros(a);
    b >>= Integer.numberOfTrailingZeros(b);

    while (a != b) {
      if (a < b) {
        int tmp = a;
        a = b;
        b = tmp;
      }
      a -= b;
      a >>= Integer.numberOfTrailingZeros(a);
    }

    return a << twos;
  }
}
