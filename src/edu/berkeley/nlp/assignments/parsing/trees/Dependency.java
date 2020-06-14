package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;

import java.io.Serializable;



public interface Dependency<G extends Label,D extends Label,N> extends Serializable {

  /**
   * Describes the governor (regent/head) of the dependency relation.
   * @return The governor of this dependency
   */
  public G governor();

  /**
   * Describes the dependent (argument/modifier) of
   * the dependency relation.
   * @return the dependent of this dependency
   */
  public D dependent();

  
  public N name();

  
  public boolean equalsIgnoreName(Object o);

  
  public String toString(String format);

  
  public DependencyFactory dependencyFactory();

}
