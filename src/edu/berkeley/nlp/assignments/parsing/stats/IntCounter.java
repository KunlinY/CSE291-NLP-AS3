package edu.berkeley.nlp.assignments.parsing.stats;

import edu.berkeley.nlp.assignments.parsing.util.*;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;

public class IntCounter<E> extends AbstractCounter<E> implements Serializable {

  @SuppressWarnings({"NonSerializableFieldInSerializableClass"})
  private Map<E, MutableInteger>  map;
  @SuppressWarnings("unchecked")
  private MapFactory mapFactory;
  private int totalCount;
  private int defaultValue; // = 0;

  private static final Comparator<Object> naturalComparator = new NaturalComparator<>();
  private static final long serialVersionUID = 4;

  public IntCounter() {
    this(MapFactory.<E,MutableInteger>hashMapFactory());
  }

  public IntCounter(MapFactory<E,MutableInteger> mapFactory) {
    this.mapFactory = mapFactory;
    map = mapFactory.newMap();
    totalCount = 0;
  }

  public IntCounter(IntCounter<E> c) {
    this();
    addAll(c);
  }


  public MapFactory<E, MutableInteger> getMapFactory() {
    return ErasureUtils.<MapFactory<E,MutableInteger>>uncheckedCast(mapFactory);
  }

  public void setDefaultReturnValue(double rv) {
    defaultValue = (int) rv;
  }

  public void setDefaultReturnValue(int rv) {
    defaultValue = rv;
  }

  public double defaultReturnValue() {
    return defaultValue;
  }

  public int totalIntCount() {
    return totalCount;
  }

  public double totalDoubleCount() {
    return totalCount;
  }

  public int totalIntCount(Predicate<E> filter) {
    int total = 0;
    for (E key : map.keySet()) {
      if (filter.test(key)) {
        total += getIntCount(key);
      }
    }
    return (total);
  }

  public double totalDoubleCount(Predicate<E> filter) {
    return totalIntCount(filter);
  }

  public double totalCount(Predicate<E> filter) {
    return totalDoubleCount(filter);
  }

  public double averageCount() {
    return totalCount() / map.size();
  }

  
  public double getCount(Object key) {
    return getIntCount(key);
  }

  public String getCountAsString(E key) {
    return Integer.toString(getIntCount(key));
  }

  
  public int getIntCount(Object key) {
    MutableInteger count =  map.get(key);
    if (count == null) {
      return defaultValue; // haven't seen this object before -> 0 count
    }
    return count.intValue();
  }

  
  public double getNormalizedCount(E key) {
    return getCount(key) / (totalCount());
  }

  
  public void setCount(E key, int count) {
    if (tempMInteger == null) {
      tempMInteger = new MutableInteger();
    }
    tempMInteger.set(count);
    tempMInteger = map.put(key, tempMInteger);


    totalCount += count;
    if (tempMInteger != null) {
      totalCount -= tempMInteger.intValue();
    }

  }

  public void setCount(E key, String s) {
    setCount(key, Integer.parseInt(s));
  }

  // for more efficient memory usage
  private transient MutableInteger tempMInteger = null;

  
  public void setCounts(Collection<E> keys, int count) {
    for (E key : keys) {
      setCount(key, count);
    }
  }

  
  public int incrementCount(E key, int count) {
    if (tempMInteger == null) {
      tempMInteger = new MutableInteger();
    }

    MutableInteger oldMInteger = map.put(key, tempMInteger);
    totalCount += count;
    if (oldMInteger != null) {
      count += oldMInteger.intValue();
    }
    tempMInteger.set(count);
    tempMInteger = oldMInteger;

    return count;
  }

  
  @Override
  public double incrementCount(E key) {
    return incrementCount(key, 1);
  }

  
  public void incrementCounts(Collection<E> keys, int count) {
    for (E key : keys) {
      incrementCount(key, count);
    }
  }

  
  public void incrementCounts(Collection<E> keys) {
    incrementCounts(keys, 1);
  }

  
  public int decrementCount(E key, int count) {
    return incrementCount(key, -count);
  }

  
  @Override
  public double decrementCount(E key) {
    return decrementCount(key, 1);
  }

  
  public void decrementCounts(Collection<E> keys, int count) {
    incrementCounts(keys, -count);
  }

  
  public void decrementCounts(Collection<E> keys) {
    decrementCounts(keys, 1);
  }

  
  public void addAll(IntCounter<E> counter) {
    for (E key : counter.keySet()) {
      int count = counter.getIntCount(key);
      incrementCount(key, count);
    }
  }

  
  public void subtractAll(IntCounter<E> counter) {
    for (E key : map.keySet()) {
      decrementCount(key, counter.getIntCount(key));
    }
  }

  // MAP LIKE OPERATIONS

  public boolean containsKey(E key) {
    return map.containsKey(key);
  }

  
  public double remove(E key) {
    totalCount -= getCount(key); // subtract removed count from total (may be 0)
    MutableInteger val = map.remove(key);
    if (val == null) {
      return Double.NaN;
    } else {
      return val.doubleValue();
    }
  }

  
  public void removeAll(Collection<E> c) {
    for (E key : c) {
      remove(key);
    }
  }

  
  public void clear() {
    map.clear();
    totalCount = 0;
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public Set<E> keySet() {
    return map.keySet();
  }

  
  public Set<Map.Entry<E,Double>> entrySet() {
    return new AbstractSet<Map.Entry<E,Double>>() {
      @Override
      public Iterator<Entry<E, Double>> iterator() {
        return new Iterator<Entry<E,Double>>() {
          final Iterator<Entry<E,MutableInteger>> inner = map.entrySet().iterator();

          public boolean hasNext() {
            return inner.hasNext();
          }

          public Entry<E, Double> next() {
            return new Map.Entry<E,Double>() {
              final Entry<E,MutableInteger> e = inner.next();

              public E getKey() {
                return e.getKey();
              }

              public Double getValue() {
                return e.getValue().doubleValue();
              }

              public Double setValue(Double value) {
                final double old = e.getValue().doubleValue();
                e.getValue().set(value.intValue());
                totalCount = totalCount - (int)old + value.intValue();
                return old;
              }
            };
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public int size() {
        return map.size();
      }
    };
  }

  // OBJECT STUFF

  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof IntCounter)) {
      return false;
    }

    final IntCounter counter = (IntCounter) o;

    return map.equals(counter.map);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public String toString() {
    return map.toString();
  }


  @SuppressWarnings("unchecked")
  public String toString(NumberFormat nf, String preAppend, String postAppend, String keyValSeparator, String itemSeparator) {
    StringBuilder sb = new StringBuilder();
    sb.append(preAppend);
    List<E> list = new ArrayList<>(map.keySet());
    try {
      Collections.sort((List)list); // see if it can be sorted
    } catch (Exception e) {
    }
    for (Iterator<E> iter = list.iterator(); iter.hasNext();) {
      Object key = iter.next();
      MutableInteger d = map.get(key);
      sb.append(key + keyValSeparator);
      sb.append(nf.format(d));
      if (iter.hasNext()) {
        sb.append(itemSeparator);
      }
    }
    sb.append(postAppend);
    return sb.toString();
  }


  @SuppressWarnings("unchecked")
  public String toString(NumberFormat nf) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    List<E> list = new ArrayList<>(map.keySet());
    try {
      Collections.sort((List)list); // see if it can be sorted
    } catch (Exception e) {
    }
    for (Iterator<E> iter = list.iterator(); iter.hasNext();) {
      Object key = iter.next();
      MutableInteger d = map.get(key);
      sb.append(key + "=");
      sb.append(nf.format(d));
      if (iter.hasNext()) {
        sb.append(", ");
      }
    }
    sb.append("}");
    return sb.toString();
  }

  @Override
  public Object clone() {
    return new IntCounter<>(this);
  }

  // EXTRA CALCULATION METHODS

  
  public void removeZeroCounts() {
    map.keySet().removeIf(e -> getCount(e) == 0);
  }

  
  public int max() {
    int max = Integer.MIN_VALUE;
    for (E key : map.keySet()) {
      max = Math.max(max, getIntCount(key));
    }
    return max;
  }

  public int min() {
    int min = Integer.MAX_VALUE;
    for (E key : map.keySet()) {
      min = Math.min(min, getIntCount(key));
    }
    return min;
  }

  public E argmax(Comparator<E> tieBreaker) {
    int max = Integer.MIN_VALUE;
    E argmax = null;
    for (E key : keySet()) {
      int count = getIntCount(key);
      if (argmax == null || count > max || (count == max && tieBreaker.compare(key, argmax) < 0)) {
        max = count;
        argmax = key;
      }
    }
    return argmax;
  }


  public E argmax() {
    return argmax(ErasureUtils.<Comparator<E>>uncheckedCast(naturalComparator));
  }

  public E argmin(Comparator<E> tieBreaker) {
    int min = Integer.MAX_VALUE;
    E argmin = null;
    for (E key : map.keySet()) {
      int count = getIntCount(key);
      if (argmin == null || count < min || (count == min && tieBreaker.compare(key, argmin) < 0)) {
        min = count;
        argmin = key;
      }
    }
    return argmin;
  }

  private static class NaturalComparator<T> implements Comparator<T> {
    public int compare(T o1, T o2) {
      if (o1 instanceof Comparable) {
        return ErasureUtils.<Comparable<T>>uncheckedCast(o1).compareTo(o2);
      }
      return 0; // soft-fail
    }
  }

  //
  // For compatibilty with the Counter interface
  //

  public Factory<Counter<E>> getFactory() {
    return new Factory<Counter<E>>() {
      private static final long serialVersionUID = 7470763055803428477L;

      public Counter<E> create() {
        return new IntCounter<>(getMapFactory());
      }
    };
  }

  public void setCount(E key, double value) {
    setCount(key, (int)value);
  }

  @Override
  public double incrementCount(E key, double value) {
    incrementCount(key, (int)value);
    return getCount(key);
  }

  public double totalCount() {
    return totalDoubleCount();
  }

  public Collection<Double> values() {
    return new AbstractCollection<Double>() {
      @Override
      public Iterator<Double> iterator() {
        return new Iterator<Double>() {
          Iterator<MutableInteger> inner = map.values().iterator();

          public boolean hasNext() {
            return inner.hasNext();
          }

          public Double next() {
            return inner.next().doubleValue();
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public int size() {
        return map.size();
      }
    };
  }

  public Iterator<E> iterator() {
    return keySet().iterator();
  }

}
