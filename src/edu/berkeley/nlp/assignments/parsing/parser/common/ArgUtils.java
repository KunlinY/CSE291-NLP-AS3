package edu.berkeley.nlp.assignments.parsing.parser.common;

import edu.berkeley.nlp.assignments.parsing.util.Pair;
import edu.berkeley.nlp.assignments.parsing.util.Triple;

import java.io.FileFilter;

/**
 * Utility methods or common blocks of code for dealing with parser
 * arguments, such as extracting Treebank information
 */
public class ArgUtils {
  private ArgUtils() {}

  // helper function
  public static int numSubArgs(String[] args, int index) {
    int i = index;
    while (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
      i++;
    }
    return i - index;
  }

  public static Pair<String, FileFilter> getTreebankDescription(String[] args, int argIndex, String flag) {
    Triple<String, FileFilter, Double> description = getWeightedTreebankDescription(args, argIndex, flag);
    return Pair.makePair(description.first(), description.second());
  }

  public static Triple<String, FileFilter, Double> getWeightedTreebankDescription(String[] args, int argIndex, String flag) {
    return null;
  }
}
