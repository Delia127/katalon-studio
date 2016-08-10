package com.kms.katalon.composer.codeassist.editor;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.eclipse.codebrowsing.requestor.CodeSelectHelper;
import org.codehaus.groovy.eclipse.codebrowsing.requestor.CodeSelectRequestor;
import org.codehaus.groovy.eclipse.editor.GroovyExtraInformationHover;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

@SuppressWarnings("restriction")
public class ImprovedGroovyTextEditorHover extends GroovyExtraInformationHover {

    @Override
    protected IJavaElement[] getJavaElementsAt(ITextViewer textViewer, IRegion hoverRegion) {
        ICodeAssist codeAssist = getCodeAssist();
        if (codeAssist instanceof GroovyCompilationUnit) {
            return new CustomCodeSelectHelper().select((GroovyCompilationUnit) codeAssist, hoverRegion.getOffset(),
                    hoverRegion.getLength());
        }
        return super.getJavaElementsAt(textViewer, hoverRegion);
    }

    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        IJavaElement[] elements = getJavaElementsAt(textViewer, hoverRegion);
        if (elements == null) {
            return super.getHoverInfo2(textViewer, hoverRegion);
        }

        return JavadocHover.getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion, null);
    }

    private class CustomCodeSelectHelper extends CodeSelectHelper {

        @Override
        protected CodeSelectRequestor createRequestor(GroovyCompilationUnit unit, ASTNode nodeToLookFor) {
            return new CustomCodeSelectRequestor(nodeToLookFor, unit);
        }
    }
}
