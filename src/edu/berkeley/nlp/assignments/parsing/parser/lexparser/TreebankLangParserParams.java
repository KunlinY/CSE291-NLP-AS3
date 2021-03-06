package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.trees.*;
import edu.berkeley.nlp.assignments.parsing.util.Index;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;


/**
 * Contains language-specific methods commonly necessary to get a parser
 * to parse an arbitrary treebank.
 *
 * @author Roger Levy
 * @version 03/05/2003
 */
public interface TreebankLangParserParams extends TreebankFactory, Serializable {

  HeadFinder headFinder();

  HeadFinder typedDependencyHeadFinder();

  /**
   * Allows language specific processing (e.g., stemming) of head words.
   *
   * @param headWord An {@link edu.berkeley.nlp.assignments.parsing.ling.Label} that minimally implements the
   * {@link edu.berkeley.nlp.assignments.parsing.ling.HasWord} and {@link edu.berkeley.nlp.assignments.parsing.ling.HasTag} interfaces.
   * @return A processed {@link edu.berkeley.nlp.assignments.parsing.ling.Label}
   */
  Label processHeadWord(Label headWord);

  void setInputEncoding(String encoding);

  void setOutputEncoding(String encoding);

  /**
   * If evalGFs = true, then the evaluation of parse trees will include evaluation on grammatical functions.
   * Otherwise, evaluation will strip the grammatical functions.
   */
  void setEvaluateGrammaticalFunctions(boolean evalGFs);

  /**
   * Returns the output encoding being used.
   * @return The output encoding being used.
   */
  String getOutputEncoding();

  /**
   * Returns the input encoding being used.
   * @return The input encoding being used.
   */
  String getInputEncoding();


  /**
   * Returns a factory for reading in trees from the source you want.  It's
   * the responsibility of trf to deal properly with character-set encoding
   * of the input.  It also is the responsibility of trf to properly
   * normalize trees.
   *
   * @return A factory that vends an appropriate TreeReader
   */
  TreeReaderFactory treeReaderFactory();


  /**
   * Vends a {@link Lexicon} object suitable to the particular language/treebank combination of interest.
   * @param op Options as to how the Lexicon behaves
   * @return A Lexicon, constructed based on the given option
   */
  Lexicon lex(Options op, Index<String> wordIndex, Index<String> tagIndex);


  /**
   * The tree transformer applied to trees prior to evaluation.
   * For instance, it might delete punctuation nodes.  This method will
   * be applied both to the parse output tree and to the gold
   * tree.  The exact specification depends on "standard practice" for
   * various treebanks.
   *
   * @return A TreeTransformer that performs adjustments to trees to delete
   *     or equivalence class things not evaluated in the parser performance
   *     evaluation.
   */
  TreeTransformer collinizer();


  /**
   * the tree transformer used to produce trees for evaluation.  Will
   * be applied both to the parse output tree and to the gold
   * tree. Should strip punctuation and maybe do some other
   * things. The evalb version should strip some more stuff
   * off. (finish this doc!)
   */
  TreeTransformer collinizerEvalb();

  /**
   * Required to extend TreebankFactory
   */
  Treebank treebank();

  /**
   * returns a TreebankLanguagePack containing Treebank-specific (but
   * not parser-specific) info such as what is punctuation, and also
   * information about the structure of labels
   */
  TreebankLanguagePack treebankLanguagePack();

  /**
   * returns a PrintWriter used to print output. It's the
   * responsibility of the returned PrintWriter to deal properly with
   * character encodings for the relevant treebank
   */
  PrintWriter pw();

  /**
   * returns a PrintWriter used to print output to the OutputStream
   * o. It's the responsibility of the returned PrintWriter to deal
   * properly with character encodings for the relevant treebank
   */
  PrintWriter pw(OutputStream o);


  /**
   * Returns the splitting strings used for selective splits.
   *
   * @return An array containing ancestor-annotated Strings: categories
   *         should be split according to these ancestor annotations.
   */
  String[] sisterSplitters();


  /**
   * Returns a TreeTransformer appropriate to the Treebank which
   * can be used to remove functional tags (such as "-TMP") from
   * categories.
   */
  TreeTransformer subcategoryStripper();

  /**
   * This method does language-specific tree transformations such
   * as annotating particular nodes with language-relevant features.
   * Such parameterizations should be inside the specific
   * TreebankLangParserParams class.  This method is recursively
   * applied to each node in the tree (depth first, left-to-right),
   * so you shouldn't write this method to apply recursively to tree
   * members.  This method is allowed to (and in some cases does)
   * destructively change the input tree {@code t}. It changes both
   * labels and the tree shape.
   *
   * @param t The input tree (with non-language specific annotation already
   *           done, so you need to strip back to basic categories)
   * @param root The root of the current tree (can be null for words)
   * @return The fully annotated tree node (with daughters still as you
   *           want them in the final result)
   */
  Tree transformTree(Tree t, Tree root);

  /**
   * display language-specific settings
   */
  void display();

  /**
   * Set a language-specific option according to command-line flags.
   * This routine should try to process the option starting at args[i] (which
   * might potentially be several arguments long if it takes arguments).
   * It should return the index after the last index it consumed in
   * processing.  In particular, if it cannot process the current option,
   * the return value should be i.
   *
   * @param args Array of command line arguments
   * @param i    Index in command line arguments to try to process as an option
   * @return The index of the item after arguments processed as part of this
   *         command line option.
   */
  int setOptionFlag(String[] args, int i);


  /**
   * Return a default sentence of the language (for testing).
   * @return A default sentence of the language
   */
  List<? extends HasWord> defaultTestSentence();

  Extractor<DependencyGrammar> dependencyGrammarExtractor(Options op, Index<String> wordIndex, Index<String> tagIndex);

  /**
   * Give the parameters for smoothing in the MLEDependencyGrammar.
   * @return an array of doubles with smooth_aT_hTWd, smooth_aTW_hTWd, smooth_stop, and interp
   */
  double[] MLEDependencyGrammarSmoothingParams();

  /** Whether our code provides support for converting phrase structure
   *  (constituency) parses to (basic) dependency parses.
   *  @return Whether dependencies are supported for a language
   *
   */
  boolean supportsBasicDependencies();

  /** Set whether to generate original Stanford Dependencies or the newer
   *  Universal Dependencies.
   *
   *  @param originalDependencies Whether to generate SD
   */
  void setGenerateOriginalDependencies(boolean originalDependencies);

  /** Whether to generate original Stanford Dependencies or the newer
   *  Universal Dependencies.
   *
   *  @return Whether to generate SD
   */
  boolean generateOriginalDependencies();


  /** When run inside StanfordCoreNLP, which flags should be used by default.
   *  E.g., the current use is that for English, we want it to run with the
   *  option to retain "-TMP" functional tags but not to impose that on
   *  other languages.
   */
  String[] defaultCoreNLPFlags();

}
