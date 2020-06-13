package edu.berkeley.nlp.assignments.parsing.trees;

/**
 * An interface for treebank vendors.
 *
 * @author Roger Levy
 */
public interface TreebankFactory {

  /**
   * Returns a treebank instance
   */
  public Treebank treebank();

}
