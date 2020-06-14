package edu.berkeley.nlp.assignments.parsing.util;

import java.util.Comparator;
import java.io.Serializable;

final public class ScoredComparator implements Comparator<Scored>, Serializable {

  private static final long serialVersionUID = 1L;

  private static final boolean DESCENDING = false;

  public static final ScoredComparator DESCENDING_COMPARATOR = new ScoredComparator(DESCENDING);

  private final boolean ascending;

  private ScoredComparator(boolean ascending) {
    this.ascending = ascending;
  }

  public int compare(Scored o1, Scored o2) {
    if (o1 == o2) {
      return 0;
    }
    double d1 = o1.score();
    double d2 = o2.score();
    if (ascending) {
      if (d1 < d2) {
        return -1;
      }
      if (d1 > d2) {
        return 1;
      }
    } else {
      if (d1 < d2) {
        return 1;
      }
      if (d1 > d2) {
        return -1;
      }
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ScoredComparator) {
      ScoredComparator sc = (ScoredComparator) o;
      if (ascending == sc.ascending) {
        return true;
      }
    }
    return false;
  }

  
  @Override
  public int hashCode() {
    if (ascending) {
      return (1 << 23);
    } else {
      return (1 << 23) + 1;
    }
  }

  @Override
  public String toString() {
    return "ScoredComparator(" + (ascending ? "ascending": "descending") + ")";
  }
}
