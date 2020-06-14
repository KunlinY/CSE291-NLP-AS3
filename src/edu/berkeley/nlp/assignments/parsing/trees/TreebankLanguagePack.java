package edu.berkeley.nlp.assignments.parsing.trees;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * This interface specifies language/treebank specific information for a
 * Treebank, which a parser or other treebank user might need to know.
 *
 * Some of this is fixed for a (treebank,language) pair, but some of it
 * reflects feature extraction decisions, so it can be sensible to have
 * multiple implementations of this interface for the same
 * (treebank,language) pair.
 *
 * So far this covers punctuation, character encodings, and characters
 * reserved for label annotations.  It should probably be expanded to
 * cover other stuff (unknown words?).
 *
 * Various methods in this class return arrays.  You should treat them
 * as read-only, even though one cannot enforce that in Java.
 *
 * Implementations in this class do not call basicCategory() on arguments
 * before testing them, so if needed, you should explicitly call
 * basicCategory() yourself before passing arguments to these routines for
 * testing.
 *
 * This class should be able to be an immutable singleton.  It contains
 * data on various things, but no state.  At some point we should make it
 * a real immutable singleton.
 *
 * @author Christopher Manning
 * @version 1.1, Mar 2003
 */
public interface TreebankLanguagePack extends Serializable {

  
  String DEFAULT_ENCODING = "UTF-8";


  
  boolean isPunctuationTag(String str);


  
  boolean isPunctuationWord(String str);


  
  boolean isSentenceFinalPunctuationTag(String str);


  
  boolean isEvalBIgnoredPunctuationTag(String str);


  
  Predicate<String> punctuationTagAcceptFilter();


  
  Predicate<String> punctuationTagRejectFilter();

  
  Predicate<String> punctuationWordAcceptFilter();


  
  Predicate<String> punctuationWordRejectFilter();


  
  Predicate<String> sentenceFinalPunctuationTagAcceptFilter();


  
  Predicate<String> evalBIgnoredPunctuationTagAcceptFilter();


  
  Predicate<String> evalBIgnoredPunctuationTagRejectFilter();


  /**
   * Returns a String array of punctuation tags for this treebank/language.
   *
   * @return The punctuation tags
   */
  String[] punctuationTags();


  /**
   * Returns a String array of punctuation words for this treebank/language.
   *
   * @return The punctuation words
   */
  String[] punctuationWords();


  /**
   * Returns a String array of sentence final punctuation tags for this
   * treebank/language.  The first in the list is assumed to be the most
   * basic one.
   *
   * @return The sentence final punctuation tags
   */
  String[] sentenceFinalPunctuationTags();


  /**
   * Returns a String array of sentence final punctuation words for
   * this treebank/language.
   *
   * @return The punctuation words
   */
  String[] sentenceFinalPunctuationWords();

  /**
   * Returns a String array of punctuation tags that EVALB-style evaluation
   * should ignore for this treebank/language.
   * Traditionally, EVALB has ignored a subset of the total set of
   * punctuation tags in the English Penn Treebank (quotes and
   * period, comma, colon, etc., but not brackets)
   *
   * @return Whether this is a EVALB-ignored punctuation tag
   */
  String[] evalBIgnoredPunctuationTags();

  boolean supportsGrammaticalStructures();

  
  String getEncoding();


  
  char[] labelAnnotationIntroducingCharacters();


  
  boolean isLabelAnnotationIntroducingCharacter(char ch);


  
  String basicCategory(String category);

  
  String stripGF(String category);


  
  Function<String,String> getBasicCategoryFunction();

  
  String categoryAndFunction(String category);

  
  Function<String,String> getCategoryAndFunctionFunction();

  
  boolean isStartSymbol(String str);


  
  Predicate<String> startSymbolAcceptFilter();


  
  String[] startSymbols();

  
  String startSymbol();


  
  String treebankFileExtension();

  
  void setGfCharacter(char gfCharacter);

  TreeReaderFactory treeReaderFactory();

  
  HeadFinder headFinder();


  
  HeadFinder typedDependencyHeadFinder();


  
  void setGenerateOriginalDependencies(boolean generateOriginalDependencies);

  
  boolean generateOriginalDependencies();

}
