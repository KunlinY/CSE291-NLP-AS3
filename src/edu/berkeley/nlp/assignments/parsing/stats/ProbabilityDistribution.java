package edu.berkeley.nlp.assignments.parsing.stats;

import java.util.Random;



public interface ProbabilityDistribution<E> extends java.io.Serializable {

  public double probabilityOf(E object) ;
  public double logProbabilityOf(E object) ;
  public E drawSample(Random random) ;
  
}
