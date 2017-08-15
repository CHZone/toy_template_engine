/*******************************************************************************
 * This file is part of Pebble.
 * 
 * Copyright (c) 2012 Mitchell Bosecke.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 
 * Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/
 ******************************************************************************/
package chzone.template_engine.error;

public class TemplateException extends Exception {

	private static final long serialVersionUID = -2855774187093732189L;

	protected Integer lineNumber;
	protected String filename;
	protected String message;
	protected TemplateException previous;

	public TemplateException(String message) {
		this(message, null, null);
	}

	public TemplateException(String message, Integer lineNumber, String filename) {
		this(message, lineNumber, filename, null);
	}

	public TemplateException(String message, Integer lineNumber, String filename,
			TemplateException previous) {
		super(String.format("%s(%s:%d)", message, filename, lineNumber));
		this.message = message;
		this.lineNumber = lineNumber;
		this.filename = filename;
		this.previous = previous;
	}

}
