package edu.berkeley.nlp.assignments.parsing.util;


public final class MutableDouble extends Number implements Comparable<MutableDouble> {

  private double d;

  // Mutable
  public void set(double d) {
    this.d = d;
  }

  @Override
  public int hashCode() {
    long bits = Double.doubleToLongBits(d);
    return (int) (bits ^ (bits >>> 32));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof MutableDouble && d == ((MutableDouble) obj).d;
  }

  @Override
  public String toString() {
    return Double.toString(d);
  }

  // Comparable interface

  public int compareTo(MutableDouble anotherMutableDouble) {
    double thisVal = this.d;
    double anotherVal = anotherMutableDouble.d;
    return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
  }

  // Number interface
  @Override
  public int intValue() {
    return (int) d;
  }

  @Override
  public long longValue() {
    return (long) d;
  }

  @Override
  public short shortValue() {
    return (short) d;
  }

  @Override
  public byte byteValue() {
    return (byte) d;
  }

  @Override
  public float floatValue() {
    return (float) d;
  }

  @Override
  public double doubleValue() {
    return d;
  }

  public MutableDouble() {
    this(0.0);
  }

  public MutableDouble(double d) {
    this.d = d;
  }

  public MutableDouble(Number num) {
    this.d = num.doubleValue();
  }

  private static final long serialVersionUID = 624465615824626762L;

}
