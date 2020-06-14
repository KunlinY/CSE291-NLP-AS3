package edu.berkeley.nlp.assignments.parsing.util;



public class ReflectionLoading {

  // static methods only
  private ReflectionLoading() {}

  
  @SuppressWarnings("unchecked")
  public static <T> T loadByReflection(String className,
                                       Object ... arguments) {
    try{
      return (T) new MetaClass(className).createInstance(arguments);
    } catch (Exception e) {
      throw new ReflectionLoadingException("Error creating " + className, e);
    }
  }

  
  public static class ReflectionLoadingException extends RuntimeException {

    private static final long serialVersionUID = -3324911744277952585L;


    public ReflectionLoadingException(String message, Throwable reason) {
      super(message, reason);
    }

  }

}
