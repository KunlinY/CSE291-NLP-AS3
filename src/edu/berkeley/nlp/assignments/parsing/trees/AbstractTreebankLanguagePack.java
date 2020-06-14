package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.util.Filters;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;



public abstract class AbstractTreebankLanguagePack implements TreebankLanguagePack {

  
  private static final long serialVersionUID = -6506749780512708352L;


  //Grammatical function parameters
  
  protected char gfCharacter;
  protected static final char DEFAULT_GF_CHAR = '-';


  
  public static final String DEFAULT_ENCODING = "UTF-8";


  
  protected boolean generateOriginalDependencies;


  
  public AbstractTreebankLanguagePack() {
    this(DEFAULT_GF_CHAR);
  }


  
  public AbstractTreebankLanguagePack(char gfChar) {
    this.gfCharacter = gfChar;
  }

  /**
   * Returns a String array of punctuation tags for this treebank/language.
   *
   * @return The punctuation tags
   */
  @Override
  public abstract String[] punctuationTags();

  /**
   * Returns a String array of punctuation words for this treebank/language.
   *
   * @return The punctuation words
   */
  @Override
  public abstract String[] punctuationWords();

  /**
   * Returns a String array of sentence final punctuation tags for this
   * treebank/language.
   *
   * @return The sentence final punctuation tags
   */
  @Override
  public abstract String[] sentenceFinalPunctuationTags();

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
    return punctuationTags();
  }


  
  @Override
  public boolean isPunctuationTag(String str) {
    return punctTagStringAcceptFilter.test(str);
  }


  
  @Override
  public boolean isPunctuationWord(String str) {
    return punctWordStringAcceptFilter.test(str);
  }


  
  @Override
  public boolean isSentenceFinalPunctuationTag(String str) {
    return sFPunctTagStringAcceptFilter.test(str);
  }


  
  @Override
  public boolean isEvalBIgnoredPunctuationTag(String str) {
    return eIPunctTagStringAcceptFilter.test(str);
  }


  
  @Override
  public Predicate<String> punctuationTagAcceptFilter() {
    return punctTagStringAcceptFilter;
  }


  
  @Override
  public Predicate<String> punctuationTagRejectFilter() {
    return Filters.notFilter(punctTagStringAcceptFilter);
  }


  
  @Override
  public Predicate<String> punctuationWordAcceptFilter() {
    return punctWordStringAcceptFilter;
  }


  
  @Override
  public Predicate<String> punctuationWordRejectFilter() {
    return Filters.notFilter(punctWordStringAcceptFilter);
  }


  
  @Override
  public Predicate<String> sentenceFinalPunctuationTagAcceptFilter() {
    return sFPunctTagStringAcceptFilter;
  }


  
  @Override
  public Predicate<String> evalBIgnoredPunctuationTagAcceptFilter() {
    return eIPunctTagStringAcceptFilter;
  }


  
  @Override
  public Predicate<String> evalBIgnoredPunctuationTagRejectFilter() {
    return Filters.notFilter(eIPunctTagStringAcceptFilter);
  }


  /**
   * Return the input Charset encoding for the Treebank.
   * See documentation for the <code>Charset</code> class.
   *
   * @return Name of Charset
   */
  @Override
  public String getEncoding() {
    return DEFAULT_ENCODING;
  }


  private static final char[] EMPTY_CHAR_ARRAY = new char[0];

  
  @Override
  public char[] labelAnnotationIntroducingCharacters() {
    return EMPTY_CHAR_ARRAY;
  }


  
  private int postBasicCategoryIndex(String category) {
    boolean sawAtZero = false;
    char seenAtZero = '\u0000';
    int i = 0;
    for (int leng = category.length(); i < leng; i++) {
      char ch = category.charAt(i);
      if (isLabelAnnotationIntroducingCharacter(ch)) {
        if (i == 0) {
          sawAtZero = true;
          seenAtZero = ch;
        } else if (sawAtZero && i > 1 && ch == seenAtZero) {
          sawAtZero = false;
        } else {
          // still skip past identical ones for weird negra-penn "---CJ" (should we just delete it?)
          // if (i + 1 < leng && category.charAt(i + 1) == ch) {
            // keep looping
          // } else {
          break;
          // }
        }
      }
    }
    return i;
  }

  /**
   * Returns the basic syntactic category of a String.
   * This implementation basically truncates
   * stuff after an occurrence of one of the
   * <code>labelAnnotationIntroducingCharacters()</code>.
   * However, there is also special case stuff to deal with
   * labelAnnotationIntroducingCharacters in category labels:
   * (i) if the first char is in this set, it's never truncated
   * (e.g., '-' or '=' as a token), and (ii) if it starts with
   * one of this set, a second instance of the same item from this set is
   * also excluded (to deal with '-LLB-', '-RCB-', etc.).
   *
   * @param category The whole String name of the label
   * @return The basic category of the String
   */
  @Override
  public String basicCategory(String category) {
    if (category == null) {
      return null;
    }
    return category.substring(0, postBasicCategoryIndex(category));
  }


  @Override
  public String stripGF(String category) {
    if(category == null) {
      return null;
    }
    int index = category.lastIndexOf(gfCharacter);
    if(index > 0) {
      category = category.substring(0, index);
    }
    return category;
  }

  
  @Override
  public Function<String,String> getBasicCategoryFunction() {
    return new BasicCategoryStringFunction(this);
  }


  private static class BasicCategoryStringFunction implements Function<String,String>, Serializable {

    private static final long serialVersionUID = 1L;

    private TreebankLanguagePack tlp;

    BasicCategoryStringFunction(TreebankLanguagePack tlp) {
      this.tlp = tlp;
    }

    @Override
    public String apply(String in) {
      return tlp.basicCategory(in);
    }

  }


  private static class CategoryAndFunctionStringFunction implements Function<String,String>, Serializable {

    private static final long serialVersionUID = 1L;

    private TreebankLanguagePack tlp;

    CategoryAndFunctionStringFunction(TreebankLanguagePack tlp) {
      this.tlp = tlp;
    }

    @Override
    public String apply(String in) {
      return tlp.categoryAndFunction(in);
    }

  }


  /**
   * Returns the syntactic category and 'function' of a String.
   * This normally involves truncating numerical coindexation
   * showing coreference, etc.  By 'function', this means
   * keeping, say, Penn Treebank functional tags or ICE phrasal functions,
   * perhaps returning them as <code>category-function</code>.
   * <p/>
   * This implementation strips numeric tags after label introducing
   * characters (assuming that non-numeric things are functional tags).
   *
   * @param category The whole String name of the label
   * @return A String giving the category and function
   */
  @Override
  public String categoryAndFunction(String category) {
    if (category == null) {
      return null;
    }
    String catFunc = category;
    int i = lastIndexOfNumericTag(catFunc);
    while (i >= 0) {
      catFunc = catFunc.substring(0, i);
      i = lastIndexOfNumericTag(catFunc);
    }
    return catFunc;
  }

  /**
   * Returns the index within this string of the last occurrence of a
   * isLabelAnnotationIntroducingCharacter which is followed by only
   * digits, corresponding to a numeric tag at the end of the string.
   * Example: <code>lastIndexOfNumericTag("NP-TMP-1") returns
   * 6</code>.
   *
   * @param category A String category
   * @return The index within this string of the last occurrence of a
   *     isLabelAnnotationIntroducingCharacter which is followed by only
   *     digits
   */
  private int lastIndexOfNumericTag(String category) {
    if (category == null) {
      return -1;
    }
    int last = -1;
    for (int i = category.length() - 1; i >= 0; i--) {
      if (isLabelAnnotationIntroducingCharacter(category.charAt(i))) {
        boolean onlyDigitsFollow = false;
        for (int j = i + 1; j < category.length(); j++) {
          onlyDigitsFollow = true;
          if (!(Character.isDigit(category.charAt(j)))) {
            onlyDigitsFollow = false;
            break;
          }
        }
        if (onlyDigitsFollow) {
          last = i;
        }
      }
    }
    return last;
  }

  
  @Override
  public Function<String,String> getCategoryAndFunctionFunction() {
    return new CategoryAndFunctionStringFunction(this);
  }


  
  @Override
  public boolean isLabelAnnotationIntroducingCharacter(char ch) {
    char[] cutChars = labelAnnotationIntroducingCharacters();
    for (char cutChar : cutChars) {
      if (ch == cutChar) {
        return true;
      }
    }
    return false;
  }


  
  @Override
  public boolean isStartSymbol(String str) {
    return startSymbolAcceptFilter.test(str);
  }


  
  @Override
  public Predicate<String> startSymbolAcceptFilter() {
    return startSymbolAcceptFilter;
  }


  
  @Override
  public abstract String[] startSymbols();


  
  @Override
  public String startSymbol() {
    String[] ssyms = startSymbols();
    if (ssyms == null || ssyms.length == 0) {
      return null;
    }
    return ssyms[0];
  }


  private final Predicate<String> punctTagStringAcceptFilter = Filters.collectionAcceptFilter(punctuationTags());

  private final Predicate<String> punctWordStringAcceptFilter = Filters.collectionAcceptFilter(punctuationWords());

  private final Predicate<String> sFPunctTagStringAcceptFilter = Filters.collectionAcceptFilter(sentenceFinalPunctuationTags());

  private final Predicate<String> eIPunctTagStringAcceptFilter = Filters.collectionAcceptFilter(evalBIgnoredPunctuationTags());

  private final Predicate<String> startSymbolAcceptFilter = Filters.collectionAcceptFilter(startSymbols());


  @Override
  public boolean supportsGrammaticalStructures() {
    return false;
  }

  public char getGfCharacter() {
    return gfCharacter;
  }


  @Override
  public void setGfCharacter(char gfCharacter) {
    this.gfCharacter = gfCharacter;
  }

  /** {@inheritDoc} */
  @Override
  public TreeReaderFactory treeReaderFactory() {
    return null;
  }

  @Override
  public void setGenerateOriginalDependencies(boolean generateOriginalDependencies) {
    this.generateOriginalDependencies = generateOriginalDependencies;
  }

  @Override
  public boolean generateOriginalDependencies() {
    return this.generateOriginalDependencies;
  }

}
