package com.kms.katalon.composer.codeassist.proposal;

import org.codehaus.groovy.eclipse.codeassist.proposals.AbstractGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import com.kms.katalon.composer.codeassist.proposal.completion.KatalonLocalVariableCompletionProposal;

public class KatalonLocalVariableProposal extends AbstractGroovyProposal {

    private String variableName;

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public KatalonLocalVariableProposal(String variableName) {
        setVariableName(variableName);
    }

    @Override
    public IJavaCompletionProposal createJavaProposal(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext) {
        if (variableName.startsWith(context.completionExpression)) {
            KatalonLocalVariableCompletionProposal proposal = new KatalonLocalVariableCompletionProposal(context,
                    variableName);
            return proposal.getCompletionProposal();
        }
        return null;
    }
}
