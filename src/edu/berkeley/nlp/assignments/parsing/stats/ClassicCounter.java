// Stanford JavaNLP support classes
// Copyright (c) 2001-2008 The Board of Trustees of
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

import edu.berkeley.nlp.assignments.parsing.math.SloppyMath;
import edu.berkeley.nlp.assignments.parsing.util.Factory;
import edu.berkeley.nlp.assignments.parsing.util.MapFactory;
import edu.berkeley.nlp.assignments.parsing.util.MutableDouble;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class ClassicCounter<E> implements Serializable, Counter<E>, Iterable<E> {

  Map<E, MutableDouble> map;  // accessed by DeltaCounter
  private final MapFactory<E, MutableDouble> mapFactory;
  private double totalCount; // = 0.0
  private double defaultValue; // = 0.0;


  private static final long serialVersionUID = 4L;

  private transient MutableDouble tempMDouble; // = null;

  public ClassicCounter() {
    this(MapFactory.hashMapFactory());
  }

  public ClassicCounter(int initialCapacity) {
    this(MapFactory.hashMapFactory(), initialCapacity);
  }

  public ClassicCounter(MapFactory<E,MutableDouble> mapFactory) {
    this.mapFactory = mapFactory;
    this.map = mapFactory.newMap();
  }

  public ClassicCounter(MapFactory<E,MutableDouble> mapFactory, int initialCapacity) {
    this.mapFactory = mapFactory;
    this.map = mapFactory.newMap(initialCapacity);
  }

  public ClassicCounter(Counter<E> c) {
    this();
    Counters.addInPlace(this, c);
    setDefaultReturnValue(c.defaultReturnValue());
  }

  public ClassicCounter(Collection<E> collection) {
    this();
    for (E key : collection) {
      incrementCount(key);
    }
  }

  MapFactory<E,MutableDouble> getMapFactory() {
    return mapFactory;
  }


  @Override
  public Factory<Counter<E>> getFactory() {
    return new ClassicCounterFactory<>(getMapFactory());
  }

  private static class ClassicCounterFactory<E> implements Factory<Counter<E>> {

    private static final long serialVersionUID = 1L;

    private final MapFactory<E,MutableDouble> mf;

    private ClassicCounterFactory(MapFactory<E,MutableDouble> mf) {
      this.mf = mf;
    }

    @Override
    public Counter<E> create() {
      return new ClassicCounter<>(mf);
    }
  }

  @Override
  public final void setDefaultReturnValue(double rv) { defaultValue = rv; }

  @Override
  public double defaultReturnValue() { return defaultValue; }


  @Override
  public double getCount(Object key) {
    Number count = map.get(key);
    if (count == null) {
      return defaultValue; // haven't seen this object before -> default count
    }
    return count.doubleValue();
  }

  @Override
  public void setCount(E key, double count) {
    if (tempMDouble == null) {
      //System.out.println("creating mdouble");
      tempMDouble = new MutableDouble();
    }
    //System.out.println("setting mdouble");
    tempMDouble.set(count);
    //System.out.println("putting mdouble in map");
    tempMDouble = map.put(key, tempMDouble);
    //System.out.println("placed mDouble in map");

    totalCount += count;
    if (tempMDouble != null) {
      totalCount -= tempMDouble.doubleValue();
    }
  }


  @Override
  public double incrementCount(E key, double count) {
    if (tempMDouble == null) {
      tempMDouble = new MutableDouble();
    }
    MutableDouble oldMDouble = map.put(key, tempMDouble);
    totalCount += count;
    if (oldMDouble != null) {
      count += oldMDouble.doubleValue();
    }
    tempMDouble.set(count);
    tempMDouble = oldMDouble;

    return count;
  }

  @Override
  public final double incrementCount(E key) {
    return incrementCount(key, 1.0);
  }

  @Override
  public double decrementCount(E key, double count) {
    return incrementCount(key, -count);
  }

  @Override
  public double decrementCount(E key) {
    return incrementCount(key, -1.0);
  }

  @Override
  public double logIncrementCount(E key, double count) {
    if (tempMDouble == null) {
      tempMDouble = new MutableDouble();
    }
    MutableDouble oldMDouble = map.put(key, tempMDouble);
    if (oldMDouble != null) {
      count = SloppyMath.logAdd(count, oldMDouble.doubleValue());
      totalCount += count - oldMDouble.doubleValue();
    } else {
      totalCount += count;
    }
    tempMDouble.set(count);
    tempMDouble = oldMDouble;

    return count;
  }


  @Override
  public void addAll(Counter<E> counter) {
    Counters.addInPlace(this, counter);
  }

  @Override
  public double remove(E key) {
    MutableDouble d = mutableRemove(key); // this also updates totalCount
    if(d != null) {
      return d.doubleValue();
    }
    return defaultValue;
  }

  @Override
  public boolean containsKey(E key) {
    return map.containsKey(key);
  }

  @Override
  public Set<E> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<Double> values() {
    return new AbstractCollection<Double>() {
      @Override
      public Iterator<Double> iterator() {
        return new Iterator<Double>() {
          Iterator<MutableDouble> inner = map.values().iterator();

          @Override
          public boolean hasNext() {
            return inner.hasNext();
          }

          @Override
          public Double next() {
            // copy so as to give safety to mutable internal representation
            return Double.valueOf(inner.next().doubleValue());
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }

      @Override
      public int size() {
        return map.size();
      }

      @Override
      public boolean contains(Object v) {
        return v instanceof Double && map.values().contains(new MutableDouble((Double) v));
      }

    };
  }

  @Override
  public Set<Map.Entry<E,Double>> entrySet() {
    return new AbstractSet<Map.Entry<E,Double>>() {
      @Override
      public Iterator<Entry<E, Double>> iterator() {
        return new Iterator<Entry<E,Double>>() {
          final Iterator<Entry<E,MutableDouble>> inner = map.entrySet().iterator();

          @Override
          public boolean hasNext() {
            return inner.hasNext();
          }

          @Override
          public Entry<E, Double> next() {
            return new Entry<E,Double>() {
              final Entry<E,MutableDouble> e = inner.next();

              public double getDoubleValue() {
                return e.getValue().doubleValue();
              }

              public double setValue(double value) {
                final double old = e.getValue().doubleValue();
                e.getValue().set(value);
                totalCount = totalCount - old + value;
                return old;
              }

              @Override
              public E getKey() {
                return e.getKey();
              }

              @Override
              public Double getValue() {
                return getDoubleValue();
              }

              @Override
              public Double setValue(Double value) {
                return setValue(value.doubleValue());
              }
            };
          }

          @Override
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

  @Override
  public void clear() {
    map.clear();
    totalCount = 0.0;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public double totalCount() {
    return totalCount;
  }

  @Override
  public Iterator<E> iterator() {
    return keySet().iterator();
  }

  private MutableDouble mutableRemove(E key) {
    MutableDouble md = map.remove(key);
    if (md != null) {
      totalCount -= md.doubleValue();
    }
    return md;
  }

  public void removeAll(Collection<E> keys) {
    for (E key : keys) {
      mutableRemove(key);
    }
  }

  public boolean isEmpty() {
    return size() == 0;
  }


  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if ( ! (o instanceof Counter)) {
      return false;
    } else if ( ! (o instanceof ClassicCounter)) {
      return Counters.equals(this, (Counter<E>) o);
    }

    final ClassicCounter<E> counter = (ClassicCounter<E>) o;
    return totalCount == counter.totalCount && map.equals(counter.map);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public String toString() {
    return map.toString();
  }

  public static ClassicCounter<String> valueOfIgnoreComments(String s) {
      ClassicCounter<String> result = new ClassicCounter<>();
      String[] lines = s.split("\n");
      for (String line : lines) {
        String[] fields = line.split("\t");
        if (fields.length != 2) {
          if (line.startsWith("#")) {
            continue;
          } else {
            throw new RuntimeException("Got unsplittable line: \"" + line + '\"');
          }
        }
        result.setCount(fields[0], Double.parseDouble(fields[1]));
      }
      return result;
    }

  public static ClassicCounter<String> fromString(String s) {
    ClassicCounter<String> result = new ClassicCounter<>();
    if (!s.startsWith("{") || !s.endsWith("}")) {
      throw new RuntimeException("invalid format: ||"+s+"||");
    }
    s = s.substring(1, s.length()-1);
    String[] lines = s.split(", ");
    for (String line : lines) {
      String[] fields = line.split("=");
      if (fields.length!=2) throw new RuntimeException("Got unsplittable line: \"" + line + '\"');
      result.setCount(fields[0], Double.parseDouble(fields[1]));
    }
    return result;
  }

}
