package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.assignments.parsing.Parser;
import edu.berkeley.nlp.assignments.parsing.ParserFactory;
import edu.berkeley.nlp.assignments.parsing.TreeAnnotations;
import edu.berkeley.nlp.assignments.parsing.ling.CoreLabel;
import edu.berkeley.nlp.assignments.parsing.ling.HasWord;
import edu.berkeley.nlp.assignments.parsing.ling.Label;
import edu.berkeley.nlp.assignments.parsing.parser.lexparser.*;
import edu.berkeley.nlp.assignments.parsing.trees.LabeledScoredTreeFactory;
import edu.berkeley.nlp.assignments.parsing.trees.LabeledScoredTreeNode;
import edu.berkeley.nlp.assignments.parsing.trees.TreeTransformer;
import edu.berkeley.nlp.assignments.parsing.util.HashIndex;
import edu.berkeley.nlp.assignments.parsing.util.Index;
import edu.berkeley.nlp.assignments.parsing.util.Pair;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.CounterMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GenerativeParserFactory implements ParserFactory {
	public GenerativeParserFactory() {
	}

	public Parser getParser(List<Tree<String>> trainTrees) {
		return new GenerativeParser(trainTrees);
	}

	public static class GenerativeParser implements Parser {
		CounterMap<Integer, String> spanToCategories;
//		SimpleLexicon lexicon;
//		Grammar grammar;
		LabeledScoredTreeFactory lf = new LabeledScoredTreeFactory();
		LexicalizedParser lp;
		ExhaustivePCFGParser pparser;
		TreeTransformer debinarizer = new Debinarizer(false);
		TreeTransformer subcategoryStripper;
		TreeAnnotatorAndBinarizer binarizer;

		public Tree<String> getBestParse(List<String> sentence) {
			List<HasWord> sentenceB = new ArrayList<>();
			for (String word : sentence) {
				CoreLabel w = new CoreLabel();
				w.setWord(word);
				w.setValue(word);
				sentenceB.add(w);
			}
			CoreLabel boundary = new CoreLabel();
			boundary.setWord(Lexicon.BOUNDARY);
			boundary.setValue(Lexicon.BOUNDARY);
			boundary.setTag(Lexicon.BOUNDARY_TAG);
			boundary.setIndex(sentence.size()+1);//1-based indexing used in the parser
			sentenceB.add(boundary);

			pparser.parse(sentenceB);

			edu.berkeley.nlp.assignments.parsing.trees.Tree tree = pparser.getBestParse();
			if (tree != null) {
				tree = debinarizer.transformTree(tree);
				tree = subcategoryStripper.transformTree(tree);

				List<edu.berkeley.nlp.assignments.parsing.trees.Tree> leaves = tree.getLeaves();
				Iterator<edu.berkeley.nlp.assignments.parsing.trees.Tree> leafIterator = leaves.iterator();
				sentenceB.remove(sentenceB.size() - 1);
				for (HasWord word : sentenceB) {
					edu.berkeley.nlp.assignments.parsing.trees.Tree leaf = leafIterator.next();
					if (!(word instanceof Label)) {
						continue;
					}
					leaf.setLabel((Label) word);
				}

				return TreeAnnotations.unAnnotateTree(convertBack(tree));
			}
			return new Tree<String>("ROOT", Collections.singletonList(new Tree<String>("JUNK")));
		}

		public GenerativeParser(List<Tree<String>> trainTrees) {
//			List<Tree<String>> annotatedTrainTrees = this.annotateTrees(trainTrees);
			Options op = new Options();
			op.doDep = false;
			op.testOptions.iterativeCKY = false;
			binarizer = new TreeAnnotatorAndBinarizer(op.tlpParams, op.forceCNF, !op.trainOptions.outsideFactor(), !op.trainOptions.predictSplits, op);

			List<edu.berkeley.nlp.assignments.parsing.trees.Tree> treeBank = convertTrainTrees(trainTrees);

			Index<String> stateIndex;
			Index<String> wordIndex;
			Index<String> tagIndex;

			Pair<UnaryGrammar, BinaryGrammar> bgug;

			stateIndex = new HashIndex<>();
			wordIndex = new HashIndex<>();
			tagIndex = new HashIndex<>();

			BinaryGrammarExtractor bgExtractor = new BinaryGrammarExtractor(stateIndex);
			bgug = bgExtractor.extract(treeBank);

			double trainSize = treeBank.size();

			Lexicon lex = op.tlpParams.lex(op, wordIndex, tagIndex);
			lex.initializeTraining(trainSize);
			lex.train(treeBank);
			lex.finishTraining();

			BinaryGrammar bg = bgug.second;
			bg.splitRules();
			UnaryGrammar ug = bgug.first;
			ug.purgeRules();

			lp = new LexicalizedParser(lex, bg, ug, null, stateIndex, wordIndex, tagIndex, op);
			pparser = new ExhaustivePCFGParser(bg, ug, lex, op, stateIndex, wordIndex, tagIndex);
			subcategoryStripper = op.tlpParams.subcategoryStripper();

//			this.grammar = Grammar.generativeGrammarFromTrees(annotatedTrainTrees);
//			System.out.println("done. (" + this.grammar.getLabelIndexer().size() + " states)");
//			System.out.print("Discarding grammar and setting up a baseline parser ... ");
//			this.lexicon = new SimpleLexicon(annotatedTrainTrees);
//			this.spanToCategories = new CounterMap();
//			Iterator var4 = annotatedTrainTrees.iterator();
//
//			while(var4.hasNext()) {
//				Tree<String> trainTree = (Tree)var4.next();
//				this.tallySpans(trainTree, 0);
//			}

			System.out.println("done.");
		}

		public List<edu.berkeley.nlp.assignments.parsing.trees.Tree> convertTrainTrees(List<Tree<String>> trainTrees) {
			List<edu.berkeley.nlp.assignments.parsing.trees.Tree> newTrees = new ArrayList<>();
			for (Tree<String> tree: trainTrees) {
				newTrees.add(binarizer.transformTree(convert(tree)));
			}
			return newTrees;
		}

		private LabeledScoredTreeNode convert(Tree<String> tree) {
			if (tree.isLeaf()) {
				return (LabeledScoredTreeNode)lf.newLeaf(tree.getLabel());
			}

			List<edu.berkeley.nlp.assignments.parsing.trees.Tree> children = new ArrayList<>();
			for (Tree<String> t : tree.getChildren()) {
				LabeledScoredTreeNode tt = convert(t);
				children.add(tt);
			}
			return (LabeledScoredTreeNode)lf.newTreeNode(tree.getLabel(), children);
		}

		public List<Tree<String>> convertBackTrees(List<edu.berkeley.nlp.assignments.parsing.trees.Tree> trees) {
			List<Tree<String>> newTrees = new ArrayList<>();
			for (edu.berkeley.nlp.assignments.parsing.trees.Tree tree : trees) {
				newTrees.add(convertBack(tree));
			}
			return newTrees;
		}

		public Tree<String> convertBack(edu.berkeley.nlp.assignments.parsing.trees.Tree tree) {
			if (tree.isLeaf()) {
				return new Tree<String>(tree.label().value());
			}

			List<Tree<String>> children = new ArrayList<>();
			for (edu.berkeley.nlp.assignments.parsing.trees.Tree t : tree.children()) {
				Tree<String> tt = convertBack(t);
				children.add(tt);
			}
			return new Tree<>(tree.label().value(), children);
		}

		private List<Tree<String>> annotateTrees(List<Tree<String>> trees) {
			List<Tree<String>> annotatedTrees = new ArrayList();
			Iterator var3 = trees.iterator();
			TreeBinarizer binarizer = new TreeBinarizer();

			while(var3.hasNext()) {
				Tree<String> tree = (Tree)var3.next();
				Tree<String> binarized = binarizer.transformTree(annotator(tree, ""));
				annotatedTrees.add(binarized);
//				annotatedTrees.add(TreeAnnotations.annotateTreeLosslessBinarization(tree));
			}

			return annotatedTrees;
		}

		private Tree<String> annotator(Tree<String> t, String parentStr) {

			if (t.isLeaf() || t.isPreTerminal()) {
				return t;
			}

			List<Tree<String>> children = new ArrayList();
			String label = t.getLabel();

			// handle root
			if (parentStr.length() != 0) {
				t.setLabel(label + "^" + parentStr);
			}

			for (Tree<String> tt: t.getChildren()) {
				Tree<String> child = annotator(tt, label);
				children.add(child);
			}

			if (parentStr.length() == 0) {
				children.add(
						new Tree<String>(
								".$$.",
								new ArrayList<Tree<String>>(List.of(new Tree<String>(".$.")))
						));
			}

			t.setChildren(children);
			return t;
		}

		private int tallySpans(Tree<String> tree, int start) {
			if (!tree.isLeaf() && !tree.isPreTerminal()) {
				int end = start;

				int childSpan;
				for(Iterator var4 = tree.getChildren().iterator(); var4.hasNext(); end += childSpan) {
					Tree<String> child = (Tree)var4.next();
					childSpan = this.tallySpans(child, end);
				}

				String category = (String)tree.getLabel();
				if (!category.equals("ROOT")) {
					this.spanToCategories.incrementCount(end - start, category, 1.0D);
				}

				return end - start;
			} else {
				return 1;
			}
		}

	}
}
