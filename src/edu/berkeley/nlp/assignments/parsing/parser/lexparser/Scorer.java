package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

import java.util.List;

import edu.berkeley.nlp.assignments.parsing.ling.HasWord;


/** Interface for supporting A* scoring.
 *
 *  @author Dan Klein
 */
public interface Scorer {

  public double oScore(Edge edge);

  public double iScore(Edge edge);

  public boolean oPossible(Hook hook);

  public boolean iPossible(Hook hook);

  public boolean parse(List<? extends HasWord> words);

}
