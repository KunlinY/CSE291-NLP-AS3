package edu.berkeley.nlp.assignments.parsing.parser.lexparser; 
import edu.berkeley.nlp.assignments.parsing.ling.CategoryWordTag;
import edu.berkeley.nlp.assignments.parsing.ling.HasTag;
import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.stats.EquivalenceClasser;
import edu.berkeley.nlp.assignments.parsing.trees.*;
import edu.berkeley.nlp.assignments.parsing.util.Index;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;


/**
 * An abstract class providing a common method base from which to
 * complete a {@code TreebankLangParserParams} implementing class.
 * <p/>
 * With some extending classes you'll want to have access to special
 * attributes of the corresponding TreebankLanguagePack while taking
 * advantage of this class's code for making the TreebankLanguagePack
 * accessible.  A good way to do this is to pass a new instance of the
 * appropriate TreebankLanguagePack into this class's constructor,
 * then get it back later on by casting a call to
 * treebankLanguagePack().  See ChineseTreebankParserParams for an
 * example.
 *
 * @author Roger Levy
 */
public abstract class AbstractTreebankParserParams implements TreebankLangParserParams  {


  
  protected boolean evalGF = true;

  /** The job of this class is to remove subcategorizations from
   *  tag and category nodes, so as to put a tree in a suitable
   *  state for evaluation.  Providing the TreebankLanguagePack
   *  is defined correctly, this should work for any language.
   */
  protected class SubcategoryStripper implements TreeTransformer {

    protected TreeFactory tf = new LabeledScoredTreeFactory();

    @Override
    public Tree transformTree(Tree tree) {
      Label lab = tree.label();
      if (tree.isLeaf()) {
        Tree leaf = tf.newLeaf(lab);
        leaf.setScore(tree.score());
        return leaf;
      }
      String s = lab.value();
      s = treebankLanguagePack().basicCategory(s);
      int numKids = tree.numChildren();
      List<Tree> children = new ArrayList<>(numKids);
      for (int cNum = 0; cNum < numKids; cNum++) {
        Tree child = tree.getChild(cNum);
        Tree newChild = transformTree(child);
        // cdm 2007: for just subcategory stripping, null shouldn't happen
        // if (newChild != null) {
        children.add(newChild);
        // }
      }
      // if (children.isEmpty()) {
      //   return null;
      // }
      CategoryWordTag newLabel = new CategoryWordTag(lab);
      newLabel.setCategory(s);
      if (lab instanceof HasTag) {
        String tag = ((HasTag) lab).tag();
        tag = treebankLanguagePack().basicCategory(tag);
        newLabel.setTag(tag);
      }
      Tree node = tf.newTreeNode(newLabel, children);
      node.setScore(tree.score());
      return node;
    }

  } // end class SubcategoryStripper

  /** The job of this class is to remove subcategorizations from
   *  tag and category nodes, so as to put a tree in a suitable
   *  state for evaluation.  Providing the TreebankLanguagePack
   *  is defined correctly, this should work for any language.
   *  Very simililar to subcategory stripper, but strips grammatical
   *  functions as well.
   */
  protected class RemoveGFSubcategoryStripper implements TreeTransformer {

    protected TreeFactory tf = new LabeledScoredTreeFactory();

    @Override
    public Tree transformTree(Tree tree) {
      Label lab = tree.label();
      if (tree.isLeaf()) {
        Tree leaf = tf.newLeaf(lab);
        leaf.setScore(tree.score());
        return leaf;
      }
      String s = lab.value();
      s = treebankLanguagePack().basicCategory(s);
      s = treebankLanguagePack().stripGF(s);
      int numKids = tree.numChildren();
      List<Tree> children = new ArrayList<>(numKids);
      for (int cNum = 0; cNum < numKids; cNum++) {
        Tree child = tree.getChild(cNum);
        Tree newChild = transformTree(child);
        children.add(newChild);
      }
      CategoryWordTag newLabel = new CategoryWordTag(lab);
      newLabel.setCategory(s);
      if (lab instanceof HasTag) {
        String tag = ((HasTag) lab).tag();
        tag = treebankLanguagePack().basicCategory(tag);
        tag = treebankLanguagePack().stripGF(tag);

        newLabel.setTag(tag);
      }
      Tree node = tf.newTreeNode(newLabel, children);
      node.setScore(tree.score());
      return node;
    }
  } // end class RemoveGFSubcategoryStripper

  protected String inputEncoding;
  protected String outputEncoding;
  protected TreebankLanguagePack tlp;
  protected boolean generateOriginalDependencies;


  
  protected AbstractTreebankParserParams(TreebankLanguagePack tlp) {
    this.tlp = tlp;
    inputEncoding = tlp.getEncoding();
    outputEncoding = tlp.getEncoding();
    generateOriginalDependencies = false;
  }

  @Override
  public Label processHeadWord(Label headWord) {
    return headWord;
  }

  
  @Override
  public void setEvaluateGrammaticalFunctions(boolean evalGFs) {
    this.evalGF = evalGFs;
  }

  
  @Override
  public void setInputEncoding(String encoding) {
    inputEncoding = encoding;
  }

  
  @Override
  public void setOutputEncoding(String encoding) {
    outputEncoding = encoding;
  }

  
  @Override
  public String getOutputEncoding() {
    return outputEncoding;
  }

  
  @Override
  public String getInputEncoding() {
    return inputEncoding;
  }


  
  @Override
  public Treebank treebank() {
    return null;
  }

  
  @Override
  public PrintWriter pw() {
    return pw(System.out);
  }

  
  @Override
  public PrintWriter pw(OutputStream o) {
    String encoding = outputEncoding;
    if (!java.nio.charset.Charset.isSupported(encoding)) {
      encoding = "UTF-8";
    }

    //log.info("TreebankParserParams.pw(): encoding is " + encoding);
    try {
      return new PrintWriter(new OutputStreamWriter(o, encoding), true);
    } catch (UnsupportedEncodingException e) {
      try {
        return new PrintWriter(new OutputStreamWriter(o, "UTF-8"), true);
      } catch (UnsupportedEncodingException e1) {
        return new PrintWriter(o, true);
      }
    }
  }


  
  @Override
  public TreebankLanguagePack treebankLanguagePack() {
    return tlp;
  }

  
  @Override
  public abstract HeadFinder headFinder();

  
  @Override
  public abstract HeadFinder typedDependencyHeadFinder();

  @Override
  public Lexicon lex(Options op, Index<String> wordIndex, Index<String> tagIndex) {
    return new BaseLexicon(op, wordIndex, tagIndex);
  }

  
  @Override
  public double[] MLEDependencyGrammarSmoothingParams() {
    return new double[] { 16.0, 16.0, 4.0, 0.6 };
  }


  
  public static Collection<Constituent> parsevalObjectify(Tree t, TreeTransformer collinizer) {
    return parsevalObjectify(t,collinizer,true);
  }

  /**
   * Takes a Tree and a collinizer and returns a Collection of {@link Constituent}s for
   * PARSEVAL evaluation.  Some notes on this particular parseval:
   * <ul>
   * <li> It is character-based, which allows it to be used on segmentation/parsing combination evaluation.
   * <li> whether it gives you labeled or unlabeled bracketings depends on the value of the {@code labelConstituents}
   * parameter
   * </ul>
   *
   * (Note that I haven't checked this rigorously yet with the PARSEVAL definition
   * -- Roger.)
   */
  public static Collection<Constituent> parsevalObjectify(Tree t, TreeTransformer collinizer, boolean labelConstituents) {
    Collection<Constituent> spans = new ArrayList<>();
    Tree t1 = collinizer.transformTree(t);
    if (t1 == null) {
      return spans;
    }
    for (Tree node : t1) {
      if (node.isLeaf() || node.isPreTerminal() || (node != t1 && node.parent(t1) == null)) {
        continue;
      }
      int leftEdge = t1.leftCharEdge(node);
      int rightEdge = t1.rightCharEdge(node);
      if(labelConstituents)
        spans.add(new LabeledConstituent(leftEdge, rightEdge, node.label()));
      else
        spans.add(new SimpleConstituent(leftEdge, rightEdge));
    }
    return spans;
  }


  
  public static Collection<List<String>> untypedDependencyObjectify(Tree t, HeadFinder hf, TreeTransformer collinizer) {
    return dependencyObjectify(t, hf, collinizer, new UntypedDependencyTyper(hf));
  }

  
  public static Collection<List<String>> unorderedUntypedDependencyObjectify(Tree t, HeadFinder hf, TreeTransformer collinizer) {
    return dependencyObjectify(t, hf, collinizer, new UnorderedUntypedDependencyTyper(hf));
  }

  
  public static Collection<List<String>> typedDependencyObjectify(Tree t, HeadFinder hf, TreeTransformer collinizer) {
    return dependencyObjectify(t, hf, collinizer, new TypedDependencyTyper(hf));
  }

  
  public static Collection<List<String>> unorderedTypedDependencyObjectify(Tree t, HeadFinder hf, TreeTransformer collinizer) {
    return dependencyObjectify(t, hf, collinizer, new UnorderedTypedDependencyTyper(hf));
  }

  
  public static <E> Collection<E> dependencyObjectify(Tree t, HeadFinder hf, TreeTransformer collinizer, DependencyTyper<E> typer) {
    Collection<E> deps = new ArrayList<>();
    Tree t1 = collinizer.transformTree(t);
    if(t1==null)
      return deps;
    dependencyObjectifyHelper(t1, t1, hf, deps, typer);
    return deps;
  }

  private static <E> void dependencyObjectifyHelper(Tree t, Tree root, HeadFinder hf, Collection<E> c, DependencyTyper<E> typer) {
    if (t.isLeaf() || t.isPreTerminal()) {
      return;
    }
    Tree headDtr = hf.determineHead(t);
    for (Tree child : t.children()) {
      dependencyObjectifyHelper(child, root, hf, c, typer);
      if (child != headDtr) {
        c.add(typer.makeDependency(headDtr, child, root));
      }
    }
  }


  private static class UntypedDependencyTyper implements DependencyTyper<List<String>> {
    HeadFinder hf;

    public UntypedDependencyTyper(HeadFinder hf) {
      this.hf = hf;
    }

    @Override
    public List<String> makeDependency(Tree head, Tree dep, Tree root) {
      List<String> result = new ArrayList<>(3);
      Tree headTerm = head.headTerminal(hf);
      Tree depTerm = dep.headTerminal(hf);
      boolean headLeft = root.leftCharEdge(headTerm) < root.leftCharEdge(depTerm);
      result.add(headTerm.value());
      result.add(depTerm.value());
      if(headLeft)
        result.add(leftHeaded);
      else
        result.add(rightHeaded);
      return result;
    }

  }

  private static class UnorderedUntypedDependencyTyper implements DependencyTyper<List<String>> {
    HeadFinder hf;

    public UnorderedUntypedDependencyTyper(HeadFinder hf) {
      this.hf = hf;
    }

    @Override
    public List<String> makeDependency(Tree head, Tree dep, Tree root) {
      List<String> result = new ArrayList<>(3);
      Tree headTerm = head.headTerminal(hf);
      Tree depTerm = dep.headTerminal(hf);
      result.add(headTerm.value());
      result.add(depTerm.value());
      return result;
    }

  }

  private static final String leftHeaded = "leftHeaded";
  private static final String rightHeaded = "rightHeaded";

  private static class TypedDependencyTyper implements DependencyTyper<List<String>> {
    HeadFinder hf;

    public TypedDependencyTyper(HeadFinder hf) {
      this.hf = hf;
    }


    @Override
    public List<String> makeDependency(Tree head, Tree dep, Tree root) {
      List<String> result = new ArrayList<>(6);
      Tree headTerm = head.headTerminal(hf);
      Tree depTerm = dep.headTerminal(hf);
      boolean headLeft = root.leftCharEdge(headTerm) < root.leftCharEdge(depTerm);
      result.add(headTerm.value());
      result.add(depTerm.value());
      result.add(head.parent(root).value());
      result.add(head.value());
      result.add(dep.value());
      if(headLeft)
        result.add(leftHeaded);
      else
        result.add(rightHeaded);
      return result;
    }

  }

    private static class UnorderedTypedDependencyTyper implements DependencyTyper<List<String>> {
    HeadFinder hf;

    public UnorderedTypedDependencyTyper(HeadFinder hf) {
      this.hf = hf;
    }

    @Override
    public List<String> makeDependency(Tree head, Tree dep, Tree root) {
      List<String> result = new ArrayList<>(6);
      Tree headTerm = head.headTerminal(hf);
      Tree depTerm = dep.headTerminal(hf);
      result.add(headTerm.value());
      result.add(depTerm.value());
      result.add(head.parent(root).value());
      result.add(head.value());
      result.add(dep.value());
      return result;
    }

  }

  /** Returns an EquivalenceClasser that classes typed dependencies
   *  by the syntactic categories of mother, head and daughter,
   *  plus direction.
   *
   *  @return An Equivalence class for typed dependencies
   */
  public static EquivalenceClasser<List<String>, String> typedDependencyClasser() {
    return s -> {
      if(s.get(5).equals(leftHeaded))
        return s.get(2) + '(' + s.get(3) + "->" + s.get(4) + ')';
      return s.get(2) + '(' + s.get(4) + "<-" + s.get(3) + ')';
    };
  }


  
  @Override
  public abstract TreeTransformer collinizer();

  
  @Override
  public abstract TreeTransformer collinizerEvalb();


  
  @Override
  public abstract String[] sisterSplitters();


  
  @Override
  public TreeTransformer subcategoryStripper() {
    if(evalGF)
      return new SubcategoryStripper();
    return new RemoveGFSubcategoryStripper();
  }

  
  @Override
  public abstract Tree transformTree(Tree t, Tree root);

  
  @Override
  public abstract void display();

  
  @Override
  public int setOptionFlag(String[] args, int i) {
    return i;
  }

  private static final long serialVersionUID = 4299501909017975915L;

  @Override
  public Extractor<DependencyGrammar> dependencyGrammarExtractor(Options op, Index<String> wordIndex, Index<String> tagIndex) {
    return new MLEDependencyGrammarExtractor(op, wordIndex, tagIndex);
  }

  public boolean isEvalGF() {
    return evalGF;
  }

  public void setEvalGF(boolean evalGF) {
    this.evalGF = evalGF;
  }

  @Override
  public boolean supportsBasicDependencies() {
    return false;
  }

  
  @Override
  public void setGenerateOriginalDependencies(boolean originalDependencies) {
    this.generateOriginalDependencies = originalDependencies;
    if (this.tlp != null) {
      this.tlp.setGenerateOriginalDependencies(originalDependencies);
    }
  }

  @Override
  public boolean generateOriginalDependencies() {
    return this.generateOriginalDependencies;
  }

  private static final String[] EMPTY_ARGS = new String[0];

  @Override
  public String[] defaultCoreNLPFlags() {
    return EMPTY_ARGS;
  }

}
