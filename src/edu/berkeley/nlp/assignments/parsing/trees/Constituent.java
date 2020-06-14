package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.util.Scored;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class Constituent implements Labeled, Scored, Label {

  public Constituent() {}

  
  public abstract int start();

  
  public abstract void setStart(int start);


  
  public abstract int end();


  
  public abstract void setEnd(int end);


  
  public Label label() {
    return null;
  }


  
  public void setLabel(Label label) {
    // a noop
  }


  
  public Collection<Label> labels() {
    return Collections.singletonList(label());
  }


  public void setLabels(Collection<Label> labels) {
    throw new UnsupportedOperationException("Constituent can't be multilabeled");
  }


  
  public double score() {
    return Double.NaN;
  }


  
  public void setScore(double score) {
    // a no-op
  }


  /**
   * Return a string representation of a <code>Constituent</code>.
   *
   * @return The full string representation.
   */
  @Override
  public String toString() {
    StringBuffer sb;
    Label lab = label();
    if (lab != null) {
      sb = new StringBuffer(lab.toString());
    } else {
      sb = new StringBuffer();
    }
    sb.append("(").append(start()).append(",").append(end()).append(")");
    return sb.toString();
  }


  /**
   * Return the length of a <code>Constituent</code>
   */
  public int size() {
    return end() - start();
  }


  /**
   * Compare with another Object for equality.
   * Two Constituent objects are equal if they have the same start and end,
   * and, if at least one of them has a non-null label, then their labels are equal.
   * The score of a Constituent is not considered in the equality test.
   * This seems to make sense for most of the applications we have in mind
   * where one wants to assess equality independent of score, and then if
   * necessary to relax a constituent if one with a better score is found.
   * (Note, however, that if you do want to compare Constituent scores for
   * equality, then you have to be careful,
   * because two <code>double</code> NaN values are considered unequal in
   * Java.)
   * The general contract of equals() implies that one can't have a
   * subclass of a concrete [non-abstract] class redefine equals() to use
   * extra aspects, so subclasses shouldn't override this in ways that
   * make use of extra fields.
   *
   * @param obj The object being compared with
   * @return true if the objects are equal
   */
  @Override
  public boolean equals(Object obj) {
    // unclear if this will be a speedup in general
    // if (this == o)
    //      return true;
    if (obj instanceof Constituent) {
      Constituent c = (Constituent) obj;
      // System.out.println("Comparing " + this + " to " + c + "\n  " +
      //	"start: " + (start() == c.start()) + " end: " +
      //	(end() == c.end()) + " score: " + (score() == c.score()));
      if ((start() == c.start()) && (end() == c.end())) {
        Label lab1 = label();
        Label lab2 = c.label();
        if (lab1 == null) {
          return lab2 == null;
        }

        String lv1 = lab1.value();
        String lv2 = lab2.value();
        if (lv1 == null && lv2 == null) {
          return true;
        }
        if (lv1  != null && lv2 != null) {
          return lab1.value().equals(lab2.value());
        }
      }
    }
    return false;
  }


  
  @Override
  public int hashCode() {
    int hash = (start() << 16) | end();
    Label lab = label();
    return (lab == null || lab.value() == null) ? hash : hash ^ lab.value().hashCode();
  }


  
  public boolean crosses(Constituent c) {
    return (start() < c.start() && c.start() < end() && end() < c.end()) || (c.start() < start() && start() < c.end() && c.end() < end());
  }


  
  public boolean crosses(Collection<Constituent> constColl) {
    for (Constituent c : constColl) {
      if (crosses(c)) {
        return true;
      }
    }
    return false;
  }


  
  public boolean contains(Constituent c) {
    return start() <= c.start() && end() >= c.end();
  }



  // -- below here is stuff to implement the Label interface

  
  public String value() {
    Label lab = label();
    if (lab == null) {
      return null;
    }
    return lab.value();
  }


  
  public void setValue(String value) {
    Label lab = label();
    if (lab != null) {
      lab.setValue(value);
    }
  }


  /**
   * Make a new label with this <code>String</code> as the "name", perhaps
   * by doing some appropriate decoding of the string.
   *
   * @param labelStr the String that translates into the content of the
   *                 label
   */
  public void setFromString(String labelStr) {
    Label lab = label();
    if (lab != null) {
      lab.setFromString(labelStr);
    }
  }


  /**
   * Print out as a string the subpart of a sentence covered
   * by this <code>Constituent</code>.
   *
   * @return The subpart of the sentence
   */
  // TODO: genericize this!
  public String toSentenceString(ArrayList s) {
    StringBuilder sb = new StringBuilder();
    for (int wordNum = start(), end = end(); wordNum <= end; wordNum++) {
      sb.append(s.get(wordNum));
      if (wordNum != end) {
        sb.append(" ");
      }
    }
    return sb.toString();
  }

}
