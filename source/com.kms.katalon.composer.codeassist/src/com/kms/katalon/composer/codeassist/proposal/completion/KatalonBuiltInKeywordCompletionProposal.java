package com.kms.katalon.composer.codeassist.proposal.completion;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.eclipse.codeassist.ProposalUtils;
import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyJavaFieldCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.internal.ui.text.java.ProposalInfo;
import org.eclipse.jdt.internal.ui.text.java.TypeProposalInfo;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;

import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.custom.keyword.KeywordClass;

@SuppressWarnings("restriction")
public class KatalonBuiltInKeywordCompletionProposal extends GroovyCompletionProposal {

    private final KeywordClass keywordClass;

    private JavaContentAssistInvocationContext javaContext;

    public KatalonBuiltInKeywordCompletionProposal(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext, KeywordClass keywordClass) {
        super(GroovyCompletionProposal.TYPE_REF, context.completionLocation);
        this.keywordClass = keywordClass;
        this.javaContext = javaContext;
        setName(keywordClass.getAliasName().toCharArray());
        setRelevance(Relevance.HIGH.getRelavance());

        String aliasName = keywordClass.getAliasName();
        setCompletion(aliasName.toCharArray());

        int completionLength = context.completionExpression.length();
        setReplaceRange(context.completionLocation - completionLength, context.completionLocation + aliasName.length()
                - completionLength);
        ClassNode classNode = new ClassNode(keywordClass.getType());
        char[] classSignature = ProposalUtils.createTypeSignature(classNode);
        setTypeName(classSignature);
        setSignature(classSignature);
    }

    public KeywordClass getKeywordClass() {
        return keywordClass;
    }

    public IJavaCompletionProposal getCompletionProposal() {
        return new LocalKeywordCompletionProposal(this);
    }

    private class LocalKeywordCompletionProposal extends GroovyJavaFieldCompletionProposal {

        public LocalKeywordCompletionProposal(KatalonBuiltInKeywordCompletionProposal proposal) {
            super(proposal, ImageConstants.IMG_16_BRANDING, null);
        }

        @Override
        protected ProposalInfo getProposalInfo() {
            return new TypeProposalInfo(javaContext.getProject(), getProposal());
        }

        @Override
        public StyledString getStyledDisplayString() {
            return new StyledString(String.valueOf(getName())).append(
                    KatalonContextUtil.getClassSignature(keywordClass))
                    .append(KatalonContextUtil.getKatalonSignature());
        }
    }
}
