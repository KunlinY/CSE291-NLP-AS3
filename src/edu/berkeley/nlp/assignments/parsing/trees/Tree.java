package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.*;
import edu.berkeley.nlp.assignments.parsing.util.*;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * The abstract class {@code Tree} is used to collect all of the
 * tree types, and acts as a generic extensible type.  This is the
 * standard implementation of inheritance-based polymorphism.
 * All {@code Tree} objects support accessors for their children (a
 * {@code Tree[]}), their label (a {@code Label}), and their
 * score (a {@code double}).  However, different concrete
 * implementations may or may not include the latter two, in which
 * case a default value is returned.  The class Tree defines no data
 * fields.  The two abstract methods that must be implemented are:
 * {@code children()}, and {@code treeFactory()}.  Notes
 * that {@code setChildren(Tree[])} is now an optional
 * operation, whereas it was previously required to be
 * implemented. There is now support for finding the parent of a
 * tree.  This may be done by search from a tree root, or via a
 * directly stored parent.  The {@code Tree} class now
 * implements the {@code Collection} interface: in terms of
 * this, each <i>node</i> of the tree is an element of the
 * collection; hence one can explore the tree by using the methods of
 * this interface.  A {@code Tree} is regarded as a read-only
 * {@code Collection} (even though the {@code Tree} class
 * has various methods that modify trees).  Moreover, the
 * implementation is <i>not</i> thread-safe: no attempt is made to
 * detect and report concurrent modifications.
 *
 * @author Christopher Manning
 * @author Dan Klein
 * @author Sarah Spikes (sdspikes@cs.stanford.edu) - filled in types
 */
public abstract class Tree extends AbstractCollection<Tree> implements Label, Labeled, Scored, Serializable  {

  /** A logger for this class */
  private static final long serialVersionUID = 5441849457648722744L;

  
  public static final Tree[] EMPTY_TREE_ARRAY = new Tree[0];

  public Tree() {
  }

  
  public boolean isLeaf() {
    return numChildren() == 0;
  }


  
  public int numChildren() {
    return children().length;
  }


  
  public boolean isUnaryRewrite() {
    return numChildren() == 1;
  }


  
  public boolean isPreTerminal() {
    Tree[] kids = children();
    return (kids.length == 1) && (kids[0].isLeaf());
  }


  
  public boolean isPrePreTerminal() {
    Tree[] kids = children();
    if (kids.length == 0) {
      return false;
    }
    for (Tree kid : kids) {
      if ( ! kid.isPreTerminal()) {
        return false;
      }
    }
    return true;
  }


  
  public boolean isPhrasal() {
    Tree[] kids = children();
    return !(kids == null || kids.length == 0 || (kids.length == 1 && kids[0].isLeaf()));
  }


  
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Tree)) {
      return false;
    }
    Tree t = (Tree) o;
    String value1 = this.value();
    String value2 = t.value();
    if (value1 != null || value2 != null) {
    	if (value1 == null || value2 == null || !value1.equals(value2)) {
    		return false;
    	}
    }
    Tree[] myKids = children();
    Tree[] theirKids = t.children();
    //if((myKids == null && (theirKids == null || theirKids.length != 0)) || (theirKids == null && myKids.length != 0) || (myKids.length != theirKids.length)){
    if (myKids.length != theirKids.length) {
      return false;
    }
    for (int i = 0; i < myKids.length; i++) {
      if (!myKids[i].equals(theirKids[i])) {
        return false;
      }
    }
    return true;
  }


  
  @Override
  public int hashCode() {
    String v = this.value();
    int hc = (v == null) ? 1 : v.hashCode();
    Tree[] kids = children();
    for (int i = 0; i < kids.length; i++) {
      v = kids[i].value();
      int hc2 = (v == null) ? i : v.hashCode();
      hc ^= (hc2 << i);
    }
    return hc;
  }


  
  public int objectIndexOf(Tree tree) {
    Tree[] kids = children();
    for (int i = 0; i < kids.length; i++) {
      if (kids[i] == tree) {
        return i;
      }
    }
    return -1;
  }


  
  public abstract Tree[] children();


  
  public List<Tree> getChildrenAsList() {
    return new ArrayList<>(Arrays.asList(children()));
  }


  /**
   * Set the children of this node to be the children given in the
   * array.  This is an <b>optional</b> operation; by default it is
   * unsupported.  Note for subclasses that if there are no
   * children, the children() method must return a Tree[] array of
   * length 0.  This class provides a
   * {@code EMPTY_TREE_ARRAY} canonical zero-length Tree[] array
   * to represent zero children, but it is <i>not</i> required that
   * leaf nodes use this particular zero-length array to represent
   * a leaf node.
   *
   * @param children The array of children, each a {@code Tree}
   * @see #setChildren(List)
   */
  public void setChildren(Tree[] children) {
    throw new UnsupportedOperationException();
  }


  
  public void setChildren(List<? extends Tree> childTreesList) {
    if (childTreesList == null || childTreesList.isEmpty()) {
      setChildren(EMPTY_TREE_ARRAY);
    } else {
      Tree[] childTrees = new Tree[childTreesList.size()];
      childTreesList.toArray(childTrees);
      setChildren(childTrees);
    }
  }


  
  @Override
  public Label label() {
    return null;
  }


  
  @Override
  public void setLabel(Label label) {
    // a noop
  }


  
  @Override
  public double score() {
    return Double.NaN;
  }


  
  public void setScore(double score) {
    throw new UnsupportedOperationException("You must use a tree type that implements scoring in order call setScore()");
  }


  
  public Tree firstChild() {
    Tree[] kids = children();
    if (kids.length == 0) {
      return null;
    }
    return kids[0];
  }


  
  public Tree lastChild() {
    Tree[] kids = children();
    if (kids.length == 0) {
      return null;
    }
    return kids[kids.length - 1];
  }

  /** Return the highest node of the (perhaps trivial) unary chain that
   *  this node is part of.
   *  In case this node is the only child of its parent, trace up the chain of
   *  unaries, and return the uppermost node of the chain (the node whose
   *  parent has multiple children, or the node that is the root of the tree).
   *
   *  @param root The root of the tree that contains this subtree
   *  @return The uppermost node of the unary chain, if this node is in a unary
   *         chain, or else the current node
   */
  public Tree upperMostUnary(Tree root) {
    Tree parent = parent(root);
    if (parent == null) {
      return this;
    }
    if (parent.numChildren() > 1) {
      return this;
    }
    return parent.upperMostUnary(root);
  }



  
  public Set<Constituent> constituents() {
    return constituents(new SimpleConstituentFactory());
  }


  
  public Set<Constituent> constituents(ConstituentFactory cf) {
    return constituents(cf,false);
  }

  
  public Set<Constituent> constituents(ConstituentFactory cf, int maxDepth) {
    Set<Constituent> constituentsSet = Generics.newHashSet();
    constituents(constituentsSet, 0, cf, false, null, maxDepth, 0);
    return constituentsSet;
  }

  
  public Set<Constituent> constituents(ConstituentFactory cf, boolean charLevel) {
    Set<Constituent> constituentsSet = Generics.newHashSet();
    constituents(constituentsSet, 0, cf, charLevel, null, -1, 0);
    return constituentsSet;
  }

  public Set<Constituent> constituents(ConstituentFactory cf, boolean charLevel, Predicate<Tree> filter) {
    Set<Constituent> constituentsSet = Generics.newHashSet();
    constituents(constituentsSet, 0, cf, charLevel, filter, -1, 0);
    return constituentsSet;
  }

  
  private int constituents(Set<Constituent> constituentsSet, int left, ConstituentFactory cf, boolean charLevel, Predicate<Tree> filter, int maxDepth, int depth) {

    if(isPreTerminal())
      return left + ((charLevel) ? firstChild().value().length() : 1);

    int position = left;

    // log.info("In bracketing trees left is " + left);
    // log.info("  label is " + label() +
    //                       "; num daughters: " + children().length);
    Tree[] kids = children();
    for (Tree kid : kids) {
      position = kid.constituents(constituentsSet, position, cf, charLevel, filter, maxDepth, depth + 1);
      // log.info("  position went to " + position);
    }

    if ((filter == null || filter.test(this)) &&
        (maxDepth < 0 || depth <= maxDepth)) {
      //Compute span of entire tree at the end of recursion
      constituentsSet.add(cf.newConstituent(left, position - 1, label(), score()));
    }
    // log.info("  added " + label());
    return position;
  }


  
  public Tree localTree() {
    Tree[] kids = children();
    Tree[] newKids = new Tree[kids.length];
    TreeFactory tf = treeFactory();
    for (int i = 0, n = kids.length; i < n; i++) {
      newKids[i] = tf.newTreeNode(kids[i].label(), Arrays.asList(EMPTY_TREE_ARRAY));
    }
    return tf.newTreeNode(label(), Arrays.asList(newKids));
  }


  
  public Set<Tree> localTrees() {
    Set<Tree> set = Generics.newHashSet();
    for (Tree st : this) {
      if (st.isPhrasal()) {
        set.add(st.localTree());
      }
    }
    return set;
  }


  
  private static final int initialPrintStringBuilderSize = 500;

  
  public StringBuilder toStringBuilder(StringBuilder sb) {
    return toStringBuilder(sb, label -> (label.value() == null) ? "": label.value());
  }

  
  public StringBuilder toStringBuilder(StringBuilder sb, Function<Label,String> labelFormatter) {
    if (isLeaf()) {
      if (label() != null) {
        sb.append(labelFormatter.apply(label()));
      }
      return sb;
    } else {
      sb.append('(');
      if (label() != null) {
        sb.append(labelFormatter.apply(label()));
      }
      Tree[] kids = children();
      if (kids != null) {
        for (Tree kid : kids) {
          sb.append(' ');
          kid.toStringBuilder(sb, labelFormatter);
        }
      }
      return sb.append(')');
    }
  }


  
  @Override
  public String toString() {
    return toStringBuilder(new StringBuilder(Tree.initialPrintStringBuilderSize)).toString();
  }


  private static final int indentIncr = 2;


  private static String makeIndentString(int indent) {
    StringBuilder sb = new StringBuilder(indent);
    for (int i = 0; i < indentIncr; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }


  public void printLocalTree() {
    printLocalTree(new PrintWriter(System.out, true));
  }

  
  public void printLocalTree(PrintWriter pw) {
    pw.print("(" + label() + ' ');
    for (Tree kid : children()) {
      pw.print("(");
      pw.print(kid.label());
      pw.print(") ");
    }
    pw.println(")");
  }


  
  public void indentedListPrint() {
    indentedListPrint(new PrintWriter(System.out, true), false);
  }


  
  public void indentedListPrint(PrintWriter pw, boolean printScores) {
    indentedListPrint("", makeIndentString(indentIncr), pw, printScores);
  }


  
  private void indentedListPrint(String indent, String pad, PrintWriter pw, boolean printScores) {
    StringBuilder sb = new StringBuilder(indent);
    Label label = label();
    if (label != null) {
      sb.append(label);
    }
    if (printScores) {
      sb.append("  ");
      sb.append(score());
    }
    pw.println(sb);
    Tree[] children = children();
    String newIndent = indent + pad;
    for (Tree child : children) {
      child.indentedListPrint(newIndent, pad, pw, printScores);
    }
  }

  
  public void indentedXMLPrint() {
    indentedXMLPrint(new PrintWriter(System.out, true), false);
  }


  
  public void indentedXMLPrint(PrintWriter pw, boolean printScores) {
    indentedXMLPrint("", makeIndentString(indentIncr), pw, printScores);
  }


  
  private void indentedXMLPrint(String indent, String pad,
                                PrintWriter pw, boolean printScores) {
  }


  private static void displayChildren(Tree[] trChildren, int indent, boolean parentLabelNull,
                                      Function<Label,String> labelFormatter, PrintWriter pw) {
    boolean firstSibling = true;
    boolean leftSibIsPreTerm = true;  // counts as true at beginning
    for (Tree currentTree : trChildren) {
      currentTree.display(indent, parentLabelNull, firstSibling, leftSibIsPreTerm, false, labelFormatter, pw);
      leftSibIsPreTerm = currentTree.isPreTerminal();
      // CC is a special case for English, but leave it in so we can exactly match PTB3 tree formatting
      if (currentTree.value() != null && currentTree.value().startsWith("CC")) {
        leftSibIsPreTerm = false;
      }
      firstSibling = false;
    }
  }

  
  public String nodeString() {
    return (value() == null) ? "" : value();
  }

  
  private void display(int indent, boolean parentLabelNull, boolean firstSibling, boolean leftSiblingPreTerminal, boolean topLevel, Function<Label,String> labelFormatter, PrintWriter pw) {
    // the condition for staying on the same line in Penn Treebank
    boolean suppressIndent = (parentLabelNull || (firstSibling && isPreTerminal()) || (leftSiblingPreTerminal && isPreTerminal() && (label() == null || !label().value().startsWith("CC"))));
    if (suppressIndent) {
      pw.print(" ");
      // pw.flush();
    } else {
      if (!topLevel) {
        pw.println();
      }
      for (int i = 0; i < indent; i++) {
        pw.print("  ");
        // pw.flush();
      }
    }
    if (isLeaf() || isPreTerminal()) {
      String terminalString = toStringBuilder(new StringBuilder(), labelFormatter).toString();
      pw.print(terminalString);
      pw.flush();
      return;
    }
    pw.print("(");
    pw.print(labelFormatter.apply(label()));
    // pw.flush();
    boolean parentIsNull = label() == null || label().value() == null;
    displayChildren(children(), indent + 1, parentIsNull, labelFormatter, pw);
    pw.print(")");
    pw.flush();
  }

  
  public void pennPrint(PrintWriter pw) {
    pennPrint(pw, label -> (label.value() == null) ? "": label.value());
  }

  public void pennPrint(PrintWriter pw, Function<Label,String> labelFormatter) {
    display(0, false, false, false, true, labelFormatter, pw);
    pw.println();
    pw.flush();
  }


  
  public void pennPrint(PrintStream ps) {
    pennPrint(new PrintWriter(new OutputStreamWriter(ps), true));
  }

  public void pennPrint(PrintStream ps, Function<Label,String> labelFormatter) {
    pennPrint(new PrintWriter(new OutputStreamWriter(ps), true), labelFormatter);
  }

  
  public String pennString() {
    StringWriter sw = new StringWriter();
    pennPrint(new PrintWriter(sw));
    return sw.toString();
  }

  
  public void pennPrint() {
    pennPrint(System.out);
  }


  
  public int depth() {
    if (isLeaf()) {
      return 0;
    }
    int maxDepth = 0;
    Tree[] kids = children();
    for (Tree kid : kids) {
      int curDepth = kid.depth();
      if (curDepth > maxDepth) {
        maxDepth = curDepth;
      }
    }
    return maxDepth + 1;
  }

  
  public int depth(Tree node) {
    Tree p = node.parent(this);
    if (this == node) { return 0; }
    if (p == null) { return -1; }
    int depth = 1;
    while (this != p) {
      p = p.parent(this);
      depth++;
    }
    return depth;
  }


  
  public Tree headTerminal(HeadFinder hf, Tree parent) {
    if (isLeaf()) {
      return this;
    }
    Tree head = hf.determineHead(this, parent);
    if (head != null) {
      return head.headTerminal(hf, parent);
    }
    return null;
  }

  
  public Tree headTerminal(HeadFinder hf) {
    return headTerminal(hf, null);
  }


  
  public Tree headPreTerminal(HeadFinder hf) {
    if (isPreTerminal()) {
      return this;
    } else if (isLeaf()) {
      throw new IllegalArgumentException("Called headPreTerminal on a leaf: " + this);
    } else {
      Tree head = hf.determineHead(this);
      if (head != null) {
        return head.headPreTerminal(hf);
      }
      return null;
    }
  }

  
  public void percolateHeadAnnotations(HeadFinder hf) {
    if (!(label() instanceof CoreLabel)) {
      throw new IllegalArgumentException("Expected CoreLabels in the trees");
    }
    CoreLabel nodeLabel = (CoreLabel) label();

    if (isLeaf()) {
      return;
    }

    if (isPreTerminal()) {
      nodeLabel.set(TreeCoreAnnotations.HeadWordLabelAnnotation.class, (CoreLabel) children()[0].label());
      nodeLabel.set(TreeCoreAnnotations.HeadTagLabelAnnotation.class, nodeLabel);
      return;
    }

    for (Tree kid : children()) {
      kid.percolateHeadAnnotations(hf);
    }

    final Tree head = hf.determineHead(this);
    if (head == null) {
      throw new NullPointerException("HeadFinder " + hf + " returned null for " + this);
    } else if (head.isLeaf()) {
      nodeLabel.set(TreeCoreAnnotations.HeadWordLabelAnnotation.class, (CoreLabel) head.label());
      nodeLabel.set(TreeCoreAnnotations.HeadTagLabelAnnotation.class, (CoreLabel) head.parent(this).label());
    } else if (head.isPreTerminal()) {
      nodeLabel.set(TreeCoreAnnotations.HeadWordLabelAnnotation.class, (CoreLabel) head.children()[0].label());
      nodeLabel.set(TreeCoreAnnotations.HeadTagLabelAnnotation.class, (CoreLabel) head.label());
    } else {
      if (!(head.label() instanceof CoreLabel)) {
        throw new AssertionError("Horrible bug");
      }
      CoreLabel headLabel = (CoreLabel) head.label();
      nodeLabel.set(TreeCoreAnnotations.HeadWordLabelAnnotation.class, headLabel.get(TreeCoreAnnotations.HeadWordLabelAnnotation.class));
      nodeLabel.set(TreeCoreAnnotations.HeadTagLabelAnnotation.class, headLabel.get(TreeCoreAnnotations.HeadTagLabelAnnotation.class));
    }
  }


  
  public void percolateHeads(HeadFinder hf) {
    Label nodeLabel = label();
    if (isLeaf()) {
      // Sanity check: word() is usually set by the TreeReader.
      if (nodeLabel instanceof HasWord) {
        HasWord w = (HasWord) nodeLabel;
        if (w.word() == null) {
          w.setWord(nodeLabel.value());
        }
      }

    } else {
      for (Tree kid : children()) {
        kid.percolateHeads(hf);
      }

      final Tree head = hf.determineHead(this);
      if (head != null) {
        final Label headLabel = head.label();

        // Set the head tag.
        String headTag = (headLabel instanceof HasTag) ? ((HasTag) headLabel).tag() : null;
        if (headTag == null && head.isLeaf()) {
          // below us is a leaf
          headTag = nodeLabel.value();
        }

        // Set the head word
        String headWord = (headLabel instanceof HasWord) ? ((HasWord) headLabel).word() : null;
        if (headWord == null && head.isLeaf()) {
          // below us is a leaf
          // this might be useful despite case for leaf above in
          // case the leaf label type doesn't support word()
          headWord = headLabel.value();
        }

        // Set the head index
        int headIndex = (headLabel instanceof HasIndex) ? ((HasIndex) headLabel).index() : -1;

        if (nodeLabel instanceof HasWord) {
          ((HasWord) nodeLabel).setWord(headWord);
        }
        if (nodeLabel instanceof HasTag) {
          ((HasTag) nodeLabel).setTag(headTag);
        }
        if (nodeLabel instanceof HasIndex && headIndex >= 0) {
          ((HasIndex) nodeLabel).setIndex(headIndex);
        }

      } else {
      }
    }
  }

  
  public Set<Dependency<Label, Label, Object>> dependencies() {
    return dependencies(Filters.acceptFilter());
  }

  public Set<Dependency<Label, Label, Object>> dependencies(Predicate<Dependency<Label, Label, Object>> f) {
    return dependencies(f, true, true, false);
  }

  
  private static Label makeDependencyLabel(Label oldLabel, boolean copyLabel, boolean copyIndex, boolean copyPosTag) {
    if ( ! copyLabel)
      return oldLabel;

    String wordForm = (oldLabel instanceof HasWord) ? ((HasWord) oldLabel).word() : oldLabel.value();
    Label newLabel = oldLabel.labelFactory().newLabel(wordForm);
    if (newLabel instanceof HasWord) ((HasWord) newLabel).setWord(wordForm);
    if (copyPosTag && newLabel instanceof HasTag && oldLabel instanceof HasTag) {
      String tag = ((HasTag) oldLabel).tag();
      ((HasTag) newLabel).setTag(tag);
    }
    if (copyIndex && newLabel instanceof HasIndex && oldLabel instanceof HasIndex) {
      int index = ((HasIndex) oldLabel).index();
      ((HasIndex) newLabel).setIndex(index);
    }

    return newLabel;
  }

  
  public Set<Dependency<Label, Label, Object>> dependencies(Predicate<Dependency<Label, Label, Object>> f, boolean isConcrete, boolean copyLabel, boolean copyPosTag) {
    Set<Dependency<Label, Label, Object>> deps = Generics.newHashSet();
    for (Tree node : this) {
      // Skip leaves and unary re-writes
      if (node.isLeaf() || node.children().length < 2) {
        continue;
      }
      // Create the head label (percolateHeads has already been executed)
      Label headLabel = makeDependencyLabel(node.label(), copyLabel, isConcrete, copyPosTag);
      String headWord = ((HasWord) headLabel).word();
      if (headWord == null) {
        headWord = headLabel.value();
      }
      int headIndex = (isConcrete && (headLabel instanceof HasIndex)) ? ((HasIndex) headLabel).index() : -1;

      // every child with a different (or repeated) head is an argument
      boolean seenHead = false;
      for (Tree child : node.children()) {
        Label depLabel = makeDependencyLabel(child.label(), copyLabel, isConcrete, copyPosTag);
        String depWord = ((HasWord) depLabel).word();
        if (depWord == null) {
          depWord = depLabel.value();
        }
        int depIndex = (isConcrete && (depLabel instanceof HasIndex)) ? ((HasIndex) depLabel).index() : -1;

        if (!seenHead && headIndex == depIndex && headWord.equals(depWord)) {
          seenHead = true;
        } else {
          Dependency<Label, Label, Object> dependency = (isConcrete && depIndex != headIndex) ?
              new UnnamedConcreteDependency(headLabel, depLabel) :
              new UnnamedDependency(headLabel, depLabel);

          if (f.test(dependency)) {
            deps.add(dependency);
          }
        }
      }
    }
    return deps;
  }

  
  public Set<Dependency<Label, Label, Object>> mapDependencies(Predicate<Dependency<Label, Label, Object>> f, HeadFinder hf) {
    if (hf == null) {
      throw new IllegalArgumentException("mapDependencies: need HeadFinder");
    }
    Set<Dependency<Label, Label, Object>> deps = Generics.newHashSet();
    for (Tree node : this) {
      if (node.isLeaf() || node.children().length < 2) {
        continue;
      }
      // Label l = node.label();
      // log.info("doing kids of label: " + l);
      //Tree hwt = node.headPreTerminal(hf);
      Tree hwt = node.headTerminal(hf);
      // log.info("have hf, found head preterm: " + hwt);
      if (hwt == null) {
        throw new IllegalStateException("mapDependencies: HeadFinder failed!");
      }

      for (Tree child : node.children()) {
        // Label dl = child.label();
        // Tree dwt = child.headPreTerminal(hf);
        Tree dwt = child.headTerminal(hf);
        if (dwt == null) {
          throw new IllegalStateException("mapDependencies: HeadFinder failed!");
        }
        //log.info("kid is " + dl);
         //log.info("transformed to " + dml.toString("value{map}"));
        if (dwt != hwt) {
          Dependency<Label, Label, Object> p = new UnnamedDependency(hwt.label(), dwt.label());
          if (f.test(p)) {
            deps.add(p);
          }
        }
      }
    }
    return deps;
  }

  
  public Set<Dependency<Label, Label, Object>> mapDependencies(Predicate<Dependency<Label, Label, Object>> f, HeadFinder hf, String rootName) {
    Set<Dependency<Label, Label, Object>> deps = mapDependencies(f, hf);
    if(rootName != null) {
      Label hl = headTerminal(hf).label();
      CoreLabel rl = new CoreLabel();
      rl.set(CoreAnnotations.TextAnnotation.class, rootName);
      rl.set(CoreAnnotations.IndexAnnotation.class, 0);
      deps.add(new NamedDependency(rl, hl, rootName));
    }
    return deps;
  }

  
  public ArrayList<Label> yield() {
    return yield(new ArrayList<>());
  }

  /**
   * Gets the yield of the tree.  The {@code Label} of all leaf nodes
   * is returned
   * as a list ordered by the natural left to right order of the
   * leaves.  Null values, if any, are inserted into the list like any
   * other value.
   * <p><i>Implementation notes:</i> c. 2003: This has been rewritten to thread, so only one List
   * is used. 2007: This method was duplicated to start to give type safety to Sentence.
   * This method will now make a Word for any Leaf which does not itself implement HasWord, and
   * put the Word into the Sentence, so the Sentence elements MUST implement HasWord.
   *
   * @param y The list in which the yield of the tree will be placed.
   *          Normally, this will be empty when the routine is called, but
   *          if not, the new yield is added to the end of the list.
   * @return a {@code List} of the data in the tree's leaves.
   */
  public ArrayList<Label> yield(ArrayList<Label> y) {
    if (isLeaf()) {
      y.add(label());

    } else {
      Tree[] kids = children();
      for (Tree kid : kids) {
        kid.yield(y);
      }
    }
    return y;
  }

  public ArrayList<Word> yieldWords() {
    return yieldWords(new ArrayList<>());
  }

  public ArrayList<Word> yieldWords(ArrayList<Word> y) {
    if (isLeaf()) {
      y.add(new Word(label()));
    } else {
      for (Tree kid : children()) {
        kid.yieldWords(y);
      }
    }
    return y;
  }

  public <X extends HasWord> ArrayList<X> yieldHasWord() {
    return yieldHasWord(new ArrayList<>());
  }

  @SuppressWarnings("unchecked")
  public <X extends HasWord> ArrayList<X> yieldHasWord(ArrayList<X> y) {
    if (isLeaf()) {
      Label lab = label();
      // cdm: this is new hacked in stuff in Mar 2007 so we can now have a
      // well-typed version of a Sentence, whose objects MUST implement HasWord
      //
      // wsg (Feb. 2010) - More hacks for trees with CoreLabels in which the type implements
      // HasWord but only the value field is populated. This can happen if legacy code uses
      // LabeledScoredTreeFactory but passes in a StringLabel to e.g. newLeaf().
      if (lab instanceof HasWord) {
        if(lab instanceof CoreLabel) {
          CoreLabel cl = (CoreLabel) lab;
          if(cl.word() == null)
            cl.setWord(cl.value());
          y.add((X) cl);
        } else {
          y.add((X) lab);
        }

      } else {
        y.add((X) new Word(lab));
      }

    } else {
      Tree[] kids = children();
      for (Tree kid : kids) {
        kid.yield(y);
      }
    }
    return y;
  }


  
  @SuppressWarnings("unchecked")
  public <T> List<T> yield(List<T> y) {
    if (isLeaf()) {
      if(label() instanceof HasWord) {
        HasWord hw = (HasWord) label();
        hw.setWord(label().value());
      }
      y.add((T) label());

    } else {
      Tree[] kids = children();
      for (Tree kid : kids) {
        kid.yield(y);
      }
    }
    return y;
  }

  
  public ArrayList<TaggedWord> taggedYield() {
    return taggedYield(new ArrayList<>());
  }

  public List<LabeledWord> labeledYield() {
    return labeledYield(new ArrayList<>());
  }

  /**
   * Gets the tagged yield of the tree -- that is, get the preterminals
   * as well as the terminals.  The {@code Label} of all leaf nodes
   * is returned
   * as a list ordered by the natural left to right order of the
   * leaves.  Null values, if any, are inserted into the list like any
   * other value.  This has been rewritten to thread, so only one List
   * is used.
   * <p/>
   * <i>Implementation note:</i> when we summon up enough courage, this
   * method will be changed to take and return a {@code List<W extends TaggedWord>}.
   *
   * @param ty The list in which the tagged yield of the tree will be
   *           placed. Normally, this will be empty when the routine is called,
   *           but if not, the new yield is added to the end of the list.
   * @return a {@code List} of the data in the tree's leaves.
   */
  public <X extends List<TaggedWord>> X taggedYield(X ty) {
    if (isPreTerminal()) {
      ty.add(new TaggedWord(firstChild().label(), label()));
    } else {
      for (Tree kid : children()) {
        kid.taggedYield(ty);
      }
    }
    return ty;
  }

  public List<LabeledWord> labeledYield(List<LabeledWord> ty) {
    if (isPreTerminal()) {
      ty.add(new LabeledWord(firstChild().label(), label()));
    } else {
      for (Tree kid : children()) {
        kid.labeledYield(ty);
      }
    }
    return ty;
  }

  /** Returns a {@code List<CoreLabel>} from the tree.
   *  These are a copy of the complete token representation
   *  that adds the tag as the tag and value.
   *
   *  @return A tagged, labeled yield.
   */
  public List<CoreLabel> taggedLabeledYield() {
    List<CoreLabel> ty = new ArrayList<>();
    taggedLabeledYield(ty, 0);
    return ty;
  }

  private int taggedLabeledYield(List<CoreLabel> ty, int termIdx) {
    if (isPreTerminal()) {
      // usually this will fill in all the usual keys for a token
      CoreLabel taggedWord = new CoreLabel(firstChild().label());
      // but in case this just came from reading a tree that just has a value for words
      if (taggedWord.word() == null) {
        taggedWord.setWord(firstChild().value());
      }
      final String tag = (value() == null) ? "" : value();
      // set value and tag to the tag
      taggedWord.setValue(tag);
      taggedWord.setTag(tag);
      taggedWord.setIndex(termIdx);
      ty.add(taggedWord);

      return termIdx + 1;

    } else {
      for (Tree kid : getChildrenAsList())
        termIdx = kid.taggedLabeledYield(ty, termIdx);
    }

    return termIdx;
  }

  
  public List<Label> preTerminalYield() {
    return preTerminalYield(new ArrayList<>());
  }


  
  public List<Label> preTerminalYield(List<Label> y) {
    if (isPreTerminal()) {
      y.add(label());
    } else {
      Tree[] kids = children();
      for (Tree kid : kids) {
        kid.preTerminalYield(y);
      }
    }
    return y;
  }

  
  public <T extends Tree> List<T> getLeaves() {
    return getLeaves(new ArrayList<>());
  }

  
  @SuppressWarnings("unchecked")
  public <T extends Tree> List<T> getLeaves(List<T> list) {
    if (isLeaf()) {
      list.add((T)this);
    } else {
      for (Tree kid : children()) {
        kid.getLeaves(list);
      }
    }
    return list;
  }


  
  @Override
  public Collection<Label> labels() {
    Set<Label> n = Generics.newHashSet();
    n.add(label());
    Tree[] kids = children();
    for (Tree kid : kids) {
      n.addAll(kid.labels());
    }
    return n;
  }


  @Override
  public void setLabels(Collection<Label> c) {
    throw new UnsupportedOperationException("Can't set Tree labels");
  }


  
  public Tree flatten() {
    return flatten(treeFactory());
  }

  
  public Tree flatten(TreeFactory tf) {
    if (isLeaf() || isPreTerminal()) {
      return this;
    }
    Tree[] kids = children();
    List<Tree> newChildren = new ArrayList<>(kids.length);
    for (Tree child : kids) {
      if (child.isLeaf() || child.isPreTerminal()) {
        newChildren.add(child);
      } else {
        Tree newChild = child.flatten(tf);
        if (label().equals(newChild.label())) {
          newChildren.addAll(newChild.getChildrenAsList());
        } else {
          newChildren.add(newChild);
        }
      }
    }
    return tf.newTreeNode(label(), newChildren);
  }


  /**
   * Get the set of all subtrees inside the tree by returning a tree
   * rooted at each node.  These are <i>not</i> copies, but all share
   * structure.  The tree is regarded as a subtree of itself.
   *
   * <i>Note:</i> If you only want to form this Set so that you can
   * iterate over it, it is more efficient to simply use the Tree class's
   * own {@code iterator()} method. This will iterate over the exact same
   * elements (but perhaps/probably in a different order).
   *
   * @return the {@code Set} of all subtrees in the tree.
   */
  public Set<Tree> subTrees() {
    return subTrees(Generics.newHashSet());
  }

  /**
   * Get the list of all subtrees inside the tree by returning a tree
   * rooted at each node.  These are <i>not</i> copies, but all share
   * structure.  The tree is regarded as a subtree of itself.
   *
   * <i>Note:</i> If you only want to form this Collection so that you can
   * iterate over it, it is more efficient to simply use the Tree class's
   * own {@code iterator()} method. This will iterate over the exact same
   * elements (but perhaps/probably in a different order).
   *
   * @return the {@code List} of all subtrees in the tree.
   */
  public List<Tree> subTreeList() {
    return subTrees(new ArrayList<>());
  }


  /**
   * Add the set of all subtrees inside a tree (including the tree itself)
   * to the given {@code Collection}.
   *
   * <i>Note:</i> If you only want to form this Collection so that you can
   * iterate over it, it is more efficient to simply use the Tree class's
   * own {@code iterator()} method. This will iterate over the exact same
   * elements (but perhaps/probably in a different order).
   *
   * @param n A collection of nodes to which the subtrees will be added.
   * @return The collection parameter with the subtrees added.
   */
  public <T extends Collection<Tree>> T subTrees(T n) {
    n.add(this);
    Tree[] kids = children();
    for (Tree kid : kids) {
      kid.subTrees(n);
    }
    return n;
  }

  
  public Tree deepCopy() {
    return deepCopy(treeFactory());
  }


  
  public Tree deepCopy(TreeFactory tf) {
    return deepCopy(tf, label().labelFactory());
  }


  

  @SuppressWarnings({"unchecked"})
  public Tree deepCopy(TreeFactory tf, LabelFactory lf) {
    Label label = lf.newLabel(label());
    if (isLeaf()) {
      return tf.newLeaf(label);
    }
    Tree[] kids = children();
    // NB: The below list may not be of type Tree but TreeGraphNode, so we leave it untyped
    List newKids = new ArrayList(kids.length);
    for (Tree kid : kids) {
      newKids.add(kid.deepCopy(tf, lf));
    }
    return tf.newTreeNode(label, newKids);
  }


  
  public Tree treeSkeletonCopy() {
    return treeSkeletonCopy(treeFactory());
  }


  
  public Tree treeSkeletonCopy(TreeFactory tf) {
    Tree t;
    if (isLeaf()) {
      t = tf.newLeaf(label());
    } else {
      Tree[] kids = children();
      List<Tree> newKids = new ArrayList<>(kids.length);
      for (Tree kid : kids) {
        newKids.add(kid.treeSkeletonCopy(tf));
      }
      t = tf.newTreeNode(label(), newKids);
    }
    return t;
  }

  
  public Tree treeSkeletonConstituentCopy() {
    return treeSkeletonConstituentCopy(treeFactory(), label().labelFactory());
  }

  public Tree treeSkeletonConstituentCopy(TreeFactory tf, LabelFactory lf) {
    if (isLeaf()) {
      // Reuse the current label for a leaf.  This way, trees which
      // are based on tokens in a sentence can have the same tokens
      // even after a "deep copy".
      // TODO: the LabeledScoredTreeFactory copies the label for a new
      // leaf.  Perhaps we could add a newLeafNoCopy or something like
      // that for efficiency.
      Tree newLeaf = tf.newLeaf(label());
      newLeaf.setLabel(label());
      return newLeaf;
    }
    Label label = lf.newLabel(label());
    Tree[] kids = children();
    List<Tree> newKids = new ArrayList<>(kids.length);
    for (Tree kid : kids) {
      newKids.add(kid.treeSkeletonConstituentCopy(tf, lf));
    }
    return tf.newTreeNode(label, newKids);
  }

  
  public Tree transform(final TreeTransformer transformer) {
    return transform(transformer, treeFactory());
  }


  
  public Tree transform(final TreeTransformer transformer, final TreeFactory tf) {
    Tree t;
    if (isLeaf()) {
      t = tf.newLeaf(label());
    } else {
      Tree[] kids = children();
      List<Tree> newKids = new ArrayList<>(kids.length);
      for (Tree kid : kids) {
        newKids.add(kid.transform(transformer, tf));
      }
      t = tf.newTreeNode(label(), newKids);
    }
    return transformer.transformTree(t);
  }


  
  public Tree spliceOut(final Predicate<Tree> nodeFilter) {
    return spliceOut(nodeFilter, treeFactory());
  }


  
  public Tree spliceOut(final Predicate<Tree> nodeFilter, final TreeFactory tf) {
    List<Tree> l = spliceOutHelper(nodeFilter, tf);
    if (l.isEmpty()) {
      return null;
    } else if (l.size() == 1) {
      return l.get(0);
    }
    // for a forest, make a new root
    return tf.newTreeNode((Label) null, l);
  }


  private List<Tree> spliceOutHelper(Predicate<Tree> nodeFilter, TreeFactory tf) {
    // recurse over all children first
    Tree[] kids = children();
    List<Tree> l = new ArrayList<>();
    for (Tree kid : kids) {
      l.addAll(kid.spliceOutHelper(nodeFilter, tf));
    }
    // check if this node is being spliced out
    if (nodeFilter.test(this)) {
      // no, so add our children and return
      Tree t;
      if ( ! l.isEmpty()) {
        t = tf.newTreeNode(label(), l);
      } else {
        t = tf.newLeaf(label());
      }
      l = new ArrayList<>(1);
      l.add(t);
      return l;
    }
    // we're out, so return our children
    return l;
  }


  /**
   * Creates a deep copy of the tree, where all nodes that the filter
   * does not accept and all children of such nodes are pruned.  If all
   * of a node's children are pruned, that node is cut as well.
   * A {@code Filter} can assume
   * that it will not be called with a {@code null} argument.
   * <p/>
   * For example, the following code excises all PP nodes from a Tree: <br>
   * <tt>
   * Filter<Tree> f = new Filter<Tree> { <br>
   * public boolean accept(Tree t) { <br>
   * return ! t.label().value().equals("PP"); <br>
   * } <br>
   * }; <br>
   * tree.prune(f);
   * </tt> <br>
   *
   * If the root of the tree is pruned, null will be returned.
   *
   * @param filter the filter to be applied
   * @return a filtered copy of the tree, including the possibility of
   *         {@code null} if the root node of the tree is filtered
   */
  public Tree prune(final Predicate<Tree> filter) {
    return prune(filter, treeFactory());
  }


  
  public Tree prune(Predicate<Tree> filter, TreeFactory tf) {
    // is the current node to be pruned?
    if ( ! filter.test(this)) {
      return null;
    }
    // if not, recurse over all children
    List<Tree> l = new ArrayList<>();
    Tree[] kids = children();
    for (Tree kid : kids) {
      Tree prunedChild = kid.prune(filter, tf);
      if (prunedChild != null) {
        l.add(prunedChild);
      }
    }
    // and check if this node has lost all its children
    if (l.isEmpty() && !(kids.length == 0)) {
      return null;
    }
    // if we're still ok, copy the node
    if (isLeaf()) {
      return tf.newLeaf(label());
    }
    return tf.newTreeNode(label(), l);
  }

  
  public Tree skipRoot() {
    if(!isUnaryRewrite())
      return this;
    String lab = label().value();
    return (lab == null || lab.isEmpty() || "ROOT".equals(lab)) ? firstChild() : this;
  }

  
  public abstract TreeFactory treeFactory();


  
  public Tree parent() {
    throw new UnsupportedOperationException();
  }


  
  public Tree parent(Tree root) {
    Tree[] kids = root.children();
    return parentHelper(root, kids, this);
  }


  private static Tree parentHelper(Tree parent, Tree[] kids, Tree node) {
    for (Tree kid : kids) {
      if (kid == node) {
        return parent;
      }
      Tree ret = node.parent(kid);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }


  
  @Override
  public int size() {
    int size = 1;
    Tree[] kids = children();
    for (Tree kid : kids) {
      size += kid.size();
    }
    return size;
  }

  
  public Tree ancestor(int height, Tree root) {
    if (height < 0) {
      throw new IllegalArgumentException("ancestor: height cannot be negative");
    }
    if (height == 0) {
      return this;
    }
    Tree par = parent(root);
    if (par == null) {
      return null;
    }
    return par.ancestor(height - 1, root);
  }


  private static class TreeIterator implements Iterator<Tree> {

    private final List<Tree> treeStack;

    protected TreeIterator(Tree t) {
      treeStack = new ArrayList<>();
      treeStack.add(t);
    }

    @Override
    public boolean hasNext() {
      return (!treeStack.isEmpty());
    }

    @Override
    public Tree next() {
      int lastIndex = treeStack.size() - 1;
      if (lastIndex < 0) {
        throw new NoSuchElementException("TreeIterator exhausted");
      }
      Tree tr = treeStack.remove(lastIndex);
      Tree[] kids = tr.children();
      // so that we can efficiently use one List, we reverse them
      for (int i = kids.length - 1; i >= 0; i--) {
        treeStack.add(kids[i]);
      }
      return tr;
    }

    
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "TreeIterator";
    }

  }


  
  @Override
  public Iterator<Tree> iterator() {
    return new TreeIterator(this);
  }

  public List<Tree> postOrderNodeList() {
    List<Tree> nodes = new ArrayList<>();
    postOrderRecurse(this, nodes);
    return nodes;
  }
  private static void postOrderRecurse(Tree t, List<Tree> nodes) {
    for (Tree c : t.children()) {
      postOrderRecurse(c, nodes);
    }
    nodes.add(t);
  }

  public List<Tree> preOrderNodeList() {
    List<Tree> nodes = new ArrayList<>();
    preOrderRecurse(this, nodes);
    return nodes;
  }
  private static void preOrderRecurse(Tree t, List<Tree> nodes) {
    nodes.add(t);
    for (Tree c : t.children()) {
      preOrderRecurse(c, nodes);
    }
  }

  
  public static Tree valueOf(String str) {
      return valueOf(str, new LabeledScoredTreeReaderFactory());
  }

  
  public static Tree valueOf(String str, TreeReaderFactory trf) {
    try {
      return trf.newTreeReader(new StringReader(str)).readTree();
    } catch (IOException ioe) {
      throw new RuntimeException("Tree.valueOf() tree construction failed", ioe);
    }
  }


  
  public Tree getChild(int i) {
    Tree[] kids = children();
    return kids[i];
  }

  
  public Tree removeChild(int i) {
    Tree[] kids = children();
    Tree kid = kids[i];
    Tree[] newKids = new Tree[kids.length - 1];
    for (int j = 0; j < newKids.length; j++) {
      if (j < i) {
        newKids[j] = kids[j];
      } else {
        newKids[j] = kids[j + 1];
      }
    }
    setChildren(newKids);
    return kid;
  }

  
  public void addChild(int i, Tree t) {
    Tree[] kids = children();
    Tree[] newKids = new Tree[kids.length + 1];
    if (i != 0) {
      System.arraycopy(kids, 0, newKids, 0, i);
    }
    newKids[i] = t;
    if (i != kids.length) {
      System.arraycopy(kids, i, newKids, i + 1, kids.length - i);
    }
    setChildren(newKids);
  }

  
  public void addChild(Tree t) {
    addChild(children().length, t);
  }

  
  public Tree setChild(int i, Tree t) {
    Tree[] kids = children();
    Tree old = kids[i];
    kids[i] = t;
    return old;
  }

  
  public boolean dominates(Tree t) {
    List<Tree> dominationPath = dominationPath(t);
    return dominationPath != null && dominationPath.size() > 1;
  }

  
  public List<Tree> dominationPath(Tree t) {
    //Tree[] result = dominationPathHelper(t, 0);
    Tree[] result = dominationPath(t, 0);
    if (result == null) {
      return null;
    }
    return Arrays.asList(result);
  }

  private Tree[] dominationPathHelper(Tree t, int depth) {
    Tree[] kids = children();
    for (int i = kids.length - 1; i >= 0; i--) {
      Tree t1 = kids[i];
      if (t1 == null) {
        return null;
      }
      Tree[] result;
      if ((result = t1.dominationPath(t, depth + 1)) != null) {
        result[depth] = this;
        return result;
      }
    }
    return null;
  }

  private Tree[] dominationPath(Tree t, int depth) {
    if (this == t) {
      Tree[] result = new Tree[depth + 1];
      result[depth] = this;
      return result;
    }
    return dominationPathHelper(t, depth);
  }

  
  public List<Tree> pathNodeToNode(Tree t1, Tree t2) {
    if (!contains(t1) || !contains(t2)) {
      return null;
    }
    if (t1 == t2) {
      return Collections.singletonList(t1);
    }
    if (t1.dominates(t2)) {
      return t1.dominationPath(t2);
    }
    if (t2.dominates(t1)) {
      List<Tree> path = t2.dominationPath(t1);
      Collections.reverse(path);
      return path;
    }
    Tree joinNode = joinNode(t1, t2);
    if (joinNode == null) {
      return null;
    }
    List<Tree> t1DomPath = joinNode.dominationPath(t1);
    List<Tree> t2DomPath = joinNode.dominationPath(t2);
    if (t1DomPath == null || t2DomPath == null) {
      return null;
    }
    ArrayList<Tree> path = new ArrayList<>(t1DomPath);
    Collections.reverse(path);
    path.remove(joinNode);
    path.addAll(t2DomPath);
    return path;
  }

  
  public Tree joinNode(Tree t1, Tree t2) {
    if (!contains(t1) || !contains(t2)) {
      return null;
    }
    if (this == t1 || this == t2) {
      return this;
    }
    Tree joinNode = null;
    List<Tree> t1DomPath = dominationPath(t1);
    List<Tree> t2DomPath = dominationPath(t2);
    if (t1DomPath == null || t2DomPath == null) {
      return null;
    }
    Iterator<Tree> it1 = t1DomPath.iterator();
    Iterator<Tree> it2 = t2DomPath.iterator();
    while (it1.hasNext() && it2.hasNext()) {
      Tree n1 = it1.next();
      Tree n2 = it2.next();
      if (n1 != n2) {
        break;
      }
      joinNode = n1;
    }
    return joinNode;
  }

  
  public boolean cCommands(Tree t1, Tree t2) {
    List<Tree> sibs = t1.siblings(this);
    if (sibs == null) {
      return false;
    }
    for (Tree sib : sibs) {
      if (sib == t2 || sib.contains(t2)) {
        return true;
      }
    }
    return false;
  }

  
  public List<Tree> siblings(Tree root) {
    Tree parent = parent(root);
    if (parent == null) {
      return null;
    }
    List<Tree> siblings = parent.getChildrenAsList();
    siblings.remove(this);
    return siblings;
  }

  
  public void insertDtr(Tree dtr, int position) {
    Tree[] kids = children();
    if (position > kids.length) {
      throw new IllegalArgumentException("Can't insert tree after the " + position + "th daughter in " + this + "; only " + kids.length + " daughters exist!");
    }
    Tree[] newKids = new Tree[kids.length + 1];
    int i = 0;
    for (; i < position; i++) {
      newKids[i] = kids[i];
    }
    newKids[i] = dtr;
    for (; i < kids.length; i++) {
      newKids[i + 1] = kids[i];
    }
    setChildren(newKids);
  }

  // --- composition methods to implement Label interface

  @Override
  public String value() {
    Label lab = label();
    if (lab == null) {
      return null;
    }
    return lab.value();
  }


  @Override
  public void setValue(String value) {
    Label lab = label();
    if (lab != null) {
      lab.setValue(value);
    }
  }


  @Override
  public void setFromString(String labelStr) {
    Label lab = label();
    if (lab != null) {
      lab.setFromString(labelStr);
    }
  }

  
  @Override
  public LabelFactory labelFactory() {
    Label lab = label();
    if (lab == null) {
      return null;
    }
    return lab.labelFactory();
  }

  /**
   * Returns the positional index of the left edge of  <i>node</i> within the tree,
   * as measured by characters.  Returns -1 if <i>node is not found.</i>
   * Note: These methods were written for internal evaluation routines. They are
   * not the right methods to relate tree nodes to textual offsets. For these,
   * look at the appropriate annotations on a CoreLabel (CharacterOffsetBeginAnnotation, etc.).
   */
  public int leftCharEdge(Tree node) {
    MutableInteger i = new MutableInteger(0);
    if (leftCharEdge(node, i)) {
      return i.intValue();
    }
    return -1;
  }

  private boolean leftCharEdge(Tree node, MutableInteger i) {
    if (this == node) {
      return true;
    } else if (isLeaf()) {
      i.set(i.intValue() + value().length());
      return false;
    } else {
      for (Tree child : children()) {
        if (child.leftCharEdge(node, i)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Returns the positional index of the right edge of  <i>node</i> within the tree,
   * as measured by characters. Returns -1 if <i>node is not found.</i>
   *
   * rightCharEdge returns the index of the rightmost character + 1, so that
   * rightCharEdge(getLeaves().get(i)) == leftCharEdge(getLeaves().get(i+1))
   *
   * Note: These methods were written for internal evaluation routines. They are
   * not the right methods to relate tree nodes to textual offsets. For these,
   * look at the appropriate annotations on a CoreLabel (CharacterOffsetBeginAnnotation, etc.).
   *
   * @param node The subtree to look for in this Tree
   * @return The positional index of the right edge of node
   */
  public int rightCharEdge(Tree node) {
    List<Tree> s = getLeaves();
    int length = 0;
    for (Tree leaf : s) {
      length += leaf.label().value().length();
    }
    MutableInteger i = new MutableInteger(length);
    if (rightCharEdge(node, i)) {
      return i.intValue();
    }
    return -1;
  }

  private boolean rightCharEdge(Tree node, MutableInteger i) {
    if (this == node) {
      return true;
    } else if (isLeaf()) {
      i.set(i.intValue() - label().value().length());
      return false;
    } else {
      for (int j = children().length - 1; j >= 0; j--) {
        if (children()[j].rightCharEdge(node, i)) {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Calculates the node's <i>number</i>, defined as the number of nodes traversed in a left-to-right, depth-first search of the
   * tree starting at {@code root} and ending at {@code this}.  Returns -1 if {@code root} does not contain {@code this}.
   * @param root the root node of the relevant tree
   * @return the number of the current node, or -1 if {@code root} does not contain {@code this}.
   */
  public int nodeNumber(Tree root) {
    MutableInteger i = new MutableInteger(1);
    if(nodeNumberHelper(root,i))
      return i.intValue();
    return -1;
  }

  private boolean nodeNumberHelper(Tree t, MutableInteger i) {
    if (this == t) {
      return true;
    }
    i.incValue(1);
    for (Tree kid : t.children()) {
      if (nodeNumberHelper(kid, i))
        return true;
    }
    return false;
  }

  
  public Tree getNodeNumber(int i) {
    return getNodeNumberHelper(new MutableInteger(1),i);
  }

  private Tree getNodeNumberHelper(MutableInteger i, int target) {
    int i1 = i.intValue();
    if(i1 == target)
      return this;
    if(i1 > target)
      throw new IndexOutOfBoundsException("Error -- tree does not contain " + i + " nodes.");
    i.incValue(1);
    for (Tree kid : children()) {
      Tree temp = kid.getNodeNumberHelper(i, target);
      if(temp != null)
        return temp;
    }
    return null;
  }

  
  public void indexLeaves() {
    indexLeaves(1, false);
  }

  
  public void indexLeaves(boolean overWrite) {
    indexLeaves(1, overWrite);
  }

  
  public int indexLeaves(int startIndex, boolean overWrite) {
    if (isLeaf()) {

      /*CoreLabel afl = (CoreLabel) label();
      Integer oldIndex = afl.get(CoreAnnotations.IndexAnnotation.class);
      if (!overWrite && oldIndex != null && oldIndex >= 0) {
        startIndex = oldIndex;
      } else {
        afl.set(CoreAnnotations.IndexAnnotation.class, startIndex);
      }*/

      if(label() instanceof HasIndex) {
        HasIndex hi = (HasIndex) label();
        int oldIndex = hi.index();
        if (!overWrite && oldIndex >= 0) {
          startIndex = oldIndex;
        } else {
          hi.setIndex(startIndex);
        }
        startIndex++;
      }
    } else {
      for (Tree kid : children()) {
        startIndex = kid.indexLeaves(startIndex, overWrite);
      }
    }
    return startIndex;
  }

  
  public void percolateHeadIndices() {
    if (isPreTerminal()) {
      int nodeIndex = ((HasIndex) firstChild().label()).index();
      ((HasIndex) label()).setIndex(nodeIndex);
      return;
    }

    // Assign the head index to the first child that we encounter with a matching
    // surface form. Obviously a head can have the same surface form as its dependent,
    // and in this case the head index is ambiguous.
    String wordAnnotation = ((HasWord) label()).word();
    if (wordAnnotation == null) {
      wordAnnotation = value();
    }
    boolean seenHead = false;
    for (Tree child : children()) {
      child.percolateHeadIndices();
      String childWordAnnotation = ((HasWord) child.label()).word();
      if (childWordAnnotation == null) {
        childWordAnnotation = child.value();
      }
      if ( !seenHead && wordAnnotation.equals(childWordAnnotation)) {
        seenHead = true;
        int nodeIndex = ((HasIndex) child.label()).index();
        ((HasIndex) label()).setIndex(nodeIndex);
      }
    }
  }

  /** Index all spans (constituents) in the tree.
   *  For this, spans uses 0-based indexing and the span records the fencepost
   *  to the left of the first word and after the last word of the span.
   *  The spans are only recorded if the Tree has labels of a class which
   *  extends CoreMap.
   */
  public void indexSpans() {
    indexSpans(0);
  }

  public void indexSpans(int startIndex) {
    indexSpans(new MutableInteger(startIndex));
  }

  
  public Pair<Integer, Integer> indexSpans(MutableInteger startIndex) {
    int start = Integer.MAX_VALUE;
    int end = Integer.MIN_VALUE;

    if(isLeaf()){
      start = startIndex.intValue();
      end = startIndex.intValue() + 1;
      startIndex.incValue(1);
    } else {
      for (Tree kid : children()) {
        Pair<Integer, Integer>  span = kid.indexSpans(startIndex);
        if(span.first < start) start = span.first;
        if(span.second > end) end = span.second;
      }
    }

    Label label = label();
    if (label instanceof CoreMap) {
    CoreMap afl = (CoreMap) label();
      afl.set(CoreAnnotations.BeginIndexAnnotation.class, start);
      afl.set(CoreAnnotations.EndIndexAnnotation.class, end);
    }
    return new Pair<>(start, end);
  }

}
