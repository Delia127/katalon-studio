package com.kms.katalon.composer.codeassist.editor;

import java.lang.reflect.Field;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.eclipse.codebrowsing.elements.GroovyResolvedBinaryMethod;
import org.codehaus.groovy.eclipse.codebrowsing.requestor.CodeSelectHelper;
import org.codehaus.groovy.eclipse.codebrowsing.requestor.CodeSelectRequestor;
import org.codehaus.groovy.eclipse.codebrowsing.requestor.Region;
import org.codehaus.groovy.eclipse.editor.GroovyExtraInformationHover;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.text.java.hover.JavadocHover;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;

import com.kms.katalon.composer.codeassist.constant.ComposerCodeAssistMessageConstants;
import com.kms.katalon.composer.codeassist.constant.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.util.KeywordURLUtil;

@SuppressWarnings("restriction")
public class ImprovedGroovyTextEditorHover extends GroovyExtraInformationHover {

    private KatalonInformationControlCreator katalonInformationControl;

    private static String keywordDescURI = null;

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
        updateCurrentKeyword(elements);
        if (elements == null) {
            return super.getHoverInfo2(textViewer, hoverRegion);
        }

        return JavadocHover.getHoverInfo(elements, getEditorInputJavaElement(), hoverRegion, null);
    }

    private void updateCurrentKeyword(IJavaElement[] elements) {
        keywordDescURI = null;

        if (elements == null || elements.length != 1 || !(elements[0] instanceof GroovyResolvedBinaryMethod)) {
            return;
        }
        String elementKey = ((GroovyResolvedBinaryMethod) elements[0]).getKey();
        if (elementKey == null) {
            return;
        }
        keywordDescURI = KeywordURLUtil.getKeywordDescriptionURI(elementKey);
    }

    private class CustomCodeSelectHelper extends CodeSelectHelper {

        @Override
        protected CodeSelectRequestor createRequestor(ASTNode node, Region nodeRegion, Region selectRegion,
                GroovyCompilationUnit unit) {
            return new CustomCodeSelectRequestor(node, unit);
        }
    }

    @Override
    public IInformationControlCreator getInformationPresenterControlCreator() {
        if (katalonInformationControl == null) {
            katalonInformationControl = new KatalonInformationControlCreator();
        }
        return katalonInformationControl;
    }

    private class KatalonInformationControlCreator implements IInformationControlCreator {

        private PresenterControlCreator presenterControlCreator = null;

        @Override
        public IInformationControl createInformationControl(Shell parent) {
            if (presenterControlCreator == null) {
                presenterControlCreator = new PresenterControlCreator(getSite());
            }
            BrowserInformationControl bi = (BrowserInformationControl) presenterControlCreator.createInformationControl(parent);
            if (keywordDescURI == null) {
                return bi;
            }
            addOpenDescKeywordButton(bi);

            return bi;
        }

        private void addOpenDescKeywordButton(BrowserInformationControl bi) {
            try {
                Field f = bi.getClass().getSuperclass().getDeclaredField("fToolBar");
                f.setAccessible(true);
                ToolBar tb = (ToolBar) f.get(bi);
                ToolItem katItem = new ToolItem(tb, SWT.NONE);
                katItem.setToolTipText(ComposerCodeAssistMessageConstants.KEYWORD_DESC_BUTTON_TOOLTIP);
                katItem.setImage(ImageConstants.IMG_16_KEYWORD_WIKI);
                katItem.addListener(SWT.Selection, new Listener() {

                    @Override
                    public void handleEvent(Event event) {
                        try {
                            Program.launch(keywordDescURI);
                        } catch (Exception ex) {
                            LoggerSingleton.logError(ex);
                        }
                    }

                });

            } catch (Exception ex) {
                LoggerSingleton.logError(ex);
            }

        }

        private IWorkbenchSite getSite() {
            IEditorPart editor = getEditor();
            if (editor == null) {
                IWorkbenchPage page = JavaPlugin.getActivePage();
                if (page != null) {
                    editor = page.getActiveEditor();
                }
            }
            if (editor != null) {
                return editor.getSite();
            }

            return null;
        }
    }

}
