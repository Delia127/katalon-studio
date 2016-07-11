package com.kms.katalon.composer.codeassist.proposal.completion;

import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyJavaFieldCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.testcase.constants.ImageConstants;

@SuppressWarnings("restriction")
public class KatalonLocalVariableCompletionProposal extends GroovyCompletionProposal {

    private static final Image KATALON_IMAGE = ImageConstants.IMG_16_VARIABLE;

    private String variableName;
    private KatalonJavaLocalVariableCompletionProposal completionProposal;
    public KatalonLocalVariableCompletionProposal(ContentAssistContext context, String variableName) {
        super(GroovyCompletionProposal.FIELD_REF, context.completionLocation);
        setName(variableName.toCharArray());
        setTypeName(Object.class.getName().toCharArray());
        setRelevance(Relevance.HIGH.getRelavance());

        String replaceString = variableName.substring(context.completionExpression.length(), variableName.length());
        setCompletion(replaceString.toCharArray());

        setReplaceRange(context.completionLocation - context.completionExpression.length(), context.completionLocation
                + replaceString.length() - context.completionExpression.length());
        setVariableName(variableName);
        completionProposal = new KatalonJavaLocalVariableCompletionProposal(this);
    }

    public String getVariableName() {
        return variableName;
    }

    private void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public IJavaCompletionProposal getCompletionProposal() {
        return completionProposal;
    }

    private class KatalonJavaLocalVariableCompletionProposal extends GroovyJavaFieldCompletionProposal {

        public KatalonJavaLocalVariableCompletionProposal(KatalonLocalVariableCompletionProposal proposal) {
            super(proposal, KATALON_IMAGE,
                    new StyledString(proposal.getVariableName()).append(KatalonContextUtil.getKatalonSignature()));
        }
    }

}
