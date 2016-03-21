package de.unistuttgart.iaas.bpmn.cycle;

public class CycleException extends Exception {
	  public CycleException() { super(); }
	  public CycleException(String message) { super(message); }
	  public CycleException(String message, Throwable cause) { super(message, cause); }
	  public CycleException(Throwable cause) { super(cause); }
	}