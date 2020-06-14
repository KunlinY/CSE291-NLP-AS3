package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;

/**
 * A <code>ConstituentFactory</code> is a factory for creating objects
 * of class <code>Constituent</code>, or some descendent class.
 * An interface.
 *
 * @author Christopher Manning
 */
public interface ConstituentFactory {

  
  public Constituent newConstituent(int start, int end);

  
  public Constituent newConstituent(int start, int end, Label label, double score);
}
