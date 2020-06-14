package edu.berkeley.nlp.assignments.parsing.trees;

import edu.berkeley.nlp.assignments.parsing.ling.Label;


public class NamedDependency extends UnnamedDependency {

  private static final long serialVersionUID = -1635646451505721133L;

  private final Object name;

  public NamedDependency(String regent, String dependent, Object name) {
    super(regent, dependent);
    this.name = name;
  }

  public NamedDependency(Label regent, Label dependent, Object name) {
    super(regent, dependent);
    this.name = name;
  }

  @Override
  public Object name() {
    return name;
  }

  @Override
  public int hashCode() {
    return regentText.hashCode() ^ dependentText.hashCode() ^ name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if ( !(o instanceof NamedDependency)) {
      return false;
    }
    NamedDependency d = (NamedDependency) o;
    return equalsIgnoreName(o) && name.equals(d.name);
  }

  @Override
  public String toString() {
    return String.format("%s --%s--> %s", regentText, name.toString(), dependentText);
  }
  
  
  @Override
  public String toString(String format) {
    return "";
  }

  @Override
  public DependencyFactory dependencyFactory() {
    return DependencyFactoryHolder.df;
  }
  
  public static DependencyFactory factory() {
    return DependencyFactoryHolder.df;
  }

  // extra class guarantees correct lazy loading (Bloch p.194)
  private static class DependencyFactoryHolder {
    private static final DependencyFactory df = new NamedDependencyFactory();
  }

  /**
   * A <code>DependencyFactory</code> acts as a factory for creating objects
   * of class <code>Dependency</code>
   */
  private static class NamedDependencyFactory implements DependencyFactory {
    /**
     * Create a new <code>Dependency</code>.
     */
    public Dependency<Label, Label, Object> newDependency(Label regent, Label dependent) {
      return newDependency(regent, dependent, null);
    }

    /**
     * Create a new <code>Dependency</code>.
     */
    public Dependency<Label, Label, Object> newDependency(Label regent, Label dependent, Object name) {
      return new NamedDependency(regent, dependent, name);
    }
  }
}
