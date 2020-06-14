package edu.berkeley.nlp.assignments.parsing.trees;

import java.io.File;
import java.io.FileFilter;
import java.util.Iterator;


/** This class wraps another Treebank, and will vend trees that have been through
 *  a TreeTransformer.  You can access them via requests like <code>apply()</code> or
 *  <code>iterator()</code>.
 *  <p>
 *  <i>Important note</i>: This class will only function properly if the TreeTransformer
 *  used is a function (which doesn't change its argument) rather than if it is a
 *  TreeMunger.
 *
 *  @author Pi-Chuan Chang
 *  @author Christopher Manning
 */
public class TransformingTreebank extends Treebank {

  private TreeTransformer transformer;
  private Treebank tb;

  private static final boolean VERBOSE = false;


  /**
   * Create a new TransformingTreebank.
   * The trees are made with a <code>LabeledScoredTreeReaderFactory</code>.
   * <p/>
   * <i>Compatibility note: Until Sep 2004, this used to create a Treebank
   * with a SimpleTreeReaderFactory, but this was changed as the old
   * default wasn't very useful, especially to naive users.</i>
   */
  public TransformingTreebank() {
    this(new LabeledScoredTreeReaderFactory());
  }

  /**
   * Create a new TransformingTreebank.
   *
   * @param trf the factory class to be called to create a new
   *            <code>TreeReader</code>
   */
  public TransformingTreebank(TreeReaderFactory trf) {
    super(trf);
  }

  /**
   * Create a new TransformingTreebank from a base Treebank that will
   * transform trees with the given TreeTransformer.
   * This is the constructor that you should use.
   *
   * @param tb The base Treebank
   * @param transformer The TreeTransformer applied to each Tree.
   */
  public TransformingTreebank(Treebank tb, TreeTransformer transformer) {
    this.tb = tb;
    this.transformer = transformer;
  }


  /**
   * Empty a <code>Treebank</code>.
   */
  @Override
  public void clear() {
    tb.clear();
    transformer = null;
  }


  // public String toString() {
  //   return "TransformingTreebank[transformer=" + transformer + "]\n" + super.toString();
  // }


  /**
   * Load trees from given path specification.  Not supported for this
   * type of treebank.
   *
   * @param path file or directory to load from
   * @param filt a FilenameFilter of files to load
   */
  @Override
  public void loadPath(File path, FileFilter filt) {
    throw new UnsupportedOperationException();
  }

  /**
   * Applies the TreeVisitor to to all trees in the Treebank.
   *
   * @param tv A class that can process trees.
   */
  @Override
  public void apply(TreeVisitor tv) {
    for (Tree t : tb) {
      if (VERBOSE) System.out.println("TfTbApply transforming " + t);
      Tree tmpT = t.deepCopy();
      if (transformer != null) {
        tmpT = transformer.transformTree(tmpT);
      }
      if (VERBOSE) System.out.println("  to " + tmpT);
      tv.visitTree(tmpT);
    }
  }

  /**
   */
  @Override
  public Iterator<Tree> iterator() {
    return new TransformingTreebankIterator(tb.iterator(), transformer);
  }

  private static class TransformingTreebankIterator implements Iterator<Tree> {

    private Iterator<Tree> iter;
    private TreeTransformer transformer;

    TransformingTreebankIterator (Iterator<Tree> iter, TreeTransformer transformer) {
      this.iter = iter;
      this.transformer = transformer;
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Tree next() {
      // this line will throw NoSuchElement exception if empty base iterator....
      Tree ret = iter.next();
      if (VERBOSE) System.out.println("TfTbIterator transforming " + ret);
      if (transformer != null) {
        ret = transformer.transformTree(ret);
      }
      if (VERBOSE) System.out.println("  to " + ret);
      return ret;
    }

    public void remove() {
      throw new UnsupportedOperationException();
    }

  } // end static class TransformingTreebankIterator

}
