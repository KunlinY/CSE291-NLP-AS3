package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import java.util.Collection;
import java.util.Iterator;

import java.util.function.Function;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;

/**
 * @author grenager
 * @author Sarah Spikes (sdspikes@cs.stanford.edu) (Templatization)
 */

public interface Extractor<T> {
  public T extract(Collection<Tree> trees);

  public T extract(Iterator<Tree> iterator, Function<Tree, Tree> f);
}

