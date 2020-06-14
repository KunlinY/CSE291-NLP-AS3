package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.ling.WordFactory;
import edu.berkeley.nlp.assignments.parsing.trees.*;
import edu.berkeley.nlp.assignments.parsing.util.Generics;

import java.io.Reader;
import java.util.*;

// todo [cdm 2014]: This class is all but dead. Delete it.
/**
 * Class for getting an annotated treebank.
 *
 * @author Dan Klein
 */
public class TreebankAnnotator {

  final TreeTransformer treeTransformer;
  final TreeTransformer treeUnTransformer;
  final TreeTransformer collinizer;
  final Options op;

  public List<Tree> annotateTrees(List<Tree> trees) {
    List<Tree> annotatedTrees = new ArrayList<>();
    for (Tree tree : trees) {
      annotatedTrees.add(treeTransformer.transformTree(tree));
    }
    return annotatedTrees;
  }

  public List<Tree> deannotateTrees(List<Tree> trees) {
    List<Tree> deannotatedTrees = new ArrayList<>();
    for (Tree tree : trees) {
      deannotatedTrees.add(treeUnTransformer.transformTree(tree));
    }
    return deannotatedTrees;
  }


  public static List<Tree> getTrees(String path, int low, int high, int minLength, int maxLength) {
    return null;
  }

  public static List<Tree> removeDependencyRoots(List<Tree> trees) {
    List<Tree> prunedTrees = new ArrayList<>();
    for (Tree tree : trees) {
      prunedTrees.add(removeDependencyRoot(tree));
    }
    return prunedTrees;
  }

  static Tree removeDependencyRoot(Tree tree) {
    List<Tree> childList = tree.getChildrenAsList();
    Tree last = childList.get(childList.size() - 1);
    if (!last.label().value().equals(Lexicon.BOUNDARY_TAG)) {
      return tree;
    }
    List<Tree> lastGoneList = childList.subList(0, childList.size() - 1);
    tree.setChildren(lastGoneList);
    return tree;
  }

  public Tree collinize(Tree tree) {
    return collinizer.transformTree(tree);
  }

  public TreebankAnnotator(Options op, String treebankRoot) {
    //    op.tlpParams = new EnglishTreebankParserParams();
    // CDM: Aug 2004: With new implementation of treebank split categories,
    // I've hardwired this to load English ones.  Otherwise need training data.
    // op.trainOptions.splitters = Generics.newHashSet(Arrays.asList(op.tlpParams.splitters()));
    op.trainOptions.splitters = ParentAnnotationStats.getEnglishSplitCategories(treebankRoot);
    op.trainOptions.sisterSplitters = Generics.newHashSet(Arrays.asList(op.tlpParams.sisterSplitters()));
    op.setOptions("-acl03pcfg", "-cnf");
    treeTransformer = new TreeAnnotatorAndBinarizer(op.tlpParams, op.forceCNF, !op.trainOptions.outsideFactor(), true, op);
    //    BinarizerFactory.TreeAnnotator.setTreebankLang(op.tlpParams);
    treeUnTransformer = new Debinarizer(op.forceCNF);
    collinizer = op.tlpParams.collinizer();
    this.op = op;
  }

}
