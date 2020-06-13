package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.assignments.parsing.BinaryRule;
import edu.berkeley.nlp.assignments.parsing.Grammar;
import edu.berkeley.nlp.assignments.parsing.SimpleLexicon;
import edu.berkeley.nlp.assignments.parsing.UnaryRule;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.CounterMap;
import edu.berkeley.nlp.util.Indexer;

import java.util.*;

public class PCFGParser {
//    protected final String goalStr;
//    protected final Indexer<String> stateIndex;
//    protected final Set<String> tagIndex;
//    protected final Grammar grammar;
//    protected final SimpleLexicon lex;
//
//    // inside scores
//    protected double[][][] iScore;  // start idx, end idx, state -> logProb (ragged; null for end <= start)
//    // outside scores
//    protected double[][][] oScore;  // start idx, end idx, state -> logProb
//    protected double bestScore;
//
//    protected int[][][] wordsInSpan; // number of words in span with this state
//
//    protected boolean[][] oFilteredStart; // [start][state]; only used by unused outsideRuleFilter
//    protected boolean[][] oFilteredEnd; // [end][state]; only used by unused outsideRuleFilter
//
//    protected boolean[][] iPossibleByL; // [start][state]
//    protected boolean[][] iPossibleByR; // [end][state]
//    protected boolean[][] oPossibleByL; // [start][state]
//    protected boolean[][] oPossibleByR; // [end][state]
//
//    protected Boolean[] words;  // words of sentence being parsed as word Numberer ints
//    private String[] originalCoreLabels;
//    protected int length; // one larger than true length of sentence; includes boundary symbol in count
//    protected boolean[][] tags;
//    protected int myMaxLength = -0xDEADBEEF;
//
//    protected final int numStates;
//    protected int arraySize = 0;
//
//    static final boolean spillGuts = false;
//
//    protected boolean floodTags = false;
//    protected List sentence = null;
//
//    protected int[][] narrowLExtent; // = null; // [end][state]: the rightmost left extent of state s ending at position i
//    protected int[][] wideLExtent; // = null; // [end][state] the leftmost left extent of state s ending at position i
//    protected int[][] narrowRExtent; // = null; // [start][state]: the leftmost right extent of state s starting at position i
//    protected int[][] wideRExtent; // = null; // [start][state] the rightmost right extent of state s starting at position i
//
//    protected final boolean[] isTag; // this records whether grammar states (stateIndex) correspond to POS tags
//
//
//    public boolean parse(List<String> sentence) {
//        if (sentence != this.sentence) {
//            this.sentence = sentence;
//            floodTags = false;
//        }
//
//        System.out.println("Starting PCFG parse...");
//
//        length = sentence.size();
//        if (length > arraySize) {
//            considerCreatingArrays(length);
//        }
//        int goal = stateIndex.indexOf(goalStr);
//
//        System.out.println("Initializing PCFG...");
//
//        // map input words to words array (wordIndex ints)
//        words = new Boolean[length];
//        originalCoreLabels = new String[length];
//        int unk = 0;
//        StringBuilder unkWords = new StringBuilder("[");
//        // int unkIndex = wordIndex.size();
//
//        for (int i = 0; i < length; i++) {
//            String s = sentence.get(i);
//            originalCoreLabels[i] = sentence.get(i);
//            words[i] = lex.isKnown(s);
//        }
//
//        // initialize inside and outside score arrays
//        System.out.println("Wiping arrays...");
//
//        for (int start = 0; start < length; start++) {
//            for (int end = start + 1; end <= length; end++) {
//                Arrays.fill(iScore[start][end], Float.NEGATIVE_INFINITY);
//            }
//        }
//
//        for (int loc = 0; loc <= length; loc++) {
//            Arrays.fill(narrowLExtent[loc], -1); // the rightmost left with state s ending at i that we can get is the beginning
//            Arrays.fill(wideLExtent[loc], length + 1); // the leftmost left with state s ending at i that we can get is the end
//        }
//        for (int loc = 0; loc < length; loc++) {
//            Arrays.fill(narrowRExtent[loc], length + 1); // the leftmost right with state s starting at i that we can get is the end
//            Arrays.fill(wideRExtent[loc], -1); // the rightmost right with state s starting at i that we can get is the beginning
//        }
//
//        System.out.println("Starting filters...");
//        System.out.println("Tagging...");
//
//        initializeChart(sentence);
//
//        System.out.println("Starting insides...");
//
//        // do the inside probabilities
//        doInsideScores();
//        System.out.println("PCFG parsing " + length + " words (incl. stop): insideScore = " + iScore[0][length][goal]);
//
//        bestScore = iScore[0][length][goal];
//        boolean succeeded = hasParse();
//        if (!succeeded && !floodTags) {
//            floodTags = true; // sentence will try to reparse
//            // ms: disabled message. this is annoying and it doesn't really provide much information
//            //log.info("Trying recovery parse...");
//            return parse(sentence);
//        }
//
//        return succeeded;
//    }
//
//    /** Fills in the iScore array of each category over each span
//     *  of length 2 or more.
//     */
//    void doInsideScores() {
//        for (int diff = 2; diff <= length; diff++) {
//
//            // usually stop one short because boundary symbol only combines
//            // with whole sentence span. So for 3 word sentence + boundary = 4,
//            // length == 4, and do [0,2], [1,3]; [0,3]; [0,4]
//            for (int start = 0; start < ((diff == length) ? 1: length - diff); start++) {
//                doInsideChartCell(diff, start);
//            } // for start
//        } // for diff (i.e., span)
//    } // end doInsideScores()
//
//
//    private void doInsideChartCell(final int diff, final int start) {
//        int end = start + diff;
//
//        int[] narrowRExtent_start = narrowRExtent[start];
//        // caching this saved 2% in the inner loop
//        int[] wideRExtent_start = wideRExtent[start];
//        int[] narrowLExtent_end = narrowLExtent[end];
//        int[] wideLExtent_end = wideLExtent[end];
//        double[][] iScore_start = iScore[start];
//        double[] iScore_start_end = iScore_start[end];
//
//        for (int leftState = 0; leftState < numStates; leftState++) {
//            int narrowR = narrowRExtent_start[leftState];
//            if (narrowR >= end) {  // can this left constituent leave space for a right constituent?
//                continue;
//            }
//
//            List<BinaryRule> leftRules = grammar.getBinaryRulesByLeftChild(leftState);
//            //      if (spillGuts) System.out.println("Found " + leftRules.length + " left rules for state " + stateIndex.get(leftState));
//            for (BinaryRule rule : leftRules) {
//                int rightChild = rule.rightChild;
//                int narrowL = narrowLExtent_end[rightChild];
//                if (narrowL < narrowR) { // can this right constituent fit next to the left constituent?
//                    continue;
//                }
//                int min2 = wideLExtent_end[rightChild];
//                int min = (narrowR > min2 ? narrowR : min2);
//                // Erik Frey 2009-12-17: This is unnecessary: narrowR is <= narrowL (established in previous check) and wideLExtent[e][r] is always <= narrowLExtent[e][r] by design, so the check will never evaluate true.
//                // if (min > narrowL) { // can this right constituent stretch far enough to reach the left constituent?
//                //   continue;
//                // }
//                int max1 = wideRExtent_start[leftState];
//                int max = (max1 < narrowL ? max1 : narrowL);
//                if (min > max) { // can this left constituent stretch far enough to reach the right constituent?
//                    continue;
//                }
//                double pS = rule.getScore();
//                int parentState = rule.parent;
//                double oldIScore = iScore_start_end[parentState];
//                double bestIScore = oldIScore;
//                boolean foundBetter;  // always set below for this rule
//                //System.out.println("Min "+min+" max "+max+" start "+start+" end "+end);
//
//                for (int split = min; split <= max; split++) {
//
//                    double lS = iScore_start[split][leftState];
//                    if (lS == Float.NEGATIVE_INFINITY) {
//                        continue;
//                    }
//                    double rS = iScore[split][end][rightChild];
//                    if (rS == Float.NEGATIVE_INFINITY) {
//                        continue;
//                    }
//                    double tot = pS + lS + rS;
//                    if (tot > bestIScore) {
//                        bestIScore = tot;
//                    }
//                } // for split point
//                foundBetter = bestIScore > oldIScore;
//
//                if (foundBetter) { // this way of making "parentState" is better than previous
//                    iScore_start_end[parentState] = bestIScore;
//
//                    if (oldIScore == Float.NEGATIVE_INFINITY) {
//                        if (start > narrowLExtent_end[parentState]) {
//                            narrowLExtent_end[parentState] = wideLExtent_end[parentState] = start;
//                        } else if (start < wideLExtent_end[parentState]) {
//                            wideLExtent_end[parentState] = start;
//                        }
//                        if (end < narrowRExtent_start[parentState]) {
//                            narrowRExtent_start[parentState] = wideRExtent_start[parentState] = end;
//                        } else if (end > wideRExtent_start[parentState]) {
//                            wideRExtent_start[parentState] = end;
//                        }
//                    }
//                } // end if foundBetter
//            } // end for leftRules
//        } // end for leftState
//        // do right restricted rules
//        for (int rightState = 0; rightState < numStates; rightState++) {
//            int narrowL = narrowLExtent_end[rightState];
//            if (narrowL <= start) {
//                continue;
//            }
//            List<BinaryRule> rightRules = grammar.getBinaryRulesByRightChild(rightState);
//            //      if (spillGuts) System.out.println("Found " + rightRules.length + " right rules for state " + stateIndex.get(rightState));
//            for (BinaryRule rule : rightRules) {
//                //      if (spillGuts) System.out.println("Considering rule for " + start + " to " + end + ": " + rightRules[i]);
//
//                int leftChild = rule.leftChild;
//                int narrowR = narrowRExtent_start[leftChild];
//                if (narrowR > narrowL) {
//                    continue;
//                }
//                int min2 = wideLExtent_end[rightState];
//                int min = (narrowR > min2 ? narrowR : min2);
//                // Erik Frey 2009-12-17: This is unnecessary: narrowR is <= narrowL (established in previous check) and wideLExtent[e][r] is always <= narrowLExtent[e][r] by design, so the check will never evaluate true.
//                // if (min > narrowL) {
//                //   continue;
//                // }
//                int max1 = wideRExtent_start[leftChild];
//                int max = (max1 < narrowL ? max1 : narrowL);
//                if (min > max) {
//                    continue;
//                }
//                double pS = rule.getScore();
//                int parentState = rule.parent;
//                double oldIScore = iScore_start_end[parentState];
//                double bestIScore = oldIScore;
//                boolean foundBetter; // always initialized below
//                //System.out.println("Start "+start+" end "+end+" min "+min+" max "+max);
//
//                // find the split that can use this rule to make the max score
//                for (int split = min; split <= max; split++) {
//
//                    double lS = iScore_start[split][leftChild];
//                    // cdm [2012]: Test whether removing these 2 tests might speed things up because less branching?
//                    // jab [2014]: oddly enough, removing these tests helps the chinese parser but not the english parser.
//                    if (lS == Float.NEGATIVE_INFINITY) {
//                        continue;
//                    }
//                    double rS = iScore[split][end][rightState];
//                    if (rS == Float.NEGATIVE_INFINITY) {
//                        continue;
//                    }
//                    double tot = pS + lS + rS;
//                    if (tot > bestIScore) {
//                        bestIScore = tot;
//                    }
//                } // end for split
//                foundBetter = bestIScore > oldIScore;
//
//                if (foundBetter) { // this way of making "parentState" is better than previous
//                    iScore_start_end[parentState] = bestIScore;
//                    if (oldIScore == Float.NEGATIVE_INFINITY) {
//                        if (start > narrowLExtent_end[parentState]) {
//                            narrowLExtent_end[parentState] = wideLExtent_end[parentState] = start;
//                        } else if (start < wideLExtent_end[parentState]) {
//                            wideLExtent_end[parentState] = start;
//                        }
//                        if (end < narrowRExtent_start[parentState]) {
//                            narrowRExtent_start[parentState] = wideRExtent_start[parentState] = end;
//                        } else if (end > wideRExtent_start[parentState]) {
//                            wideRExtent_start[parentState] = end;
//                        }
//                    }
//                } // end if foundBetter
//            } // for rightRules
//        } // for rightState
//
//        // do unary rules -- one could promote this loop and put start inside
//        for (int state = 0; state < numStates; state++) {
//            double iS = iScore_start_end[state];
//            if (iS == Float.NEGATIVE_INFINITY) {
//                continue;
//            }
//
//            List<UnaryRule> unaries = grammar.getUnaryRulesByChild(state);
//            for (UnaryRule ur : unaries) {
//
//                int parentState = ur.parent;
//                double pS = ur.getScore();
//                double tot = iS + pS;
//                double cur = iScore_start_end[parentState];
//                boolean foundBetter;  // always set below
//                foundBetter = (tot > cur);
//                if (foundBetter) {
////                    if (spillGuts) log.info("Could build " + stateIndex.get(parentState) + " from " + start + " to " + end + " with score " + tot);
//                    iScore_start_end[parentState] = tot;
//                    if (cur == Float.NEGATIVE_INFINITY) {
//                        if (start > narrowLExtent_end[parentState]) {
//                            narrowLExtent_end[parentState] = wideLExtent_end[parentState] = start;
//                        } else if (start < wideLExtent_end[parentState]) {
//                            wideLExtent_end[parentState] = start;
//                        }
//                        if (end < narrowRExtent_start[parentState]) {
//                            narrowRExtent_start[parentState] = wideRExtent_start[parentState] = end;
//                        } else if (end > wideRExtent_start[parentState]) {
//                            wideRExtent_start[parentState] = end;
//                        }
//                    }
//                } // end if foundBetter
//            } // for UnaryRule r
//        } // for unary rules
//    }
//
//
//    private void initializeChart(List<String>  sentence) {
//        int boundary = wordIndex.indexOf(Lexicon.BOUNDARY);
//
//        for (int start = 0; start < length; start++) {
//            int word = words[start];
//            int end = start + 1;
//            Arrays.fill(tags[start], false);
//
//            double[] iScore_start_end = iScore[start][end];
//            int[] narrowRExtent_start = narrowRExtent[start];
//            int[] narrowLExtent_end = narrowLExtent[end];
//            int[] wideRExtent_start = wideRExtent[start];
//            int[] wideLExtent_end = wideLExtent[end];
//
//            boolean assignedSomeTag = false;
//
//            if ( ! floodTags || word == boundary) {
//                // in this case we generate the taggings in the lexicon,
//                // which may itself be tagging flexibly or using a strict lexicon.
//                for (Iterator<IntTaggedWord> taggingI = lex.ruleIteratorByWord(word, start, null); taggingI.hasNext(); ) {
//                    IntTaggedWord tagging = taggingI.next();
//                    int state = stateIndex.indexOf(tagIndex.get(tagging.tag));
//                    // if word was supplied with a POS tag, skip all taggings
//                    // not basicCategory() compatible with supplied tag.
//                    // try {
//                    double lexScore = lex.score(tagging, start, wordIndex.get(tagging.word), null); // score the cell according to P(word|tag) in the lexicon
//                    if (lexScore > Float.NEGATIVE_INFINITY) {
//                        assignedSomeTag = true;
//                        iScore_start_end[state] = lexScore;
//                        narrowRExtent_start[state] = end;
//                        narrowLExtent_end[state] = start;
//                        wideRExtent_start[state] = end;
//                        wideLExtent_end[state] = start;
//                    }
//                    // } catch (Exception e) {
//                    // e.printStackTrace();
//                    // System.out.println("State: " + state + " tags " + Numberer.getGlobalNumberer("tags").object(tagging.tag));
//                    // }
//                    int tag = tagging.tag;
//                    tags[start][tag] = true;
//                    //if (start == length-2 && tagging.parent == puncTag)
//                    //  lastIsPunc = true;
//                }
//            } // end if ( ! floodTags || word == boundary)
//
//            if ( ! assignedSomeTag) {
//                // If you got here, either you were using forceTags (gold tags)
//                // and the gold tag was not seen with that word in the training data
//                // or we are in floodTags=true (recovery parse) mode
//                // Here, we give words all tags for
//                // which the lexicon score is not -Inf, not just seen or
//                // specified taggings
//                for (int state = 0; state < numStates; state++) {
//                    if (isTag[state] && iScore_start_end[state] == Float.NEGATIVE_INFINITY) {
//
//                        double lexScore = lex.score(new IntTaggedWord(word, tagIndex.indexOf(stateIndex.get(state))), start, wordIndex.get(word), null);
//
//                        if (lexScore > Float.NEGATIVE_INFINITY) {
//                            iScore_start_end[state] = lexScore;
//                            narrowRExtent_start[state] = end;
//                            narrowLExtent_end[state] = start;
//                            wideRExtent_start[state] = end;
//                            wideLExtent_end[state] = start;
//                        }
//                    }
//                }
//            } // end if ! assignedSomeTag
//
//            // tag multi-counting
//            for (int state = 0; state < numStates; state++) {
//                if (isTag[state]) {
//                    iScore_start_end[state] *= (1.0 + 1.0);
//                }
//            }
//
//            if (floodTags && ! (word == boundary)) {
//                // if parse failed because of tag coverage, we put in all tags with
//                // a score of -1000, by fiat.  You get here from the invocation of
//                // parse(ls) inside parse(ls) *after* floodTags has been turned on.
//                // Search above for "floodTags = true".
//                for (int state = 0; state < numStates; state++) {
//                    if (isTag[state] && iScore_start_end[state] == Float.NEGATIVE_INFINITY) {
//                        iScore_start_end[state] = -1000.0f;
//                        narrowRExtent_start[state] = end;
//                        narrowLExtent_end[state] = start;
//                        wideRExtent_start[state] = end;
//                        wideLExtent_end[state] = start;
//                    }
//                }
//            }
//
//            // Apply unary rules in diagonal cells of chart
//            for (int state = 0; state < numStates; state++) {
//                double iS = iScore_start_end[state];
//                if (iS == Float.NEGATIVE_INFINITY) {
//                    continue;
//                }
//                List<UnaryRule> unaries = grammar.getUnaryRulesByChild(state);
//                for (UnaryRule ur : unaries) {
//                    int parentState = ur.parent;
//                    double pS = ur.getScore();
//                    double tot = iS + pS;
//                    if (tot > iScore_start_end[parentState]) {
//                        iScore_start_end[parentState] = tot;
//                        narrowRExtent_start[parentState] = end;
//                        narrowLExtent_end[parentState] = start;
//                        wideRExtent_start[parentState] = end;
//                        wideLExtent_end[parentState] = start;
//                    }
//                }
//            }
//        } // end for start
//    } // end initializeChart(List sentence)
//
//
//    public boolean hasParse() {
//        return getBestScore() > Double.NEGATIVE_INFINITY;
//    }
//
//    private static final double TOL = 1e-5;
//
//    protected static boolean matches(double x, double y) {
//        return (Math.abs(x - y) / (Math.abs(x) + Math.abs(y) + 1e-10) < TOL);
//    }
//
//    public double getBestScore() {
//        return getBestScore(goalStr);
//    }
//
//    public double getBestScore(String stateName) {
//        if (length > arraySize) {
//            return Double.NEGATIVE_INFINITY;
//        }
//        if (!stateIndex.contains(stateName)) {
//            return Double.NEGATIVE_INFINITY;
//        }
//        int goal = stateIndex.indexOf(stateName);
//        if (iScore == null || iScore.length == 0 || iScore[0].length <= length || iScore[0][length].length <= goal) {
//            return Double.NEGATIVE_INFINITY;
//        }
//        return iScore[0][length][goal];
//    }
//
//
//    public Tree getBestParse() {
//        Tree internalTree = extractBestParse(goalStr, 0, length);
//        return internalTree;
//    }
//
//    /** Return the best parse of some category/state over a certain span. */
//    protected Tree extractBestParse(String goalStr, int start, int end) {
//        return extractBestParse(stateIndex.indexOf(goalStr), start, end);
//    }
//
//    private Tree extractBestParse(int goal, int start, int end) {
//        // find source of inside score
//        // no backtraces so we can speed up the parsing for its primary use
//        double bestScore = iScore[start][end][goal];
//        String goalStr = stateIndex.get(goal);
//
//        // check tags
//        if (end - start <= 1 && tagIndex.contains(goalStr)) {
//            IntTaggedWord tagging = new IntTaggedWord(words[start], tagIndex.indexOf(goalStr));
//            String contextStr = originalCoreLabels[start];
//            double tagScore = lex.score(tagging, start, wordIndex.get(words[start]), contextStr);
//            if (tagScore > Float.NEGATIVE_INFINITY || floodTags) {
//                // return a pre-terminal tree
//                String terminalLabel = originalCoreLabels[start];
//
//                Tree wordNode = tf.newLeaf(terminalLabel);
//                Tree tagNode = tf.newTreeNode(goalStr, Collections.singletonList(wordNode));
//                tagNode.setScore(bestScore);
//                if (terminalLabel.tag() != null) {
//                    tagNode.label().setValue(terminalLabel.tag());
//                }
//                if (tagNode.label() instanceof HasTag) {
//                    ((HasTag) tagNode.label()).setTag(tagNode.label().value());
//                }
//                return tagNode;
//            }
//        }
//        // check binaries first
//        for (int split = start + 1; split < end; split++) {
//            for (BinaryRule br : grammar.getBinaryRulesByParent(goal)) {
//                double score = br.getScore() + iScore[start][split][br.leftChild] + iScore[split][end][br.rightChild];
//                boolean matches;
//                matches = matches(score, bestScore);
//                if (matches) {
//                    // build binary split
//                    Tree<String> leftChildTree = extractBestParse(br.leftChild, start, split);
//                    Tree<String> rightChildTree = extractBestParse(br.rightChild, split, end);
//                    List<Tree<String>> children = new ArrayList<>();
//                    children.add(leftChildTree);
//                    children.add(rightChildTree);
//                    Tree<String> result = new Tree<>(goalStr, children);
//                    result.setScore(score);
//                    // log.info("    Found Binary node: "+result);
//                    return result;
//                }
//            }
//        }
//        // check unaries
//        // note that even though we parse with the unary-closed grammar, we can
//        // extract the best parse with the non-unary-closed grammar, since all
//        // the intermediate states in the chain must have been built, and hence
//        // we can exploit the sparser space and reconstruct the full tree as we go.
//        // for (Iterator<UnaryRule> unaryI = ug.closedRuleIteratorByParent(goal); unaryI.hasNext(); ) {
//        for (UnaryRule ur : grammar.getUnaryRulesByParent(goal)) {
//            // log.info("  Trying " + ur + " dtr score: " + iScore[start][end][ur.child]);
//            double score = ur.getScore() + iScore[start][end][ur.child];
//            boolean matches;
//            matches = matches(score, bestScore);
//            if (ur.child != ur.parent && matches) {
//                // build unary
//                Tree<String> childTree = extractBestParse(ur.child, start, end);
//                Tree<String> result = new Tree<>(goalStr, Collections.singletonList(childTree));
//                // log.info("    Matched!  Unary node: "+result);
//                result.setScore(score);
//                return result;
//            }
//        }
//        System.out.println("Warning: no parse found in ExhaustivePCFGParser.extractBestParse: failing on: [" + start + ", " + end + "] looking for " + goalStr);
//        return null;
//    }
//
//    public PCFGParser(Grammar grammar, SimpleLexicon lexicon, CounterMap<Integer, String> spanToCategories) {
//        //    System.out.println("ExhaustivePCFGParser constructor called.");
//        this.lex = lexicon;
//        this.grammar = grammar;
//        goalStr = "ROOT";
//        this.stateIndex = grammar.getLabelIndexer();
//        this.tagIndex = lexicon.getAllTags();
//
//        numStates = stateIndex.size();
//        isTag = new boolean[numStates];
//        // tag index is smaller, so we fill by iterating over the tag index
//        // rather than over the state index
//        for (String tag : tagIndex) {
//            int state = stateIndex.indexOf(tag);
//            if (state < 0) {
//                continue;
//            }
//            isTag[state] = true;
//        }
//    }
//
//    private void considerCreatingArrays(int length) {
//        try {
//            createArrays(length + 1);
//        } catch (OutOfMemoryError e) {
//            myMaxLength = length;
//            if (arraySize > 0) {
//                try {
//                    createArrays(arraySize);
//                } catch (OutOfMemoryError e2) {
//                    throw new RuntimeException("CANNOT EVEN CREATE ARRAYS OF ORIGINAL SIZE!!");
//                }
//            }
//            throw e;
//        }
//        arraySize = length + 1;
//        System.out.println("Created PCFG parser arrays of size " + arraySize);
//    }
//
//    protected void createArrays(int length) {
//        // zero out some stuff first in case we recently ran out of memory and are reallocating
//        clearArrays();
//
//        int numTags = tagIndex.size();
//        // allocate just the parts of iScore and oScore used (end > start, etc.)
//        // todo: with some modifications to doInsideScores, we wouldn't need to allocate iScore[i,length] for i != 0 and i != length
//        //    System.out.println("initializing iScore arrays with length " + length + " and numStates " + numStates);
//        iScore = new double[length][length + 1][];
//        for (int start = 0; start < length; start++) {
//            for (int end = start + 1; end <= length; end++) {
//                iScore[start][end] = new double[numStates];
//            }
//        }
//
//        narrowRExtent = new int[length][numStates];
//        wideRExtent = new int[length][numStates];
//        narrowLExtent = new int[length + 1][numStates];
//        wideLExtent = new int[length + 1][numStates];
//        tags = new boolean[length][numTags];
//        System.out.println("ExhaustivePCFGParser constructor finished.");
//    }
//
//    private void clearArrays() {
//        iScore = oScore = null;
//        iPossibleByL = iPossibleByR = oPossibleByL = oPossibleByR = null;
//        oFilteredEnd = oFilteredStart = null;
//        tags = null;
//        narrowRExtent = wideRExtent = narrowLExtent = wideLExtent = null;
//    }
}
