package edu.berkeley.nlp.assignments.parsing.fsm;

import java.util.Set;

/**
 * @author Dan Klein (klein@cs.stanford.edu)
 */
public interface Block<E> {

  public Set<E> getMembers();

}
