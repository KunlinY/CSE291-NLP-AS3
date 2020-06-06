package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.assignments.parsing.*;
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
		CounterMap<List<String>, Tree<String>> knownParses;
		CounterMap<Integer, String> spanToCategories;
		SimpleLexicon lexicon;

		public Tree<String> getBestParse(List<String> sentence) {
			List<String> tags = this.getBaselineTagging(sentence);
			Tree<String> annotatedBestParse = null;
			if (this.knownParses.keySet().contains(tags)) {
				annotatedBestParse = this.getBestKnownParse(tags);
			} else {
				annotatedBestParse = this.buildRightBranchParse(sentence, tags);
			}

			return TreeAnnotations.unAnnotateTree(annotatedBestParse);
		}

		private Tree<String> buildRightBranchParse(List<String> words, List<String> tags) {
			int currentPosition = words.size() - 1;

			Tree rightBranchTree;
			for(rightBranchTree = this.buildTagTree(words, tags, currentPosition); currentPosition > 0; rightBranchTree = this.merge(this.buildTagTree(words, tags, currentPosition), rightBranchTree)) {
				--currentPosition;
			}

			rightBranchTree = this.addRoot(rightBranchTree);
			return rightBranchTree;
		}

		private Tree<String> merge(Tree<String> leftTree, Tree<String> rightTree) {
			int span = leftTree.getYield().size() + rightTree.getYield().size();
			String mostFrequentLabel = (String)this.spanToCategories.getCounter(span).argMax();
			if (mostFrequentLabel == null) {
				mostFrequentLabel = "NP";
			}

			List<Tree<String>> children = new ArrayList();
			children.add(leftTree);
			children.add(rightTree);
			return new Tree(mostFrequentLabel, children);
		}

		private Tree<String> addRoot(Tree<String> tree) {
			return new Tree("ROOT", Collections.singletonList(tree));
		}

		private Tree<String> buildTagTree(List<String> words, List<String> tags, int currentPosition) {
			Tree<String> leafTree = new Tree(words.get(currentPosition));
			Tree<String> tagTree = new Tree(tags.get(currentPosition), Collections.singletonList(leafTree));
			return tagTree;
		}

		private Tree<String> getBestKnownParse(List<String> tags) {
			return (Tree)this.knownParses.getCounter(tags).argMax();
		}

		private List<String> getBaselineTagging(List<String> sentence) {
			List<String> tags = new ArrayList();
			Iterator var3 = sentence.iterator();

			while(var3.hasNext()) {
				String word = (String)var3.next();
				String tag = this.getBestTag(word);
				tags.add(tag);
			}

			return tags;
		}

		private String getBestTag(String word) {
			double bestScore = -1.0D / 0.0;
			String bestTag = null;
			Iterator var5 = this.lexicon.getAllTags().iterator();

			while(true) {
				String tag;
				double score;
				do {
					if (!var5.hasNext()) {
						return bestTag;
					}

					tag = (String)var5.next();
					score = this.lexicon.scoreTagging(word, tag);
				} while(bestTag != null && score <= bestScore);

				bestScore = score;
				bestTag = tag;
			}
		}

		public GenerativeParser(List<Tree<String>> trainTrees) {
			System.out.print("Annotating / binarizing training trees ... ");
			List<Tree<String>> annotatedTrainTrees = this.annotateTrees(trainTrees);
			System.out.println("done.");
			System.out.print("Building grammar ... ");
			Grammar grammar = Grammar.generativeGrammarFromTrees(annotatedTrainTrees);
			System.out.println("done. (" + grammar.getLabelIndexer().size() + " states)");
			System.out.print("Discarding grammar and setting up a baseline parser ... ");
			this.lexicon = new SimpleLexicon(annotatedTrainTrees);
			this.knownParses = new CounterMap();
			this.spanToCategories = new CounterMap();
			Iterator var4 = annotatedTrainTrees.iterator();

			while(var4.hasNext()) {
				Tree<String> trainTree = (Tree)var4.next();
				List<String> tags = trainTree.getPreTerminalYield();
				this.knownParses.incrementCount(tags, trainTree, 1.0D);
				this.tallySpans(trainTree, 0);
			}

			System.out.println("done.");
		}

		private List<Tree<String>> annotateTrees(List<Tree<String>> trees) {
			List<Tree<String>> annotatedTrees = new ArrayList();
			Iterator var3 = trees.iterator();

			while(var3.hasNext()) {
				Tree<String> tree = (Tree)var3.next();
				annotatedTrees.add(TreeAnnotations.annotateTreeLosslessBinarization(tree));
			}

			return annotatedTrees;
		}

		private List<Tree<String>> annotateAndBinarizerTree(List<Tree<String>> trees) {
			List<Tree<String>> annotatedTrees = new ArrayList();
			Iterator var3 = trees.iterator();

			while(var3.hasNext()) {
				Tree<String> tree = (Tree)var3.next();
				Tree<String> copy = tree.deepCopy();
				copy = this.transformTree(copy, copy);
				annotatedTrees.add(copy);
			}

			return annotatedTrees;
		}

		private Tree<String> transformTree(Tree<String> t, Tree<String> root) {
			if (t == null) {
				return null;
			}
			if (t.isLeaf()) {
				return t;
			}

			String cat = t.getLabel();
			Tree<String> parent;
			String parentStr;
			String grandParentStr;

			if (root == null || treeEqual(t, root)) {
				parent = null;
				parentStr = "";
			} else {
				parent = getParent(t, root);
				parentStr = parent.getLabel();
			}

			if (parent == null || treeEqual(parent, root)) {
				grandParentStr = "";
			} else {
				grandParentStr = getParent(parent, root).getLabel();
			}

			if (t.isPreTerminal()) {

			}
		}

		private Tree<String> getParent(Tree<String> t, Tree<String> parent) {
			for (Tree<String> kid: parent.getChildren()) {
				if (treeEqual(t, kid)) {
					return parent;
				}
				Tree<String> ret = getParent(t, kid);
				if (ret != null) {
					return ret;
				}
			}
			return null;
		}

		private boolean treeEqual(Tree<String> a, Tree<String> b) {
			if (a == b) {
				return true;
			}

			String aLabel = a.getLabel();
			String bLabel = b.getLabel();
			if (aLabel != null || bLabel != null) {
				if (aLabel == null || bLabel == null || !aLabel.equals(bLabel)) {
					return false;
				}
			}

			List<Tree<String>> aChildren = a.getChildren();
			List<Tree<String>> bChildren = b.getChildren();
			if (aChildren.size() != bChildren.size()) {
				return false;
			}

			for (int i = 0; i < aChildren.size(); i++) {
				if (!treeEqual(aChildren.get(i), bChildren.get(i))) {
					return false;
				}
			}
			return true;
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
