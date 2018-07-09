package com.kms.katalon.composer.codeassist.processor;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.processors.PackageCompletionProcessor;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistLocation;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.ui.text.java.JavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

@SuppressWarnings("restriction")
public class ImprovedPackageCompletionProcessor extends PackageCompletionProcessor {

    public ImprovedPackageCompletionProcessor(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext, SearchableEnvironment nameEnvironment) {
        super(context, javaContext, nameEnvironment);
    }

    public List<ICompletionProposal> generateProposals(IProgressMonitor monitor) {
        List<ICompletionProposal> typeProposals = super.generateProposals(monitor);
        if (typeProposals == null || typeProposals.isEmpty()) {
            return typeProposals;
        }
        if (getContext().location == ContentAssistLocation.IMPORT) {
            List<ICompletionProposal> newTypeProposals = new ArrayList<>();
            typeProposals.stream().forEach(proposal -> {
                newTypeProposals.add(getValidProposal(proposal));
            });
            return newTypeProposals;
        }
        return typeProposals;
    }

    private ICompletionProposal getValidProposal(ICompletionProposal proposal) {
        ICompletionProposal validProposal = proposal;
        if (proposal instanceof JavaTypeCompletionProposal) {
            JavaTypeCompletionProposal oldProposal = (JavaTypeCompletionProposal) proposal;
            String qualifier = getContext().fullCompletionExpression.substring(0,
                    getContext().fullCompletionExpression.length() - getContext().completionExpression.length());
            String oldReplacementString = oldProposal.getReplacementString();
            String newReplacementString = oldReplacementString;
            if (!oldReplacementString.startsWith(qualifier)) {
                newReplacementString = qualifier + oldReplacementString;
            }
            JavaTypeCompletionProposal newProposal = new JavaTypeCompletionProposal(newReplacementString, null,
                    oldProposal.getReplacementOffset(),
                    oldProposal.getReplacementLength() + newReplacementString.length() - oldReplacementString.length(),
                    oldProposal.getImage(), oldProposal.getStyledDisplayString(), oldProposal.getRelevance(),
                    newReplacementString, getJavaContext());
            validProposal = newProposal;
        }
        return validProposal;
    }

}
