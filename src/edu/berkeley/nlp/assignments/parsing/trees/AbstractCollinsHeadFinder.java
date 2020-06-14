package edu.berkeley.nlp.assignments.parsing.trees; 

import java.util.Map;

/**
 * A base class for a HeadFinder similar to the one described in
 * Michael Collins' 1999 thesis.  For a given constituent we perform operations
 * like (this is for "left" or "right":
 * <pre>
 * for categoryList in categoryLists
 *   for index = 1 to n [or n to 1 if R-&gt;L]
 *     for category in categoryList
 *       if category equals daughter[index] choose it.
 * </pre>
 * <p>
 * with a final default that goes with the direction (L-&gt;R or R-&gt;L)
 * For most constituents, there will be only one category in the list,
 * the exception being, in Collins' original version, NP.
 * </p>
 * <p>
 * It is up to the overriding base class to initialize the map
 * from constituent type to categoryLists, "nonTerminalInfo",
 * in its constructor.
 * Entries are presumed to be of type String[][].  Each String[] is a list of
 * categories, except for the first entry, which specifies direction of
 * traversal and must be one of the following:
 * </p>
 * <ul>
 * <li> "left" means search left-to-right by category and then by position
 * <li> "leftdis" means search left-to-right by position and then by category
 * <li> "right" means search right-to-left by category and then by position
 * <li> "rightdis" means search right-to-left by position and then by category
 * <li> "leftexcept" means to take the first thing from the left that isn't in the list
 * <li> "rightexcept" means to take the first thing from the right that isn't on the list
 * </ul>
 * <p>
 * Changes:
 * </p>
 * <ul>
 * <li> 2002/10/28 -- Category label identity checking now uses the
 * equals() method instead of ==, so not interning category labels
 * shouldn't break things anymore.  (Roger Levy) <br>
 * <li> 2003/02/10 -- Changed to use TreebankLanguagePack and to cut on
 * characters that set off annotations, so this should work even if
 * functional tags are still on nodes. <br>
 * <li> 2004/03/30 -- Made abstract base class and subclasses for CollinsHeadFinder,
 * ModCollinsHeadFinder, SemanticHeadFinder, ChineseHeadFinder
 * (and trees.icegb.ICEGBHeadFinder, trees.international.negra.NegraHeadFinder,
 * and movetrees.EnglishPennMaxProjectionHeadFinder)
 * <li> 2011/01/13 -- Add support for categoriesToAvoid (which can be set to ensure that
 * punctuation is not the head if there are other options)
 * </ul>
 *
 * @author Christopher Manning
 * @author Galen Andrew
 */
public abstract class AbstractCollinsHeadFinder implements HeadFinder /* Serializable */, CopulaHeadFinder  {

  private static final boolean DEBUG = System.getProperty("HeadFinder", null) != null;
  protected final TreebankLanguagePack tlp;
  protected Map<String, String[][]> nonTerminalInfo;

  /** Default direction if no rule is found for category (the head/parent).
   *  Subclasses can turn it on if they like.
   *  If they don't it is an error if no rule is defined for a category
   *  (null is returned).
   */
  protected String[] defaultRule; // = null;

  /** These are built automatically from categoriesToAvoid and used in a fairly
   *  different fashion from defaultRule (above).  These are used for categories
   *  that do have defined rules but where none of them have matched.  Rather
   *  than picking the rightmost or leftmost child, we will use these to pick
   *  the the rightmost or leftmost child which isn't in categoriesToAvoid.
   */
  protected String[] defaultLeftRule;
  protected String[] defaultRightRule;

  
  protected AbstractCollinsHeadFinder(TreebankLanguagePack tlp, String... categoriesToAvoid) {
    this.tlp = tlp;
    // automatically build defaultLeftRule, defaultRightRule
    defaultLeftRule = new String[categoriesToAvoid.length + 1];
    defaultRightRule = new String[categoriesToAvoid.length + 1];
    if (categoriesToAvoid.length > 0) {
      defaultLeftRule[0] = "leftexcept";
      defaultRightRule[0] = "rightexcept";
      System.arraycopy(categoriesToAvoid, 0, defaultLeftRule, 1, categoriesToAvoid.length);
      System.arraycopy(categoriesToAvoid, 0, defaultRightRule, 1, categoriesToAvoid.length);
    } else {
      defaultLeftRule[0] = "left";
      defaultRightRule[0] = "right";
    }
  }

  
  @Override
  public boolean makesCopulaHead() {
    return false;
  }

  
  // to be overridden in subclasses for corpora
  //
  protected Tree findMarkedHead(Tree t) {
    return null;
  }

  /**
   * Determine which daughter of the current parse tree is the head.
   *
   * @param t The parse tree to examine the daughters of.
   *          If this is a leaf, <code>null</code> is returned
   * @return The daughter parse tree that is the head of <code>t</code>
   * @see Tree#percolateHeads(HeadFinder)
   *      for a routine to call this and spread heads throughout a tree
   */
  @Override
  public Tree determineHead(Tree t) {
    return determineHead(t, null);
  }

  /**
   * Determine which daughter of the current parse tree is the head.
   *
   * @param t The parse tree to examine the daughters of.
   *          If this is a leaf, <code>null</code> is returned
   * @param parent The parent of t
   * @return The daughter parse tree that is the head of <code>t</code>.
   *   Returns null for leaf nodes.
   * @see Tree#percolateHeads(HeadFinder)
   *      for a routine to call this and spread heads throughout a tree
   */
  @Override
  public Tree determineHead(Tree t, Tree parent) {
    if (nonTerminalInfo == null) {
      throw new IllegalStateException("Classes derived from AbstractCollinsHeadFinder must create and fill HashMap nonTerminalInfo.");
    }
    if (t == null || t.isLeaf()) {
      throw new IllegalArgumentException("Can't return head of null or leaf Tree.");
    }
    Tree[] kids = t.children();

    Tree theHead;
    // first check if subclass found explicitly marked head
    if ((theHead = findMarkedHead(t)) != null) {
      return theHead;
    }

    // if the node is a unary, then that kid must be the head
    // it used to special case preterminal and ROOT/TOP case
    // but that seemed bad (especially hardcoding string "ROOT")
    if (kids.length == 1) {
      return kids[0];
    }

    return determineNonTrivialHead(t, parent);
  }

  /** Called by determineHead and may be overridden in subclasses
   *  if special treatment is necessary for particular categories.
   *
   *  @param t The tre to determine the head daughter of
   *  @param parent The parent of t (or may be null)
   *  @return The head daughter of t
   */
  protected Tree determineNonTrivialHead(Tree t, Tree parent) {
    Tree theHead = null;
    String motherCat = tlp.basicCategory(t.label().value());
    if (motherCat.startsWith("@")) {
      motherCat = motherCat.substring(1);
    }
    // We know we have nonterminals underneath
    // (a bit of a Penn Treebank assumption, but).

    // Look at label.
    // a total special case....
    // first look for POS tag at end
    // this appears to be redundant in the Collins case since the rule already would do that
    //    Tree lastDtr = t.lastChild();
    //    if (tlp.basicCategory(lastDtr.label().value()).equals("POS")) {
    //      theHead = lastDtr;
    //    } else {
    String[][] how = nonTerminalInfo.get(motherCat);
    Tree[] kids = t.children();
    if (how == null) {
      if (defaultRule != null) {
        return traverseLocate(kids, defaultRule, true);
      } else {
        return null;
      }
    }
    for (int i = 0; i < how.length; i++) {
      boolean lastResort = (i == how.length - 1);
      theHead = traverseLocate(kids, how[i], lastResort);
      if (theHead != null) {
        break;
      }
    }
    return theHead;
  }

  /**
   * Attempt to locate head daughter tree from among daughters.
   * Go through daughterTrees looking for things from or not in a set given by
   * the contents of the array how, and if
   * you do not find one, take leftmost or rightmost perhaps matching thing iff
   * lastResort is true, otherwise return <code>null</code>.
   */
  protected Tree traverseLocate(Tree[] daughterTrees, String[] how, boolean lastResort) {
    int headIdx;
    switch (how[0]) {
      case "left":
        headIdx = findLeftHead(daughterTrees, how);
        break;
      case "leftdis":
        headIdx = findLeftDisHead(daughterTrees, how);
        break;
      case "leftexcept":
        headIdx = findLeftExceptHead(daughterTrees, how);
        break;
      case "right":
        headIdx = findRightHead(daughterTrees, how);
        break;
      case "rightdis":
        headIdx = findRightDisHead(daughterTrees, how);
        break;
      case "rightexcept":
        headIdx = findRightExceptHead(daughterTrees, how);
        break;
      default:
        throw new IllegalStateException("ERROR: invalid direction type " + how[0] + " to nonTerminalInfo map in AbstractCollinsHeadFinder.");
    }

    // what happens if our rule didn't match anything
    if (headIdx < 0) {
      if (lastResort) {
        // use the default rule to try to match anything except categoriesToAvoid
        // if that doesn't match, we'll return the left or rightmost child (by
        // setting headIdx).  We want to be careful to ensure that postOperationFix
        // runs exactly once.
        String[] rule;
        if (how[0].startsWith("left")) {
          headIdx = 0;
          rule = defaultLeftRule;
        } else {
          headIdx = daughterTrees.length - 1;
          rule = defaultRightRule;
        }
        Tree child = traverseLocate(daughterTrees, rule, false);
        if (child != null) {
          return child;
        } else {
          return daughterTrees[headIdx];
        }
      } else {
        // if we're not the last resort, we can return null to let the next rule try to match
        return null;
      }
    }

    headIdx = postOperationFix(headIdx, daughterTrees);

    return daughterTrees[headIdx];
  }

  private int findLeftHead(Tree[] daughterTrees, String[] how) {
    for (int i = 1; i < how.length; i++) {
      for (int headIdx = 0; headIdx < daughterTrees.length; headIdx++) {
        String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
        if (how[i].equals(childCat)) {
          return headIdx;
        }
      }
    }
    return -1;
  }

  private int findLeftDisHead(Tree[] daughterTrees, String[] how) {
    for (int headIdx = 0; headIdx < daughterTrees.length; headIdx++) {
      String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
      for (int i = 1; i < how.length; i++) {
        if (how[i].equals(childCat)) {
          return headIdx;
        }
      }
    }
    return -1;
  }

  private int findLeftExceptHead(Tree[] daughterTrees, String[] how) {
    for (int headIdx = 0; headIdx < daughterTrees.length; headIdx++) {
      String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
      boolean found = true;
      for (int i = 1; i < how.length; i++) {
        if (how[i].equals(childCat)) {
          found = false;
        }
      }
      if (found) {
        return headIdx;
      }
    }
    return -1;
  }

  private int findRightHead(Tree[] daughterTrees, String[] how) {
    for (int i = 1; i < how.length; i++) {
      for (int headIdx = daughterTrees.length - 1; headIdx >= 0; headIdx--) {
        String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
        if (how[i].equals(childCat)) {
          return headIdx;
        }
      }
    }
    return -1;
  }

  // from right, but search for any of the categories, not by category in turn
  private int findRightDisHead(Tree[] daughterTrees, String[] how) {
    for (int headIdx = daughterTrees.length - 1; headIdx >= 0; headIdx--) {
      String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
      for (int i = 1; i < how.length; i++) {
        if (how[i].equals(childCat)) {
          return headIdx;
        }
      }
    }
    return -1;
  }

  private int findRightExceptHead(Tree[] daughterTrees, String[] how) {
    for (int headIdx = daughterTrees.length - 1; headIdx >= 0; headIdx--) {
      String childCat = tlp.basicCategory(daughterTrees[headIdx].label().value());
      boolean found = true;
      for (int i = 1; i < how.length; i++) {
        if (how[i].equals(childCat)) {
          found = false;
        }
      }
      if (found) {
        return headIdx;
      }
    }
    return -1;
  }

  
  protected int postOperationFix(int headIdx, Tree[] daughterTrees) {
    return headIdx;
  }

  private static final long serialVersionUID = -6540278059442931087L;

}
