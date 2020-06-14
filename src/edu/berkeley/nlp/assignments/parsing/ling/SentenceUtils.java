package edu.berkeley.nlp.assignments.parsing.ling;

import edu.berkeley.nlp.assignments.parsing.util.CoreMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * SentenceUtils holds a couple utility methods for lists that are sentences.
 * Those include a method that nicely prints a list of words and methods that
 * construct lists of words from lists of strings.
 *
 * @author Dan Klein
 * @author Christopher Manning (generified)
 * @author John Bauer
 * @version 2010
 */
public class SentenceUtils {

  private SentenceUtils() {} // static methods

  public static List<HasWord> toWordList(String... words) {
    List<HasWord> sent = new ArrayList<>();
    for (String word : words) {
      CoreLabel cl = new CoreLabel();
      cl.setValue(word);
      cl.setWord(word);
      sent.add(cl);
    }
    return sent;
  }

  public static <T> String listToString(List<T> list) {
    return listToString(list, true);
  }

  public static <T> String listToString(List<T> list, final boolean justValue) {
    return listToString(list, justValue, null);
  }

  public static <T> String listToString(List<T> list, final boolean justValue,
                                        final String separator) {
    StringBuilder s = new StringBuilder();
    for (Iterator<T> wordIterator = list.iterator(); wordIterator.hasNext();) {
      T o = wordIterator.next();
      s.append(wordToString(o, justValue, separator));
      if (wordIterator.hasNext()) {
        s.append(' ');
      }
    }
    return s.toString();
  }

  public static <T> String wordToString(T o, final boolean justValue) {
    return wordToString(o, justValue, null);
  }

  public static <T> String wordToString(T o, final boolean justValue,
                                        final String separator) {
    if (justValue && o instanceof Label) {
      if (o instanceof CoreLabel) {
        CoreLabel l = (CoreLabel) o;
        String w = l.value();
        if (w == null)
          w = l.word();
        return w;
      } else {
        return (((Label) o).value());
      }
    } else if (o instanceof CoreLabel) {
      CoreLabel l = ((CoreLabel) o);
      String w = l.value();
      if (w == null)
        w = l.word();
      if (l.tag() != null) {
        if (separator == null) {
          return w + CoreLabel.TAG_SEPARATOR + l.tag();
        } else {
          return w + separator + l.tag();
        }
      }
      return w;
      // an interface that covered these next four cases would be
      // nice, but we're moving away from these data types anyway
    } else if (separator != null && o instanceof TaggedWord) {
      return ((TaggedWord) o).toString(separator);
    } else if (separator != null && o instanceof LabeledWord) {
      return ((LabeledWord) o).toString(separator);
    } else if (separator != null && o instanceof WordLemmaTag) {
      return ((WordLemmaTag) o).toString(separator);
    } else if (separator != null && o instanceof WordTag) {
      return ((WordTag) o).toString(separator);
    } else {
      return (o.toString());
    }
  }

}
