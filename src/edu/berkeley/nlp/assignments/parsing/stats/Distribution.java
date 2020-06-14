package edu.berkeley.nlp.assignments.parsing.stats; 

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Distribution<E> implements Sampler<E>, ProbabilityDistribution<E>  {

  private static final long serialVersionUID = 6707148234288637809L;

  private int numberOfKeys;
  private double reservedMass;
  protected Counter<E> counter;
  private static final int NUM_ENTRIES_IN_STRING = 20;

  private static final boolean verbose = false;

  public Counter<E> getCounter() {
    return counter;
  }

  @Override
  public E drawSample() {
    return sampleFrom();
  }

  @Override
  public E drawSample(Random random) {
    return sampleFrom(random);
  }

  public String toString(NumberFormat nf) {
    return Counters.toString(counter, nf);
  }

  public Set<E> keySet() {
    return counter.keySet();
  }

  public boolean containsKey(E key) {
    return counter.containsKey(key);
  }

  public double getCount(E key) {
    return counter.getCount(key);
  }


  public static <E> Distribution<E> getDistribution(Counter<E> counter) {
    return getDistributionWithReservedMass(counter, 0.0);
  }

  public static <E> Distribution<E> getDistributionWithReservedMass(Counter<E> counter, double reservedMass) {
    Distribution<E> norm = new Distribution<>();
    norm.counter = new ClassicCounter<>();
    norm.numberOfKeys = counter.size();
    norm.reservedMass = reservedMass;
    double total = counter.totalCount() * (1 + reservedMass);
    if (total == 0.0) {
      total = 1.0;
    }
    for (E key : counter.keySet()) {
      double count = counter.getCount(key) / total;
      //      if (Double.isNaN(count) || count < 0.0 || count> 1.0 ) throw new RuntimeException("count=" + counter.getCount(key) + " total=" + total);
      norm.counter.setCount(key, count);
    }
    return norm;
  }

  public E sampleFrom() {
    return Counters.sample(counter);
  }
  public E sampleFrom(Random random) {
    return Counters.sample(counter, random);
  }

  public double probabilityOf(E key) {
    if (counter.containsKey(key)) {
      return counter.getCount(key);
    } else {
      int remainingKeys = numberOfKeys - counter.size();
      if (remainingKeys <= 0) {
        return 0.0;
      } else {
        return (reservedMass / remainingKeys);
      }
    }
  }

  public double logProbabilityOf(E key) {
    double prob = probabilityOf(key);
    return Math.log(prob);
  }

  public double totalCount() {
    return counter.totalCount() + reservedMass;
  }

  public void addToKeySet(E o) {
    if (!counter.containsKey(o)) {
      counter.setCount(o, 0);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof Distribution && equals((Distribution) o);
  }

  public boolean equals(Distribution<E> distribution) {
    if (numberOfKeys != distribution.numberOfKeys) {
      return false;
    }
    if (reservedMass != distribution.reservedMass) {
      return false;
    }
    return counter.equals(distribution.counter);
  }

  @Override
  public int hashCode() {
    int result = numberOfKeys;
    long temp = Double.doubleToLongBits(reservedMass);
    result = 29 * result + (int) (temp ^ (temp >>> 32));
    result = 29 * result + counter.hashCode();
    return result;
  }

  private Distribution() {}

  @Override
  public String toString() {
    NumberFormat nf = new DecimalFormat("0.0##E0");
    List<E> keyList = new ArrayList<>(keySet());
    Collections.sort(keyList, (o1, o2) -> {
      if (probabilityOf(o1) < probabilityOf(o2)) {
        return 1;
      } else {
        return -1;
      }
    });
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < NUM_ENTRIES_IN_STRING; i++) {
      if (keyList.size() <= i) {
        break;
      }
      E o = keyList.get(i);
      double prob = probabilityOf(o);
      sb.append(o).append(":").append(nf.format(prob)).append(" ");
    }
    sb.append("]");
    return sb.toString();
  }

}
