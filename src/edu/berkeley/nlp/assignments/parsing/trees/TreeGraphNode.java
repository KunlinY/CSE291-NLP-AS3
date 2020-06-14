package edu.berkeley.nlp.assignments.parsing.trees; 

import edu.berkeley.nlp.assignments.parsing.ling.CoreLabel;
import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.ling.LabelFactory;

import java.util.List;


public class TreeGraphNode extends Tree implements HasParent  {


  
  private CoreLabel label;

  
  protected TreeGraphNode parent; // = null;


  
  protected TreeGraphNode[] children = ZERO_TGN_CHILDREN;

  
  private TreeGraphNode headWordNode;

  
  protected static final TreeGraphNode[] ZERO_TGN_CHILDREN = new TreeGraphNode[0];

  private static final LabelFactory mlf = CoreLabel.factory();


  
  public TreeGraphNode(Label label) {
    this.label = (CoreLabel) mlf.newLabel(label);
  }

  
  public TreeGraphNode(Label label, List<Tree> children) {
    this(label);
    setChildren(children);
  }

  
  protected TreeGraphNode(Tree t, TreeGraphNode parent) {
    this.parent = parent;
    Tree[] tKids = t.children();
    int numKids = tKids.length;
    children = new TreeGraphNode[numKids];
    for (int i = 0; i < numKids; i++) {
      children[i] = new TreeGraphNode(tKids[i], this);
      if (t.isPreTerminal()) { // add the tags to the leaves
        children[i].label.setTag(t.label().value());
      }
    }
    this.label = (CoreLabel) mlf.newLabel(t.label());
  }

  /**
   * Implements equality for {@code TreeGraphNode}s.  Unlike
   * {@code Tree}s, {@code TreeGraphNode}s should be
   * considered equal only if they are ==.  <i>Implementation note:</i>
   * TODO: This should be changed via introducing a Tree interface with the current Tree and this class implementing it, since what is done here breaks the equals() contract.
   *
   * @param o The object to compare with
   * @return Whether two things are equal
   */
  @Override
  public boolean equals(Object o) {
    return o == this;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }

  
  @Override
  public CoreLabel label() {
    return label;
  }

  @Override
  public void setLabel(Label label) {
    if (label instanceof CoreLabel) {
      this.setLabel((CoreLabel) label);
    } else {
      this.setLabel((CoreLabel) mlf.newLabel(label));
    }
  }

  
  public void setLabel(final CoreLabel label) {
    this.label = label;
  }

  
  public int index() {
    return label.index();
  }

  
  protected void setIndex(int index) {
    label.setIndex(index);
  }

  
  @Override
  public TreeGraphNode parent() {
    return parent;
  }

  
  public void setParent(TreeGraphNode parent) {
    this.parent = parent;
  }

  
  @Override
  public TreeGraphNode[] children() {
    return children;
  }

  
  @Override
  public void setChildren(Tree[] children) {
    if (children == null || children.length == 0) {
      this.children = ZERO_TGN_CHILDREN;
    } else {
      if (children instanceof TreeGraphNode[]) {
        this.children = (TreeGraphNode[]) children;
        for (TreeGraphNode child : this.children) {
          child.setParent(this);
        }
      } else {
        this.children = new TreeGraphNode[children.length];
        for (int i = 0; i < children.length; i++) {
          this.children[i] = (TreeGraphNode)children[i];
          this.children[i].setParent(this);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setChildren(List<? extends Tree> childTreesList) {
    if (childTreesList == null || childTreesList.isEmpty()) {
      setChildren(ZERO_TGN_CHILDREN);
    } else {
      int leng = childTreesList.size();
      TreeGraphNode[] childTrees = new TreeGraphNode[leng];
      childTreesList.toArray(childTrees);
      setChildren(childTrees);
    }
  }

  @Override
  public Tree setChild(int i, Tree t) {
    if (!(t instanceof TreeGraphNode)) {
      throw new IllegalArgumentException("Horrible error");
    }
    ((TreeGraphNode) t).setParent(this);
    return super.setChild(i, t);
  }

  
  @Override
  public void addChild(int i, Tree t) {
    if (!(t instanceof TreeGraphNode)) {
      throw new IllegalArgumentException("Horrible error");
    }
    ((TreeGraphNode) t).setParent(this);
    TreeGraphNode[] kids = this.children;
    TreeGraphNode[] newKids = new TreeGraphNode[kids.length + 1];
    if (i != 0) {
      System.arraycopy(kids, 0, newKids, 0, i);
    }
    newKids[i] = (TreeGraphNode) t;
    if (i != kids.length) {
      System.arraycopy(kids, i, newKids, i + 1, kids.length - i);
    }
    this.children = newKids;
  }

  
  @Override
  public Tree removeChild(int i) {
    TreeGraphNode[] kids = children();
    TreeGraphNode kid = kids[i];
    TreeGraphNode[] newKids = new TreeGraphNode[kids.length - 1];
    for (int j = 0; j < newKids.length; j++) {
      if (j < i) {
        newKids[j] = kids[j];
      } else {
        newKids[j] = kids[j + 1];
      }
    }
    this.children = newKids;
    return kid;
  }

  /**
   * Uses the specified {@link HeadFinder {@code HeadFinder}}
   * to determine the heads for this node and all its descendants,
   * and to store references to the head word node and head tag node
   * in this node's {@link CoreLabel {@code CoreLabel}} and the
   * {@code CoreLabel}s of all its descendants.<p>
   * <p/>
   * Note that, in contrast to {@link Tree#percolateHeads
   * {@code Tree.percolateHeads()}}, which assumes {@link
   * edu.berkeley.nlp.assignments.parsing.ling.CategoryWordTag
   * {@code CategoryWordTag}} labels and therefore stores head
   * words and head tags merely as {@code String}s, this
   * method stores references to the actual nodes.  This mitigates
   * potential problems in sentences which contain the same word
   * more than once.
   *
   * @param hf The headfinding algorithm to use
   */
  @Override
  public void percolateHeads(HeadFinder hf) {
    if (isLeaf()) {
      TreeGraphNode hwn = headWordNode();
      if (hwn == null) {
        setHeadWordNode(this);
      }
    } else {
      for (Tree child : children()) {
        child.percolateHeads(hf);
      }
      TreeGraphNode head = safeCast(hf.determineHead(this,parent));
      if (head != null) {

        TreeGraphNode hwn = head.headWordNode();
        if (hwn == null && head.isLeaf()) { // below us is a leaf
          setHeadWordNode(head);
        } else {
          setHeadWordNode(hwn);
        }
      } else {
      }
    }
  }

  
  public TreeGraphNode headWordNode() {
    return headWordNode;
   }

  
  private void setHeadWordNode(final TreeGraphNode hwn) {
    this.headWordNode = hwn;
  }

  
  private static TreeGraphNode safeCast(Object t) {
    if (t == null || !(t instanceof TreeGraphNode)) {
      return null;
    }
    return (TreeGraphNode) t;
  }

  
  public TreeGraphNode highestNodeWithSameHead() {
    TreeGraphNode node = this;
    while (true) {
      TreeGraphNode parent = safeCast(node.parent());
      if (parent == null || parent.headWordNode() != node.headWordNode()) {
        return node;
      }
      node = parent;
    }
  }

  // extra class guarantees correct lazy loading (Bloch p.194)
  private static class TreeFactoryHolder {

    static final TreeGraphNodeFactory tgnf = new TreeGraphNodeFactory();

    private TreeFactoryHolder() {
    }

  }

  
  @Override
  public TreeFactory treeFactory() {
    LabelFactory lf;
    if (label() != null) {
      lf = label().labelFactory();
    } else {
      lf = CoreLabel.factory();
    }
    return new TreeGraphNodeFactory(lf);
  }

  
  public static TreeFactory factory() {
    return TreeFactoryHolder.tgnf;
  }

  
  public static TreeFactory factory(LabelFactory lf) {
    return new TreeGraphNodeFactory(lf);
  }

  
  public String toPrettyString(int indentLevel) {
    StringBuilder buf = new StringBuilder("\n");
    for (int i = 0; i < indentLevel; i++) {
      buf.append("  ");
    }
    if (children == null || children.length == 0) {
      buf.append(label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP));
    } else {
      buf.append('(').append(label.toString(CoreLabel.OutputFormat.VALUE_INDEX_MAP));
      for (TreeGraphNode child : children) {
        buf.append(' ').append(child.toPrettyString(indentLevel + 1));
      }
      buf.append(')');
    }
    return buf.toString();
  }


  @Override
  public String toString() {
    return toString(CoreLabel.DEFAULT_FORMAT);
  }

  public String toString(CoreLabel.OutputFormat format) {
    return label.toString(format);
  }

  private static final long serialVersionUID = 5080098143617475328L;

}
