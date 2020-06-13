/**
 * <p>
 * This package provides a representation of dependency graphs (normally the
 * collapsed Stanford Dependencies representation) as a graph (backed by
 * the jgrapht graph library.
 * </p>
 * <p>To create a typed dependency graph, a <code>SemanticGraph</code> from a parse tree,
 * <blockquote>
 * <pre>
 * import edu.berkeley.nlp.assignments.parsing.trees.semgraph.*;
 * ...
 * Tree treeParse = processSentence(sentence);
 * SemanticGraph depGraph = SemanticGraphFactory.allTypedDependencies(treeParse, true);
 * </pre>
 * </blockquote>
 */
package edu.berkeley.nlp.assignments.parsing.semgraph;