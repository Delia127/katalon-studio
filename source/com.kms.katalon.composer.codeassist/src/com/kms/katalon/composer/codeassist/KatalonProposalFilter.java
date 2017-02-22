package com.kms.katalon.composer.codeassist;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.processors.IProposalFilter;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyCategoryMethodProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import com.kms.katalon.composer.codeassist.proposal.KatalonMethodNodeProposal;
import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;

public class KatalonProposalFilter implements IProposalFilter {

    /**
     * If the
     * <code>context<code> is Katalon BuiltinKeyword or CustomKeyword, proposes Katalon's keywords as methods only.
     * Otherwise, uses default Groovy proposal.
     */
    @Override
    public List<IGroovyProposal> filterProposals(List<IGroovyProposal> proposals, ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext) {
        if (KatalonContextUtil.isCustomKeywordCompletionClassNode(context)
                || KatalonContextUtil.isBuiltinKeywordCompletionClassNode(context)) {
            List<IGroovyProposal> whiteListProposals = new ArrayList<IGroovyProposal>();

            for (IGroovyProposal groovyProposal : proposals) {
                if (groovyProposal.getClass().equals(KatalonMethodNodeProposal.class)) {
                    whiteListProposals.add(groovyProposal);
                }
            }
            return whiteListProposals;
        } else {
            List<IGroovyProposal> whiteListProposals = new ArrayList<IGroovyProposal>();
            for (IGroovyProposal groovyProposal : proposals) {
                if (groovyProposal instanceof GroovyCategoryMethodProposal) {
                    continue;
                }
                whiteListProposals.add(groovyProposal);
            }
            return whiteListProposals;
        }
    }

}
