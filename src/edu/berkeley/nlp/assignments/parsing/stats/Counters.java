// Stanford JavaNLP support classes
// Copyright (c) 2004-2008 The Board of Trustees of
// The Leland Stanford Junior University. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//    java-nlp-support@lists.stanford.edu
//    http://nlp.stanford.edu/software/

package edu.berkeley.nlp.assignments.parsing.stats;

import edu.berkeley.nlp.assignments.parsing.math.ArrayMath;
import edu.berkeley.nlp.assignments.parsing.util.PriorityQueue;
import edu.berkeley.nlp.assignments.parsing.util.*;

import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

public class Counters  {

  private static final double LOG_E_2 = Math.log(2.0);

  private Counters() {} // only static methods

  public static <E> double logSum(Counter<E> c) {
    return ArrayMath.logSum(ArrayMath.unbox(c.values()));
  }

  public static <E> double max(Counter<E> c) {
    return max(c, Double.NEGATIVE_INFINITY);  // note[gabor]: Should the default actually be 0 rather than negative_infinity?
  }
  public static <E> double max(Counter<E> c, double valueIfEmpty) {
    if (c.size() == 0) {
      return valueIfEmpty;
    } else {
      double max = Double.NEGATIVE_INFINITY;
      for (double v : c.values()) {
        max = Math.max(max, v);
      }
      return max;
    }
  }

  public static <E> double min(Counter<E> c) {
    double min = Double.POSITIVE_INFINITY;
    for (double v : c.values()) {
      min = Math.min(min, v);
    }
    return min;
  }

  public static <E> E argmax(Counter<E> c) {
    return argmax(c, (x, y) -> 0, null);

  }

  public static <E> E argmax(Counter<E> c, Comparator<E> tieBreaker, E defaultIfEmpty) {
    if (Thread.interrupted()) {  // A good place to check for interrupts -- called from many annotators
      throw new RuntimeInterruptedException();
    }
    if (c.size() == 0) {
      return defaultIfEmpty;
    }
    double max = Double.NEGATIVE_INFINITY;
    E argmax = null;
    for (E key : c.keySet()) {
      double count = c.getCount(key);
      if (argmax == null || count > max || (count == max && tieBreaker.compare(key, argmax) < 0)) {
        max = count;
        argmax = key;
      }
    }
    return argmax;
  }

  public static <E> double mean(Counter<E> c) {
    return c.totalCount() / c.size();
  }

  public static <E> double standardDeviation(Counter<E> c) {
    double std = 0;
    double mean = c.totalCount() / c.size();
    for (Map.Entry<E, Double> en : c.entrySet()) {
      std += (en.getValue() - mean) * (en.getValue() - mean);
    }
    return Math.sqrt(std / c.size());
  }

  public static <E> void addInPlace(Counter<E> target, Counter<E> arg) {
    for (Map.Entry<E, Double> entry : arg.entrySet()) {
      double count = entry.getValue();
      if (count != 0) {
        target.incrementCount(entry.getKey(), count);
      }
    }
  }

  public static <T1, T2> TwoDimensionalCounter<T1, T2> add(TwoDimensionalCounter<T1, T2> arg1, TwoDimensionalCounter<T1, T2> arg2) {
    TwoDimensionalCounter<T1, T2> add = new TwoDimensionalCounter<>();
    Counters.addInPlace(add , arg1);
    Counters.addInPlace(add , arg2);
    return add;
  }

  public static <T1, T2> void addInPlace(TwoDimensionalCounter<T1, T2> target, TwoDimensionalCounter<T1, T2> arg) {
    for (T1 outer : arg.firstKeySet())
      for (T2 inner : arg.secondKeySet()) {
        target.incrementCount(outer, inner, arg.getCount(outer, inner));
      }
  }

  public static <E> void subtractInPlace(Counter<E> target, Counter<E> arg) {
    for (E key : arg.keySet()) {
      target.decrementCount(key, arg.getCount(key));
    }
  }

  public static <E> Counter<E> divideInPlace(Counter<E> target, double divisor) {
    for (Entry<E, Double> entry : target.entrySet()) {
      target.setCount(entry.getKey(), entry.getValue() / divisor);
    }
    return target;
  }


  public static <E> void normalize(Counter<E> target) {
    divideInPlace(target, target.totalCount());
  }

  public static <E, F> void normalize(TwoDimensionalCounter<E, F> target) {
    Counters.divideInPlace(target, target.totalCount());
  }

  public static <E> Set<E> retainNonZeros(Counter<E> counter) {
    Set<E> removed = Generics.newHashSet();
    for (E key : counter.keySet()) {
      if (counter.getCount(key) == 0.0) {
        removed.add(key);
      }
    }
    for (E key : removed) {
      counter.remove(key);
    }
    return removed;
  }
  public static <E> Set<E> retainAbove(Counter<E> counter, double countThreshold) {
    Set<E> removed = Generics.newHashSet();
    for (E key : counter.keySet()) {
      if (counter.getCount(key) < countThreshold) {
        removed.add(key);
      }
    }
    for (E key : removed) {
      counter.remove(key);
    }
    return removed;
  }

  public static <T1, T2> Counter<T2> transform(Counter<T1> c, Function<T1, T2> f) {
    Counter<T2> c2 = new ClassicCounter<>();
    for (T1 key : c.keySet()) {
      c2.setCount(f.apply(key), c.getCount(key));
    }
    return c2;
  }

  public static <E> Comparator<E> toComparatorDescending(final Counter<E> counter) {
    return (o1, o2) -> Double.compare(counter.getCount(o2), counter.getCount(o1));
  }

  public static <E> edu.berkeley.nlp.assignments.parsing.util.PriorityQueue<E> toPriorityQueue(Counter<E> c) {
    return null;
  }

  public static <E> double dotProduct(Counter<E> c1, Counter<E> c2) {
    double dotProd = 0.0;
    if (c1.size() > c2.size()) {
      Counter<E> tmpCnt = c1;
      c1 = c2;
      c2 = tmpCnt;
    }
    for (E key : c1.keySet()) {
      double count1 = c1.getCount(key);
      if (Double.isNaN(count1) || Double.isInfinite(count1)) {
        throw new RuntimeException("Counters.dotProduct infinite or NaN value for key: " + key + '\t' + c1.getCount(key) + '\t' + c2.getCount(key));
      }
      if (count1 != 0.0) {
        double count2 = c2.getCount(key);
        if (Double.isNaN(count2) || Double.isInfinite(count2)) {
          throw new RuntimeException("Counters.dotProduct infinite or NaN value for key: " + key + '\t' + c1.getCount(key) + '\t' + c2.getCount(key));
        }
        if (count2 != 0.0) {
          // this is the inner product
          dotProd += (count1 * count2);
        }
      }
    }
    return dotProd;
  }

  public static <E> Counter<E> add(Counter<E> c1, Collection<E> c2) {
    Counter<E> result = c1.getFactory().create();
    addInPlace(result, c1);
    for (E key : c2) {
      result.incrementCount(key, 1);
    }
    return result;
  }

  public static <E> Counter<E> add(Counter<E> c1, Counter<E> c2) {
    Counter<E> result = c1.getFactory().create();
    for (E key : Sets.union(c1.keySet(), c2.keySet())) {
      result.setCount(key, c1.getCount(key) + c2.getCount(key));
    }
    retainNonZeros(result);
    return result;
  }

  /**
   * increments every key in the counter by value
   */
  public static <E> Counter<E> add(Counter<E> c1, double value) {
    Counter<E> result = c1.getFactory().create();
    for (E key : c1.keySet()) {
      result.setCount(key, c1.getCount(key) + value);
    }
    return result;
  }

  public static <E> double klDivergence(Counter<E> from, Counter<E> to) {
    double result = 0.0;
    double tot = (from.totalCount());
    double tot2 = (to.totalCount());
    // System.out.println("tot is " + tot + " tot2 is " + tot2);
    for (E key : from.keySet()) {
      double num = (from.getCount(key));
      if (num == 0) {
        continue;
      }
      num /= tot;
      double num2 = (to.getCount(key));
      num2 /= tot2;
      // System.out.println("num is " + num + " num2 is " + num2);
      double logFract = Math.log(num / num2);
      if (logFract == Double.NEGATIVE_INFINITY) {
        return Double.NEGATIVE_INFINITY; // can't recover
      }
      result += num * (logFract / LOG_E_2); // express it in log base 2
    }
    return result;
  }

  public static <E, C extends Counter<E>> double sumSquares(C c) {
    double lenSq = 0.0;
    for (E key : c.keySet()) {
      double count = c.getCount(key);
      lenSq += (count * count);
    }
    return lenSq;
  }

  public static <E> Counter<E> linearCombination(Counter<E> c1, double w1, Counter<E> c2, double w2) {
    Counter<E> result = c1.getFactory().create();
    for (E o : c1.keySet()) {
      result.incrementCount(o, c1.getCount(o) * w1);
    }
    for (E o : c2.keySet()) {
      result.incrementCount(o, c2.getCount(o) * w2);
    }
    return result;
  }

  public static <E> void printCounterComparison(Counter<E> a, Counter<E> b, PrintWriter out) {
    if (a.equals(b)) {
      out.println("Counters are equal.");
      return;
    }
    for (E key : a.keySet()) {
      double aCount = a.getCount(key);
      double bCount = b.getCount(key);
      if (Math.abs(aCount - bCount) > 1e-5) {
        out.println("Counters differ on key " + key + '\t' + a.getCount(key) + " vs. " + b.getCount(key));
      }
    }
    // left overs
    Set<E> rest = Generics.newHashSet(b.keySet());
    rest.removeAll(a.keySet());

    for (E key : rest) {
      double aCount = a.getCount(key);
      double bCount = b.getCount(key);
      if (Math.abs(aCount - bCount) > 1e-5) {
        out.println("Counters differ on key " + key + '\t' + a.getCount(key) + " vs. " + b.getCount(key));
      }
    }
  }
  @SuppressWarnings("unchecked")
  public static <E, C extends Counter<E>> C scale(C c, double s) {
    C scaled = (C) c.getFactory().create();
    for (E key : c.keySet()) {
      scaled.setCount(key, c.getCount(key) * s);
    }
    return scaled;
  }

  public static <E> String toString(Counter<E> counter, int maxKeysToPrint) {
    return Counters.toPriorityQueue(counter).toString(maxKeysToPrint);
  }

  public static <E> String toString(Counter<E> counter, NumberFormat nf) {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    List<E> list = ErasureUtils.sortedIfPossible(counter.keySet());
    // */
    for (Iterator<E> iter = list.iterator(); iter.hasNext();) {
      E key = iter.next();
      sb.append(key);
      sb.append('=');
      sb.append(nf.format(counter.getCount(key)));
      if (iter.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append('}');
    return sb.toString();
  }

  public static <E> String toString(Counter<E> counter, NumberFormat nf, String preAppend, String postAppend, String keyValSeparator, String itemSeparator) {
    StringBuilder sb = new StringBuilder();
    sb.append(preAppend);
    // List<E> list = new ArrayList<E>(map.keySet());
    // try {
    // Collections.sort(list); // see if it can be sorted
    // } catch (Exception e) {
    // }
    for (Iterator<E> iter = counter.keySet().iterator(); iter.hasNext();) {
      E key = iter.next();
      double d = counter.getCount(key);
      sb.append(key);
      sb.append(keyValSeparator);
      sb.append(nf.format(d));
      if (iter.hasNext()) {
        sb.append(itemSeparator);
      }
    }
    sb.append(postAppend);
    return sb.toString();
  }
  public static <E> String toVerticalString(Counter<E> c, int k, String fmt, boolean swap) {
    PriorityQueue<E> q = Counters.toPriorityQueue(c);
    List<E> sortedKeys = q.toSortedList();
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (Iterator<E> keyI = sortedKeys.iterator(); keyI.hasNext() && i < k; i++) {
      E key = keyI.next();
      double val = q.getPriority(key);
      if (swap) {
        sb.append(String.format(fmt, key, val));
      } else {
        sb.append(String.format(fmt, val, key));
      }
      if (keyI.hasNext()) {
        sb.append('\n');
      }
    }
    return sb.toString();
  }

  static final Random RAND = new Random();

  public static <T> T sample(Counter<T> c, Random rand) {
    if (rand == null) rand = RAND;
    double r = rand.nextDouble() * c.totalCount();
    double total = 0.0;

    for (T t : c.keySet()) { // arbitrary ordering, but presumably stable
      total += c.getCount(t);
      if (total >= r)
        return t;
    }
    return c.keySet().iterator().next();
  }

  public static <T> T sample(Counter<T> c) {
    return sample(c, null);
  }


  public static <T> Counter<T> exp(Counter<T> c) {
    Counter<T> d = c.getFactory().create();
    for (T t : c.keySet()) {
      d.setCount(t, Math.exp(c.getCount(t)));
    }
    return d;
  }

  public static <E> boolean equals(Counter<E> o1, Counter<E> o2) {
    return equals(o1, o2, 0.0);
  }

  public static <E> boolean equals(Counter<E> o1, Counter<E> o2, double tolerance) {
    if (o1 == o2) {
      return true;
    }

    if (Math.abs(o1.totalCount() - o2.totalCount()) > tolerance) {
      return false;
    }

    if (!o1.keySet().equals(o2.keySet())) {
      return false;
    }

    for (E key : o1.keySet()) {
      if (Math.abs(o1.getCount(key) - o2.getCount(key)) > tolerance) {
        return false;
      }
    }

    return true;

  }

  public static <E> Map<E, Double> asMap(final Counter<E> counter) {
    return new AbstractMap<E, Double>() {
      @Override
      public int size() {
        return counter.size();
      }

      @Override
      public Set<Entry<E, Double>> entrySet() {
        return counter.entrySet();
      }

      @Override
      @SuppressWarnings("unchecked")
      public boolean containsKey(Object key) {
        return counter.containsKey((E) key);
      }

      @Override
      @SuppressWarnings("unchecked")
      public Double get(Object key) {
        return counter.getCount((E) key);
      }

      @Override
      public Double put(E key, Double value) {
        double last = counter.getCount(key);
        counter.setCount(key, value);
        return last;
      }

      @Override
      @SuppressWarnings("unchecked")
      public Double remove(Object key) {
        return counter.remove((E) key);
      }

      @Override
      public Set<E> keySet() {
        return counter.keySet();
      }
    };
  }

  public static<A,B> void divideInPlace(TwoDimensionalCounter<A, B> counter, double divisor) {
    for(Entry<A, ClassicCounter<B>> c: counter.entrySet()){
      Counters.divideInPlace(c.getValue(), divisor);
    }
    counter.recomputeTotal();
  }

}
