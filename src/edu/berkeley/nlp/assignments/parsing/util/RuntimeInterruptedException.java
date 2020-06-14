package edu.berkeley.nlp.assignments.parsing.util;



public class RuntimeInterruptedException extends RuntimeException {
  public RuntimeInterruptedException() {
    super();
  }

  public RuntimeInterruptedException(InterruptedException e) {
    super(e);
  }
}