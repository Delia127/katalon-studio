package com.kms.katalon.composer.testcase.exceptions;

import org.codehaus.groovy.control.CompilationFailedException;

public class GroovyParsingException extends Exception {
    private static final long serialVersionUID = 1234953841101111729L;

    public GroovyParsingException(CompilationFailedException e) {
        super(e);
    }
}
