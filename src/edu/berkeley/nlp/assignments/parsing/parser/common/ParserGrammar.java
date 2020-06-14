package edu.berkeley.nlp.assignments.parsing.parser.common;

import edu.berkeley.nlp.assignments.parsing.ling.CoreLabel;
import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.ling.TaggedWord;
import edu.berkeley.nlp.assignments.parsing.parser.lexparser.Options;
import edu.berkeley.nlp.assignments.parsing.parser.lexparser.TreebankLangParserParams;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack;
import edu.berkeley.nlp.assignments.parsing.util.ReflectionLoading;

import java.util.List;
import java.util.function.Function;


public abstract class ParserGrammar implements Function<List<? extends HasWord>, Tree> {

  public abstract ParserQuery parserQuery();

  /**
   * Parses the list of HasWord.  If the parse fails for some reason,
   * an X tree is returned instead of barfing.
   *
   * @param words The input sentence (a List of words)
   * @return A Tree that is the parse tree for the sentence.  If the parser
   *         fails, a new Tree is synthesized which attaches all words to the
   *         root.
   */
  @Override
  public Tree apply(List<? extends HasWord> words) {
    return parse(words);
  }

  /**
   * Tokenize the text using the parser's tokenizer
   */
  public List<? extends HasWord> tokenize(String sentence) {
    return null;
  }

  /**
   * Will parse the text in <code>sentence</code> as if it represented
   * a single sentence by first processing it with a tokenizer.
   */
  public Tree parse(String sentence) {
    List<? extends HasWord> tokens = tokenize(sentence);
    if (getOp().testOptions.preTag) {
      Function<List<? extends HasWord>, List<TaggedWord>> tagger = loadTagger();
      tokens = tagger.apply(tokens);
    }
    return parse(tokens);
  }

  private transient Function<List<? extends HasWord>, List<TaggedWord>> tagger;
  private transient String taggerPath;

  public Function<List<? extends HasWord>, List<TaggedWord>> loadTagger() {
    Options op = getOp();
    if (op.testOptions.preTag) {
      synchronized(this) { // TODO: rather coarse synchronization
        if (!op.testOptions.taggerSerializedFile.equals(taggerPath)) {
          taggerPath = op.testOptions.taggerSerializedFile;
          tagger = ReflectionLoading.loadByReflection("edu.berkeley.nlp.assignments.parsing.tagger.maxent.MaxentTagger", taggerPath);
        }
        return tagger;
      }
    } else {
      return null;
    }
  }

  public List<CoreLabel> lemmatize(String sentence) {
    List<? extends HasWord> tokens = tokenize(sentence);
    return lemmatize(tokens);
  }

  /**
   * Only works on English, as it is hard coded for using the
   * Morphology class, which is English-only
   */
  public List<CoreLabel> lemmatize(List<? extends HasWord> tokens) {
    return null;
  }

  /**
   * Parses the list of HasWord.  If the parse fails for some reason,
   * an X tree is returned instead of barfing.
   *
   * @param words The input sentence (a List of words)
   * @return A Tree that is the parse tree for the sentence.  If the parser
   *         fails, a new Tree is synthesized which attaches all words to the
   *         root.
   */
  public abstract Tree parse(List<? extends HasWord> words);

  public abstract Options getOp();

  public abstract TreebankLangParserParams getTLPParams();

  public abstract TreebankLanguagePack treebankLanguagePack();

  /**
   * Returns a set of options which should be set by default when used
   * in corenlp.  For example, the English PCFG/RNN models want
   * -retainTmpSubcategories, and the ShiftReduceParser models may
   * want -beamSize 4 depending on how they were trained.
   * <br>
   * TODO: right now completely hardcoded, should be settable as a training time option
   */
  public abstract String[] defaultCoreNLPFlags();

  public abstract void setOptionFlags(String ... flags);

  /**
   * The model requires text to be pretagged
   */
  public abstract boolean requiresTags();

}
