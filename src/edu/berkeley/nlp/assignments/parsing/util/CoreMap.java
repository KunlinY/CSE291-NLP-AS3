package edu.berkeley.nlp.assignments.parsing.util;

import java.io.Serializable;

/**
 * Base type for all annotatable core objects. Should usually be instantiated as
 * {@link ArrayCoreMap}. Many common key definitions live in
 * {@link edu.berkeley.nlp.assignments.parsing.ling.CoreAnnotations}, but others may be defined elsewhere. See
 * {@link edu.berkeley.nlp.assignments.parsing.ling.CoreAnnotations} for details.
 *
 * @author dramage
 * @author rafferty
 */
public interface CoreMap extends TypesafeMap, Serializable {

  /** Attempt to provide a briefer and more human readable String for the contents of
   *  a CoreMap.
   *  The method may not be capable of printing circular dependencies in CoreMaps.
   *
   *  @param what An array (varargs) of Strings that say what annotation keys
   *     to print.  These need to be provided in a shortened form where you
   *     are just giving the part of the class name without package and up to
   *     "Annotation". That is,
   *     edu.berkeley.nlp.assignments.parsing.ling.CoreAnnotations.PartOfSpeechAnnotation ➔ PartOfSpeech .
   *     As a special case, an empty array means to print everything, not nothing.
   *  @return A more human readable String giving possibly partial contents of a
   *     CoreMap.
   */
  String toShorterString(String... what);

}
