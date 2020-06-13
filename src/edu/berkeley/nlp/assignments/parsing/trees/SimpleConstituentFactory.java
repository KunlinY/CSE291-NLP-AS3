package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;

/**
 * A <code>ConstituentFactory</code> acts as a factory for creating objects
 * of class <code>Constituent</code>, or some descendent class.
 * An interface.
 *
 * @author Christopher Manning
 */
public class SimpleConstituentFactory implements ConstituentFactory {

  public Constituent newConstituent(int start, int end) {
    return new SimpleConstituent(start, end);
  }


  public Constituent newConstituent(int start, int end, Label label, double score) {
    return new SimpleConstituent(start, end);
  }

}
