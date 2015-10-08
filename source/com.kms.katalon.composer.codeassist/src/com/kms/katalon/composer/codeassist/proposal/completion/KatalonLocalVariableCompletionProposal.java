package com.kms.katalon.composer.codeassist.proposal.completion;

import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

@SuppressWarnings("restriction")
public class KatalonLocalVariableCompletionProposal extends GroovyCompletionProposal {

    private String variableName;
    
	public KatalonLocalVariableCompletionProposal(ContentAssistContext context, String variableName) {
		super(GroovyCompletionProposal.FIELD_REF, context.completionLocation);
		setName(variableName.toCharArray());
		setTypeName(Object.class.getName().toCharArray());
		setRelevance(Relevance.HIGH.getRelavance());
		
		String replaceString = variableName.substring(context.completionExpression.length(), variableName.length());
		setCompletion(replaceString.toCharArray());
		
		setReplaceRange(context.completionLocation - context.completionExpression.length(), 
				context.completionLocation +  replaceString.length() - context.completionExpression.length());
		setVariableName(variableName);
	}

    public String getVariableName() {
        return variableName;
    }

    private void setVariableName(String variableName) {
        this.variableName = variableName;
    }
}
