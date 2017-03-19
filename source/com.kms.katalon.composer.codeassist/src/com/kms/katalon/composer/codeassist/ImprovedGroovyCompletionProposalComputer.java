package com.kms.katalon.composer.codeassist;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.GroovyCompletionProposalComputer;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyGenericTypeProposal;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class ImprovedGroovyCompletionProposalComputer extends GroovyCompletionProposalComputer {
    private static final int EXTENDED_SIZE = 5;

    private static final int PREFFERD_COMPLETION_PROPOSALS_SIZE = 100;

    private static final char[] EMPTY_TRIGGERS = new char[0];

    public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
            IProgressMonitor monitor) {
        if (!(context instanceof JavaContentAssistInvocationContext)) {
            return Collections.emptyList();
        }

        JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
        ICompilationUnit unit = javaContext.getCompilationUnit();
        if (!commitWorkingCopy(unit, javaContext)) {
            return Collections.emptyList();
        }
        return filterProposal(getImportedNames(unit), super.computeCompletionProposals(context, monitor));
    }

    private Collection<String> getImportedNames(ICompilationUnit unit) {
        final Collection<String> importedNames = new LinkedHashSet<>();
        importedNames.add(GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME);
        if (unit == null) {
            return importedNames;
        }
        try {
            for (IImportDeclaration imp : unit.getImports()) {
                importedNames.add(imp.getElementName());
            }
        } catch (JavaModelException e) {
            LoggerSingleton.logError(e);
        }
        return importedNames;
    }

    private List<ICompletionProposal> filterProposal(final Collection<String> importedNames,
            List<ICompletionProposal> completionProposals) {
        adjustProposals(importedNames, completionProposals);

        return orderAndCollectProposal(completionProposals);
    }

    private void adjustProposals(final Collection<String> importedNames,
            List<ICompletionProposal> completionProposals) {
        for (ICompletionProposal proposal : completionProposals) {
            // No trigger for auto-completion
            if (proposal instanceof AbstractJavaCompletionProposal) {
                AbstractJavaCompletionProposal javaCompletionProposal = (AbstractJavaCompletionProposal) proposal;
                javaCompletionProposal.setTriggerCharacters(EMPTY_TRIGGERS);
            }

            // Bring up the suggested element type that was imported
            if (proposal instanceof LazyGenericTypeProposal) {
                LazyGenericTypeProposal typeProposal = (LazyGenericTypeProposal) proposal;
                String lowerCaseTypeName = StringUtils.defaultString(typeProposal.getQualifiedTypeName()).toLowerCase();
                if (importedNames.stream()
                        .filter(importedName -> lowerCaseTypeName.startsWith(importedName.toLowerCase()))
                        .findFirst()
                        .isPresent()) {
                    typeProposal.setRelevance(typeProposal.getRelevance() + Relevance.MEDIUM.getRelavance());
                }
            }
        }
    }

    private List<ICompletionProposal> orderAndCollectProposal(List<ICompletionProposal> completionProposals) {
        completionProposals.sort(new Comparator<ICompletionProposal>() {
            @Override
            public int compare(ICompletionProposal o1, ICompletionProposal o2) {
                if (!(o1 instanceof IJavaCompletionProposal)) {
                    return 1;
                }

                if (!(o2 instanceof IJavaCompletionProposal)) {
                    return -1;
                }
                IJavaCompletionProposal completion1 = (IJavaCompletionProposal) o1;
                IJavaCompletionProposal completion2 = (IJavaCompletionProposal) o2;

                int evaluation = completion2.getRelevance() - completion1.getRelevance();
                if (evaluation != 0) {
                    return evaluation;
                }

                return completion1.getDisplayString()
                        .toLowerCase()
                        .compareTo(completion2.getDisplayString().toLowerCase());
            }
        });
        return filterProposals(completionProposals);
    }

    private List<ICompletionProposal> filterProposals(List<ICompletionProposal> proposals) {
        int preferredSize = PREFFERD_COMPLETION_PROPOSALS_SIZE;

        int totalSize = proposals.size();
        if (totalSize <= preferredSize) {
            return proposals;
        }
        while (totalSize > preferredSize) {
            ICompletionProposal tailProposal = proposals.get(preferredSize);
            if (!(tailProposal instanceof IJavaCompletionProposal)) {
                break;
            }

            if (((IJavaCompletionProposal) tailProposal).getRelevance() < Relevance.LOW.getRelavance()) {
                break;
            }
            preferredSize += EXTENDED_SIZE;
        }

        return proposals.subList(0, Math.min(preferredSize, totalSize) - 1);
    }

    private boolean commitWorkingCopy(ICompilationUnit unit, JavaContentAssistInvocationContext javaContext) {
        if (unit.isWorkingCopy()) {
            return true;
        }
        try {
            unit.becomeWorkingCopy(null);
            IBuffer buffer = unit.getBuffer();
            if (buffer == null) {
                return false;
            }
            buffer.setContents(javaContext.getDocument().get());
            return true;
        } catch (JavaModelException e) {
            try {
                unit.discardWorkingCopy();
            } catch (JavaModelException ex) {
                LoggerSingleton.logError(ex);
            }
            return false;
        }
    }

    @Override
    public ContentAssistContext createContentAssistContext(GroovyCompilationUnit unit, int invocationOffset,
            IDocument document) {
        ContentAssistContext cs = super.createContentAssistContext(unit, invocationOffset, document);
        // Remove suggestions for declaring variable name
        if (isSuggestionForVariableDeclare(cs)) {
            return null;
        }
        return cs;
    }

    private boolean isSuggestionForVariableDeclare(ContentAssistContext cs) {
        return cs != null && cs.lhsNode != null && ObjectUtils.equals(cs.lhsNode, cs.completionNode);
    }
}
