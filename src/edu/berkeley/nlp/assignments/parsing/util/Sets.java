package edu.berkeley.nlp.assignments.parsing.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;



public class Sets {

  // private to prevent instantiation
  private Sets() {}


  /**
   * Returns the set cross product of s1 and s2, as <code>Pair</code>s
   */
  public static <E,F> Set<Pair<E,F>> cross(Set<E> s1, Set<F> s2) {
    Set<Pair<E,F>> s = Generics.newHashSet();
    for (E o1 : s1) {
      for (F o2 : s2) {
        s.add(new Pair<>(o1, o2));
      }
    }
    return s;
  }

  
  public static <E> Set<E> diff(Set<E> s1, Set<E> s2) {
    Set<E> s = Generics.newHashSet();
    for (E o : s1) {
      if (!s2.contains(o)) {
        s.add(o);
      }
    }
    return s;
  }

  
  public static <E> Set<E> symmetricDiff(Set<E> s1, Set<E> s2) {
    Set<E> s = Generics.newHashSet();
    for (E o : s1) {
      if (!s2.contains(o)) {
        s.add(o);
      }
    }
    for (E o : s2) {
      if (!s1.contains(o)) {
        s.add(o);
      }
    }
    return s;
  }

  
  public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> s = Generics.newHashSet();
    s.addAll(s1);
    s.addAll(s2);
    return s;
  }

  
  public static <E> Set<E> intersection(Set<E> s1, Set<E> s2) {
    Set<E> s = Generics.newHashSet();
    s.addAll(s1);
    s.retainAll(s2);
    return s;
  }

  
  public static <E> boolean intersects(Set<E> s1, Set<E> s2) {
    // *ahem* It would seem that Java already had this method. Hopefully this
    // stub will help people find it better than I did.
    return !Collections.disjoint(s1, s2);
  }

  
  public static <E> Set<Set<E>> powerSet(Set<E> s) {
    if (s.isEmpty()) {
      Set<Set<E>> h = Generics.newHashSet();
      Set<E> h0 = Generics.newHashSet(0);
      h.add(h0);
      return h;
    } else {
      Iterator<E> i = s.iterator();
      E elt = i.next();
      s.remove(elt);
      Set<Set<E>> pow = powerSet(s);
      Set<Set<E>> pow1 = powerSet(s);
      // for (Iterator j = pow1.iterator(); j.hasNext();) {
      for (Set<E> t : pow1) {
        // Set<E> t = Generics.newHashSet((Set<E>) j.next());
        t.add(elt);
        pow.add(t);
      }
      s.add(elt);
      return pow;
    }
  }


}
