package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;


/**
 * A factory for dependencies of a certain type.
 *
 * @author Christopher Manning
 */
public interface DependencyFactory {

  public Dependency newDependency(Label regent, Label dependent);

  public Dependency newDependency(Label regent, Label dependent, Object name);

}
