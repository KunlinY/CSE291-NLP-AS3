package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.ling.LabelFactory;
import edu.berkeley.nlp.assignments.parsing.ling.StringLabel;

/**
 * A <code>LabeledScoredConstituent</code> object defines an edge in a graph
 * with a label and a score.
 *
 * @author Christopher Manning
 */
public class LabeledScoredConstituent extends LabeledConstituent {

  private double score;

  /**
   * Create an empty <code>LabeledScoredConstituent</code> object.
   */
  public LabeledScoredConstituent() {
    // implicitly super();
  }


  /**
   * Create a <code>LabeledScoredConstituent</code> object with given
   * values.
   *
   * @param start start node of edge
   * @param end   end node of edge
   */
  public LabeledScoredConstituent(int start, int end) {
    super(start, end);
  }


  /**
   * Create a <code>LabeledScoredConstituent</code> object with given
   * values.
   *
   * @param start start node of edge
   * @param end   end node of edge
   */
  public LabeledScoredConstituent(int start, int end, Label label, double score) {
    super(start, end, label);
    this.score = score;
  }


  
  @Override
  public double score() {
    return score;
  }


  
  @Override
  public void setScore(final double score) {
    this.score = score;
  }


  /**
   * A <code>LabeledScoredConstituentLabelFactory</code> object makes a
   * <code>LabeledScoredConstituent</code> with a <code>StringLabel</code>
   * label (or of the type of label passed in for the final constructor).
   */
  private static class LabeledScoredConstituentLabelFactory implements LabelFactory {

    /**
     * Make a new <code>LabeledScoredConstituent</code>.
     *
     * @param labelStr A string
     * @return The created label
     */
    public Label newLabel(final String labelStr) {
      return new LabeledScoredConstituent(0, 0, new StringLabel(labelStr), 0.0);
    }


    /**
     * Make a new <code>LabeledScoredConstituent</code>.
     *
     * @param labelStr A string.
     * @param options  The options are ignored.
     * @return The created label
     */
    public Label newLabel(final String labelStr, final int options) {
      return newLabel(labelStr);
    }


    /**
     * Make a new <code>LabeledScoredConstituent</code>.
     *
     * @param labelStr A string that
     * @return The created label
     */
    public Label newLabelFromString(final String labelStr) {
      return newLabel(labelStr);
    }


    /**
     * Create a new <code>LabeledScoredConstituent</code>.
     *
     * @param oldLabel A <code>Label</code>.
     * @return A new <code>LabeledScoredConstituent</code>
     */
    public Label newLabel(Label oldLabel) {
      return new LabeledScoredConstituent(0, 0, oldLabel, 0.0);
    }

  }


  // extra class guarantees correct lazy loading (Bloch p.194)
  private static class LabelFactoryHolder {
    static final LabelFactory lf = new LabeledScoredConstituentLabelFactory();
  }

  
  @Override
  public LabelFactory labelFactory() {
    return LabelFactoryHolder.lf;
  }


  // extra class guarantees correct lazy loading (Bloch p.194)
  private static class ConstituentFactoryHolder {

    private static final ConstituentFactory cf = new LabeledScoredConstituentFactory();

  }


  
  @Override
  public ConstituentFactory constituentFactory() {
    return ConstituentFactoryHolder.cf;
  }


  
  public static ConstituentFactory factory() {
    return ConstituentFactoryHolder.cf;
  }

}
