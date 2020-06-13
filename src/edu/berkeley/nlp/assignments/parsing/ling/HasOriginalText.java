package edu.berkeley.nlp.assignments.parsing.ling;


/**
 * This token can produce / set original texts
 *
 * @author Gabor Angeli
 */
public interface HasOriginalText {

  // These next two are a partial implementation of HasContext. Maybe clean this up someday?

  String originalText();

  void setOriginalText(String originalText);

}
