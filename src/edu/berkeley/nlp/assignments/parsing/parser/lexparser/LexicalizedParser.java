// Stanford Parser -- a probabilistic lexicalized NL CFG parser
// Copyright (c) 2002 - 2014 The Board of Trustees of
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

import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.ling.TaggedWord;
import edu.berkeley.nlp.assignments.parsing.parser.common.ArgUtils;
import edu.berkeley.nlp.assignments.parsing.parser.common.ParserGrammar;
import edu.berkeley.nlp.assignments.parsing.parser.common.ParserQuery;
import edu.berkeley.nlp.assignments.parsing.parser.metrics.Eval;
import edu.berkeley.nlp.assignments.parsing.parser.metrics.ParserQueryEval;
import edu.berkeley.nlp.assignments.parsing.trees.*;
import edu.berkeley.nlp.assignments.parsing.util.*;
import edu.berkeley.nlp.assignments.parsing.util.logging.Redwood;

import java.io.FileFilter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;



/**
 * This class provides the top-level API and command-line interface to a set
 * of reasonably good treebank-trained parsers.  The name reflects the main
 * factored parsing model, which provides a lexicalized PCFG parser
 * implemented as a product
 * model of a plain PCFG parser and a lexicalized dependency parser.
 * But you can also run either component parser alone.  In particular, it
 * is often useful to do unlexicalized PCFG parsing by using just that
 * component parser.
 * <p>
 * See the package documentation for more details and examples of use.
 * <p>
 * For information on invoking the parser from the command-line, and for
 * a more detailed list of options, see the {@link #main} method.
 * <p>
 * Note that training on a 1 million word treebank requires a fair amount of
 * memory to run.  Try -mx1500m to increase the memory allocated by the JVM.
 *
 * @author Dan Klein (original version)
 * @author Christopher Manning (better features, ParserParams, serialization)
 * @author Roger Levy (internationalization)
 * @author Teg Grenager (grammar compaction, tokenization, etc.)
 * @author Galen Andrew (considerable refactoring)
 * @author John Bauer (made threadsafe)
 */
public class LexicalizedParser extends ParserGrammar implements Serializable  {

  /** A logger for this class */
  private static final Redwood.RedwoodChannels log = Redwood.channels(LexicalizedParser.class);

  public Lexicon lex;
  public BinaryGrammar bg;
  public UnaryGrammar ug;
  public DependencyGrammar dg;
  public Index<String> stateIndex, wordIndex, tagIndex;

  private Options op;

  @Override
  public Options getOp() { return op; }

  public Reranker reranker; // = null;

  @Override
  public TreebankLangParserParams getTLPParams() { return op.tlpParams; }

  @Override
  public TreebankLanguagePack treebankLanguagePack() { return getTLPParams().treebankLanguagePack(); }

  @Override
  public String[] defaultCoreNLPFlags() {
    return getTLPParams().defaultCoreNLPFlags();
  }

  @Override
  public boolean requiresTags() {
    return false;
  }

  public LexicalizedParser(Lexicon lex, BinaryGrammar bg, UnaryGrammar ug, DependencyGrammar dg, Index<String> stateIndex, Index<String> wordIndex, Index<String> tagIndex, Options op) {
    this.lex = lex;
    this.bg = bg;
    this.ug = ug;
    this.dg = dg;
    this.stateIndex = stateIndex;
    this.wordIndex = wordIndex;
    this.tagIndex = tagIndex;
    this.op = op;
  }

  @Override
  public List<Eval> getExtraEvals() {
    return Collections.emptyList();
  }


  @Override
  public List<ParserQueryEval> getParserQueryEvals() {
    return Collections.emptyList();
  }


  @Override
  public ParserQuery parserQuery() {
    return new LexicalizedParserQuery(this);
  }

  @Override
  public Tree parse(List<? extends HasWord> words) {
    return null;
  }

  private static Treebank makeTreebank(String treebankPath, Options op, FileFilter filt) {
    log.info("Training a parser from treebank dir: " + treebankPath);
    Treebank trainTreebank = op.tlpParams.diskTreebank();
    log.info("Reading trees...");
    trainTreebank.loadPath(treebankPath, filt);

    Timing.tick("done [read " + trainTreebank.size() + " trees].");
    return trainTreebank;
  }


  private static void printOptions(boolean train, Options op) {
    op.display();
    if (train) {
      op.trainOptions.display();
    } else {
      op.testOptions.display();
    }
    op.tlpParams.display();
  }


  // TODO: Make below method work with arbitrarily large secondary treebank via iteration
  // TODO: Have weight implemented for training lexicon

  /**
   * A method for training from two different treebanks, the second of which is presumed
   * to be orders of magnitude larger.
   * <p/>
   * Trees are not read into memory but processed as they are read from disk.
   * <p/>
   * A weight (typically &lt;= 1) can be put on the second treebank.
   *
   * @param trainTreebank A treebank to train from
   * @param secondaryTrainTreebank Another treebank to train from
   * @param weight A weight factor to give the secondary treebank. If the weight
   *     is 0.25, each example in the secondaryTrainTreebank will be treated as
   *     1/4 of an example sentence.
   * @param compactor A class for compacting grammars. May be null.
   * @param op Options for how the grammar is built from the treebank
   * @param tuneTreebank  A treebank to tune free params on (may be null)
   * @param extraTaggedWords A list of words to add to the Lexicon
   * @return The trained LexicalizedParser
   */
  public static LexicalizedParser
  getParserFromTreebank(Treebank trainTreebank,
                        Treebank secondaryTrainTreebank,
                        double weight,
                        GrammarCompactor compactor,
                        Options op,
                        Treebank tuneTreebank,
                        List<List<TaggedWord>> extraTaggedWords)
  {
    // log.info("Currently " + new Date()); // now printed when command-line args are printed
    printOptions(true, op);
    Timing.startTime();

    Triple<Treebank, Treebank, Treebank> treebanks = TreeAnnotatorAndBinarizer.getAnnotatedBinaryTreebankFromTreebank(trainTreebank, secondaryTrainTreebank, tuneTreebank, op);
    Timing.tick("done.");

    Treebank trainTreebankRaw = trainTreebank;
    trainTreebank = treebanks.first();

    // +1 to account for the boundary symbol
    trainTreebank = new FilteringTreebank(trainTreebank, new LengthTreeFilter(op.trainOptions.trainLengthLimit + 1));

    Index<String> stateIndex;
    Index<String> wordIndex;
    Index<String> tagIndex;

    Pair<UnaryGrammar, BinaryGrammar> bgug;
    Lexicon lex;

    stateIndex = new HashIndex<>();
    wordIndex = new HashIndex<>();
    tagIndex = new HashIndex<>();

    // extract grammars
    BinaryGrammarExtractor bgExtractor = new BinaryGrammarExtractor(stateIndex);
    // Extractor lexExtractor = new LexiconExtractor();
    //TreeExtractor uwmExtractor = new UnknownWordModelExtractor(trainTreebank.size());
    log.info("Extracting PCFG...");
    bgug = bgExtractor.extract(trainTreebank);
    Timing.tick("done.");

    log.info("Extracting Lexicon...");
    lex = op.tlpParams.lex(op, wordIndex, tagIndex);

    double trainSize = trainTreebank.size();

    lex.initializeTraining(trainSize);
    // wsg2012: The raw treebank has CoreLabels, which we need for FactoredLexicon
    // training. If TreeAnnotator is updated so that it produces CoreLabels, then we can
    // remove the trainTreebankRaw.
    lex.train(trainTreebank, trainTreebankRaw);
    lex.finishTraining();
    Timing.tick("done.");

    log.info("Compiling grammar...");
    BinaryGrammar bg = bgug.second;
    bg.splitRules();
    UnaryGrammar ug = bgug.first;
    ug.purgeRules();
    Timing.tick("done");

    DependencyGrammar dg = null;

    log.info("Done training parser.");
    return new LexicalizedParser(lex, bg, ug, dg, stateIndex, wordIndex, tagIndex, op);
  }


  /**
   * This will set options to the parser, in a way exactly equivalent to
   * passing in the same sequence of command-line arguments.  This is a useful
   * convenience method when building a parser programmatically. The options
   * passed in should
   * be specified like command-line arguments, including with an initial
   * minus sign.
   * <p/>
   * <i>Notes:</i> This can be used to set parsing-time flags for a
   * serialized parser.  You can also still change things serialized
   * in Options, but this will probably degrade parsing performance.
   * The vast majority of command line flags can be passed to this
   * method, but you cannot pass in options that specify the treebank
   * or grammar to be loaded, the grammar to be written, trees or
   * files to be parsed or details of their encoding, nor the
   * TreebankLangParserParams ({@code -tLPP}) to use. The
   * TreebankLangParserParams should be set up on construction of a
   * LexicalizedParser, by constructing an Options that uses
   * the required TreebankLangParserParams, and passing that to a
   * LexicalizedParser constructor.  Note that despite this
   * method being an instance method, many flags are actually set as
   * static class variables.
   *
   * @param flags Arguments to the parser, for example,
   *              {"-outputFormat", "typedDependencies", "-maxLength", "70"}
   * @throws IllegalArgumentException If an unknown flag is passed in
   */
  @Override
  public void setOptionFlags(String... flags) {
    op.setOptions(flags);
  }


  /**
   * A main program for using the parser with various options.
   * This program can be used for building and serializing
   * a parser from treebank data, for parsing sentences from a file
   * or URL using a serialized or text grammar parser,
   * and (mainly for parser quality testing)
   * for training and testing a parser on a treebank all in one go.
   *
   * <p>
   * Sample Usages:
   * <ul>
   *   <li> <b>Train a parser (saved to <i>serializedGrammarFilename</i>)
   *      from a directory of trees (<i>trainFilesPath</i>, with an optional <i>fileRange</i>, e.g., 0-1000):</b>
   *    {@code java -mx1500m edu.berkeley.nlp.assignments.parsing.parser.lexparser.LexicalizedParser [-v] -train trainFilesPath [fileRange] -saveToSerializedFile serializedGrammarFilename}
   *   </li>
   *
   *   <li> <b>Train a parser (not saved) from a directory of trees, and test it (reporting scores) on a directory of trees</b>
   *    {@code java -mx1500m edu.berkeley.nlp.assignments.parsing.parser.lexparser.LexicalizedParser [-v] -train trainFilesPath [fileRange] -testTreebank testFilePath [fileRange] }
   *   </li>
   *
   *   <li> <b>Parse one or more files, given a serialized grammar and a list of files</b>
   *    {@code java -mx512m edu.berkeley.nlp.assignments.parsing.parser.lexparser.LexicalizedParser [-v] serializedGrammarPath filename [filename]*}
   *   </li>
   *
   *   <li> <b>Test and report scores for a serialized grammar on trees in an output directory</b>
   *    {@code java -mx512m edu.berkeley.nlp.assignments.parsing.parser.lexparser.LexicalizedParser [-v] -loadFromSerializedFile serializedGrammarPath -testTreebank testFilePath [fileRange]}
   *   </li>
   * </ul>
   *
   *<p>
   * If the {@code serializedGrammarPath} ends in {@code .gz},
   * then the grammar is written and read as a compressed file (GZip).
   * If the {@code serializedGrammarPath} is a URL, starting with
   * {@code http://}, then the parser is read from the URL.
   * A fileRange specifies a numeric value that must be included within a
   * filename for it to be used in training or testing (this works well with
   * most current treebanks).  It can be specified like a range of pages to be
   * printed, for instance as {@code 200-2199} or
   * {@code 1-300,500-725,9000} or just as {@code 1} (if all your
   * trees are in a single file, either omit this parameter or just give a dummy
   * argument such as {@code 0}).
   * If the filename to parse is "-" then the parser parses from stdin.
   * If no files are supplied to parse, then a hardwired sentence
   * is parsed.
   *
   * <p>
   * The parser can write a grammar as either a serialized Java object file
   * or in a text format (or as both), specified with the following options:
   * <blockquote>{@code
   * java edu.berkeley.nlp.assignments.parsing.parser.lexparser.LexicalizedParser
   * [-v] -train
   * trainFilesPath [fileRange] [-saveToSerializedFile grammarPath]
   * [-saveToTextFile grammarPath]
   * }</blockquote>
   *
   * <p>
   * In the same position as the verbose flag ({@code -v}), many other
   * options can be specified.  The most useful to an end user are:
   * <ul>
   * <LI>{@code -tLPP class} Specify a different
   * TreebankLangParserParams, for when using a different language or
   * treebank (the default is English Penn Treebank). <i>This option MUST occur
   * before any other language-specific options that are used (or else they
   * are ignored!).</i>
   * (It's usually a good idea to specify this option even when loading a
   * serialized grammar; it is necessary if the language pack specifies a
   * needed character encoding or you wish to specify language-specific
   * options on the command line.)</LI>
   * <LI>{@code -encoding charset} Specify the character encoding of the
   * input and output files.  This will override the value in the
   * {@code TreebankLangParserParams}, provided this option appears
   * <i>after</i> any {@code -tLPP} option.</LI>
   * <LI>{@code -tokenized} Says that the input is already separated
   * into whitespace-delimited tokens.  If this option is specified, any
   * tokenizer specified for the language is ignored, and a universal (Unicode)
   * tokenizer, which divides only on whitespace, is used.
   * Unless you also specify
   * {@code -escaper}, the tokens <i>must</i> all be correctly
   * tokenized tokens of the appropriate treebank for the parser to work
   * well (for instance, if using the Penn English Treebank, you must have
   * coded "(" as "-LRB-", etc.). (Note: we do not use the backslash escaping
   * in front of / and * that appeared in Penn Treebank releases through 1999.)</li>
   * <li>{@code -escaper class} Specify a class of type
   * {@link Function}&lt;List&lt;HasWord&gt;,List&lt;HasWord&gt;&gt; to do
   * customized escaping of tokenized text.  This class will be run over the
   * tokenized text and can fix the representation of tokens. For instance,
   * it could change "(" to "-LRB-" for the Penn English Treebank.  A
   * provided escaper that does such things for the Penn English Treebank is
   * {@code edu.berkeley.nlp.assignments.parsing.process.PTBEscapingProcessor}
   * <li>{@code -tokenizerFactory class} Specifies a
   * TokenizerFactory class to be used for tokenization</li>
   * <li>{@code -tokenizerOptions options} Specifies options to a
   * TokenizerFactory class to be used for tokenization.   A comma-separated
   * list. For PTBTokenizer, options of interest include
   * {@code americanize=false} and {@code asciiQuotes} (for German).
   * Note that any choice of tokenizer options that conflicts with the
   * tokenization used in the parser training data will likely degrade parser
   * performance. </li>
   * <li>{@code -sentences token } Specifies a token that marks sentence
   * boundaries.  A value of {@code newline} causes sentence breaking on
   * newlines.  A value of {@code onePerElement} causes each element
   * (using the XML {@code -parseInside} option) to be treated as a
   * sentence. All other tokens will be interpreted literally, and must be
   * exactly the same as tokens returned by the tokenizer.  For example,
   * you might specify "|||" and put that symbol sequence as a token between
   * sentences.
   * If no explicit sentence breaking option is chosen, sentence breaking
   * is done based on a set of language-particular sentence-ending patterns.
   * </li>
   * <LI>{@code -parseInside element} Specifies that parsing should only
   * be done for tokens inside the indicated XML-style
   * elements (done as simple pattern matching, rather than XML parsing).
   * For example, if this is specified as {@code sentence}, then
   * the text inside the {@code sentence} element
   * would be parsed.
   * Using "-parseInside s" gives you support for the input format of
   * Charniak's parser. Sentences cannot span elements. Whether the
   * contents of the element are treated as one sentence or potentially
   * multiple sentences is controlled by the {@code -sentences} flag.
   * The default is potentially multiple sentences.
   * This option gives support for extracting and parsing
   * text from very simple SGML and XML documents, and is provided as a
   * user convenience for that purpose. If you want to really parse XML
   * documents before NLP parsing them, you should use an XML parser, and then
   * call to a LexicalizedParser on appropriate CDATA.
   * <LI>{@code -tagSeparator char} Specifies to look for tags on words
   * following the word and separated from it by a special character
   * {@code char}.  For instance, many tagged corpora have the
   * representation "house/NN" and you would use {@code -tagSeparator /}.
   * Notes: This option requires that the input be pretokenized.
   * The separator has to be only a single character, and there is no
   * escaping mechanism. However, splitting is done on the <i>last</i>
   * instance of the character in the token, so that cases like
   * "3\/4/CD" are handled correctly.  The parser will in all normal
   * circumstances use the tag you provide, but will override it in the
   * case of very common words in cases where the tag that you provide
   * is not one that it regards as a possible tagging for the word.
   * The parser supports a format where only some of the words in a sentence
   * have a tag (if you are calling the parser programmatically, you indicate
   * them by having them implement the {@code HasTag} interface).
   * You can do this at the command-line by only having tags after some words,
   * but you are limited by the fact that there is no way to escape the
   * tagSeparator character.</LI>
   * <LI>{@code -maxLength leng} Specify the longest sentence that
   * will be parsed (and hence indirectly the amount of memory
   * needed for the parser). If this is not specified, the parser will
   * try to dynamically grow its parse chart when long sentence are
   * encountered, but may run out of memory trying to do so.</LI>
   * <LI>{@code -outputFormat styles} Choose the style(s) of output
   * sentences: {@code penn} for prettyprinting as in the Penn
   * treebank files, or {@code oneline} for printing sentences one
   * per line, {@code words}, {@code wordsAndTags},
   * {@code dependencies}, {@code typedDependencies},
   * or {@code typedDependenciesCollapsed}.
   * Multiple options may be specified as a comma-separated
   * list.  See TreePrint class for further documentation.</LI>
   * <LI>{@code -outputFormatOptions} Provide options that control the
   * behavior of various {@code -outputFormat} choices, such as
   * {@code lexicalize}, {@code stem}, {@code markHeadNodes},
   * or {@code xml}.  {@link edu.berkeley.nlp.assignments.parsing.trees.TreePrint}
   * Options are specified as a comma-separated list.</LI>
   * <LI>{@code -writeOutputFiles} Write output files corresponding
   * to the input files, with the same name but a {@code ".stp"}
   * file extension.  The format of these files depends on the
   * {@code outputFormat} option.  (If not specified, output is sent
   * to stdout.)</LI>
   * <LI>{@code -outputFilesExtension} The extension that is appended to
   * the filename that is being parsed to produce an output file name (with the
   * -writeOutputFiles option). The default is {@code stp}.  Don't
   * include the period.
   * <LI>{@code -outputFilesDirectory} The directory in which output
   * files are written (when the -writeOutputFiles option is specified).
   * If not specified, output files are written in the same directory as the
   * input files.
   * <LI>{@code -nthreads} Parsing files and testing on treebanks
   * can use multiple threads.  This option tells the parser how many
   * threads to use.  A negative number indicates to use as many
   * threads as the machine has cores.
   * </ul>
   * See also the package documentation for more details and examples of use.
   *
   * @param args Command line arguments, as above
   */
  public static void main(String[] args) {
    boolean train = false;
    String treebankPath = null;
    Treebank testTreebank = null;
    Treebank tuneTreebank = null;
    String testPath = null;
    FileFilter testFilter = null;
    FileFilter trainFilter = null;
    double secondaryTreebankWeight = 1.0;
    int argIndex = 0;

    Options op = new Options();
    op.doDep = false;
    // while loop through option arguments
    while (argIndex < args.length && args[argIndex].charAt(0) == '-') {
      if (args[argIndex].equalsIgnoreCase("-train") ||
          args[argIndex].equalsIgnoreCase("-trainTreebank")) {
        train = true;
        Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-train");
        argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
        treebankPath = treebankDescription.first();
        trainFilter = treebankDescription.second();
      }  else if (args[argIndex].equalsIgnoreCase("-treebank") ||
                 args[argIndex].equalsIgnoreCase("-testTreebank") ||
                 args[argIndex].equalsIgnoreCase("-test")) {
        Pair<String, FileFilter> treebankDescription = ArgUtils.getTreebankDescription(args, argIndex, "-test");
        argIndex = argIndex + ArgUtils.numSubArgs(args, argIndex) + 1;
        testPath = treebankDescription.first();
        testFilter = treebankDescription.second();
      }
    } // end while loop through arguments

    // all other arguments are order dependent and
    // are processed in order below

    if (!train && op.testOptions.verbose) {
      StringUtils.logInvocationString(log, args);
    }
    LexicalizedParser lp; // always initialized in next if-then-else block
    StringUtils.logInvocationString(log, args);

    // so we train a parser using the treebank
    GrammarCompactor compactor = null;

    Treebank trainTreebank = makeTreebank(treebankPath, op, trainFilter);

    Treebank secondaryTrainTreebank = null;

    List<List<TaggedWord>> extraTaggedWords = null;

    lp = getParserFromTreebank(trainTreebank, secondaryTrainTreebank, secondaryTreebankWeight, compactor, op, tuneTreebank, extraTaggedWords);

    if (testFilter != null || testPath != null) {
      testTreebank = op.tlpParams.testMemoryTreebank();
      testTreebank.loadPath(testPath, testFilter);
    }

    op.trainOptions.sisterSplitters = Generics.newHashSet(Arrays.asList(op.tlpParams.sisterSplitters()));

    if (op.testOptions.verbose || train) {
      // Tell the user a little or a lot about what we have made
      // get lexicon size separately as it may have its own prints in it....
      String lexNumRules = lp.lex != null ? Integer.toString(lp.lex.numRules()): "";
      log.info("Grammar\tStates\tTags\tWords\tUnaryR\tBinaryR\tTaggings");
      log.info("Grammar\t" +
          lp.stateIndex.size() + '\t' +
          lp.tagIndex.size() + '\t' +
          lp.wordIndex.size() + '\t' +
          (lp.ug != null ? lp.ug.numRules(): "") + '\t' +
          (lp.bg != null ? lp.bg.numRules(): "") + '\t' +
          lexNumRules);
      log.info("ParserPack is " + op.tlpParams.getClass().getName());
      log.info("Lexicon is " + lp.lex.getClass().getName());
      if (op.testOptions.verbose) {
        log.info("Tags are: " + lp.tagIndex);
        // log.info("States are: " + lp.pd.stateIndex); // This is too verbose. It was already printed out by the below printOptions command if the flag -printStates is given (at training time)!
      }
      printOptions(false, op);
    }

    if (testTreebank != null) {
      // test parser on treebank
      EvaluateTreebank evaluator = new EvaluateTreebank(lp);
      evaluator.testOnTreebank(testTreebank);
    }

  } // end main

  private static final long serialVersionUID = 2;

} // end class LexicalizedParser
