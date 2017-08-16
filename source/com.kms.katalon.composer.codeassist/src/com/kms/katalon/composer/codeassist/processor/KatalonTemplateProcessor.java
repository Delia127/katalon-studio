package com.kms.katalon.composer.codeassist.processor;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.eclipse.codeassist.relevance.Relevance;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistLocation;
import org.codehaus.groovy.eclipse.quickfix.templates.GroovyContext;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

import com.kms.katalon.composer.components.log.LoggerSingleton;

@SuppressWarnings("restriction")
public class KatalonTemplateProcessor extends TemplateCompletionProcessor {

    private ContributionTemplateStore templateStore;

    private JavaContentAssistInvocationContext javaContext;

    private ContributionContextTypeRegistry registry;

    private ContentAssistContext assistContext;

    public KatalonTemplateProcessor(JavaContentAssistInvocationContext javaContext, ContentAssistContext assistContext,
            ContributionTemplateStore templateStore, ContributionContextTypeRegistry registry) {
        this.assistContext = assistContext;
        this.javaContext = javaContext;
        this.templateStore = templateStore;
        this.registry = registry;
    }

    @Override
    protected Template[] getTemplates(String contextTypeId) {
        return templateStore.getTemplates(contextTypeId);
    }

    @Override
    protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
        return registry.getContextType("groovy");
    }

    @Override
    protected Image getImage(Template template) {
        return null;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        try {
            if (javaContext == null) {
                return new ICompletionProposal[0];
            }

            if (assistContext.location != ContentAssistLocation.SCRIPT
                    && assistContext.location != ContentAssistLocation.STATEMENT) {
                return new ICompletionProposal[0];
            }
            Region region = new Region(javaContext.getInvocationOffset(), 0);
            IDocument document = javaContext.getDocument();
            GroovyContext templateContext = new GroovyContext(getContextType(viewer, region), document,
                    javaContext.getInvocationOffset(), 0, javaContext.getCompilationUnit());
            String prefix = String.valueOf(javaContext.computeIdentifierPrefix());

            templateContext.setForceEvaluation(true);
            templateContext.setVariable("selection", document.get(region.getOffset(), region.getLength()));

            List<ICompletionProposal> proposals = new ArrayList<>();
            for (Template template : getTemplates("groovy")) {
                Template newTemplate = clone(template);
                if (!template.getName().startsWith(prefix)) {
                    continue;
                }
                TemplateProposal templateProposal = new TemplateProposal(newTemplate, templateContext, region, null);
                templateProposal.setRelevance(Relevance.VERY_HIGH.getRelavance());
                proposals.add(templateProposal);
            }
            return proposals.toArray(new ICompletionProposal[proposals.size()]);
        } catch (BadLocationException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    private Template clone(Template template) {
        Template newTemplate = new Template(template.getName(), template.getDescription(), template.getContextTypeId(),
                template.getPattern(), true);
        return newTemplate;
    }
}
