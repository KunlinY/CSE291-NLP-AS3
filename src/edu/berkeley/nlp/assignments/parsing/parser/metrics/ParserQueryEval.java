package edu.berkeley.nlp.assignments.parsing.parser.metrics;

import java.io.PrintWriter;

import edu.berkeley.nlp.assignments.parsing.parser.common.ParserQuery;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;

/**
 * Evaluate based on the ParserQuery rather than the Tree produced
 *
 * @author John Bauer
 */
public interface ParserQueryEval {
  public void evaluate(ParserQuery query, Tree gold, PrintWriter pw);

  /**
   * Called after the evaluation is finished.  While that generally
   * means you want to display final stats here, you can also use this
   * as a chance to close open files, etc
   */
  public void display(boolean verbose, PrintWriter pw);
}

