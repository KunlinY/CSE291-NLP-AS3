package edu.berkeley.nlp.assignments.parsing.util;

import java.lang.reflect.*;
import java.util.*;


public class MetaClass {

  public static class ClassCreationException extends RuntimeException {

    private static final long serialVersionUID = -5980065992461870357L;

    private ClassCreationException() {
      super();
    }

    private ClassCreationException(String msg) {
      super(msg);
    }

    private ClassCreationException(Throwable cause) {
      super(cause);
    }

    private ClassCreationException(String msg, Throwable cause) {
      super(msg, cause);
    }

  }

  public static final class ConstructorNotFoundException extends ClassCreationException {
    private static final long serialVersionUID = -5980065992461870357L;

    private ConstructorNotFoundException() {
      super();
    }

    private ConstructorNotFoundException(String msg) {
      super(msg);
    }

    private ConstructorNotFoundException(Throwable cause) {
      super(cause);
    }

    private ConstructorNotFoundException(String msg, Throwable cause) {
      super(msg, cause);
    }


  }

  public static final class ClassFactory<T> {
    private Class<?>[] classParams;
    private Class<T> cl;
    private Constructor<T> constructor;

    private static boolean samePrimitive(Class<?> a, Class<?> b){
      if(!a.isPrimitive() && !b.isPrimitive()) return false;
      if(a.isPrimitive()){
        try {
          Class<?> type = (Class<?>) b.getField("TYPE").get(null);
          return type.equals(a);
        } catch (Exception e) {
          return false;
        }
      }
      if(b.isPrimitive()){
        try {
          Class<?> type = (Class<?>) a.getField("TYPE").get(null);
          return type.equals(b);
        } catch (Exception e) {
          return false;
        }
      }
      throw new IllegalStateException("Impossible case");
    }

    private static int superDistance(Class<?> candidate, Class<?> target) {
      if (candidate == null) {
        // --base case: does not implement
        return Integer.MIN_VALUE;
      } else if (candidate.equals(target)) {
        // --base case: exact match
        return 0;
      } else if(samePrimitive(candidate, target)){
        // --base case: primitive and wrapper
        return 0;
      } else {
        // --recursive case: try superclasses
        // case: direct superclass
        Class<?> directSuper = candidate.getSuperclass();
        int superDist = superDistance(directSuper, target);
        if (superDist >= 0)
          return superDist + 1; // case: superclass distance
        // case: implementing interfaces
        Class<?>[] interfaces = candidate.getInterfaces();
        int minDist = Integer.MAX_VALUE;
        for (Class<?> i : interfaces) {
          superDist = superDistance(i, target);
          if (superDist >= 0) {
            minDist = Math.min(minDist, superDist);
          }
        }
        if (minDist != Integer.MAX_VALUE)
          return minDist + 1; // case: interface distance
        else
          return -1; // case: failure
      }
    }

    @SuppressWarnings("unchecked")
    private void construct(String classname, Class<?>... params)
        throws ClassNotFoundException, NoSuchMethodException {
      // (save class parameters)
      this.classParams = params;
      // (create class)
      try {
        this.cl = (Class<T>) Class.forName(classname);
      } catch (ClassCastException e) {
        throw new ClassCreationException("Class " + classname
            + " could not be cast to the correct type");
      }
      // --Find Constructor
      // (get constructors)
      Constructor<?>[] constructors = cl.getDeclaredConstructors();
      Constructor<?>[] potentials = new Constructor<?>[constructors.length];
      Class<?>[][] constructorParams = new Class<?>[constructors.length][];
      int[] distances = new int[constructors.length]; //distance from base class
      // (filter: length)
      for (int i = 0; i < constructors.length; i++) {
        constructorParams[i] = constructors[i].getParameterTypes();
        if (params.length == constructorParams[i].length) { // length is good
          potentials[i] = constructors[i];
          distances[i] = 0;
        } else { // length is bad
          potentials[i] = null;
          distances[i] = -1;
        }
      }
      // (filter:type)
      for (int paramIndex = 0; paramIndex < params.length; paramIndex++) { // for each parameter...
        Class<?> target = params[paramIndex];
        for (int conIndex = 0; conIndex < potentials.length; conIndex++) { // for each constructor...
          if (potentials[conIndex] != null) { // if the constructor is in the pool...
            Class<?> cand = constructorParams[conIndex][paramIndex];
            int dist = superDistance(target, cand);
            if (dist >= 0) { // and if the constructor matches...
              distances[conIndex] += dist; // keep it
            } else {
              potentials[conIndex] = null; // else, remove it from the pool
              distances[conIndex] = -1;
            }
          }
        }
      }
      // (filter:min)
      this.constructor = (Constructor<T>) argmin(potentials, distances, 0);
      if (this.constructor == null) {
        StringBuilder b = new StringBuilder();
        b.append(classname).append("(");
        for (Class<?> c : params) {
          b.append(c.getName()).append(", ");
        }
        String target = b.substring(0, params.length==0 ? b.length() : b.length() - 2) + ")";
        throw new ConstructorNotFoundException(
            "No constructor found to match: " + target);
      }
    }

    private ClassFactory(String classname, Class<?>... params)
        throws ClassNotFoundException, NoSuchMethodException {
      // (generic construct)
      construct(classname, params);
    }

    private ClassFactory(String classname, Object... params)
        throws ClassNotFoundException, NoSuchMethodException {
      // (convert class parameters)
      Class<?>[] classParams = new Class[params.length];
      for (int i = 0; i < params.length; i++) {
        if(params[i] == null) throw new ClassCreationException("Argument " + i + " to class constructor is null");
        classParams[i] = params[i].getClass();
      }
      // (generic construct)
      construct(classname, classParams);
    }

    private ClassFactory(String classname, String... params)
        throws ClassNotFoundException, NoSuchMethodException {
      // (convert class parameters)
      Class<?>[] classParams = new Class[params.length];
      for (int i = 0; i < params.length; i++) {
        classParams[i] = Class.forName(params[i]);
      }
      // (generic construct)
      construct(classname, classParams);
    }

    
    public T createInstance(Object... params) {
      try {
        boolean accessible = true;
        if(!constructor.isAccessible()){
          accessible = false;
          constructor.setAccessible(true);
        }
        T rtn = constructor.newInstance(params);
        if(!accessible){ constructor.setAccessible(false); }
        return rtn;
      } catch (Exception e) {
        throw new ClassCreationException("MetaClass couldn't create " + constructor + " with args " + Arrays.toString(params), e);
      }
    }

    
    public String getName() {
      return cl.getName();
    }

    @Override
    public String toString() {
      StringBuilder b = new StringBuilder();
      b.append(cl.getName()).append('(');
      for (Class<?> cl : classParams) {
        b.append(' ').append(cl.getName()).append(',');
      }
      b.replace(b.length() - 1, b.length(), " ");
      b.append(')');
      return b.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
      if (o instanceof ClassFactory) {
        ClassFactory other = (ClassFactory) o;
        if (!this.cl.equals(other.cl))
          return false;
        for (int i = 0; i < classParams.length; i++) {
          if (!this.classParams[i].equals(other.classParams[i]))
            return false;
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return cl.hashCode();
    }

  } // end static class ClassFactory

  private String classname;

  
  public MetaClass(String classname) {
    this.classname = classname;
  }

  
  public MetaClass(Class<?> classname) {
    this.classname = classname.getName();
  }

  
  public <E> ClassFactory<E> createFactory(Class<?>... classes) {
    try {
      return new ClassFactory<>(classname, classes);
    } catch (ClassCreationException e){
      throw e;
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
  }

  
  public <E> ClassFactory<E> createFactory(String... classes) {
    try {
      return new ClassFactory<>(classname, classes);
    } catch (ClassCreationException e){
      throw e;
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
  }

  
  public <E> ClassFactory<E> createFactory(Object... objects) {
    try {
      return new ClassFactory<>(classname, objects);
    } catch (ClassCreationException e){
      throw e;
    } catch (Exception e) {
      throw new ClassCreationException(e);
    }
  }

  
  public <E> E createInstance(Object... objects) {
    ClassFactory<E> fact = createFactory(objects);
    return fact.createInstance(objects);
  }

  
  @SuppressWarnings("unchecked")
  public <E,F extends E> F createInstance(Class<E> type, Object... params) {
    Object obj = createInstance(params);
    if (type.isInstance(obj)) {
      return (F) obj;
    } else {
      throw new ClassCreationException("Cannot cast " + classname
          + " into " + type.getName());
    }
  }

  @Override
  public String toString() {
    return classname;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof MetaClass) {
      return ((MetaClass) o).classname.equals(this.classname);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return classname.hashCode();
  }

  
  public static MetaClass create(String classname) {
    return new MetaClass(classname);
  }

  
  public static MetaClass create(Class <?> clazz) {
    return new MetaClass(clazz);
  }

  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static <E> E cast(String value, Type type){
    return null;
  }

  private static <E> E argmin(E[] elems, int[] scores, int atLeast) {
    int argmin = argmin(scores, atLeast);
    return argmin >= 0 ? elems[argmin] : null;
  }

  private static int argmin(int[] scores, int atLeast) {
    int min = Integer.MAX_VALUE;
    int argmin = -1;
    for(int i=0; i<scores.length; i++){
      if(scores[i] < min && scores[i] >= atLeast){
        min = scores[i];
        argmin = i;
      }
    }
    return argmin;
  }

  private static final HashMap<Class, MetaClass> abstractToConcreteCollectionMap = new HashMap<>();
  static {
    abstractToConcreteCollectionMap.put(Collection.class, MetaClass.create(ArrayList.class));
    abstractToConcreteCollectionMap.put(List.class, MetaClass.create(ArrayList.class));
    abstractToConcreteCollectionMap.put(Set.class, MetaClass.create(HashSet.class));
    abstractToConcreteCollectionMap.put(Queue.class, MetaClass.create(LinkedList.class));
    abstractToConcreteCollectionMap.put(Deque.class, MetaClass.create(LinkedList.class));
  }

}
