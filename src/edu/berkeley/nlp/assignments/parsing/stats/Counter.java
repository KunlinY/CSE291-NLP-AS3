package edu.berkeley.nlp.assignments.parsing.stats;

import edu.berkeley.nlp.assignments.parsing.util.Factory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface Counter<E>  {

  Factory<Counter<E>> getFactory();

  void setDefaultReturnValue(double rv) ;

  double defaultReturnValue() ;

  double getCount(Object key);

  void setCount(E key, double value);

  double incrementCount(E key, double value);

  double incrementCount(E key);

  double decrementCount(E key, double value);

  double decrementCount(E key);

  double logIncrementCount(E key, double value);

  void addAll(Counter<E> counter);

  double remove(E key);

  boolean containsKey(E key);

  Set<E> keySet();

  Collection<Double> values();

  Set<Map.Entry<E,Double>> entrySet();

  void clear();

  int size();

  double totalCount();

}
