package edu.berkeley.nlp.assignments.parsing.trees; 

import java.io.IOException;
import java.util.List;

import edu.berkeley.nlp.assignments.parsing.trees.tregex.TregexPattern;
import edu.berkeley.nlp.assignments.parsing.trees.tregex.TregexPatternCompiler;
import edu.berkeley.nlp.assignments.parsing.trees.tregex.tsurgeon.Tsurgeon;
import edu.berkeley.nlp.assignments.parsing.trees.tregex.tsurgeon.TsurgeonPattern;
import edu.berkeley.nlp.assignments.parsing.util.Pair;
import edu.berkeley.nlp.assignments.parsing.util.logging.Redwood;

/**
 * Helper class to perform a context-sensitive mapping of POS
 * tags in a tree to universal POS tags.
 *
 * @author Sebastian Schuster
 */

public class UniversalPOSMapper  {

  /** A logger for this class */
  private static final Redwood.RedwoodChannels log = Redwood.channels(UniversalPOSMapper.class);

  @SuppressWarnings("WeakerAccess")
  public static final String DEFAULT_TSURGEON_FILE = "edu/stanford/nlp/models/upos/ENUniversalPOS.tsurgeon";

  private static boolean loaded; // = false;

  private static List<Pair<TregexPattern, TsurgeonPattern>> operations; // = null;

  private UniversalPOSMapper() {} // static methods

  public static void load() {
    load(DEFAULT_TSURGEON_FILE);
  }

  public static void load(String filename) {
    try {
      operations = Tsurgeon.getOperationsFromFile(filename, "UTF-8", new TregexPatternCompiler());
    } catch (IOException e) {
      log.error(String.format("%s: Warning - could not load Tsurgeon file from %s.%n",
          UniversalPOSMapper.class.getSimpleName(), filename));
    }
    loaded = true;
  }

  public static Tree mapTree(Tree t) {
    if ( ! loaded) {
      load();
    }

    if (operations == null) {
      return t;
    }

    return Tsurgeon.processPatternsOnTree(operations, t.deepCopy());
  }

}
