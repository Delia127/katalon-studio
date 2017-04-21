package com.kms.katalon.composer.codeassist.proposal;

import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.codeassist.proposal.completion.ArtifactIDCompletionProposal;

public class ArtifactIDProposal extends KatalonLocalVariableProposal {

    private Image image;

    public ArtifactIDProposal(String artifactID, Image image) {
        super(artifactID);
        this.image = image;
    }

    @Override
    public IJavaCompletionProposal createJavaProposal(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext) {
        String variableName = getVariableName();
        String replacedString = context.completionExpression;
        SequenceFinder finder = new SequenceFinder(variableName, SequenceFinder.getToken(replacedString));
        List<int[]> foundSequences = finder.findSequences();
        if (foundSequences == null || foundSequences.isEmpty()) {
            return null;
        }

        ReplacementInfo replacementInfo = updateReplacementInfoWithTextSelection(javaContext,
                getReplacedString(context, javaContext));
        ArtifactIDCompletionProposal proposal = new ArtifactIDCompletionProposal(context, javaContext, variableName,
                replacementInfo, finder, image);
        return proposal.getCompletionProposal();
    }

    private ReplacementInfo updateReplacementInfoWithTextSelection(JavaContentAssistInvocationContext javaContext,
            ReplacementInfo replacementInfo) {
        TextSelection selection = getSelection(javaContext);
        return new ReplacementInfo(selection.getOffset(), selection.getOffset() + selection.getLength());
    }

    private ReplacementInfo getReplacedString(ContentAssistContext context,
            JavaContentAssistInvocationContext javaContext) {
        ASTNode completionNode = context.completionNode;
        if (completionNode instanceof ConstantExpression || completionNode instanceof VariableExpression) {
            return new ReplacementInfo(completionNode.getStart(), completionNode.getEnd());
        }
        return new ReplacementInfo(context.completionLocation, context.completionLocation);
    }

    private TextSelection getSelection(JavaContentAssistInvocationContext javaContext) {
        return (TextSelection) javaContext.getViewer().getSelectionProvider().getSelection();
    }
}
