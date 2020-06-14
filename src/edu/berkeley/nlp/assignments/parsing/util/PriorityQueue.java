package edu.berkeley.nlp.assignments.parsing.util;

import java.util.List;
import java.util.Set;

public interface PriorityQueue<E> extends Set<E> {

  
  public E removeFirst();

  
  public E getFirst();

  
  public double getPriority();

  
  public double getPriority(E key);

  
  public boolean add(E key, double priority);


  
  public boolean changePriority(E key, double priority);

  
  public boolean relaxPriority(E key, double priority);

  public List<E> toSortedList();

  
  public String toString(int maxKeysToPrint);

}
