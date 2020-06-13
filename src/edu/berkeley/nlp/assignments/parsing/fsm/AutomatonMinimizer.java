package edu.berkeley.nlp.assignments.parsing.fsm;


/**
 * @author Dan Klein (klein@cs.stanford.edu)
 */
public interface AutomatonMinimizer {
  public TransducerGraph minimizeFA(TransducerGraph unminimizedFA);
}
