// Stanford Parser -- a probabilistic lexicalized NL CFG parser
// Copyright (c) 2002, 2003, 2004, 2005, 2008 The Board of Trustees of
// The Leland Stanford Junior University. All Rights Reserved.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see http://www.gnu.org/licenses/ .
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 2A
//    Stanford CA 94305-9020
//    USA
//    parser-support@lists.stanford.edu
//    https://nlp.stanford.edu/software/lex-parser.html

package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.stats.ClassicCounter;
import edu.berkeley.nlp.assignments.parsing.util.Index;


/**
 * This is a basic unknown word model for English.  It supports 5 different
 * types of feature modeling; see {@link #getSignature(String, int)}.
 *
 * <i>Implementation note: the contents of this class tend to overlap somewhat
 *
 * @author Dan Klein
 * @author Galen Andrew
 * @author Christopher Manning
 * @author Anna Rafferty
 */
public class EnglishUnknownWordModel extends BaseUnknownWordModel  {

  /** A logger for this class */

  private static final long serialVersionUID = 4825624957364628770L;

  private static final boolean DEBUG_UWM = false;

  protected final boolean smartMutation;

  protected final int unknownSuffixSize;
  protected final int unknownPrefixSize;

  protected final String wordClassesFile;

  private static final int MIN_UNKNOWN = 0;
  private static final int MAX_UNKNOWN = 8;

  public EnglishUnknownWordModel(Options op, Lexicon lex,
                                 Index<String> wordIndex,
                                 Index<String> tagIndex,
                                 ClassicCounter<IntTaggedWord> unSeenCounter) {
    super(op, lex, wordIndex, tagIndex, unSeenCounter, null, null, null);
    if (unknownLevel < MIN_UNKNOWN || unknownLevel > MAX_UNKNOWN) {
      throw new IllegalArgumentException("Invalid value for useUnknownWordSignatures: " + unknownLevel);
    }
    this.smartMutation = op.lexOptions.smartMutation;
    this.unknownSuffixSize = op.lexOptions.unknownSuffixSize;
    this.unknownPrefixSize = op.lexOptions.unknownPrefixSize;
    wordClassesFile = op.lexOptions.wordClassesFile;
  }

  /**
   * This constructor creates an UWM with empty data structures.  Only
   * use if loading in the data separately, such as by reading in text
   * lines containing the data.
   */
  public EnglishUnknownWordModel(Options op, Lexicon lex,
                                 Index<String> wordIndex,
                                 Index<String> tagIndex) {
    this(op, lex, wordIndex, tagIndex, new ClassicCounter<>());
  }

  @Override
  public float score(IntTaggedWord iTW, int loc, double c_Tseen, double total, double smooth, String word) {
    double pb_T_S = scoreProbTagGivenWordSignature(iTW, loc, smooth, word);
    double p_T = (c_Tseen / total);
    double p_W = 1.0 / total;
    double pb_W_T = Math.log(pb_T_S * p_W / p_T);

    if (pb_W_T > -100.0) {
      return (float) pb_W_T;
    }
    return Float.NEGATIVE_INFINITY;
  } // end score()


  /** Calculate P(Tag|Signature) with Bayesian smoothing via just P(Tag|Unknown) */
  @Override
  public double scoreProbTagGivenWordSignature(IntTaggedWord iTW, int loc, double smooth, String word) {
    // iTW.tag = nullTag;
    // double c_W = ((BaseLexicon) l).getCount(iTW);
    // iTW.tag = tag;

    // unknown word model for P(T|S)

    int wordSig = getSignatureIndex(iTW.word, loc, word);
    IntTaggedWord temp = new IntTaggedWord(wordSig, iTW.tag);
    double c_TS = unSeenCounter.getCount(temp);
    temp = new IntTaggedWord(wordSig, nullTag);
    double c_S = unSeenCounter.getCount(temp);
    double c_U = unSeenCounter.getCount(NULL_ITW);
    temp = new IntTaggedWord(nullWord, iTW.tag);
    double c_T = unSeenCounter.getCount(temp);

    double p_T_U = c_T / c_U;
    if (unknownLevel == 0) {
      c_TS = 0;
      c_S = 0;
    }
    return (c_TS + smooth * p_T_U) / (c_S + smooth);
  }


  /**
   * Returns the index of the signature of the word numbered wordIndex, where
   * the signature is the String representation of unknown word features.
   */
  @Override
  public int getSignatureIndex(int index, int sentencePosition, String word) {
    String uwSig = getSignature(word, sentencePosition);
    int sig = wordIndex.addToIndex(uwSig);
    return sig;
  }

  /**
   * This routine returns a String that is the "signature" of the class of a
   * word. For, example, it might represent whether it is a number of ends in
   * -s. The strings returned by convention matches the pattern UNK(-.+)? ,
   * which is just assumed to not match any real word. Behavior depends on the
   * unknownLevel (-uwm flag) passed in to the class. The recognized numbers are
   * 1-5: 5 is fairly English-specific; 4, 3, and 2 look for various word
   * features (digits, dashes, etc.) which are only vaguely English-specific; 1
   * uses the last two characters combined with a simple classification by
   * capitalization.
   *
   * @param word The word to make a signature for
   * @param loc Its position in the sentence (mainly so sentence-initial
   *          capitalized words can be treated differently)
   * @return A String that is its signature (equivalence class)
   */
  @Override
  public String getSignature(String word, int loc) {
    StringBuilder sb = new StringBuilder("UNK");
    switch (unknownLevel) {
      case 8:
        getSignature8(word, sb);
        break;
      case 7:
        getSignature7(word, loc, sb);
        break;
      case 6:
        getSignature6(word, loc, sb);
        break;
      case 5:
        getSignature5(word, loc, sb);
        break;
      case 4:
        getSignature4(word, loc, sb);
        break;
      case 3:
        getSignature3(word, loc, sb);
        break;
      case 2:
        getSignature2(word, loc, sb);
        break;
      case 1:
        getSignature1(word, loc, sb);
        break;
      default:
        // 0 = do nothing so it just stays as "UNK"
    } // end switch (unknownLevel)
    // log.info("Summarized " + word + " to " + sb.toString());
    return sb.toString();
  } // end getSignature()


  private static void getSignature7(String word, int loc, StringBuilder sb) {
    // New Sep 2008. Like 2 but rely more on Caps somewhere than initial Caps
    // {-ALLC, -INIT, -UC somewhere, -LC, zero} +
    // {-DASH, zero} +
    // {-NUM, -DIG, zero} +
    // {lowerLastChar, zeroIfShort}
    boolean hasDigit = false;
    boolean hasNonDigit = false;
    boolean hasLower = false;
    boolean hasUpper = false;
    boolean hasDash = false;
    int wlen = word.length();
    for (int i = 0; i < wlen; i++) {
      char ch = word.charAt(i);
      if (Character.isDigit(ch)) {
        hasDigit = true;
      } else {
        hasNonDigit = true;
        if (Character.isLetter(ch)) {
          if (Character.isLowerCase(ch) || Character.isTitleCase(ch)) {
            hasLower = true;
          } else {
            hasUpper = true;
          }
        } else if (ch == '-') {
          hasDash = true;
        }
      }
    }
    if (wlen > 0 && hasUpper) {
      if ( ! hasLower) {
        sb.append("-ALLC");
      } else if (loc == 0) {
        sb.append("-INIT");
      } else {
        sb.append("-UC");
      }
    } else if (hasLower) { // if (Character.isLowerCase(word.charAt(0))) {
      sb.append("-LC");
    }
    // no suffix = no (lowercase) letters
    if (hasDash) {
      sb.append("-DASH");
    }
    if (hasDigit) {
      if (!hasNonDigit) {
        sb.append("-NUM");
      } else {
        sb.append("-DIG");
      }
    } else if (wlen > 3) {
      // don't do for very short words: "yes" isn't an "-es" word
      // try doing to lower for further densening and skipping digits
      char ch = word.charAt(word.length() - 1);
      sb.append(Character.toLowerCase(ch));
    }
    // no suffix = short non-number, non-alphabetic
  }


  private void getSignature6(String word, int loc, StringBuilder sb) {
    // New Sep 2008. Like 5 but rely more on Caps somewhere than initial Caps
    // { -INITC, -CAPS, (has) -CAP, -LC lowercase, 0 } +
    // { -KNOWNLC, 0 } + [only for INITC]
    // { -NUM, 0 } +
    // { -DASH, 0 } +
    // { -last lowered char(s) if known discriminating suffix, 0}
    int wlen = word.length();
    int numCaps = 0;
    boolean hasDigit = false;
    boolean hasDash = false;
    boolean hasLower = false;
    for (int i = 0; i < wlen; i++) {
      char ch = word.charAt(i);
      if (Character.isDigit(ch)) {
        hasDigit = true;
      } else if (ch == '-') {
        hasDash = true;
      } else if (Character.isLetter(ch)) {
        if (Character.isLowerCase(ch)) {
          hasLower = true;
        } else if (Character.isTitleCase(ch)) {
          hasLower = true;
          numCaps++;
        } else {
          numCaps++;
        }
      }
    }
    String lowered = word.toLowerCase();
    if (numCaps > 1) {
      sb.append("-CAPS");
    } else if (numCaps > 0) {
      if (loc == 0) {
        sb.append("-INITC");
        if (getLexicon().isKnown(lowered)) {
          sb.append("-KNOWNLC");
        }
      } else {
        sb.append("-CAP");
      }
    } else if (hasLower) { // (Character.isLowerCase(ch0)) {
      sb.append("-LC");
    }
    if (hasDigit) {
      sb.append("-NUM");
    }
    if (hasDash) {
      sb.append("-DASH");
    }
    if (lowered.endsWith("s") && wlen >= 3) {
      // here length 3, so you don't miss out on ones like 80s
      char ch2 = lowered.charAt(wlen - 2);
      // not -ess suffixes or greek/latin -us, -is
      if (ch2 != 's' && ch2 != 'i' && ch2 != 'u') {
        sb.append("-s");
      }
    } else if (word.length() >= 5 && !hasDash && !(hasDigit && numCaps > 0)) {
      // don't do for very short words;
      // Implement common discriminating suffixes
      if (lowered.endsWith("ed")) {
        sb.append("-ed");
      } else if (lowered.endsWith("ing")) {
        sb.append("-ing");
      } else if (lowered.endsWith("ion")) {
        sb.append("-ion");
      } else if (lowered.endsWith("er")) {
        sb.append("-er");
      } else if (lowered.endsWith("est")) {
        sb.append("-est");
      } else if (lowered.endsWith("ly")) {
        sb.append("-ly");
      } else if (lowered.endsWith("ity")) {
        sb.append("-ity");
      } else if (lowered.endsWith("y")) {
        sb.append("-y");
      } else if (lowered.endsWith("al")) {
        sb.append("-al");
        // } else if (lowered.endsWith("ble")) {
        // sb.append("-ble");
        // } else if (lowered.endsWith("e")) {
        // sb.append("-e");
      }
    }
  }


  private void getSignature5(String word, int loc, StringBuilder sb) {
  }


  private static void getSignature4(String word, int loc, StringBuilder sb) {
  }


  private static void getSignature3(String word, int loc, StringBuilder sb) {
  }


  private static void getSignature2(String word, int loc, StringBuilder sb) {
  }


  private static void getSignature1(String word, int loc, StringBuilder sb) {
  }


  private void getSignature8(String word, StringBuilder sb) {
  }

} // end class
