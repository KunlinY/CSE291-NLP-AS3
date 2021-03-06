package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import java.io.Serializable;

import edu.berkeley.nlp.assignments.parsing.util.Index;

/**
 * Binary rules (ints for parent, left and right children)
 *
 * @author Dan Klein
 * @author Christopher Manning
 */
public class BinaryRule implements Rule, Comparable<BinaryRule>, Serializable {

  public int parent;
  /**
   * Score should be a log probability
   */
  public float score;
  public int leftChild;
  public int rightChild;

  /** Create a new BinaryRule with the parent and children coded as ints.
   *  Score defaults to Float.NaN.
   *  @param parent The parent int
   *  @param leftChild The left child int
   *  @param rightChild The right child int
   */
  public BinaryRule(int parent, int leftChild, int rightChild) {
    this.parent = parent;
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.score = Float.NaN;
  }

  public BinaryRule(int parent, int leftChild, int rightChild, double score) {
    this.parent = parent;
    this.leftChild = leftChild;
    this.rightChild = rightChild;
    this.score = (float) score;
  }

  /**
   * Creates a BinaryRule from String s, assuming it was created using toString().
   *
   * @param s A String in which the binary rule is represented as parent,
   *     left-child, right-child, score, with the items quoted as needed
   * @param index Index used to convert String names to ints
   */
  public BinaryRule(String s, Index<String> index) {
  }

  public float score() {
    return score;
  }

  public int parent() {
    return parent;
  }

  private int hashCode = -1;

  @Override
  public int hashCode() {
    if (hashCode < 0) {
      hashCode = (parent << 16) ^ (leftChild << 8) ^ rightChild;
    }
    return hashCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof BinaryRule) {
      BinaryRule br = (BinaryRule) o;
      if (parent == br.parent && leftChild == br.leftChild && rightChild == br.rightChild) {
        return true;
      }
    }
    return false;
  }

  private static final char[] charsToEscape = { '\"' };


  public String toString() {
    return parent + " -> " + leftChild + ' ' + rightChild + ' ' + score;
  }

  public String toString(Index<String> index) {
    return "";
  }

  private transient String cached; // = null;


  public int compareTo(BinaryRule br) {
    if (parent < br.parent) {
      return -1;
    }
    if (parent > br.parent) {
      return 1;
    }
    if (leftChild < br.leftChild) {
      return -1;
    }
    if (leftChild > br.leftChild) {
      return 1;
    }
    if (rightChild < br.rightChild) {
      return -1;
    }
    if (rightChild > br.rightChild) {
      return 1;
    }
    return 0;
  }

  private static final long serialVersionUID = 1L;

}
