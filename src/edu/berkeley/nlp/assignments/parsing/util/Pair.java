package edu.berkeley.nlp.assignments.parsing.util;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Pair is a Class for holding mutable pairs of objects.
 * <p>
 * <i>Implementation note:</i>
 * On a 32-bit JVM uses ~ 8 (this) + 4 (first) + 4 (second) = 16 bytes.
 * On a 64-bit JVM uses ~ 16 (this) + 8 (first) + 8 (second) = 32 bytes.
 * <p>
 * Many applications use a lot of Pairs so it's good to keep this
 * number small.
 *
 * @author Dan Klein
 * @author Christopher Manning (added stuff from Kristina's, rounded out)
 * @version 2002/08/25
 */

public class Pair <T1,T2> implements Comparable<Pair<T1,T2>>, Serializable {

  /**
   * Direct access is deprecated.  Use first().
   *
   * @serial
   */
  public T1 first;

  /**
   * Direct access is deprecated.  Use second().
   *
   * @serial
   */
  public T2 second;

  public Pair() {
    // first = null; second = null; -- default initialization
  }

  public Pair(T1 first, T2 second) {
    this.first = first;
    this.second = second;
  }

  public T1 first() {
    return first;
  }

  public T2 second() {
    return second;
  }

  public void setFirst(T1 o) {
    first = o;
  }

  public void setSecond(T2 o) {
    second = o;
  }

  @Override
  public String toString() {
    return "(" + first + "," + second + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Pair) {
      @SuppressWarnings("rawtypes")
      Pair p = (Pair) o;
      return (first == null ? p.first() == null : first.equals(p.first())) && (second == null ? p.second() == null : second.equals(p.second()));
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int firstHash  = (first == null ? 0 : first.hashCode());
    int secondHash = (second == null ? 0 : second.hashCode());

    return firstHash*31 + secondHash;
  }

  public List<Object> asList() {
    return null;
  }

  public static <X, Y> Pair<X, Y> makePair(X x, Y y) {
    return new Pair<>(x, y);
  }

  public void save(DataOutputStream out) {
    try {
      out.writeUTF(first.toString());
      out.writeUTF(second.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public int compareTo(Pair<T1,T2> another) {
    if (first() instanceof Comparable) {
      int comp = ((Comparable<T1>) first()).compareTo(another.first());
      if (comp != 0) {
        return comp;
      }
    }

    if (second() instanceof Comparable) {
      return ((Comparable<T2>) second()).compareTo(another.second());
    }

    if ((!(first() instanceof Comparable)) && (!(second() instanceof Comparable))) {
      throw new AssertionError("Neither element of pair comparable");
    }

    return 0;
  }
  private static final long serialVersionUID = 1360822168806852921L;

}
