package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;



public interface DependencyFactory {

  public Dependency newDependency(Label regent, Label dependent);

  public Dependency newDependency(Label regent, Label dependent, Object name);

}
