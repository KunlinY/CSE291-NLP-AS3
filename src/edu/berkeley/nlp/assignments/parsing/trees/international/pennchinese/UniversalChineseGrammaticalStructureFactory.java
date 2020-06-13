package edu.berkeley.nlp.assignments.parsing.trees.international.pennchinese;

import edu.berkeley.nlp.assignments.parsing.trees.GrammaticalStructureFactory;
import edu.berkeley.nlp.assignments.parsing.trees.HeadFinder;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import java.util.function.Predicate;

public class UniversalChineseGrammaticalStructureFactory implements GrammaticalStructureFactory {

  private final Predicate<String> puncFilter;
  private final HeadFinder hf;

  public UniversalChineseGrammaticalStructureFactory() {
    this(null, null);
  }

  public UniversalChineseGrammaticalStructureFactory(Predicate<String> puncFilter) {
    this(puncFilter, null);
  }

  public UniversalChineseGrammaticalStructureFactory(Predicate<String> puncFilter, HeadFinder hf) {
    this.puncFilter = puncFilter;
    this.hf = hf;
  }

  @Override
  public UniversalChineseGrammaticalStructure newGrammaticalStructure(Tree t) {
    if (puncFilter == null && hf == null) {
      return new UniversalChineseGrammaticalStructure(t);
    } else if (hf == null) {
      return new UniversalChineseGrammaticalStructure(t, puncFilter);
    } else {
      return new UniversalChineseGrammaticalStructure(t, puncFilter, hf);
    }
  }

}
