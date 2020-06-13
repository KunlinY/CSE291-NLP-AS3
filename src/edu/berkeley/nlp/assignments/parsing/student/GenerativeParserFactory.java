package edu.berkeley.nlp.assignments.parsing.student;

import edu.berkeley.nlp.assignments.parsing.*;
import edu.berkeley.nlp.ling.Tree;
import edu.berkeley.nlp.util.CounterMap;

import java.util.*;

public class GenerativeParserFactory implements ParserFactory {
	public GenerativeParserFactory() {
	}

	public Parser getParser(List<Tree<String>> trainTrees) {
		return new GenerativeParser(trainTrees);
	}

	public static class GenerativeParser implements Parser {
		CounterMap<Integer, String> spanToCategories;
		SimpleLexicon lexicon;
		Grammar grammar;

		public Tree<String> getBestParse(List<String> sentence) {

			List<String> tags = this.getBaselineTagging(sentence);
			Tree<String> annotatedBestParse = this.buildRightBranchParse(sentence, tags);

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
			this.grammar = Grammar.generativeGrammarFromTrees(annotatedTrainTrees);
			System.out.println("done. (" + this.grammar.getLabelIndexer().size() + " states)");
			System.out.print("Discarding grammar and setting up a baseline parser ... ");
			this.lexicon = new SimpleLexicon(annotatedTrainTrees);
			this.spanToCategories = new CounterMap();
			Iterator var4 = annotatedTrainTrees.iterator();

			while(var4.hasNext()) {
				Tree<String> trainTree = (Tree)var4.next();
				this.tallySpans(trainTree, 0);
			}

			System.out.println("done.");
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
