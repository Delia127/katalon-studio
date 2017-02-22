package com.kms.katalon.composer.codeassist;

import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.requestor.GroovyCompletionProposalComputer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class ImprovedGroovyCompletionProposalComputer extends GroovyCompletionProposalComputer {

    public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
            IProgressMonitor monitor) {
        if (!(context instanceof JavaContentAssistInvocationContext)) {
            return Collections.emptyList();
        }

        JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
        ICompilationUnit unit = javaContext.getCompilationUnit();
        if (!unit.isWorkingCopy()) {
            try {
                unit.becomeWorkingCopy(null);
                IBuffer buffer = unit.getBuffer();
                buffer.setContents(javaContext.getDocument().get());
            } catch (JavaModelException e) {
                try {
                    unit.discardWorkingCopy();
                } catch (JavaModelException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        }

        return super.computeCompletionProposals(context, monitor);
    }

}
