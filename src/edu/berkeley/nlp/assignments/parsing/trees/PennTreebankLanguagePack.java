package edu.berkeley.nlp.assignments.parsing.trees;

/**
 * Specifies the treebank/language specific components needed for
 * parsing the English Penn Treebank.
 *
 * @author Christopher Manning
 * @version 1.2
 */
public class PennTreebankLanguagePack extends AbstractTreebankLanguagePack {

  
  public PennTreebankLanguagePack() {
  }


  public static final String[] pennPunctTags = {"''", "``", "-LRB-", "-RRB-", ".", ":", ","};

  private static final String[] pennSFPunctTags = {"."};

  private static final String[] collinsPunctTags = {"''", "``", ".", ":", ","};

  private static final String[] pennPunctWords = {"''", "'", "``", "`", "-LRB-", "-RRB-", "-LCB-", "-RCB-", ".", "?", "!", ",", ":", "-", "--", "...", ";"};

  private static final String[] pennSFPunctWords = {".", "!", "?"};


  
  private static final char[] annotationIntroducingChars = {'-', '=', '|', '#', '^', '~', '_', '['};

  
  private static final String[] pennStartSymbols = {"ROOT", "TOP"};


  /**
   * Returns a String array of punctuation tags for this treebank/language.
   *
   * @return The punctuation tags
   */
  @Override
  public String[] punctuationTags() {
    return pennPunctTags;
  }


  /**
   * Returns a String array of punctuation words for this treebank/language.
   *
   * @return The punctuation words
   */
  @Override
  public String[] punctuationWords() {
    return pennPunctWords;
  }


  /**
   * Returns a String array of sentence final punctuation tags for this
   * treebank/language.
   *
   * @return The sentence final punctuation tags
   */
  @Override
  public String[] sentenceFinalPunctuationTags() {
    return pennSFPunctTags;
  }

  /**
   * Returns a String array of sentence final punctuation words for this
   * treebank/language.
   *
   * @return The sentence final punctuation tags
   */
  @Override
  public String[] sentenceFinalPunctuationWords() {
    return pennSFPunctWords;
  }

  /**
   * Returns a String array of punctuation tags that EVALB-style evaluation
   * should ignore for this treebank/language.
   * Traditionally, EVALB has ignored a subset of the total set of
   * punctuation tags in the English Penn Treebank (quotes and
   * period, comma, colon, etc., but not brackets)
   *
   * @return Whether this is a EVALB-ignored punctuation tag
   */
  @Override
  public String[] evalBIgnoredPunctuationTags() {
    return collinsPunctTags;
  }


  
  @Override
  public char[] labelAnnotationIntroducingCharacters() {
    return annotationIntroducingChars;
  }


  
  @Override
  public String[] startSymbols() {
    return pennStartSymbols;
  }

  
  @Override
  public String treebankFileExtension() {
    return "mrg";
  }


  @Override
  public boolean supportsGrammaticalStructures() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public HeadFinder headFinder() {
    return new ModCollinsHeadFinder(this);
  }

  /** {@inheritDoc} */
  @Override
  public HeadFinder typedDependencyHeadFinder() {
    return null;
  }


  private static final long serialVersionUID = 9081305982861675328L;

}
