package edu.berkeley.nlp.assignments.parsing.international.arabic.pipeline;

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
public class ATBCorrectorVisitor implements TreeVisitor {

  private final TreeTransformer atbCorrector = new ATBCorrector();
  
  public void visitTree(Tree t) {
    atbCorrector.transformTree(t);
  }

}
