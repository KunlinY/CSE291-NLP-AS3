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
import edu.berkeley.nlp.assignments.parsing.parser.common.ParserGrammar;
import edu.berkeley.nlp.assignments.parsing.parser.common.ParserQuery;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack;
import edu.berkeley.nlp.assignments.parsing.util.Index;

import java.io.Serializable;
import java.util.List;



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
  public ParserQuery parserQuery() {
    return null;
  }

  @Override
  public Tree parse(List<? extends HasWord> words) {
    return null;
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

  private static final long serialVersionUID = 2;

} // end class LexicalizedParser
