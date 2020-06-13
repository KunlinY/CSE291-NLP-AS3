package edu.berkeley.nlp.assignments.parsing.trees.international.french;

import edu.berkeley.nlp.assignments.parsing.international.french.FrenchMorphoFeatureSpecification;
import edu.berkeley.nlp.assignments.parsing.international.french.process.FrenchTokenizer;
import edu.berkeley.nlp.assignments.parsing.international.morph.MorphoFeatureSpecification;
import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.process.TokenizerFactory;
import edu.berkeley.nlp.assignments.parsing.trees.AbstractTreebankLanguagePack;
import edu.berkeley.nlp.assignments.parsing.trees.HeadFinder;


/**
 * Language pack for the French treebank.
 *
 * @author mcdm
 */
public class FrenchTreebankLanguagePack extends AbstractTreebankLanguagePack {

  private static final long serialVersionUID = -7338244949063822519L;

  //wsg2011: The distributed treebank is encoding in ISO8859_1, but
  //the current FrenchTreebankParserParams is currently configured to
  //read UTF-8, PTB style trees that have been extracted from the XML
  //files.
  public static final String FTB_ENCODING = "ISO8859_1";

  //The raw treebank uses "PONCT". Change to LDC convention.
  private static final String[] frenchPunctTags = {"PUNC"};

  private static final String[] frenchSFPunctTags = {"PUNC"};

  private static final String[] frenchPunctWords = {"=","*","/","\\","]","[","\"","''", "'", "``", "`", "-LRB-", "-RRB-", "-LCB-", "-RCB-", ".", "?", "!", ",", ":", "-", "--", "...", ";", "&quot;"};

  private static final String[] frenchSFPunctWords = {".", "!", "?"};

  private static final char[] annotationIntroducingChars = {'-', '=', '|', '#', '^', '~'};

  private static final String[] frenchStartSymbols = {"ROOT"};

  @Override
  public TokenizerFactory<? extends HasWord> getTokenizerFactory() {
    return FrenchTokenizer.ftbFactory();
  }
  
  @Override
  public String getEncoding() {
    return FTB_ENCODING;
  }
  
  /**
   * Returns a String array of punctuation tags for this treebank/language.
   *
   * @return The punctuation tags
   */
  @Override
  public String[] punctuationTags() {
    return frenchPunctTags;
  }


  /**
   * Returns a String array of punctuation words for this treebank/language.
   *
   * @return The punctuation words
   */
  @Override
  public String[] punctuationWords() {
    return frenchPunctWords;
  }


  /**
   * Returns a String array of sentence final punctuation tags for this
   * treebank/language.
   *
   * @return The sentence final punctuation tags
   */
  @Override
  public String[] sentenceFinalPunctuationTags() {
    return frenchSFPunctTags;
  }

  /**
   * Returns a String array of sentence final punctuation words for this
   * treebank/language.
   *
   * @return The sentence final punctuation tags
   */
  public String[] sentenceFinalPunctuationWords() {
    return frenchSFPunctWords;
  }


  /**
   * Return an array of characters at which a String should be
   * truncated to give the basic syntactic category of a label.
   * The idea here is that French treebank style labels follow a syntactic
   * category with various functional and crossreferencing information
   * introduced by special characters (such as "NP-SUBJ").  This would
   * be truncated to "NP" by the array containing '-'.
   *
   * @return An array of characters that set off label name suffixes
   */
  @Override
  public char[] labelAnnotationIntroducingCharacters() {
    return annotationIntroducingChars;
  }


  /**
   * Returns a String array of treebank start symbols.
   *
   * @return The start symbols
   */
  @Override
  public String[] startSymbols() {
    return frenchStartSymbols;
  }


  /**
   * Returns the extension of treebank files for this treebank.
   */
  public String treebankFileExtension() {
    return "xml";
  }

  /** {@inheritDoc} */
  public HeadFinder headFinder() {
    return new FrenchHeadFinder(this);
  }
  
  /** {@inheritDoc} */
  public HeadFinder typedDependencyHeadFinder() {
    return new FrenchHeadFinder(this);
  }
  
  @Override
  public MorphoFeatureSpecification morphFeatureSpec() {
    return new FrenchMorphoFeatureSpecification();
  }

}
