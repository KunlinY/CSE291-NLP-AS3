package edu.berkeley.nlp.assignments.parsing.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Utilities for Maps, including inverting, composing, and support for list/set values.
 *
 * @author Dan Klein (klein@cs.stanford.edu)
 */
public class Maps {

  private Maps() {}

  
  public static <K extends Comparable<? super K>, V> List<Map.Entry<K, V>> sortedEntries(Collection<Map.Entry<K, V>> entries) {
    List<Entry<K,V>> entriesList = new ArrayList<>(entries);
    Collections.sort(entriesList, (e1, e2) -> e1.getKey().compareTo(e2.getKey()));
    return entriesList;
  }

  
  public static <K extends Comparable<? super K>, V> List<Map.Entry<K, V>> sortedEntries(Map<K, V> map) {
    return sortedEntries(map.entrySet());
  }

  public static <K, V1, V2> void addAll(Map<K, V1> to, Map<K, V2> from, Function<V2, V1> function) {
    for (Map.Entry<K, V2> entry : from.entrySet()) {
      to.put(entry.getKey(), function.apply(entry.getValue()));
    }
  }

  public static<T,V> String toString(Map<T, V> map, String preAppend, String postAppend, String keyValSeparator, String itemSeparator){

    StringBuilder sb = new StringBuilder();
    sb.append(preAppend);
    int i = 0;
    for (Entry<T, V> en: map.entrySet()) {
      if(i != 0)
        sb.append(itemSeparator);

      sb.append(en.getKey());
      sb.append(keyValSeparator);
      sb.append(en.getValue());
      i++;
    }
    sb.append(postAppend);
    return sb.toString();
  }
}
