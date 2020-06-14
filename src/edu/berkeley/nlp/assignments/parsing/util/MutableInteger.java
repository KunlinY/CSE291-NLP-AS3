package edu.berkeley.nlp.assignments.parsing.util;


public final class MutableInteger extends Number implements Comparable<MutableInteger> {

  private int i;

  // Mutable
  public void set(int i) {
    this.i = i;
  }

  @Override
  public int hashCode() {
    return i;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MutableInteger) {
      return i == ((MutableInteger) obj).i;
    }
    return false;
  }

  @Override
  public String toString() {
    return Integer.toString(i);
  }

  public int compareTo(MutableInteger anotherMutableInteger) {
    int thisVal = this.i;
    int anotherVal = anotherMutableInteger.i;
    return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
  }


  // Number interface
  @Override
  public int intValue() {
    return i;
  }

  @Override
  public long longValue() {
    return i;
  }

  @Override
  public short shortValue() {
    return (short) i;
  }

  @Override
  public byte byteValue() {
    return (byte) i;
  }

  @Override
  public float floatValue() {
    return i;
  }

  @Override
  public double doubleValue() {
    return i;
  }

  public void incValue(int val) {
    i += val;
  }

  public MutableInteger() {
    this(0);
  }

  public MutableInteger(int i) {
    this.i = i;
  }

  private static final long serialVersionUID = 624465615824626762L;
}
