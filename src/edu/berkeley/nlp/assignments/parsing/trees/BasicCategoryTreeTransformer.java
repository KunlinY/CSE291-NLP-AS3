package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;
import java.util.function.Function;

/**
 * Transforms trees by turning the labels into their basic categories
 * according to the 
 * {@link edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack}
 *
 * @author John Bauer
 */
public class BasicCategoryTreeTransformer extends RecursiveTreeTransformer implements Function<Tree, Tree> {
  final TreebankLanguagePack tlp;

  public BasicCategoryTreeTransformer(TreebankLanguagePack tlp) {
    this.tlp = tlp;
  }

  @Override
  public Label transformNonterminalLabel(Tree tree) {
    if (tree.label() == null) {
      return null;
    }
    return tree.label().labelFactory().newLabel(tlp.basicCategory(tree.label().value()));
  }

  public Tree apply(Tree tree) {
    return transformTree(tree);
  }
}
