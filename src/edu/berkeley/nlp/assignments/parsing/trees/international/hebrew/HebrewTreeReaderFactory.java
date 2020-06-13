package edu.berkeley.nlp.assignments.parsing.trees.international.hebrew; 
import edu.berkeley.nlp.assignments.parsing.util.logging.Redwood;

import java.io.*;

import edu.berkeley.nlp.assignments.parsing.trees.LabeledScoredTreeFactory;
import edu.berkeley.nlp.assignments.parsing.trees.PennTreeReader;
import edu.berkeley.nlp.assignments.parsing.trees.PennTreebankTokenizer;
import edu.berkeley.nlp.assignments.parsing.trees.Tree;
import edu.berkeley.nlp.assignments.parsing.trees.TreeReader;
import edu.berkeley.nlp.assignments.parsing.trees.TreeReaderFactory;
import edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack;

/**
 * 
 * @author Spence Green
 *
 */
public class HebrewTreeReaderFactory implements TreeReaderFactory, Serializable  {

  /** A logger for this class */
  private static Redwood.RedwoodChannels log = Redwood.channels(HebrewTreeReaderFactory.class);

  private static final long serialVersionUID = 818065349424602548L;

  public TreeReader newTreeReader(Reader in) {
    return new PennTreeReader(in, new LabeledScoredTreeFactory(), new HebrewTreeNormalizer(),new PennTreebankTokenizer(in));
  }


  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args.length != 1) {
      System.err.printf("Usage: java %s tree_file > trees%n", HebrewTreeReaderFactory.class.getName());
      System.exit(-1);
    }

    TreebankLanguagePack tlp = new HebrewTreebankLanguagePack();
    File treeFile = new File(args[0]);
    try {
      TreeReaderFactory trf = new HebrewTreeReaderFactory();
      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(treeFile), tlp.getEncoding()));
      TreeReader tr = trf.newTreeReader(br);

      int numTrees = 0;
      for(Tree t; ((t = tr.readTree()) != null); numTrees++)
        System.out.println(t.toString());

      tr.close();
      System.err.printf("Processed %d trees.%n",numTrees);

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
