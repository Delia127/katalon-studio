package com.kms.katalon.composer.codeassist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.codehaus.groovy.eclipse.codeassist.DocumentSourceBuffer;
import org.codehaus.groovy.eclipse.codeassist.processors.StatementAndExpressionCompletionProcessor;
import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistLocation;
import org.codehaus.groovy.eclipse.codeassist.requestor.GroovyCompletionProposalComputer;
import org.codehaus.groovy.eclipse.core.util.ExpressionFinder;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.internal.core.SearchableEnvironment;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyGenericTypeProposal;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import com.kms.katalon.composer.codeassist.processor.GroovyTemplateProcessor;
import com.kms.katalon.composer.codeassist.processor.ImprovedPackageCompletionProcessor;
import com.kms.katalon.composer.codeassist.processor.KatalonTemplateProcessor;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings("restriction")
public class ImprovedGroovyCompletionProposalComputer extends GroovyCompletionProposalComputer {
    private static final int EXTENDED_SIZE = 5;

    private static final int PREFFERD_COMPLETION_PROPOSALS_SIZE = 100;

    private static final char[] EMPTY_TRIGGERS = new char[0];

    private ContributionTemplateStore templateStore;

    private ContributionContextTypeRegistry registry;

    public ImprovedGroovyCompletionProposalComputer() {
        registry = new ContributionContextTypeRegistry();
        registry.addContextType("groovy");

        templateStore = new ContributionTemplateStore(registry, GroovyTemplateProcessor.getGroovyPreferenceStore(),
                GroovyTemplateProcessor.GROOVY_PREF_KEY);
        try {
            templateStore.load();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private SearchableEnvironment createSearchableEnvironment(JavaContentAssistInvocationContext javaContext) {
        try {
            return ((JavaProject) javaContext.getProject())
                    .newSearchableNameEnvironment(javaContext.getCompilationUnit().getOwner());
        } catch (JavaModelException e) {
            LoggerSingleton.logError(e,
                    "Exception creating searchable environment for " + javaContext.getCompilationUnit());
            return null;
        }
    }

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

        GroovyCompilationUnit gunit = (GroovyCompilationUnit) unit;
        ContentAssistContext assistContext = createContentAssistContext(gunit, context.getInvocationOffset(),
                context.getDocument());
        if (assistContext == null) {
            return Collections.emptyList();
        }
        List<ICompletionProposal> interalComputeProposals = interalComputeProposals(context, assistContext, monitor);
        return filterProposal(context, assistContext, interalComputeProposals);
    }

    private List<ICompletionProposal> interalComputeProposals(ContentAssistInvocationContext context,
            ContentAssistContext assistContext, IProgressMonitor monitor) {
        JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;
        if (assistContext.location == ContentAssistLocation.IMPORT) {
            return getImportProposals(assistContext, monitor, javaContext);
        }
        List<ICompletionProposal> proposals = super.computeCompletionProposals(context, monitor);
        proposals.addAll(getTemplateProposals(context, assistContext, javaContext));
        return proposals;
    }

    private List<ICompletionProposal> getTemplateProposals(ContentAssistInvocationContext context,
            ContentAssistContext assistContext, JavaContentAssistInvocationContext javaContext) {
        KatalonTemplateProcessor processor = new KatalonTemplateProcessor(javaContext, assistContext, templateStore,
                registry);
        return Arrays.asList(processor.computeCompletionProposals(context.getViewer(), context.getInvocationOffset()));
    }

    private List<ICompletionProposal> getImportProposals(ContentAssistContext assistContext, IProgressMonitor monitor,
            JavaContentAssistInvocationContext javaContext) {
        List<ICompletionProposal> proposals = new ArrayList<>();
        SearchableEnvironment nameEnvironment = createSearchableEnvironment(javaContext);
        proposals.addAll(new ImprovedPackageCompletionProcessor(assistContext, javaContext, nameEnvironment)
                .generateProposals(monitor));
        proposals.addAll(new StatementAndExpressionCompletionProcessor(assistContext, javaContext, nameEnvironment)
                .generateProposals(monitor));
        return proposals;
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

    private List<ICompletionProposal> filterProposal(ContentAssistInvocationContext context,
            ContentAssistContext assistContext, List<ICompletionProposal> completionProposals) {
        List<ICompletionProposal> adjustedProposals = adjusted(assistContext, completionProposals);

        evaluateProposals(context, assistContext, adjustedProposals);

        return sortAndReduce(assistContext, adjustedProposals);
    }

    private void evaluateProposals(ContentAssistInvocationContext context, ContentAssistContext assistContext,
            List<ICompletionProposal> completionProposals) {
        Collection<String> importedNames = getImportedNames(
                ((JavaContentAssistInvocationContext) context).getCompilationUnit());
        for (ICompletionProposal proposal : completionProposals) {
            // No trigger for auto-completion
            if (proposal instanceof AbstractJavaCompletionProposal) {
                AbstractJavaCompletionProposal javaCompletionProposal = (AbstractJavaCompletionProposal) proposal;
                javaCompletionProposal.setTriggerCharacters(EMPTY_TRIGGERS);

                javaCompletionProposal
                        .setRelevance(javaCompletionProposal.getRelevance() + Relevance.LOW.getRelavance());
            }

            // Bring up the suggested element type that was imported
            if (proposal instanceof LazyJavaTypeCompletionProposal) {
                LazyJavaTypeCompletionProposal typeProposal = (LazyJavaTypeCompletionProposal) proposal;
                String lowerCaseTypeName = typeProposal.getQualifiedTypeName();
                if (importedNames.stream()
                        .filter(importedName -> lowerCaseTypeName.startsWith(importedName))
                        .findFirst()
                        .isPresent()) {
                    typeProposal.setRelevance(typeProposal.getRelevance() + Relevance.MEDIUM.getRelavance());
                }
            }
        }
    }

    private List<ICompletionProposal> sortAndReduce(ContentAssistContext assistContext,
            List<ICompletionProposal> completionProposals) {
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
        return reduceSize(completionProposals);
    }

    private List<ICompletionProposal> adjusted(ContentAssistContext assistContext,
            List<ICompletionProposal> completionProposals) {
        List<ICompletionProposal> adjustedProposals = new ArrayList<>();
        for (ICompletionProposal proposal : completionProposals) {

            if (proposal instanceof LazyGenericTypeProposal) {
                LazyGenericTypeProposal typeProposal = (LazyGenericTypeProposal) proposal;
                String qualifiedTypeName = typeProposal.getQualifiedTypeName();
                if (!qualifiedTypeName.startsWith(assistContext.completionExpression)
                        && !Signature.getSimpleName(qualifiedTypeName).startsWith(assistContext.completionExpression)) {
                    continue;
                }
            }
            adjustedProposals.add(proposal);
        }
        return adjustedProposals;
    }

    private List<ICompletionProposal> reduceSize(List<ICompletionProposal> proposals) {
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

    private boolean isSuggestionForVariableDeclare(ContentAssistContext cs) {
        if (cs == null) {
            return true;
        }
        return cs.lhsNode != null && ObjectUtils.equals(cs.lhsNode.getText(), cs.completionNode.getText());
    }

    public ContentAssistContext createContentAssistContext(GroovyCompilationUnit gunit, int invocationOffset,
            IDocument document) {
        String fullCompletionText = this.findCompletionText(document, invocationOffset);
        String[] completionExpressions = this.findCompletionExpression(fullCompletionText);
        if (completionExpressions == null) {
            completionExpressions = new String[] { "", "" };
        }

        String completionExpression = completionExpressions[1] == null ? completionExpressions[0]
                : completionExpressions[1];
        int supportingNodeEnd = this.findSupportingNodeEnd(invocationOffset, fullCompletionText);
        int completionEnd = this.findCompletionEnd(document, invocationOffset);
        ImprovedCompletionNodeFinder finder = new ImprovedCompletionNodeFinder(invocationOffset, completionEnd,
                supportingNodeEnd, completionExpression, fullCompletionText);
        ContentAssistContext assistContext = finder.findContentAssistContext(gunit);
        if (isSuggestionForVariableDeclare(assistContext)) {
            return null;
        }
        return assistContext;
    }

    private int findCompletionEnd(IDocument doc, int offset) {
        DocumentSourceBuffer buffer = new DocumentSourceBuffer(doc);
        return (new ExpressionFinder()).findTokenEnd(buffer, offset);
    }

    private int findSupportingNodeEnd(int invocationOffset, String fullCompletionText) {
        String[] completionExpressions = (new ExpressionFinder()).splitForCompletionNoTrim(fullCompletionText);
        return completionExpressions[1] == null ? -1
                : invocationOffset - fullCompletionText.length() + completionExpressions[0].length();
    }

    private String[] findCompletionExpression(String completionText) {
        return (new ExpressionFinder()).splitForCompletion(completionText);
    }
}
