package com.kms.katalon.composer.codeassist.proposal.completion;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyJavaFieldCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.codeassist.proposal.ReplacementInfo;
import com.kms.katalon.composer.codeassist.proposal.SequenceFinder;
import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.groovy.util.GroovyStringUtil;

public class ArtifactIDCompletionProposal extends GroovyCompletionProposal {

    private String variableName;

    private ArtifactIDJavaCompletionProposal completionProposal;

    private static final char[] ID_TRGGER = new char[] { '\t', '=', ';' };

    private Image image;

    @SuppressWarnings("restriction")
    public ArtifactIDCompletionProposal(ContentAssistContext context, JavaContentAssistInvocationContext javaContext,
            String variableName, ReplacementInfo replacementInfo, SequenceFinder finder, Image image) {
        super(GroovyCompletionProposal.LOCAL_VARIABLE_REF, context.completionLocation);
        this.image = image;
        setName(variableName.toCharArray());
        setTypeName(Object.class.getName().toCharArray());
        setRelevance(Relevance.VERY_HIGH.getRelavance());

        String stringVariableName = GroovyStringUtil.toGroovyStringFormat(variableName);

        String replaceString = stringVariableName;
        setCompletion(replaceString.toCharArray());

        setReplaceRange(replacementInfo.getStartIndex(), replacementInfo.getEndIndex());
        setVariableName(variableName);

        completionProposal = new ArtifactIDJavaCompletionProposal(this, finder, javaContext);
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

    @SuppressWarnings("restriction")
    @Override
    public char[] getName() {
        return getCompletion();
    }

    @SuppressWarnings("restriction")
    private class ArtifactIDJavaCompletionProposal extends GroovyJavaFieldCompletionProposal {

        private SequenceFinder finder;

        private ArtifactIDCompletionProposal proposal;

        private JavaContentAssistInvocationContext javaContext;

        private String selectedText;

        private int startOffset;

        public ArtifactIDJavaCompletionProposal(ArtifactIDCompletionProposal proposal, SequenceFinder finder,
                JavaContentAssistInvocationContext javaContext) {
            super(proposal, image,
                    new StyledString(proposal.getVariableName()).append(KatalonContextUtil.getKatalonSignature()));
            this.proposal = proposal;
            this.finder = finder;
            this.javaContext = javaContext;
            selectedText = StringUtils.defaultString(textSelection().getText());
            startOffset = textSelection().getOffset();
        }

        private TextSelection textSelection() {
            return (TextSelection) javaContext.getViewer().getSelectionProvider().getSelection();
        }

        @Override
        public void apply(IDocument document, char trigger, int offset) {
            try {
                this.replace(document,
                        proposal.getReplaceStart(), offset - startOffset + proposal.getReplaceEnd()
                                - proposal.getReplaceStart() - selectedText.length() + textSelection().getLength(),
                        getReplacementString());
            } catch (BadLocationException e) {
                LoggerSingleton.logError(e);
            }
        }

        @Override
        public boolean validate(IDocument document, int offset, DocumentEvent event) {
            return super.validate(document, offset, event);
        }

        @Override
        protected String getPatternToEmphasizeMatch(IDocument document, int offset) {
            return SequenceFinder.getToken(super.getPatternToEmphasizeMatch(document, offset));
        }

        @Override
        protected boolean isValidPrefix(String prefix) {
            finder.changeToken(prefix);
            return !finder.findSequences().isEmpty();
        }

        @Override
        protected String getPrefix(IDocument document, int offset) {
            return SequenceFinder.getToken(super.getPrefix(document, offset));
        }

        @Override
        public StyledString getStyledDisplayString(IDocument document, int offset,
                BoldStylerProvider boldStylerProvider) {
            StyledString styledDisplayString = new StyledString();
            styledDisplayString.append(this.getStyledDisplayString());
            String pattern = this.getPatternToEmphasizeMatch(document, offset);
            if (pattern == null || pattern.length() == 0) {
                return styledDisplayString;
            }

            List<int[]> regions = finder.findSequences();
            if (regions == null || regions.size() == 0) {
                return styledDisplayString;
            }

            Styler boldStyler = boldStylerProvider.getBoldStyler();
            for (int[] r : regions) {
                for (int c : r) {
                    styledDisplayString.setStyle(c, 1, boldStyler);
                }
            }
            return styledDisplayString;
        }

        @Override
        public char[] getTriggerCharacters() {
            return ID_TRGGER;
        }
    }
}
