package edu.berkeley.nlp.assignments.parsing.parser.lexparser; 

import edu.berkeley.nlp.assignments.parsing.trees.CompositeTreeTransformer;
import edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack;
import edu.berkeley.nlp.assignments.parsing.trees.TreeTransformer;
import java.util.function.Function;
import edu.berkeley.nlp.assignments.parsing.util.Generics;
import edu.berkeley.nlp.assignments.parsing.util.ReflectionLoading;

import java.io.*;
import java.util.*;


/**
 * This class contains options to the parser which MUST be the SAME at
 * both training and testing (parsing) time in order for the parser to
 * work properly.  It also contains an object which stores the options
 * used by the parser at training time and an object which contains
 * default options for test use.
 *
 * @author Dan Klein
 * @author Christopher Manning
 * @author John Bauer
 */
public class Options implements Serializable  {

  public Options() {
    this(new EnglishTreebankParserParams());
  }

  public Options(TreebankLangParserParams tlpParams) {
    this.tlpParams = tlpParams;
  }

  /**
   * Set options based on a String array in the style of
   * commandline flags. This method goes through the array until it ends,
   * processing options, as for {@link #setOption}.
   *
   * @param flags Array of options (or as a varargs list of arguments).
   *      The options passed in should
   *      be specified like command-line arguments, including with an initial
   *      minus sign  for example,
   *          {"-outputFormat", "typedDependencies", "-maxLength", "70"}
   * @throws IllegalArgumentException If an unknown flag is passed in
   */
  public void setOptions(String... flags) {
    setOptions(flags, 0, flags.length);
  }

  /**
   * Set options based on a String array in the style of
   * commandline flags. This method goes through the array until it ends,
   * processing options, as for {@link #setOption}.
   *
   * @param flags Array of options.  The options passed in should
   *      be specified like command-line arguments, including with an initial
   *      minus sign  for example,
   *          {"-outputFormat", "typedDependencies", "-maxLength", "70"}
   * @param startIndex The index in the array to begin processing options at
   * @param endIndexPlusOne A number one greater than the last array index at
   *      which options should be processed
   * @throws IllegalArgumentException If an unknown flag is passed in
   */
  public void setOptions(final String[] flags, final int startIndex, final int endIndexPlusOne) {
    for (int i = startIndex; i < endIndexPlusOne;) {
      i = setOption(flags, i);
    }
  }

  /**
   * Set options based on a String array in the style of
   * commandline flags. This method goes through the array until it ends,
   * processing options, as for {@link #setOption}.
   *
   * @param flags Array of options (or as a varargs list of arguments).
   *      The options passed in should
   *      be specified like command-line arguments, including with an initial
   *      minus sign  for example,
   *          {"-outputFormat", "typedDependencies", "-maxLength", "70"}
   * @throws IllegalArgumentException If an unknown flag is passed in
   */
  public void setOptionsOrWarn(String... flags) {
    setOptionsOrWarn(flags, 0, flags.length);
  }

  /**
   * Set options based on a String array in the style of
   * commandline flags. This method goes through the array until it ends,
   * processing options, as for {@link #setOption}.
   *
   * @param flags Array of options.  The options passed in should
   *      be specified like command-line arguments, including with an initial
   *      minus sign  for example,
   *          {"-outputFormat", "typedDependencies", "-maxLength", "70"}
   * @param startIndex The index in the array to begin processing options at
   * @param endIndexPlusOne A number one greater than the last array index at
   *      which options should be processed
   * @throws IllegalArgumentException If an unknown flag is passed in
   */
  public void setOptionsOrWarn(final String[] flags, final int startIndex, final int endIndexPlusOne) {
    for (int i = startIndex; i < endIndexPlusOne;) {
      i = setOptionOrWarn(flags, i);
    }
  }

  /**
   * Set an option based on a String array in the style of
   * commandline flags. The option may
   * be either one known by the Options object, or one recognized by the
   * TreebankLangParserParams which has already been set up inside the Options
   * object, and then the option is set in the language-particular
   * TreebankLangParserParams.
   * Note that despite this method being an instance method, many flags
   * are actually set as static class variables in the Train and Test
   * classes (this should be fixed some day).
   * Some options (there are many others; see the source code):
   * <ul>
   * <li> <code>-maxLength n</code> set the maximum length sentence to parse (inclusively)
   * <li> <code>-printTT</code> print the training trees in raw, annotated, and annotated+binarized form.  Useful for debugging and other miscellany.
   * <li> <code>-printAnnotated filename</code> use only in conjunction with -printTT.  Redirects printing of annotated training trees to <code>filename</code>.
   * <li> <code>-forceTags</code> when the parser is tested against a set of gold standard trees, use the tagged yield, instead of just the yield, as input.
   * </ul>
   *
   * @param flags An array of options arguments, command-line style.  E.g. {"-maxLength", "50"}.
   * @param i The index in flags to start at when processing an option
   * @return The index in flags of the position after the last element used in
   *      processing this option. If the current array position cannot be processed as a valid
   *      option, then a warning message is printed to stderr and the return value is <code>i+1</code>
   */
  public int setOptionOrWarn(String[] flags, int i) {
    int j = setOptionFlag(flags, i);
    if (j == i) {
      j = tlpParams.setOptionFlag(flags, i);
    }
    if (j == i) {
      j++;
    }
    return j;
  }

  /**
   * Set an option based on a String array in the style of
   * commandline flags. The option may
   * be either one known by the Options object, or one recognized by the
   * TreebankLangParserParams which has already been set up inside the Options
   * object, and then the option is set in the language-particular
   * TreebankLangParserParams.
   * Note that despite this method being an instance method, many flags
   * are actually set as static class variables in the Train and Test
   * classes (this should be fixed some day).
   * Some options (there are many others; see the source code):
   * <ul>
   * <li> <code>-maxLength n</code> set the maximum length sentence to parse (inclusively)
   * <li> <code>-printTT</code> print the training trees in raw, annotated, and annotated+binarized form.  Useful for debugging and other miscellany.
   * <li> <code>-printAnnotated filename</code> use only in conjunction with -printTT.  Redirects printing of annotated training trees to <code>filename</code>.
   * <li> <code>-forceTags</code> when the parser is tested against a set of gold standard trees, use the tagged yield, instead of just the yield, as input.
   * </ul>
   *
   * @param flags An array of options arguments, command-line style.  E.g. {"-maxLength", "50"}.
   * @param i The index in flags to start at when processing an option
   * @return The index in flags of the position after the last element used in
   *      processing this option.
   * @throws IllegalArgumentException If the current array position cannot be
   *      processed as a valid option
   */
  public int setOption(String[] flags, int i) {
    int j = setOptionFlag(flags, i);
    if (j == i) {
      j = tlpParams.setOptionFlag(flags, i);
    }
    if (j == i) {
      throw new IllegalArgumentException("Unknown option: " + flags[i]);
    }
    return j;
  }

  /**
   * Set an option in this object, based on a String array in the style of
   * commandline flags.  The option is only processed with respect to
   * options directly known by the Options object.
   * Some options (there are many others; see the source code):
   * <ul>
   * <li> <code>-maxLength n</code> set the maximum length sentence to parse (inclusively)
   * <li> <code>-printTT</code> print the training trees in raw, annotated, and annotated+binarized form.  Useful for debugging and other miscellany.
   * <li> <code>-printAnnotated filename</code> use only in conjunction with -printTT.  Redirects printing of annotated training trees to <code>filename</code>.
   * <li> <code>-forceTags</code> when the parser is tested against a set of gold standard trees, use the tagged yield, instead of just the yield, as input.
   * </ul>
   *
   * @param args An array of options arguments, command-line style.  E.g. {"-maxLength", "50"}.
   * @param i The index in args to start at when processing an option
   * @return The index in args of the position after the last element used in
   *      processing this option, or the value i unchanged if a valid option couldn't
   *      be processed starting at position i.
   */
  protected int setOptionFlag(String[] args, int i) {
    return 0;
  }

  public static class LexOptions implements Serializable {

    /**
     * Whether to use suffix and capitalization information for unknowns.
     * Within the BaseLexicon model options have the following meaning:
     * 0 means a single unknown token.  1 uses suffix, and capitalization.
     * 2 uses a variant (richer) form of signature.  Good.
     * Use this one.  Using the richer signatures in versions 3 or 4 seems
     * to have very marginal or no positive value.
     * 3 uses a richer form of signature that mimics the NER word type
     * patterns.  4 is a variant of 2.  5 is another with more English
     * specific morphology (good for English unknowns!).
     * 6-9 are options for Arabic.  9 codes some patterns for numbers and
     * derivational morphology, but also supports unknownPrefixSize and
     * unknownSuffixSize.
     * For German, 0 means a single unknown token, and non-zero means to use
     * capitalization of first letter and a suffix of length
     * unknownSuffixSize.
     */
    public int useUnknownWordSignatures = 0;

    /**
     * RS: file for Turian's word vectors
     * The default value is an example of size 25 word vectors on the nlp machines
     */
    public static final String DEFAULT_WORD_VECTOR_FILE = "/u/scr/nlp/deeplearning/datasets/turian/embeddings-scaled.EMBEDDING_SIZE=25.txt";
    public String wordVectorFile = DEFAULT_WORD_VECTOR_FILE;
    /**
     * Number of hidden units in the word vectors.  As setting of 0
     * will make it try to extract the size from the data file.
     */
    public int numHid = 0;


    /**
     * Words more common than this are tagged with MLE P(t|w). Default 100. The
     * smoothing is sufficiently slight that changing this has little effect.
     * But set this to 0 to be able to use the parser as a vanilla PCFG with
     * no smoothing (not as a practical parser but for exposition or debugging).
     */
    public int smoothInUnknownsThreshold = 100;

    /**
     * Smarter smoothing for rare words.
     */
    public boolean smartMutation = false;

    /**
     * Make use of unicode code point types in smoothing.
     */
    public boolean useUnicodeType = false;

    /** For certain Lexicons, a certain number of word-final letters are
     *  used to subclassify the unknown token. This gives the number of
     *  letters.
     */
    public int unknownSuffixSize = 1;

    /** For certain Lexicons, a certain number of word-initial letters are
     *  used to subclassify the unknown token. This gives the number of
     *  letters.
     */
    public int unknownPrefixSize = 1;

    /**
     * Model for unknown words that the lexicon should use.  This is the
     * name of a class.
     */
    public String uwModelTrainer; // = null;

    /* If this option is false, then all words that were seen in the training
     * data (even once) are constrained to only have seen tags.  That is,
     * mle is used for the lexicon.
     * If this option is true, then if a word has been seen more than
     * smoothInUnknownsThreshold, then it will still only get tags with which
     * it has been seen, but rarer words will get all tags for which the
     * unknown word model (or smart mutation) does not give a score of -Inf.
     * This will normally be all open class tags.
     * If floodTags is invoked by the parser, all other tags will also be
     * given a minimal non-zero, non-infinite probability.
     */
    public boolean flexiTag = false;

    /** Whether to use signature rather than just being unknown as prior in
     *  known word smoothing.  Currently only works if turned on for English.
     */
    public boolean useSignatureForKnownSmoothing;

    /** A file of word class data which may be used for smoothing,
     *  normally instead of hand-specified signatures.
     */
    public String wordClassesFile;



    private static final long serialVersionUID = 2805351374506855632L;

    private static final String[] params = { "useUnknownWordSignatures",
                                             "smoothInUnknownsThreshold",
                                             "smartMutation",
                                             "useUnicodeType",
                                             "unknownSuffixSize",
                                             "unknownPrefixSize",
                                             "flexiTag",
                                             "useSignatureForKnownSmoothing",
                                             "wordClassesFile" };

    @Override
    public String toString() {
      return params[0] + " " + useUnknownWordSignatures + "\n" +
        params[1] + " " + smoothInUnknownsThreshold + "\n" +
        params[2] + " " + smartMutation + "\n" +
        params[3] + " " + useUnicodeType + "\n" +
        params[4] + " " + unknownSuffixSize + "\n" +
        params[5] + " " + unknownPrefixSize + "\n" +
        params[6] + " " + flexiTag + "\n" +
        params[7] + " " + useSignatureForKnownSmoothing + "\n" +
        params[8] + " " + wordClassesFile + "\n";
    }

    public void readData(BufferedReader in) throws IOException {
      for (int i = 0; i < params.length; i++) {
        String line = in.readLine();
        int idx = line.indexOf(' ');
        String key = line.substring(0, idx);
        String value = line.substring(idx + 1);
        if ( ! key.equalsIgnoreCase(params[i])) {
        }
        switch (i) {
        case 0:
          useUnknownWordSignatures = Integer.parseInt(value);
          break;
        case 1:
          smoothInUnknownsThreshold = Integer.parseInt(value);
          break;
        case 2:
          smartMutation = Boolean.parseBoolean(value);
          break;
        case 3:
          useUnicodeType = Boolean.parseBoolean(value);
          break;
        case 4:
          unknownSuffixSize = Integer.parseInt(value);
          break;
        case 5:
          unknownPrefixSize = Integer.parseInt(value);
          break;
        case 6:
          flexiTag = Boolean.parseBoolean(value);
          break;
        case 7:
          useSignatureForKnownSmoothing = Boolean.parseBoolean(value);
          break;
        case 8:
          wordClassesFile = value;
          break;
        }
      }
    }

  } // end class LexOptions


  public LexOptions lexOptions = new LexOptions();

  /**
   * The treebank-specific parser parameters  to use.
   */
  public TreebankLangParserParams tlpParams;

  /**
   * @return The treebank language pack for the treebank the parser
   * is trained on.
   */
  public TreebankLanguagePack langpack() {
    return tlpParams.treebankLanguagePack();
  }


  /**
   * Forces parsing with strictly CNF grammar -- unary chains are converted
   * to XP&amp;YP symbols and back
   */
  public boolean forceCNF = false;

  /**
   * Do a PCFG parse of the sentence.  If both variables are on,
   * also do a combined parse of the sentence.
   */
  public boolean doPCFG = true;

  /**
   * Do a dependency parse of the sentence.
   */
  public boolean doDep = true;

  /**
   * if true, any child can be the head (seems rather bad!)
   */
  public boolean freeDependencies = false;

  /**
   * Whether dependency grammar considers left/right direction. Good.
   */
  public boolean directional = true;
  public boolean genStop = true;

  public boolean useSmoothTagProjection = false;
  public boolean useUnigramWordSmoothing = false;

  /**
   * Use distance bins in the dependency calculations
   */
  public boolean distance = true;
  /**
   * Use coarser distance (4 bins) in dependency calculations
   */
  public boolean coarseDistance = false;

  /**
   * "double count" tags rewrites as word in PCFG and Dep parser.  Good for
   * combined parsing only (it used to not kick in for PCFG parsing).  This
   * option is only used at Test time, but it is now in Options, so the
   * correct choice for a grammar is recorded by a serialized parser.
   * You should turn this off for a vanilla PCFG parser.
   */
  public boolean dcTags = true;

  /**
   * If true, inside the factored parser, remove any node from the final
   * chosen tree which improves the PCFG score. This was added as the
   * dependency factor tends to encourage 'deep' trees.
   */
  public boolean nodePrune = false;


  public TrainOptions trainOptions = newTrainOptions();

  /** Separated out so subclasses of Options can override */
  public TrainOptions newTrainOptions() {
    return new TrainOptions();
  }

  /**
   * Note that the TestOptions is transient.  This means that whatever
   * options get set at creation time are forgotten when the parser is
   * serialized.  If you want an option to be remembered when the
   * parser is reloaded, put it in either TrainOptions or in this
   * class itself.
   */
  public transient TestOptions testOptions = newTestOptions();

  /** Separated out so subclasses of Options can override */
  public TestOptions newTestOptions() {
    return new TestOptions();
  }


  /**
   * A function that maps words used in training and testing to new
   * words.  For example, it could be a function to lowercase text,
   * such as edu.berkeley.nlp.assignments.parsing.util.LowercaseFunction (which makes the
   * parser case insensitive).  This function is applied in
   * LexicalizedParserQuery.parse and in the training methods which
   * build a new parser.
   */
  public Function<String, String> wordFunction = null;

  /**
   * If the parser has a reranker, it looks at this many trees when
   * building the reranked list.
   */
  public int rerankerKBest = 100;

  /**
   * If reranking sentences, we can use the score from the original
   * parser as well.  This tells us how much weight to give that score.
   */
  public double baseParserWeight = 0.0;

  /**
   * Making the TestOptions transient means it won't even be
   * constructed when you deserialize an Options, so we need to
   * construct it on our own when deserializing
   */
  private void readObject(ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    testOptions = newTestOptions();
  }

  public void writeData(Writer w) {//throws IOException {
    PrintWriter out = new PrintWriter(w);
    StringBuilder sb = new StringBuilder();
    sb.append(lexOptions.toString());
    sb.append("parserParams ").append(tlpParams.getClass().getName()).append("\n");
    sb.append("forceCNF ").append(forceCNF).append("\n");
    sb.append("doPCFG ").append(doPCFG).append("\n");
    sb.append("doDep ").append(doDep).append("\n");
    sb.append("freeDependencies ").append(freeDependencies).append("\n");
    sb.append("directional ").append(directional).append("\n");
    sb.append("genStop ").append(genStop).append("\n");
    sb.append("distance ").append(distance).append("\n");
    sb.append("coarseDistance ").append(coarseDistance).append("\n");
    sb.append("dcTags ").append(dcTags).append("\n");
    sb.append("nPrune ").append(nodePrune).append("\n");
    out.print(sb.toString());
    out.flush();
  }


  /**
   * Populates data in this Options from the character stream.
   * @param in The Reader
   * @throws IOException If there is a problem reading data
   */
  public void readData(BufferedReader in) throws IOException {
    String line, value;
    // skip old variables if still present
    lexOptions.readData(in);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    try {
      tlpParams = (TreebankLangParserParams) Class.forName(value).newInstance();
    } catch (Exception e) {
      IOException ioe = new IOException("Problem instantiating parserParams: " + line);
      ioe.initCause(e);
      throw ioe;
    }
    line = in.readLine();
    // ensure backwards compatibility
    if (line.matches("^forceCNF.*")) {
      value = line.substring(line.indexOf(' ') + 1);
      forceCNF = Boolean.parseBoolean(value);
      line = in.readLine();
    }
    value = line.substring(line.indexOf(' ') + 1);
    doPCFG = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    doDep = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    freeDependencies = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    directional = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    genStop = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    distance = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    coarseDistance = Boolean.parseBoolean(value);
    line = in.readLine();
    value = line.substring(line.indexOf(' ') + 1);
    dcTags = Boolean.parseBoolean(value);
    line = in.readLine();
    if ( ! line.matches("^nPrune.*")) {
      throw new RuntimeException("Expected nPrune, found: " + line);
    }
    value = line.substring(line.indexOf(' ') + 1);
    nodePrune = Boolean.parseBoolean(value);
    line = in.readLine(); // get rid of last line
    if (line.length() != 0) {
      throw new RuntimeException("Expected blank line, found: " + line);
    }
  }

  private static final long serialVersionUID = 4L;

} // end class Options
