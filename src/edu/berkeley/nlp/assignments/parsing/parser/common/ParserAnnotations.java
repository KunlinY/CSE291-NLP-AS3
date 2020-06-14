package edu.berkeley.nlp.assignments.parsing.parser.common;

import java.util.List;

import edu.berkeley.nlp.assignments.parsing.ling.CoreAnnotation;

/**
 * Parse time options for the Stanford lexicalized parser.  For
 * example, you can set a ConstraintAnnotation and the parser
 * annotator will extract that annotation and apply the constraints
 * when parsing.
 */

public class ParserAnnotations {
  
  private ParserAnnotations() {} // only static members



  /**
   * This CoreMap key represents a regular expression which the parser
   * will try to match when assigning tags.
   *
   * This key is typically set on token annotations.
   */
  public static class CandidatePartOfSpeechAnnotation implements CoreAnnotation<String> {
    public Class<String> getType() {
      return String.class;
    }
  }

}
