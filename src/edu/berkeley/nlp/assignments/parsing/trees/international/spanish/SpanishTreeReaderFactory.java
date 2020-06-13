package edu.berkeley.nlp.assignments.parsing.trees.international.spanish;

import java.io.Reader;
import java.io.Serializable;

import edu.berkeley.nlp.assignments.parsing.trees.LabeledScoredTreeFactory;
import edu.berkeley.nlp.assignments.parsing.trees.PennTreebankTokenizer;
import edu.berkeley.nlp.assignments.parsing.trees.PennTreeReader;
import edu.berkeley.nlp.assignments.parsing.trees.TreeReader;
import edu.berkeley.nlp.assignments.parsing.trees.TreeReaderFactory;

/**
 * A class for reading Spanish AnCora trees that have been converted
 * from XML to PTB format.
 *
 * @author Jon Gauthier
 * @author Spence Green (original French version)
 */
public class SpanishTreeReaderFactory implements TreeReaderFactory, Serializable {

  // TODO
  private static final long serialVersionUID = 8L;

  public TreeReader newTreeReader(Reader in) {
    return new PennTreeReader(in, new LabeledScoredTreeFactory(),
                              new SpanishTreeNormalizer(false, false, false),
                              new PennTreebankTokenizer(in));
  }

}
