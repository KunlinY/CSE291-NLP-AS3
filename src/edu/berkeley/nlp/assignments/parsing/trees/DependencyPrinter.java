package edu.berkeley.nlp.assignments.parsing.trees;

import java.util.Collection;

public interface DependencyPrinter {
    public String dependenciesToString(GrammaticalStructure gs, Collection<TypedDependency> deps, Tree tree);
}
