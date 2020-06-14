package edu.berkeley.nlp.assignments.parsing.parser.lexparser; 

import edu.berkeley.nlp.assignments.parsing.trees.TreebankLanguagePack;


/** @author Dan Klein */
public class BasicCategoryTagProjection implements TagProjection  {

  private static final long serialVersionUID = -2322431101811335089L;

  TreebankLanguagePack tlp;

  public BasicCategoryTagProjection(TreebankLanguagePack tlp) {
    this.tlp = tlp;
  }

  public String project(String tagStr) {
    // return tagStr;
    String ret = tlp.basicCategory(tagStr);
    // log.info("BCTP mapped " + tagStr + " to " + ret);
    return ret;
  }

}
