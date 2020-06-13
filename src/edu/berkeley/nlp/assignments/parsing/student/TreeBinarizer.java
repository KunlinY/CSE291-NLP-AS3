package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.ling.*;

import java.util.*;

/**
 * Binarizes trees, typically in such a way that head-argument structure is respected.
 * Looks only at the value of input Tree<String> nodes.
 * Produces LabeledScoredTrees with CategoryWordTag labels.  The input trees have to have category, word, and tag
 * present (as CategoryWordTag or CoreLabel labels)!
 * Although the binarizer always respects heads, you can get left or right
 * binarization by defining an appropriate HeadFinder.
 * TODO: why not use CoreLabel if the input Tree<String> used CoreLabel?
 *
 * @author Dan Klein
 * @author Teg Grenager
 * @author Christopher Manning
 */
public class TreeBinarizer {

    private HeadFinder hf = new HeadFinder();;
    private boolean insideFactor = false; // true: DT JJ NN -> DT "JJ NN", false: DT "DT"
    private final boolean simpleLabels = false;

    Tree<String> binarizeLocalTree(Tree<String> t, int headNum) {
        if (insideFactor) {
            return insideBinarizeLocalTree(t, headNum, 0, 0);
        }
        return outsideBinarizeLocalTree(t, t.getLabel(), t.getLabel(), headNum, 0, "", 0, "");
    }

    private Tree<String> insideBinarizeLocalTree(Tree<String> t, int headNum, int leftProcessed, int rightProcessed) {
        List<Tree<String>> newChildren = new ArrayList<>(2);      // check done
        List<Tree<String>> children = t.getChildren();
        if (children.size() <= leftProcessed + rightProcessed + 2) {
            Tree<String> leftChild = children.get(leftProcessed);
            newChildren.add(leftChild);
            if (children.size() == leftProcessed + rightProcessed + 1) {
                // unary ... so top level
                String finalCat = t.getLabel();
                return new Tree<String>(finalCat, newChildren);
            }
            // binary
            Tree<String> rightChild = children.get(leftProcessed + 1);
            newChildren.add(rightChild);
            String labelStr = t.getLabel();
            if (leftProcessed != 0 || rightProcessed != 0) {
                labelStr = ("@ " + leftChild.getLabel() + ' ' + rightChild.getLabel());
            }
            return new Tree<String>(labelStr, newChildren);
        }
        if (headNum > leftProcessed) {
            // eat left word
            Tree<String> leftChild = children.get(leftProcessed);
            Tree<String> rightChild = insideBinarizeLocalTree(t, headNum, leftProcessed + 1, rightProcessed);
            newChildren.add(leftChild);
            newChildren.add(rightChild);
            String labelStr = ("@ " + leftChild.getLabel() + ' ' + rightChild.getLabel().substring(2));
            if (leftProcessed == 0 && rightProcessed == 0) {
                labelStr = t.getLabel();
            }
            return new Tree<String>(labelStr, newChildren);
        } else {
            // eat right word
            Tree<String> leftChild = insideBinarizeLocalTree(t, headNum, leftProcessed, rightProcessed + 1);
            Tree<String> rightChild = children.get(children.size() - rightProcessed - 1);
            newChildren.add(leftChild);
            newChildren.add(rightChild);
            String labelStr = ("@ " + leftChild.getLabel().substring(2) + ' ' + rightChild.getLabel());
            if (leftProcessed == 0 && rightProcessed == 0) {
                labelStr = t.getLabel();
            }
            return new Tree<String>(labelStr, newChildren);
        }
    }

    private Tree<String> outsideBinarizeLocalTree(Tree<String> t, String labelStr, String finalCat, int headNum, int leftProcessed, String leftStr, int rightProcessed, String rightStr) {
        List<Tree<String>> newChildren = new ArrayList<>(2);
        // check if there are <=2 children already
        List<Tree<String>> children = t.getChildren();
        if (children.size() - leftProcessed - rightProcessed <= 2) {
            // done, return
            newChildren.add(children.get(leftProcessed));
            if (children.size() - leftProcessed - rightProcessed == 2) {
                newChildren.add(children.get(leftProcessed + 1));
            }
            return new Tree<String>(labelStr, newChildren);
        }
        if (headNum > leftProcessed) {
            // eat a left word
            Tree<String> leftChild = children.get(leftProcessed);
            String childLeftStr = leftStr + ' ' + leftChild.getLabel();
            String childLabelStr;
            if (simpleLabels) {
                childLabelStr = '@' + finalCat;
            } else {
                childLabelStr = '@' + finalCat + " ->" + childLeftStr + " ..." + rightStr;
            }
            Tree<String> rightChild = outsideBinarizeLocalTree(t, childLabelStr, finalCat, headNum, leftProcessed + 1, childLeftStr, rightProcessed, rightStr);
            newChildren.add(leftChild);
            newChildren.add(rightChild);
            return new Tree<String>(labelStr, newChildren);
        } else {
            // eat a right word
            Tree<String> rightChild = children.get(children.size() - rightProcessed - 1);
            String childRightStr = ' ' + rightChild.getLabel() + rightStr;
            String childLabelStr;
            if (simpleLabels) {
                childLabelStr = '@' + finalCat;
            } else {
                childLabelStr = '@' + finalCat + " ->" + leftStr + " ..." + childRightStr;
            }
            Tree<String> leftChild = outsideBinarizeLocalTree(t, childLabelStr, finalCat, headNum, leftProcessed, leftStr, rightProcessed + 1, childRightStr);
            newChildren.add(leftChild);
            newChildren.add(rightChild);
            return new Tree<String>(labelStr, newChildren);
        }
    }


    /** Binarizes the Tree<String> according to options set up in the constructor.
     *  Does the whole Tree<String> by calling itself recursively.
     *
     *  @param t A Tree<String> to be binarized. The non-leaf nodes must already have
     *    CategoryWordTag labels, with heads percolated.
     *  @return A binary tree.
     */
    public Tree<String> transformTree(Tree<String> t) {
        // handle null
        if (t == null) {
            return null;
        }

        String cat = t.getLabel();
        // handle words
        if (t.isLeaf()) {
            return new Tree(cat);
        }
        // handle tags
        if (t.isPreTerminal()) {
            Tree<String> childResult = transformTree(t.getChildren().get(0));
            String word = childResult.getLabel();  // would be nicer if Word/CWT ??
            List<Tree<String>> newChildren = new ArrayList<>(1);
            newChildren.add(childResult);
            return new Tree<String>(cat, newChildren);
        }
        // handle categories
        Tree<String> headChild = hf.determineHead(t);
        if (headChild == null && ! t.getLabel().startsWith("ROOT")) {
            System.out.println("### No head found for!");
        }

        int headNum = -1;
        List<Tree<String>> kids = t.getChildren();
        List<Tree<String>> newChildren = new ArrayList<>(kids.size());
        for (int childNum = 0; childNum < kids.size(); childNum++) {
            Tree<String> child = kids.get(childNum);
            Tree<String> childResult = transformTree(child);   // recursive call
            if (child == headChild) {
                headNum = childNum;
            }
            newChildren.add(childResult);
        }

        Tree<String> result;
        // XXXXX UPTO HERE!!!  ALMOST DONE!!!
        if (cat.startsWith("ROOT")) {
            // handle the ROOT Tree<String> properly
            result = new Tree<String>(cat, newChildren); // label shouldn't have changed
        } else {
            result = new Tree<String>(headChild.getLabel(), newChildren);
            // cdm Mar 2005: invent a head so I don't have to rewrite all this
            // code, but with the removal of TreeHeadPair, some of the rest of
            // this should probably be rewritten too to not use this head variable
            result = binarizeLocalTree(result, headNum);
        }
        return result;
    }

    public static void test(List<Tree<String>> trainTrees) {

        TreeBinarizer tt = new TreeBinarizer();

        for (Tree<String> t : trainTrees) {
            Tree<String> newT = tt.transformTree(t);
            System.out.println("Original tree:");
            System.out.println(t.toString());
            System.out.println("Binarized tree:");
            System.out.println(newT.toString());
            System.out.println();
        }
    } // end main

}

