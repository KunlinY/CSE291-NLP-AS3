package edu.berkeley.nlp.assignments.parsing.math;

import edu.berkeley.nlp.assignments.parsing.util.Triple;

import java.util.Collection;

public final class SloppyMath  {


  private SloppyMath() {}  // this class is just static methods.

  public static double round(double x) {
    return Math.floor(x + 0.5d);
  }

  public static double round(double x, int precision) {
    double power = Math.pow(10.0, precision);
    return round(x * power) / power;
  }

  public static int max(int a, int b, int c) {
    int ma;
    ma = a;
    if (b > ma) {
      ma = b;
    }
    if (c > ma) {
      ma = c;
    }
    return ma;
  }

  public static int max(Collection<Integer> vals) {
    if (vals.isEmpty()) { throw new RuntimeException(); }
    int max = Integer.MIN_VALUE;

    for (int i : vals) {
      if (i > max) { max = i; }
    }

    return max;
  }

  public static float max(float a, float b) {
    return (a >= b) ? a : b;
  }

  public static double max(double a, double b) {
    return (a >= b) ? a : b;
  }

  public static int min(int a, int b, int c) {
    int mi;

    mi = a;
    if (b < mi) {
      mi = b;
    }
    if (c < mi) {
      mi = c;
    }
    return mi;
  }

  public static float min(float a, float b) {
    return (a <= b) ? a : b;
  }

  public static double min(double a, double b) {
    return (a <= b) ? a : b;
  }

  public static boolean isCloseTo(double a, double b) {
    if (a>b) {
      return (a-b)<1e-4;
    } else {
      return (b-a)<1e-4;
    }
  }
  static final double LOGTOLERANCE = 30.0;
  static final float LOGTOLERANCE_F = 20.0f;

  public static double log(double num, double base) {
    return Math.log(num)/Math.log(base);
  }

  public static double logAdd(double lx, double ly) {
    double max, negDiff;
    if (lx > ly) {
      max = lx;
      negDiff = ly - lx;
    } else {
      max = ly;
      negDiff = lx - ly;
    }
    if (max == Double.NEGATIVE_INFINITY) {
      return max;
    } else if (negDiff < -LOGTOLERANCE) {
      return max;
    } else {
      return max + Math.log(1.0 + Math.exp(negDiff));
    }
  }

  private static float[] acosCache; // = null;

  public static double factorial(int x) {
    double result = 1.0;
    for (int i=x; i>1; i--) {
      result *= i;
    }
    return result;
  }

  private static final double[] exps = new double[617];
  static {
    for(int i=-308;i<308;++i) {
      String toParse = "1.0e" + i;
      exps[(i + 308)]=Double.parseDouble("1.0e" + i);
    }
  }

  public static double parseDouble(boolean negative, long mantissa, int  exponent) {
    int e = -16;
    return (negative ? -1. : 1.) * (((double)mantissa) * exps[(e + 308)]) * exps[(exponent + 308)];
  }

  public static Triple<Boolean, Long, Integer> segmentDouble(double d) {
    if (Double.isInfinite(d) || Double.isNaN(d)) {
      throw new IllegalArgumentException("Cannot handle weird double: " + d);
    }
    boolean negative = d < 0;
    d = Math.abs(d);
    int exponent = 0;
    while (d >= 10.0) {
      exponent += 1;
      d = d / 10.;
    }
    while (d < 1.0) {
      exponent -= 1;
      d = d * 10.;
    }
    return Triple.makeTriple(negative, (long) (d * 10000000000000000.), exponent);
  }

  public static long parseInt( final String s ) {
    // Check for a sign.
    long num  = 0;
    long sign = -1;
    final int len  = s.length( );
    final char ch  = s.charAt( 0 );
    if ( ch == '-' ) {
      sign = 1;
    }
    else {
      final long d = ch - '0';
      num = -d;
    }
    // Build the number.
    final long max = (sign == -1) ?
        -Long.MAX_VALUE : Long.MIN_VALUE;
    final long multmax = max / 10;
    int i = 1;
    while ( i < len ) {
      long d = s.charAt(i++) - '0';
      num *= 10;
      num -= d;
    }
    return sign * num;
  }

}
