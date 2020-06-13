package edu.berkeley.nlp.assignments.parsing.ling;


/**
 * An abstract token.
 * This simply joins all the natural token-like interfaces, like
 * {@link HasWord}, {@link HasLemma}, etc.
 *
 * @author <a href="mailto:gabor@eloquent.ai">Gabor Angeli</a>
 */
public interface AbstractToken extends HasWord, HasIndex, HasTag, HasLemma, HasNER, HasOffset, HasOriginalText, HasContext {


}
