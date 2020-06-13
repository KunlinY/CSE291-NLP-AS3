package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.ling.Tree;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class HeadFinder {
    Map<String, String[][]> nonTerminalInfo = new HashMap<String, String[][]>();
    String[] defaultLeftRule = {"leftexcept", "''", "``", "-LRB-", "-RRB-", ".", ":", ","};
    String[] defaultRightRule = {"rightexcept", "''", "``", "-LRB-", "-RRB-", ".", ":", ","};
    Set<String> isPunctuationTag = new HashSet<>(
            Arrays.asList("''", "``", "-LRB-", "-RRB-", ":", ",", "."));

    public HeadFinder() {
        nonTerminalInfo.put("PP", new String[][]{{"right", "IN", "TO", "VBG", "VBN", "RP", "FW", "JJ", "SYM"}, {"left", "PP"}});
        nonTerminalInfo.put("RRC", new String[][]{{"left", "RRC"}, {"right", "VP", "ADJP", "JJP", "NP", "PP", "ADVP"}});
        nonTerminalInfo.put("NP", new String[][]{{"rightdis", "NN", "NNP", "NNPS", "NNS", "NML", "NX", "POS", "JJR"}, {"left", "NP", "PRP"}, {"rightdis", "$", "ADJP", "JJP", "PRN", "FW"}, {"right", "CD"}, {"rightdis", "JJ", "JJS", "RB", "QP", "DT", "WDT", "RBR", "ADVP"}});
        nonTerminalInfo.put("ADVP", new String[][]{{"left", "ADVP", "IN"}, {"rightdis", "RB", "RBR", "RBS", "JJ", "JJR", "JJS"}, {"rightdis", "RP", "DT", "NN", "CD", "NP", "VBN", "NNP", "CC", "FW", "NNS", "ADJP", "NML"}});
        nonTerminalInfo.put("UCP", new String[][]{{"right"}});
        nonTerminalInfo.put("ROOT", new String[][]{{"left", "S", "SQ", "SINV", "SBAR", "FRAG"}});
        nonTerminalInfo.put("NX", new String[][]{{"rightdis", "NN", "NNP", "NNPS", "NNS", "NML", "NX", "POS", "JJR"}, {"left", "NP", "PRP"}, {"rightdis", "$", "ADJP", "JJP", "PRN", "FW"}, {"right", "CD"}, {"rightdis", "JJ", "JJS", "RB", "QP", "DT", "WDT", "RBR", "ADVP"}});
        nonTerminalInfo.put("PRN", new String[][]{{"left", "VP", "NP", "PP", "SQ", "S", "SINV", "SBAR", "ADJP", "JJP", "ADVP", "INTJ", "WHNP", "NAC", "VBP", "JJ", "NN", "NNP"}});
        nonTerminalInfo.put("JJP", new String[][]{{"left", "NNS", "NN", "$", "QP", "JJ", "VBN", "VBG", "ADJP", "JJP", "JJR", "NP", "JJS", "DT", "FW", "RBR", "RBS", "SBAR", "RB"}});
        nonTerminalInfo.put("FRAG", new String[][]{{"right"}});
        nonTerminalInfo.put("PRT", new String[][]{{"right", "RP"}});
        nonTerminalInfo.put("S", new String[][]{{"left", "TO", "VP", "S", "FRAG", "SBAR", "ADJP", "JJP", "UCP", "NP"}});
        nonTerminalInfo.put("ADV", new String[][]{{"right", "RB", "RBR", "RBS", "FW", "ADVP", "TO", "CD", "JJR", "JJ", "IN", "NP", "NML", "JJS", "NN"}});
        nonTerminalInfo.put("X", new String[][]{{"right", "S", "VP", "ADJP", "JJP", "NP", "SBAR", "PP", "X"}});
        nonTerminalInfo.put("POSSP", new String[][]{{"right", "POS"}});
        nonTerminalInfo.put("SQ", new String[][]{{"left", "VBZ", "VBD", "VBP", "VB", "MD", "AUX", "AUXG", "VP", "SQ"}});
        nonTerminalInfo.put("QP", new String[][]{{"left", "$", "IN", "NNS", "NN", "JJ", "CD", "PDT", "DT", "RB", "NCD", "QP", "JJR", "JJS"}});
        nonTerminalInfo.put("SBARQ", new String[][]{{"left", "SQ", "S", "SINV", "SBARQ", "FRAG", "SBAR"}});
        nonTerminalInfo.put("META", new String[][]{{"left"}});
        nonTerminalInfo.put("TYPO", new String[][]{{"left", "NN", "NP", "NML", "NNP", "NNPS", "TO", "VBD", "VBN", "MD", "VBZ", "VB", "VBG", "VBP", "VP", "ADJP", "JJP", "FRAG"}});
        nonTerminalInfo.put("WHADVP", new String[][]{{"right", "WRB", "WHADVP"}});
        nonTerminalInfo.put("SBAR", new String[][]{{"left", "WHNP", "WHPP", "WHADVP", "WHADJP", "IN", "DT", "S", "SQ", "SINV", "SBAR", "FRAG"}});
        nonTerminalInfo.put("LST", new String[][]{{"right", "LS", ":"}});
        nonTerminalInfo.put("NML", new String[][]{{"rightdis", "NN", "NNP", "NNPS", "NNS", "NML", "NX", "POS", "JJR"}, {"left", "NP", "PRP"}, {"rightdis", "$", "ADJP", "JJP", "PRN", "FW"}, {"right", "CD"}, {"rightdis", "JJ", "JJS", "RB", "QP", "DT", "WDT", "RBR", "ADVP"}});
        nonTerminalInfo.put("VB", new String[][]{{"left", "TO", "VBD", "VBN", "MD", "VBZ", "VB", "VBG", "VBP", "VP", "AUX", "AUXG", "ADJP", "JJP", "NN", "NNS", "JJ", "NP", "NNP"}});
        nonTerminalInfo.put("WHADJP", new String[][]{{"left", "WRB", "WHADVP", "RB", "JJ", "ADJP", "JJP", "JJR"}});
        nonTerminalInfo.put("NAC", new String[][]{{"left", "NN", "NNS", "NML", "NNP", "NNPS", "NP", "NAC", "EX", "$", "CD", "QP", "PRP", "VBG", "JJ", "JJS", "JJR", "ADJP", "JJP", "FW"}});
        nonTerminalInfo.put("TOP", new String[][]{{"left", "S", "SQ", "SINV", "SBAR", "FRAG"}});
        nonTerminalInfo.put("EDITED", new String[][]{{"left"}});
        nonTerminalInfo.put("ADJP", new String[][]{{"left", "$"}, {"rightdis", "NNS", "NN", "JJ", "QP", "VBN", "VBG"}, {"left", "ADJP"}, {"rightdis", "JJP", "JJR", "JJS", "DT", "RB", "RBR", "CD", "IN", "VBD"}, {"left", "ADVP", "NP"}});
        nonTerminalInfo.put("SINV", new String[][]{{"left", "VBZ", "VBD", "VBP", "VB", "MD", "VBN", "VP", "S", "SINV", "ADJP", "JJP", "NP"}});
        nonTerminalInfo.put("INTJ", new String[][]{{"left"}});
        nonTerminalInfo.put("WHPP", new String[][]{{"right", "IN", "TO", "FW"}});
        nonTerminalInfo.put("VP", new String[][]{{"left", "TO", "VBD", "VBN", "MD", "VBZ", "VB", "VBG", "VBP", "VP", "AUX", "AUXG", "ADJP", "JJP", "NN", "NNS", "JJ", "NP", "NNP"}});
        nonTerminalInfo.put("WHNP", new String[][]{{"left", "WDT", "WP", "WP$", "WHADJP", "WHPP", "WHNP"}});
        nonTerminalInfo.put("XS", new String[][]{{"right", "IN"}});
        nonTerminalInfo.put("CONJP", new String[][]{{"right", "CC", "RB", "IN"}});
    }

    public Tree<String> determineHead(Tree<String> t) {
        return determineHead(t, null);
    }

    public Tree<String> determineHead(Tree<String> t, Tree<String> parent) {

        List<Tree<String>> kids = t.getChildren();

        // if the node is a unary, then that kid must be the head
        // it used to special case preterminal and ROOT/TOP case
        // but that seemed bad (especially hardcoding string "ROOT")
        if (kids.size() == 1) {
            return kids.get(0);
        }

        return determineNonTrivialHead(t, parent);
    }

    protected Tree<String> determineNonTrivialHead(Tree<String> t, Tree<String> parent) {
        Tree<String> theHead = null;
        String motherCat = t.getLabel().split("\\^")[0];
        if (motherCat.startsWith("@")) {
            motherCat = motherCat.substring(1);
        }
        // We know we have nonterminals underneath
        // (a bit of a Penn Treebank assumption, but).

        // Look at label.
        // a total special case....
        // first look for POS tag at end
        // this appears to be redundant in the Collins case since the rule already would do that
        //    Tree lastDtr = t.lastChild();
        //    if (tlp.basicCategory(lastDtr.label().value()).equals("POS")) {
        //      theHead = lastDtr;
        //    } else {
        String[][] how = nonTerminalInfo.get(motherCat);
        List<Tree<String>> kids = t.getChildren();
        if (how == null) {
            System.out.println("Error!");
            return theHead;
        }

        for (int i = 0; i < how.length; i++) {
            boolean lastResort = (i == how.length - 1);
            theHead = traverseLocate(kids, how[i], lastResort);
            if (theHead != null) {
                break;
            }
        }
        return theHead;
    }
    
    /**
     * Attempt to locate head daughter tree from among daughters.
     * Go through daughterTrees looking for things from or not in a set given by
     * the contents of the array how, and if
     * you do not find one, take leftmost or rightmost perhaps matching thing iff
     * lastResort is true, otherwise return <code>null</code>.
     */
    protected Tree<String> traverseLocate(List<Tree<String>> daughterTrees, String[] how, boolean lastResort) {
        int headIdx;
        switch (how[0]) {
            case "left":
                headIdx = findLeftHead(daughterTrees, how);
                break;
            case "leftdis":
                headIdx = findLeftDisHead(daughterTrees, how);
                break;
            case "leftexcept":
                headIdx = findLeftExceptHead(daughterTrees, how);
                break;
            case "right":
                headIdx = findRightHead(daughterTrees, how);
                break;
            case "rightdis":
                headIdx = findRightDisHead(daughterTrees, how);
                break;
            case "rightexcept":
                headIdx = findRightExceptHead(daughterTrees, how);
                break;
            default:
                throw new IllegalStateException("ERROR: invalid direction type " + how[0] + " to nonTerminalInfo map in AbstractCollinsHeadFinder.");
        }

        // what happens if our rule didn't match anything
        if (headIdx < 0) {
            if (lastResort) {
                // use the default rule to try to match anything except categoriesToAvoid
                // if that doesn't match, we'll return the left or rightmost child (by
                // setting headIdx).  We want to be careful to ensure that postOperationFix
                // runs exactly once.
                String[] rule;
                if (how[0].startsWith("left")) {
                    headIdx = 0;
                    rule = defaultLeftRule;
                } else {
                    headIdx = daughterTrees.size() - 1;
                    rule = defaultRightRule;
                }
                Tree child = traverseLocate(daughterTrees, rule, false);
                if (child != null) {
                    return child;
                } else {
                    return daughterTrees.get(headIdx);
                }
            } else {
                // if we're not the last resort, we can return null to let the next rule try to match
                return null;
            }
        }

        headIdx = postOperationFix(headIdx, daughterTrees);

        return daughterTrees.get(headIdx);
    }

    private int findLeftHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int i = 1; i < how.length; i++) {
            for (int headIdx = 0; headIdx < daughterTrees.size(); headIdx++) {
                String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
                if (how[i].equals(childCat)) {
                    return headIdx;
                }
            }
        }
        return -1;
    }

    private int findLeftDisHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int headIdx = 0; headIdx < daughterTrees.size(); headIdx++) {
            String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
            for (int i = 1; i < how.length; i++) {
                if (how[i].equals(childCat)) {
                    return headIdx;
                }
            }
        }
        return -1;
    }

    private int findLeftExceptHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int headIdx = 0; headIdx < daughterTrees.size(); headIdx++) {
            String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
            boolean found = true;
            for (int i = 1; i < how.length; i++) {
                if (how[i].equals(childCat)) {
                    found = false;
                }
            }
            if (found) {
                return headIdx;
            }
        }
        return -1;
    }

    private int findRightHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int i = 1; i < how.length; i++) {
            for (int headIdx = daughterTrees.size() - 1; headIdx >= 0; headIdx--) {
                String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
                if (how[i].equals(childCat)) {
                    return headIdx;
                }
            }
        }
        return -1;
    }

    // from right, but search for any of the categories, not by category in turn
    private int findRightDisHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int headIdx = daughterTrees.size() - 1; headIdx >= 0; headIdx--) {
            String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
            for (int i = 1; i < how.length; i++) {
                if (how[i].equals(childCat)) {
                    return headIdx;
                }
            }
        }
        return -1;
    }

    private int findRightExceptHead(List<Tree<String>> daughterTrees, String[] how) {
        for (int headIdx = daughterTrees.size() - 1; headIdx >= 0; headIdx--) {
            String childCat = daughterTrees.get(headIdx).getLabel().split("\\^")[0];
            boolean found = true;
            for (int i = 1; i < how.length; i++) {
                if (how[i].equals(childCat)) {
                    found = false;
                }
            }
            if (found) {
                return headIdx;
            }
        }
        return -1;
    }

    /**
     * A way for subclasses to fix any heads under special conditions.
     * The default does nothing.
     *
     * @param headIdx       The index of the proposed head
     * @param daughterTrees The array of daughter trees
     * @return The new headIndex
     */
    protected int postOperationFix(int headIdx, List<Tree<String>> daughterTrees) {
        if (headIdx >= 2) {
            String prevLab = daughterTrees.get(headIdx - 1).getLabel().split("\\^")[0];
            if (prevLab.equals("CC") || prevLab.equals("CONJP")) {
                int newHeadIdx = headIdx - 2;
                Tree<String> t = daughterTrees.get(newHeadIdx);
                while (newHeadIdx >= 0 && t.isPreTerminal() &&
                        isPunctuationTag.contains(t.getLabel())) {
                    newHeadIdx--;
                }
                if (newHeadIdx >= 0) {
                    headIdx = newHeadIdx;
                }
            }
        }
        return headIdx;
    }
}
