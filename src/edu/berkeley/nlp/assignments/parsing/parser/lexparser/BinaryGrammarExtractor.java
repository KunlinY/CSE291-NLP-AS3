package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import edu.berkeley.nlp.assignments.parsing.stats.ClassicCounter;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.util.Generics;
import edu.berkeley.nlp.assignments.parsing.util.Index;
import edu.berkeley.nlp.assignments.parsing.util.Pair;

import java.util.Collection;
import java.util.Set;

public class BinaryGrammarExtractor {

  protected Index<String> stateIndex;
  private ClassicCounter<UnaryRule> unaryRuleCounter = new ClassicCounter<>();
  private ClassicCounter<BinaryRule> binaryRuleCounter = new ClassicCounter<>();
  protected ClassicCounter<String> symbolCounter = new ClassicCounter<>();
  private Set<BinaryRule> binaryRules = Generics.newHashSet();
  private Set<UnaryRule> unaryRules = Generics.newHashSet();

  //  protected void tallyTree(Tree t, double weight) {
  //    super.tallyTree(t, weight);
  //    System.out.println("Tree:");
  //    t.pennPrint();
  //  }

  public BinaryGrammarExtractor(Index<String> index) {
    this.stateIndex = index;
  }

  protected void tallyLocalTree(Tree lt, double weight) {
    // printTrainTree(null, "Tallying local tree:", lt);

    if (lt.isLeaf()) {
      //      System.out.println("it's a leaf");
    } else if (lt.isPreTerminal()) {
      //      System.out.println("it's a preterminal");
    } else {
      //      System.out.println("it's a internal node");
      tallyInternalNode(lt, weight);
    }
  }

  public void tallyTree(Tree t, double weight) {
    for (Tree localTree : t.subTreeList()) {
      tallyLocalTree(localTree, weight);
    }
  }

  protected void tallyTrees(Collection<Tree> trees, double weight) {
    for (Tree tree : trees) {
      tallyTree(tree, weight);
    }
  }


  public Pair<UnaryGrammar,BinaryGrammar> extract(Collection<Tree> treeList) {
    tallyTrees(treeList, 1.0);
    return formResult();
  }

  protected void tallyInternalNode(Tree lt, double weight) {
    if (lt.children().length == 1) {
      UnaryRule ur = new UnaryRule(stateIndex.addToIndex(lt.label().value()),
                        stateIndex.addToIndex(lt.children()[0].label().value()));
      symbolCounter.incrementCount(stateIndex.get(ur.parent), weight);
      unaryRuleCounter.incrementCount(ur, weight);
      unaryRules.add(ur);
    } else {
      BinaryRule br = new BinaryRule(stateIndex.addToIndex(lt.label().value()),
                         stateIndex.addToIndex(lt.children()[0].label().value()),
                         stateIndex.addToIndex(lt.children()[1].label().value()));
      symbolCounter.incrementCount(stateIndex.get(br.parent), weight);
      binaryRuleCounter.incrementCount(br, weight);
      binaryRules.add(br);
    }
  }

  public Pair<UnaryGrammar,BinaryGrammar> formResult() {
    stateIndex.addToIndex(Lexicon.BOUNDARY_TAG);
    BinaryGrammar bg = new BinaryGrammar(stateIndex);
    UnaryGrammar ug = new UnaryGrammar(stateIndex);
    // add unaries
    for (UnaryRule ur : unaryRules) {
      ur.score = (float) Math.log(unaryRuleCounter.getCount(ur) / symbolCounter.getCount(stateIndex.get(ur.parent)));
      ug.addRule(ur);
    }
    // add binaries
    for (BinaryRule br : binaryRules) {
      br.score = (float) Math.log((binaryRuleCounter.getCount(br) - 0.0) / symbolCounter.getCount(stateIndex.get(br.parent)));
      bg.addRule(br);
    }
    return new Pair<>(ug, bg);
  }

} // end class BinaryGrammarExtractor
