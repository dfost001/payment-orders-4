package com.mycompany.hosted.checkoutFlow.exceptions;

@SuppressWarnings("serial")
public class ProcessorResponseNullException extends Exception {
	
	private static String issue = "Service did not return a processor code";
	
	public ProcessorResponseNullException() {
		super(issue) ;
	}

}
