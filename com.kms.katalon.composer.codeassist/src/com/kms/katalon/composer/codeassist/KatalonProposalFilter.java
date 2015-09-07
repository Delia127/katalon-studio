package com.kms.katalon.composer.codeassist;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.processors.IProposalFilter;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyMethodProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

public class KatalonProposalFilter implements IProposalFilter {

	@Override
	public List<IGroovyProposal> filterProposals(List<IGroovyProposal> proposals, ContentAssistContext context,
			JavaContentAssistInvocationContext javaContext) {
		List<IGroovyProposal> whiteListProposals = new ArrayList<IGroovyProposal>();
		
		for (IGroovyProposal groovyProposal : proposals) {
			if (!(groovyProposal.getClass().equals(GroovyMethodProposal.class))) {
				whiteListProposals.add(groovyProposal);
			}
			
		}
		return whiteListProposals;
	}

}
