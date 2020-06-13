package edu.berkeley.nlp.assignments.parsing.trees; 
import edu.berkeley.nlp.assignments.parsing.util.logging.Redwood;

import edu.berkeley.nlp.assignments.parsing.process.TokenizerFactory;
import edu.berkeley.nlp.assignments.parsing.process.Tokenizer;
import edu.berkeley.nlp.assignments.parsing.process.AbstractTokenizer;

import java.io.Reader;
import java.io.IOException;
import java.util.Iterator;

/** Wrapper for TreeReaderFactory.  Any IOException in the readTree() method
 *  of the TreeReader will result in a null
 *  tree returned.
 *
 *  @author Roger Levy (rog@stanford.edu)
 *  @author javanlp
 */
public class TreeTokenizerFactory implements TokenizerFactory<Tree>  {

  /** A logger for this class */
  private static Redwood.RedwoodChannels log = Redwood.channels(TreeTokenizerFactory.class);

  /** Create a TreeTokenizerFactory from a TreeReaderFactory. */
  public TreeTokenizerFactory(TreeReaderFactory trf) {
    this.trf = trf;
  }

  private TreeReaderFactory trf;

  /** Gets a tokenizer from a reader.*/
  public Tokenizer<Tree> getTokenizer(final Reader r) {
    return new AbstractTokenizer<Tree>() {
      TreeReader tr = trf.newTreeReader(r);
      @Override
      public Tree getNext() {
        try {
          return tr.readTree();
        }
        catch(IOException e) {
          log.info("Error in reading tree.");
          return null;
        }
      }
    };
  }

  public Tokenizer<Tree> getTokenizer(final Reader r, String extraOptions) {
    // Silently ignore extra options
    return getTokenizer(r);
  }

  /** Same as getTokenizer().  */
  public Iterator<Tree> getIterator(Reader r) {
    return null;
  }

  public void setOptions(String options) {
    //Silently ignore
  }
}
