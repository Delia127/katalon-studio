package com.kms.katalon.composer.codeassist.proposal;

import org.codehaus.groovy.eclipse.codeassist.proposals.AbstractGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import com.kms.katalon.composer.codeassist.proposal.completion.KatalonBuiltInKeywordCompletionProposal;
import com.kms.katalon.custom.keyword.KeywordClass;

public class KatalonBuitInKeywordAliasProposal extends AbstractGroovyProposal {

    private final KeywordClass keywordClass;

    public KatalonBuitInKeywordAliasProposal(KeywordClass keywordClass) {
        this.keywordClass = keywordClass;
    }

    @Override
    public IJavaCompletionProposal createJavaProposal(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext) {
        KatalonBuiltInKeywordCompletionProposal proposal = new KatalonBuiltInKeywordCompletionProposal(context,
                javaContext, keywordClass);
        return proposal.getCompletionProposal();
    }

}
