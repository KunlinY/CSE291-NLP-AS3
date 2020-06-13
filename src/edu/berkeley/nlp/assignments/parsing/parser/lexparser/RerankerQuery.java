package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.trees.Tree;

/**
 * Process a Tree and return a score.  Typically constructed by the
 * Reranker, possibly given some extra information about the sentence
 * being parsed.
 *
 * @author John Bauer
 */
public interface RerankerQuery {
  double score(Tree tree);
}
