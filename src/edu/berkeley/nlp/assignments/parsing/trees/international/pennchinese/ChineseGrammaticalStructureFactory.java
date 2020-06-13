package edu.berkeley.nlp.assignments.parsing.trees.international.pennchinese;

import edu.berkeley.nlp.assignments.parsing.trees.GrammaticalStructureFactory;
import edu.berkeley.nlp.assignments.parsing.trees.HeadFinder;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import java.util.function.Predicate;

public class ChineseGrammaticalStructureFactory implements GrammaticalStructureFactory {

  private final Predicate<String> puncFilter;
  private final HeadFinder hf;

  public ChineseGrammaticalStructureFactory() {
    this(null, null);
  }

  public ChineseGrammaticalStructureFactory(Predicate<String> puncFilter) {
    this(puncFilter, null);
  }

  public ChineseGrammaticalStructureFactory(Predicate<String> puncFilter, HeadFinder hf) {
    this.puncFilter = puncFilter;
    this.hf = hf;
  }

  public ChineseGrammaticalStructure newGrammaticalStructure(Tree t) {
    if (puncFilter == null && hf == null) {
      return new ChineseGrammaticalStructure(t);
    } else if (hf == null) {
      return new ChineseGrammaticalStructure(t, puncFilter);
    } else {
      return new ChineseGrammaticalStructure(t, puncFilter, hf);
    }
  }
  
}
