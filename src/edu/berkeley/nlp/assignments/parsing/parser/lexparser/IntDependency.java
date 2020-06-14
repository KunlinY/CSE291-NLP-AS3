package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.util.Index;

import java.io.Serializable;


/** Maintains a dependency between head and dependent where they are each an IntTaggedWord.
 *
 *  @author Christopher Manning
 */
public class IntDependency implements Serializable {

  public static final String LEFT = "left";
  public static final String RIGHT = "right";
  public static final int ANY_DISTANCE_INT = -1;

  public final IntTaggedWord head;
  public final IntTaggedWord arg;
  public final boolean leftHeaded;
  public final short distance;

  @Override
  public int hashCode() {
    return head.hashCode() ^ (arg.hashCode() << 8) ^ ((leftHeaded ? 1 : 0) << 15) ^ (distance << 16);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof IntDependency) {
      IntDependency d = (IntDependency) o;
      return (head.equals(d.head) && arg.equals(d.arg) && distance == d.distance && leftHeaded == d.leftHeaded);
    } else {
      return false;
    }
  }

  private static final char[] charsToEscape = {'\"'};

  @Override
  public String toString() {
    return "";
  }

  public String toString(Index<String> wordIndex, Index<String> tagIndex) {
    return "";
  }

  public IntDependency(IntTaggedWord head, IntTaggedWord arg, boolean leftHeaded, int distance) {
    this.head = head;
    this.arg = arg;
    this.distance = (short) distance;
    this.leftHeaded = leftHeaded;
  }

  public IntDependency(int headWord, int headTag, int argWord, int argTag, boolean leftHeaded, int distance) {
    this.head = new IntTaggedWord(headWord, headTag);
    this.arg = new IntTaggedWord(argWord, argTag);
    this.distance = (short) distance;
    this.leftHeaded = leftHeaded;
  }

  private static final long serialVersionUID = 1L;

} // end class IntDependency
