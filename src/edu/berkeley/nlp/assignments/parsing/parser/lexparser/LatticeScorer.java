package edu.berkeley.nlp.assignments.parsing.parser.lexparser;

/**
 * 
 * @author Spence Green
 *
 */
public interface LatticeScorer extends Scorer {

	public Item convertItemSpan(Item item);
}
