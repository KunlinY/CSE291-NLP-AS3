package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.CoreLabel;
import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.ling.LabelFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;


public class LabeledScoredTreeNode extends Tree {

  private static final long serialVersionUID = -8992385140984593817L;

  
  @SuppressWarnings("serial")
  private Label label; // = null;

  
  private double score = Double.NaN;

  
  private Tree[] daughterTrees; // = null;

  
  public LabeledScoredTreeNode() {
    setChildren(EMPTY_TREE_ARRAY);
  }

  /**
   * Create a leaf parse tree with given word.
   *
   * @param label the {@code Label} representing the <i>word</i> for
   *              this new tree leaf.
   */
  public LabeledScoredTreeNode(Label label) {
    this(label, Double.NaN);
  }

  /**
   * Create a leaf parse tree with given word and score.
   *
   * @param label The {@code Label} representing the <i>word</i> for
   * @param score The score for the node
   *              this new tree leaf.
   */
  public LabeledScoredTreeNode(Label label, double score) {
    this();
    this.label = label;
    this.score = score;
  }

  
  public LabeledScoredTreeNode(Label label, List<Tree> daughterTreesList) {
    this.label = label;
    setChildren(daughterTreesList);
  }

  
  @Override
  public Tree[] children() {
    return daughterTrees;
  }

  
  @Override
  public void setChildren(Tree[] children) {
    if (children == null) {
      daughterTrees = EMPTY_TREE_ARRAY;
    } else {
      daughterTrees = children;
    }
  }

  
  @Override
  public Label label() {
    return label;
  }

  
  @Override
  public void setLabel(final Label label) {
    this.label = label;
  }

  
  @Override
  public double score() {
    return score;
  }

  
  @Override
  public void setScore(double score) {
    this.score = score;
  }

  
  @Override
  public TreeFactory treeFactory() {
    LabelFactory lf = (label() == null) ? CoreLabel.factory() : label().labelFactory();
    return new LabeledScoredTreeFactory(lf);
  }

  // extra class guarantees correct lazy loading (Bloch p.194)
  private static class TreeFactoryHolder {
    static final TreeFactory tf = new LabeledScoredTreeFactory();
  }

  
  public static TreeFactory factory() {
    return TreeFactoryHolder.tf;
  }

  
  public static TreeFactory factory(LabelFactory lf) {
    return new LabeledScoredTreeFactory(lf);
  }

  private static final NumberFormat nf = new DecimalFormat("0.000");

  @Override
  public String nodeString() {
    StringBuilder buff = new StringBuilder();
    buff.append(super.nodeString());
    if ( ! Double.isNaN(score)) {
      buff.append(" [").append(nf.format(-score)).append(']');
    }
    return buff.toString();
  }
}

