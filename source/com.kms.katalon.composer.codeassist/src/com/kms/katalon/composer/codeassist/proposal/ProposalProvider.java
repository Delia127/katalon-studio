package com.kms.katalon.composer.codeassist.proposal;

import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

public interface ProposalProvider {
    List<IGroovyProposal> getProposals(ContentAssistContext context,
            ClassNode completionType);
}
