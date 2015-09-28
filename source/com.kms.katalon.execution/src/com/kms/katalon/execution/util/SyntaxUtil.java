package com.kms.katalon.execution.util;

import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;

public class SyntaxUtil {
    public static final String LINE_SEPERATOR = System.getProperty("line.separator");
    
    public static boolean checkVariableSyntax(String variableName, String variableValue) {
        try {
            StringBuilder builder = new StringBuilder("[").append(variableName).append(":").append(variableValue).append("]");
            new AstBuilder().buildFromString(CompilePhase.CONVERSION, builder.toString());
            return true;
        } catch (MultipleCompilationErrorsException e) {
        	StringBuilder errorMessageBuilder = new StringBuilder();
            for (Object error : e.getErrorCollector().getErrors()) {
                if (error instanceof SyntaxErrorMessage) {
                    SyntaxException syntaxException =((SyntaxErrorMessage) error).getCause(); 
                    errorMessageBuilder.append(syntaxException.getMessage());
                }                
            }
            throw new IllegalArgumentException(errorMessageBuilder.toString());
        }
    }
    
    public static boolean checkScriptSyntax(String source) {
        try {
            new AstBuilder().buildFromString(CompilePhase.CONVERSION, source);
            return true;
        } catch (MultipleCompilationErrorsException e) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            for (Object error : e.getErrorCollector().getErrors()) {
                if (error instanceof SyntaxErrorMessage) {
                    SyntaxException syntaxException =((SyntaxErrorMessage) error).getCause(); 
                    errorMessageBuilder.append(syntaxException.getMessage());
                }                
            }
            throw new IllegalArgumentException(errorMessageBuilder.toString());
        }
    }
}
