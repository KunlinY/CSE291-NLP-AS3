package edu.berkeley.nlp.assignments.parsing.trees;

import java.io.IOException;
import java.util.List;

public interface DependencyReader {

  List<GrammaticalStructure> readDependencies(String fileName) throws IOException;

}
