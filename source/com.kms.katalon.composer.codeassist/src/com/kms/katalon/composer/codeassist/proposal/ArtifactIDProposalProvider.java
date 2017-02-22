package com.kms.katalon.composer.codeassist.proposal;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistLocation;

import com.kms.katalon.composer.components.impl.util.EntityIndexingUtil;
import com.kms.katalon.controller.ProjectController;

public abstract class ArtifactIDProposalProvider implements ProposalProvider {

    @Override
    public List<IGroovyProposal> getProposals(ContentAssistContext context, ClassNode completionType) {
        ContentAssistLocation contextLocation = context.location;
        if (contextLocation != ContentAssistLocation.STATEMENT
                && contextLocation != ContentAssistLocation.METHOD_CONTEXT) {
            return Collections.emptyList();
        }
        ASTNode completionNode = context.completionNode;
        if (!(completionNode instanceof MethodCall)
                || !getMethodName().equals(((MethodCall) completionNode).getMethodAsString())) {
            return Collections.emptyList();
        }
        return getArtifactProposals(context);
    }

    protected EntityIndexingUtil getIndexingUtil() throws IOException {
        return EntityIndexingUtil.getInstance(ProjectController.getInstance().getCurrentProject());
    }

    protected abstract String getMethodName();

    protected abstract List<IGroovyProposal> getArtifactProposals(ContentAssistContext context);

}
