package edu.berkeley.nlp.assignments.parsing.util;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class MapFactory<K,V> implements Serializable {

  protected MapFactory() {
  }

  private static final long serialVersionUID = 4529666940763477360L;

  @SuppressWarnings("unchecked")
  public static final MapFactory HASH_MAP_FACTORY = new HashMapFactory();

  @SuppressWarnings("unchecked")
  public static final MapFactory IDENTITY_HASH_MAP_FACTORY = new IdentityHashMapFactory();

  @SuppressWarnings("unchecked")
  private static final MapFactory WEAK_HASH_MAP_FACTORY = new WeakHashMapFactory();

  @SuppressWarnings("unchecked")
  private static final MapFactory TREE_MAP_FACTORY = new TreeMapFactory();

  @SuppressWarnings("unchecked")
  private static final MapFactory LINKED_HASH_MAP_FACTORY = new LinkedHashMapFactory();

  @SuppressWarnings("unchecked")
  private static final MapFactory ARRAY_MAP_FACTORY = new ArrayMapFactory();

  public static final MapFactory CONCURRENT_MAP_FACTORY = new ConcurrentMapFactory();

  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> hashMapFactory() {
    return HASH_MAP_FACTORY;
  }
  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> identityHashMapFactory() {
    return IDENTITY_HASH_MAP_FACTORY;
  }

  /** Return a MapFactory that returns a WeakHashMap.
   *  <i>Implementation note: This method uses the same trick as the methods
   *  like emptyMap() introduced in the Collections class in JDK1.5 where
   *  callers can call this method with apparent type safety because this
   *  method takes the hit for the cast.
   *
   *  @return A MapFactory that makes a WeakHashMap.
   */
  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> weakHashMapFactory() {
    return WEAK_HASH_MAP_FACTORY;
  }

  /** Return a MapFactory that returns a TreeMap.
   *  <i>Implementation note: This method uses the same trick as the methods
   *  like emptyMap() introduced in the Collections class in JDK1.5 where
   *  callers can call this method with apparent type safety because this
   *  method takes the hit for the cast.
   *
   *  @return A MapFactory that makes an TreeMap.
   */
  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> treeMapFactory() {
    return TREE_MAP_FACTORY;
  }

  
  public static <K,V> MapFactory<K,V> treeMapFactory(Comparator<? super K> comparator) {
    return new TreeMapFactory<>(comparator);
  }

  /** Return a MapFactory that returns an LinkedHashMap.
   *  <i>Implementation note: This method uses the same trick as the methods
   *  like emptyMap() introduced in the Collections class in JDK1.5 where
   *  callers can call this method with apparent type safety because this
   *  method takes the hit for the cast.
   *
   *  @return A MapFactory that makes an LinkedHashMap.
   */
  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> linkedHashMapFactory() {
    return LINKED_HASH_MAP_FACTORY;
  }

  /** Return a MapFactory that returns an ArrayMap.
   *  <i>Implementation note: This method uses the same trick as the methods
   *  like emptyMap() introduced in the Collections class in JDK1.5 where
   *  callers can call this method with apparent type safety because this
   *  method takes the hit for the cast.
   *
   *  @return A MapFactory that makes an ArrayMap.
   */
  @SuppressWarnings("unchecked")
  public static <K,V> MapFactory<K,V> arrayMapFactory() {
    return ARRAY_MAP_FACTORY;
  }



  private static class HashMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -9222344631596580863L;

    @Override
    public Map<K,V> newMap() {
      return Generics.newHashMap();
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return Generics.newHashMap(initCapacity);
    }

    @Override
    public Set<K> newSet() {
      return Generics.newHashSet();
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      return Generics.newHashSet(init);
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map) {
      map = Generics.newHashMap();
      return map;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      map = Generics.newHashMap(initCapacity);
      return map;
    }

  } // end class HashMapFactory


  private static class IdentityHashMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -9222344631596580863L;

    @Override
    public Map<K,V> newMap() {
      return new IdentityHashMap<>();
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return new IdentityHashMap<>(initCapacity);
    }

    @Override
    public Set<K> newSet() {
      return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      Set<K> set =  Collections.newSetFromMap(new IdentityHashMap<>());  // nothing more efficient to be done here...
      set.addAll(init);
      return set;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map) {
      map = new IdentityHashMap<>();
      return map;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      map = new IdentityHashMap<>(initCapacity);
      return map;
    }

  } // end class IdentityHashMapFactory


  private static class WeakHashMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = 4790014244304941000L;

    @Override
    public Map<K,V> newMap() {
      return new WeakHashMap<>();
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return new WeakHashMap<>(initCapacity);
    }

    @Override
    public Set<K> newSet() {
      return Collections.newSetFromMap(new WeakHashMap<>());
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      Set<K> set = Collections.newSetFromMap(new WeakHashMap<>());
      set.addAll(init);
      return set;
    }


    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map) {
      map = new WeakHashMap<>();
      return map;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      map = new WeakHashMap<>(initCapacity);
      return map;
    }

  } // end class WeakHashMapFactory


  private static class TreeMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -9138736068025818670L;

    private final Comparator<? super K> comparator;

    public TreeMapFactory() {
      this.comparator = null;
    }

    public TreeMapFactory(Comparator<? super K> comparator) {
      this.comparator = comparator;
    }

    @Override
    public Map<K,V> newMap() {
      return comparator == null ? new TreeMap<>() : new TreeMap<>(comparator);
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return newMap();
    }

    @Override
    public Set<K> newSet() {
      return comparator == null ? new TreeSet<>() : new TreeSet<>(comparator);
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      return new TreeSet<>(init);
    }


    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map) {
      if (comparator == null) {
        throw new UnsupportedOperationException();
      }
      map = new TreeMap<>();
      return map;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      if (comparator == null) {
        throw new UnsupportedOperationException();
      }
      map = new TreeMap<>();
      return map;
    }

  } // end class TreeMapFactory

  private static class LinkedHashMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -9138736068025818671L;

    @Override
    public Map<K,V> newMap() {
      return new LinkedHashMap<>();
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return newMap();
    }

    @Override
    public Set<K> newSet() {
      return new LinkedHashSet<>();
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      return new LinkedHashSet<>(init);
    }


    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map) {
      map = new LinkedHashMap<>();
      return map;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      map = new LinkedHashMap<>();
      return map;
    }

  } // end class LinkedHashMapFactory


  private static class ArrayMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -5855812734715185523L;

    @Override
    public Map<K,V> newMap() {
      return null;
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return null;
    }

    @Override
    public Set<K> newSet() {
      return null;
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      return null;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1, V1> map) {
      return null;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      return null;
    }

  } // end class ArrayMapFactory


  private static class ConcurrentMapFactory<K,V> extends MapFactory<K,V> {

    private static final long serialVersionUID = -5855812734715185523L;

    @Override
    public Map<K,V> newMap() {
      return new ConcurrentHashMap<>();
    }

    @Override
    public Map<K,V> newMap(int initCapacity) {
      return new ConcurrentHashMap<>(initCapacity);
    }

    @Override
    public Set<K> newSet() {
      return Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public Set<K> newSet(Collection<K> init) {
      Set<K> set = Collections.newSetFromMap(new ConcurrentHashMap<>());
      set.addAll(init);
      return set;
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1, V1> map) {
      return new ConcurrentHashMap<>();
    }

    @Override
    public <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity) {
      map = new ConcurrentHashMap<>(initCapacity);
      return map;
    }

  } // end class ConcurrentMapFactory

  
  public abstract Map<K,V> newMap();

  
  public abstract Map<K,V> newMap(int initCapacity);

  
  public abstract Set<K> newSet();

  
  public abstract Set<K> newSet(Collection<K> init);

  
  public abstract <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map);

  public abstract <K1, V1> Map<K1, V1> setMap(Map<K1,V1> map, int initCapacity);

}
