package edu.berkeley.nlp.assignments.parsing.parser.common;

import java.io.PrintWriter;
import java.util.List;

import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.parser.KBestViterbiParser;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.util.ScoredObject;

public interface ParserQuery {
  boolean parse(List<? extends HasWord> sentence);
  
  boolean parseAndReport(List<? extends HasWord> sentence, PrintWriter pwErr);

  double getPCFGScore();

  Tree getBestParse();

  List<ScoredObject<Tree>> getKBestParses(int k);

  double getBestScore();

  Tree getBestPCFGParse();

  Tree getBestDependencyParse(boolean debinarize);

  Tree getBestFactoredParse();

  List<ScoredObject<Tree>> getBestPCFGParses();

  void restoreOriginalWords(Tree tree);

  boolean hasFactoredParse();

  List<ScoredObject<Tree>> getKBestPCFGParses(int kbestPCFG);

  List<ScoredObject<Tree>> getKGoodFactoredParses(int kbest);

  KBestViterbiParser getPCFGParser();

  KBestViterbiParser getFactoredParser();

  KBestViterbiParser getDependencyParser();

  void setConstraints(List<ParserConstraint> constraints);

  boolean saidMemMessage();

  /**
   * Parsing succeeded without any horrible errors or fallback
   */
  boolean parseSucceeded();

  /**
   * The sentence was skipped, probably because it was too long or of length 0
   */
  boolean parseSkipped();

  /**
   * The model had to fall back to a simpler model on the previous parse
   */
  boolean parseFallback();

  /**
   * The model ran out of memory on the most recent parse
   */
  boolean parseNoMemory();

  /**
   * The model could not parse the most recent sentence for some reason
   */
  boolean parseUnparsable();

  List<? extends HasWord> originalSentence();
}
