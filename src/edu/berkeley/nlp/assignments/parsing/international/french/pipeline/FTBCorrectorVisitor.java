package edu.berkeley.nlp.assignments.parsing.international.french.pipeline;

import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.trees.TreeTransformer;
import edu.berkeley.nlp.assignments.parsing.trees.TreeVisitor;

/**
 * Wrapper class for using the ATBCorrector class with TreebankPipeline's
 * TVISITOR parameter.
 * 
 * @author Spence Green
 *
 */
public class FTBCorrectorVisitor implements TreeVisitor {

  private final TreeTransformer ftbCorrector = new FTBCorrector();

  public void visitTree(Tree t) {
    ftbCorrector.transformTree(t);
  }

}
