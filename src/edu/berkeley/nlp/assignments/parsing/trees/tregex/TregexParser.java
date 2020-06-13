/* TregexParser.java */
/* Generated By:JavaCC: Do not edit this line. TregexParser.java */
package edu.berkeley.nlp.assignments.parsing.trees.tregex;
// all generated classes are in this package

import java.util.function.Function;
import edu.berkeley.nlp.assignments.parsing.util.Generics;
import edu.berkeley.nlp.assignments.parsing.util.Pair;
import edu.berkeley.nlp.assignments.parsing.trees.HeadFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class TregexParser implements TregexParserConstants {

  // this is so we can tell, at any point during the parse
  // whether we are under a negation, which we need to know
  // because labeling nodes under negation is illegal
  private boolean underNegation = false;

  private Function<String, String> basicCatFunction =
    TregexPatternCompiler.DEFAULT_BASIC_CAT_FUNCTION;

  private HeadFinder headFinder =
    TregexPatternCompiler.DEFAULT_HEAD_FINDER;

  // keep track of which variables we've seen, so that we can reject
  // some nonsense patterns such as ones that reset variables or link
  // to variables that haven't been set
  private Set<String> knownVariables = Generics.newHashSet();

  public TregexParser(java.io.Reader stream,
                      Function<String, String> basicCatFunction,
                      HeadFinder headFinder) {
    this(stream);
    this.basicCatFunction = basicCatFunction;
    this.headFinder = headFinder;
  }

// TODO: IDENTIFIER should not allow | after the first character, but
// it breaks some | queries to allow it.  We should fix that.

// the grammar starts here
// each of these BNF rules will be converted into a function
// first expr is return val- passed up the tree after a production
  final public TregexPattern Root() throws ParseException {TregexPattern node;
  List<TregexPattern> nodes = Generics.newArrayList();
  // a local variable

    node = SubNode(Relation.ROOT);
nodes.add(node);
    label_1:
    while (true) {
      if (jj_2_1(2)) {
        ;
      } else {
        break label_1;
      }
      jj_consume_token(12);
      node = SubNode(Relation.ROOT);
nodes.add(node);
    }
    jj_consume_token(13);
if (nodes.size() == 1) {
      {if ("" != null) return nodes.get(0);}
    } else {
      {if ("" != null) return new CoordinationPattern(nodes, false);}
    }
    throw new Error("Missing return statement in function");
  }

// passing arguments down the tree - in this case the relation that
// pertains to this node gets passed all the way down to the Description node
  final public DescriptionPattern Node(Relation r) throws ParseException {DescriptionPattern node;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 14:{
      jj_consume_token(14);
      node = SubNode(r);
      jj_consume_token(15);
      break;
      }
    case IDENTIFIER:
    case BLANK:
    case REGEX:
    case 16:
    case 17:
    case 20:
    case 21:{
      node = ModDescription(r);
      break;
      }
    default:
      jj_la1[0] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return node;}
    throw new Error("Missing return statement in function");
  }

  final public DescriptionPattern SubNode(Relation r) throws ParseException {DescriptionPattern result = null;
  TregexPattern child = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 14:{
      jj_consume_token(14);
      result = SubNode(r);
      jj_consume_token(15);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case RELATION:
      case MULTI_RELATION:
      case REL_W_STR_ARG:
      case 14:
      case 16:
      case 23:
      case 24:{
        child = ChildrenDisj();
        break;
        }
      default:
        jj_la1[1] = jj_gen;
        ;
      }
if(child != null) {
        List<TregexPattern> newChildren = new ArrayList<>();
        newChildren.addAll(result.getChildren());
        newChildren.add(child);
        result.setChild(new CoordinationPattern(newChildren,true));
      }
      {if ("" != null) return result;}
      break;
      }
    case IDENTIFIER:
    case BLANK:
    case REGEX:
    case 16:
    case 17:
    case 20:
    case 21:{
      result = ModDescription(r);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case RELATION:
      case MULTI_RELATION:
      case REL_W_STR_ARG:
      case 14:
      case 16:
      case 23:
      case 24:{
        child = ChildrenDisj();
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        ;
      }
if (child != null) result.setChild(child);
      {if ("" != null) return result;}
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public DescriptionPattern ModDescription(Relation r) throws ParseException {DescriptionPattern node;
  boolean neg = false, cat = false;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 16:{
      jj_consume_token(16);
neg = true;
      break;
      }
    default:
      jj_la1[4] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 17:{
      jj_consume_token(17);
cat = true;
      break;
      }
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    node = Description(r, neg, cat);
{if ("" != null) return node;}
    throw new Error("Missing return statement in function");
  }

  final public DescriptionPattern Description(Relation r, boolean negateDesc, boolean cat) throws ParseException {Token desc = null;
  Token name = null;
  Token linkedName = null;
  boolean link = false;
  Token groupNum;
  Token groupVar;
  List<Pair<Integer,String>> varGroups = new ArrayList<>();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case IDENTIFIER:
    case BLANK:
    case REGEX:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IDENTIFIER:{
        desc = jj_consume_token(IDENTIFIER);
        break;
        }
      case REGEX:{
        desc = jj_consume_token(REGEX);
        break;
        }
      case BLANK:{
        desc = jj_consume_token(BLANK);
        break;
        }
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case 18:{
          ;
          break;
          }
        default:
          jj_la1[7] = jj_gen;
          break label_2;
        }
        jj_consume_token(18);
        groupNum = jj_consume_token(NUMBER);
        jj_consume_token(19);
        groupVar = jj_consume_token(IDENTIFIER);
varGroups.add(new Pair<>(Integer.parseInt(groupNum.image), groupVar.image));
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case 20:{
        jj_consume_token(20);
        name = jj_consume_token(IDENTIFIER);
if (knownVariables.contains(name.image)) {
            {if (true) throw new ParseException("Variable " + name.image + " has been declared twice, which makes no sense");}
          } else {
            knownVariables.add(name.image);
          }
          if (underNegation)
            {if (true) throw new ParseException("No named tregex nodes allowed in the scope of negation.");}
        break;
        }
      default:
        jj_la1[8] = jj_gen;
        ;
      }
      break;
      }
    case 21:{
      jj_consume_token(21);
      linkedName = jj_consume_token(IDENTIFIER);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case 20:{
        jj_consume_token(20);
        name = jj_consume_token(IDENTIFIER);
        break;
        }
      default:
        jj_la1[9] = jj_gen;
        ;
      }
if (!knownVariables.contains(linkedName.image)) {
          {if (true) throw new ParseException("Variable " + linkedName.image +
                                   " was referenced before it was declared");}
        }
        if (name != null) {
          if (knownVariables.contains(name.image)) {
            {if (true) throw new ParseException("Variable " + name.image + " has been declared twice, which makes no sense");}
          } else {
            knownVariables.add(name.image);
          }
        }
        link = true;
      break;
      }
    case 20:{
      jj_consume_token(20);
      name = jj_consume_token(IDENTIFIER);
if (!knownVariables.contains(name.image)) {
          {if (true) throw new ParseException("Variable " + name.image +
                                   " was referenced before it was declared");}
        }
      break;
      }
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
DescriptionPattern ret = new DescriptionPattern(r, negateDesc, desc != null ? desc.image : null, name != null ? name.image : null, cat, basicCatFunction, varGroups, link, linkedName != null ? linkedName.image : null);
    {if ("" != null) return ret;}
    throw new Error("Missing return statement in function");
  }

  final public TregexPattern ChildrenDisj() throws ParseException {TregexPattern child;
  List<TregexPattern> children = new ArrayList<>();
  // When we keep track of the known variables to assert that
  // variables are not redefined, or that links are only set to known
  // variables, we want to separate those done in different parts of the
  // disjunction.  Variables set in one part won't be set in the next
  // part if it gets there, since disjunctions exit once known.
  Set<String> originalKnownVariables = Generics.newHashSet(knownVariables);
  // However, we want to keep track of all the known variables, so that after
  // the disjunction is over, we know them all.
  Set<String> allKnownVariables = Generics.newHashSet(knownVariables);
    child = ChildrenConj();
children.add(child);
      allKnownVariables.addAll(knownVariables);
    label_3:
    while (true) {
      if (jj_2_2(2)) {
        ;
      } else {
        break label_3;
      }
knownVariables = Generics.newHashSet(originalKnownVariables);
      jj_consume_token(12);
      child = ChildrenConj();
children.add(child);
      allKnownVariables.addAll(knownVariables);
    }
knownVariables = allKnownVariables;
    if (children.size() == 1)
      {if ("" != null) return child;}
    else
      {if ("" != null) return new CoordinationPattern(children, false);}
    throw new Error("Missing return statement in function");
  }

  final public TregexPattern ChildrenConj() throws ParseException {TregexPattern child;
  List<TregexPattern> children = new ArrayList<>();
    child = ModChild();
children.add(child);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case RELATION:
      case MULTI_RELATION:
      case REL_W_STR_ARG:
      case 14:
      case 16:
      case 22:
      case 23:
      case 24:{
        ;
        break;
        }
      default:
        jj_la1[11] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case 22:{
        jj_consume_token(22);
        break;
        }
      default:
        jj_la1[12] = jj_gen;
        ;
      }
      child = ModChild();
children.add(child);
    }
if (children.size() == 1)
        {if ("" != null) return child;}
      else
        {if ("" != null) return new CoordinationPattern(children, true);}
    throw new Error("Missing return statement in function");
  }

  final public TregexPattern ModChild() throws ParseException {TregexPattern child;
  boolean startUnderNeg;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case RELATION:
    case MULTI_RELATION:
    case REL_W_STR_ARG:
    case 14:
    case 24:{
      child = Child();
      break;
      }
    case 16:{
      jj_consume_token(16);
startUnderNeg = underNegation;
          underNegation = true;
      child = ModChild();
underNegation = startUnderNeg;
child.negate();
      break;
      }
    case 23:{
      jj_consume_token(23);
      child = Child();
child.makeOptional();
      break;
      }
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return child;}
    throw new Error("Missing return statement in function");
  }

  final public TregexPattern Child() throws ParseException {TregexPattern child;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case 24:{
      jj_consume_token(24);
      child = ChildrenDisj();
      jj_consume_token(25);
      break;
      }
    case 14:{
      jj_consume_token(14);
      child = ChildrenDisj();
      jj_consume_token(15);
      break;
      }
    case RELATION:
    case MULTI_RELATION:
    case REL_W_STR_ARG:{
      child = Relation();
      break;
      }
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return child;}
    throw new Error("Missing return statement in function");
  }

  final public TregexPattern Relation() throws ParseException {Token t, strArg = null, numArg = null, negation = null, cat = null;
  // the easiest way to check if an optional production was used
  // is to set the token to null and then check it later
  Relation r;
  DescriptionPattern child;
  List<DescriptionPattern> children = Generics.newArrayList();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case RELATION:
    case REL_W_STR_ARG:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case RELATION:{
        t = jj_consume_token(RELATION);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case NUMBER:{
          numArg = jj_consume_token(NUMBER);
          break;
          }
        default:
          jj_la1[15] = jj_gen;
          ;
        }
        break;
        }
      case REL_W_STR_ARG:{
        t = jj_consume_token(REL_W_STR_ARG);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case 14:{
          jj_consume_token(14);
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 16:{
            negation = jj_consume_token(16);
            break;
            }
          default:
            jj_la1[16] = jj_gen;
            ;
          }
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 17:{
            cat = jj_consume_token(17);
            break;
            }
          default:
            jj_la1[17] = jj_gen;
            ;
          }
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case REGEX:{
            strArg = jj_consume_token(REGEX);
            break;
            }
          case IDENTIFIER:{
            strArg = jj_consume_token(IDENTIFIER);
            break;
            }
          case BLANK:{
            strArg = jj_consume_token(BLANK);
            break;
            }
          default:
            jj_la1[18] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          jj_consume_token(15);
          break;
          }
        case 24:{
          jj_consume_token(24);
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 16:{
            negation = jj_consume_token(16);
            break;
            }
          default:
            jj_la1[19] = jj_gen;
            ;
          }
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 17:{
            cat = jj_consume_token(17);
            break;
            }
          default:
            jj_la1[20] = jj_gen;
            ;
          }
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case REGEX:{
            strArg = jj_consume_token(REGEX);
            break;
            }
          case IDENTIFIER:{
            strArg = jj_consume_token(IDENTIFIER);
            break;
            }
          case BLANK:{
            strArg = jj_consume_token(BLANK);
            break;
            }
          default:
            jj_la1[21] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
          jj_consume_token(25);
          break;
          }
        case REGEX:
        case 16:{
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 16:{
            negation = jj_consume_token(16);
            break;
            }
          default:
            jj_la1[22] = jj_gen;
            ;
          }
          strArg = jj_consume_token(REGEX);
          break;
          }
        default:
          jj_la1[23] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
        }
      default:
        jj_la1[24] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
if (strArg != null) {
        String negStr = negation == null ? "": "!";
        String catStr = cat == null ? "": "@";
        r = Relation.getRelation(t.image, negStr + catStr + strArg.image,
                                 basicCatFunction, headFinder);
      } else if (numArg != null) {
        if (t.image.endsWith("-")) {
          t.image = t.image.substring(0, t.image.length()-1);
          numArg.image = "-" + numArg.image;
        }
        r = Relation.getRelation(t.image, numArg.image,
                                 basicCatFunction, headFinder);
      } else {
        r = Relation.getRelation(t.image, basicCatFunction, headFinder);
      }
      child = Node(r);
{if ("" != null) return child;}
      break;
      }
    case MULTI_RELATION:{
      t = jj_consume_token(MULTI_RELATION);
      jj_consume_token(26);
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case IDENTIFIER:
      case BLANK:
      case REGEX:
      case 14:
      case 16:
      case 17:
      case 20:
      case 21:{
        child = Node(null);
children.add(child);
        label_5:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
          case 27:{
            ;
            break;
            }
          default:
            jj_la1[25] = jj_gen;
            break label_5;
          }
          jj_consume_token(27);
          child = Node(null);
children.add(child);
        }
        break;
        }
      default:
        jj_la1[26] = jj_gen;
        ;
      }
      jj_consume_token(28);
{if ("" != null) return Relation.constructMultiRelation(t.image, children, basicCatFunction, headFinder);}
      break;
      }
    default:
      jj_la1[27] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_3R_25()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) return true;
    }
    return false;
  }

  private boolean jj_3R_9()
 {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3R_24()
 {
    if (jj_3R_25()) return true;
    return false;
  }

  private boolean jj_3R_23()
 {
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3R_20()
 {
    if (jj_scan_token(21)) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_scan_token(12)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3R_22()
 {
    if (jj_scan_token(24)) return true;
    return false;
  }

  private boolean jj_3R_16()
 {
    if (jj_scan_token(17)) return true;
    return false;
  }

  private boolean jj_3R_18()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_8()
 {
    if (jj_scan_token(14)) return true;
    return false;
  }

  private boolean jj_3R_6()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_8()) {
    jj_scanpos = xsp;
    if (jj_3R_9()) return true;
    }
    return false;
  }

  private boolean jj_3R_14()
 {
    if (jj_scan_token(23)) return true;
    return false;
  }

  private boolean jj_3R_27()
 {
    if (jj_scan_token(MULTI_RELATION)) return true;
    return false;
  }

  private boolean jj_3R_19()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(8)) {
    jj_scanpos = xsp;
    if (jj_scan_token(10)) {
    jj_scanpos = xsp;
    if (jj_scan_token(9)) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_13()
 {
    if (jj_scan_token(16)) return true;
    return false;
  }

  private boolean jj_3R_17()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_19()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3R_21()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_12()
 {
    if (jj_3R_18()) return true;
    return false;
  }

  private boolean jj_3R_10()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) return true;
    }
    }
    return false;
  }

  private boolean jj_3R_15()
 {
    if (jj_scan_token(16)) return true;
    return false;
  }

  private boolean jj_3R_29()
 {
    if (jj_scan_token(REL_W_STR_ARG)) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_scan_token(12)) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3R_28()
 {
    if (jj_scan_token(RELATION)) return true;
    return false;
  }

  private boolean jj_3R_11()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_15()) jj_scanpos = xsp;
    xsp = jj_scanpos;
    if (jj_3R_16()) jj_scanpos = xsp;
    if (jj_3R_17()) return true;
    return false;
  }

  private boolean jj_3R_21()
 {
    if (jj_scan_token(20)) return true;
    return false;
  }

  private boolean jj_3R_7()
 {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_26()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_28()) {
    jj_scanpos = xsp;
    if (jj_3R_29()) return true;
    }
    return false;
  }

  /** Generated Token Manager. */
  public TregexParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[28];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x334700,0x1814070,0x1814070,0x334700,0x10000,0x20000,0x700,0x40000,0x100000,0x100000,0x300700,0x1c14070,0x400000,0x1814070,0x1004070,0x80,0x10000,0x20000,0x700,0x10000,0x20000,0x700,0x10000,0x1014400,0x50,0x8000000,0x334700,0x70,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public TregexParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public TregexParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new TregexParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public TregexParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new TregexParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public TregexParser(TregexParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(TregexParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 28; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
          for (JJCalls jj_2_rtn : jj_2_rtns) {
              JJCalls c = jj_2_rtn;
              while (c != null) {
                  if (c.gen < jj_gen) c.first = null;
                  c = c.next;
              }
          }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop:
      for (int[] jj_expentry1 : jj_expentries) {
          int[] oldentry = (int[]) (jj_expentry1);
          if (oldentry.length == jj_expentry.length) {
              for (int i = 0; i < jj_expentry.length; i++) {
                  if (oldentry[i] != jj_expentry[i]) {
                      continue jj_entries_loop;
                  }
              }
              jj_expentries.add(jj_expentry);
              break jj_entries_loop;
          }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[29];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 28; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 29; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
