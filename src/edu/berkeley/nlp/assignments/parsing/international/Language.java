package edu.berkeley.nlp.assignments.parsing.international;

import edu.berkeley.nlp.assignments.parsing.parser.lexparser.*;
import edu.berkeley.nlp.assignments.parsing.util.StringUtils;

import java.util.Arrays;

/**
 * Constants and parameters for multilingual NLP (primarily, parsing).
 *
 * @author Spence Green (original Languages class for parsing)
 * @author Gabor Angeli (factor out Language enum)
 */
public enum Language {
  Any(              new EnglishTreebankParserParams()),
  Arabic(           new ArabicTreebankParserParams()),
  Chinese(          new ChineseTreebankParserParams()),
  English(          new EnglishTreebankParserParams(){{ setGenerateOriginalDependencies(true); }}),
  German(           new NegraPennTreebankParserParams()),
  French(           new FrenchTreebankParserParams()),
  Hebrew(           new HebrewTreebankParserParams()),
  Spanish(          new SpanishTreebankParserParams()),
  UniversalChinese( new ChineseTreebankParserParams()),
  UniversalEnglish( new EnglishTreebankParserParams()),
  Unknown(          new EnglishTreebankParserParams());

  public static final String langList = StringUtils.join(Arrays.asList(Language.values()), " ");

  public final TreebankLangParserParams params;

  Language(TreebankLangParserParams params) {
    this.params = params;
  }

  /**
   * Returns whether these two languages can be considered compatible with each other.
   * Mostly here to handle the "Any" language value.
   */
  public boolean compatibleWith(Language other) {
    return this == other || this == Any || other == Any;
  }
}
