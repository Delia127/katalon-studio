package com.kms.katalon.composer.testcase.actions;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.refactoring.actions.FormatGroovyAction;
import org.codehaus.groovy.eclipse.refactoring.actions.FormatKind;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;

public class KatalonFormatAction extends FormatGroovyAction {

    private String testCaseId;

    public KatalonFormatAction(IWorkbenchSite site, FormatKind kind, String testCaseId) {
        super(site, kind);
        this.testCaseId = testCaseId;
    }

    @Override
    public void run(ITextSelection textSelection) {
        if (!(getSite() instanceof IEditorSite)) {
            return;
        }

        IWorkbenchPart part = ((IEditorSite) getSite()).getPart();
        if (!(part instanceof GroovyEditor)) {
            return;
        }

        GroovyEditor groovyEditor = (GroovyEditor) part;
        IDocument doc = groovyEditor.getDocumentProvider().getDocument(groovyEditor.getEditorInput());

        int selectedOffset = textSelection.getOffset();
        int selectedLength = textSelection.getLength();
        String text = textSelection.getText();
        if (selectedLength == 0) {
            selectedOffset = 0;
            selectedLength = doc.getLength();
            text = doc.get();
        }

        try {
            GroovyWrapperParser parser = new GroovyWrapperParser(new StringBuilder());
            parser.parseGroovyAstIntoScript(GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(
                    text, testCaseId));

            String newValue = StringUtils.trimToEmpty(parser.getValue());
            doc.replace(selectedOffset, selectedLength, newValue);
            groovyEditor.selectAndReveal(selectedOffset, newValue.length());
        } catch (MultipleCompilationErrorsException e) {
            // User selected bad location, ignore it
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
