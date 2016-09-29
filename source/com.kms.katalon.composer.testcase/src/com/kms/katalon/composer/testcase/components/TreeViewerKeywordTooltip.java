package com.kms.katalon.composer.testcase.components;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.util.KeywordURLUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory;

public class TreeViewerKeywordTooltip {
    private static KeywordNodeTooltip tip;

    private TreeViewer treeViewer;

    protected static final int SHIFT_X = -10;

    protected static final int SHIFT_Y = 2;
    
    public TreeViewerKeywordTooltip(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
        Listener listener = createListener();

        treeViewer.getTree().addListener(SWT.MouseHover, listener);
        treeViewer.getTree().addListener(SWT.MouseMove, listener);
        treeViewer.getTree().addListener(SWT.MouseDoubleClick, listener);
        treeViewer.getTree().addListener(SWT.MouseDown, listener);
        treeViewer.getTree().addListener(SWT.KeyDown, listener);
        treeViewer.getTree().addListener(SWT.FocusOut, listener);
        treeViewer.getTree().addListener(SWT.MouseExit, listener);
    }

    private Listener createListener() {
        return new Listener() {

            @Override
            public void handleEvent(Event event) {
                switch (event.type) {
                    case SWT.MouseMove:
                        if (tip != null) {
                            showTooltip(event.x, event.y);
                        }
                        break;
                    case SWT.MouseHover:
                        showTooltip(event.x, event.y);
                        break;
                    case SWT.MouseExit:
                        if (!isMousePosInTooltip(event.x, event.y)) {
                            hideTooltip();
                        }
                        break;
                    default:
                        hideTooltip();
                        break;
                }

            }

        };
    }

    protected boolean isMousePosInTooltip(int x, int y) {
        if (tip == null || !tip.isVisible()) {
            return false;
        }
        int shift = tip.isShowBelowPoint() ? 2 : -2;
        Rectangle rect = tip.getBounds();
        return rect.contains(treeViewer.getTree().toDisplay(new Point(x, y + shift)));
    }

    protected void hideTooltip() {
        if (tip != null) {
            tip.hide();
            tip = null;
        }

    }

    protected KeywordNodeTooltip getTooltip() {
        return tip;
    }

    protected void showTooltip(int x, int y) {
        Point point = new Point(x, y);
        TreeItem item = treeViewer.getTree().getItem(point);
        if (item == null || !(item.getData() instanceof KeywordBrowserTreeEntity)) {
            hideTooltip();
            return;
        }
        KeywordBrowserTreeEntity keywordBrowserEntity = (KeywordBrowserTreeEntity)item.getData();
        String classKeyword = keywordBrowserEntity.getClassName();
        String keywordName = keywordBrowserEntity.getName();
        String text = TestCaseEntityUtil.getKeywordJavaDocText(classKeyword, keywordName);
        if (text == null || text.length() < 1) {
            text = keywordName;
        }
        if (tip != null && tip.isVisible() && text != null && text.equals(tip.getText())) {
            return;
        }
        if (tip != null) {
            tip.hide();
        }
        createTooltip(text, classKeyword, keywordName).show(getTooltipLocation(point));
    }

    protected KeywordNodeTooltip createTooltip(String text, String classKeyword, String keyword) {
        tip = new KeywordNodeTooltip(treeViewer.getTree());
        tip.setText(text);
        if (keyword.toLowerCase().equals(BuiltInMethodNodeFactory.CALL_TEST_CASE_METHOD_NAME.toLowerCase())) {
            classKeyword = "";
        }
        tip.setKeywordURL(KeywordURLUtil.getKeywordDescriptionURI(classKeyword, keyword));
        return tip;

    }

    protected Point getTooltipLocation(Point controlPoint) {
        Point screenPoint = treeViewer.getControl().toDisplay(controlPoint);
        screenPoint.x += SHIFT_X;
        screenPoint.y += SHIFT_Y;

        return screenPoint;
    }
    
    protected TreeViewer getViewer() {
        return treeViewer;
    }
    
    public boolean isProcessShowTooltip(Event ev) {
        Point point = new Point(ev.x, ev.y);
        TreeItem item = treeViewer.getTree().getItem(point);
        if (item == null) {
            return false;
        }
        return item.getData() instanceof KeywordBrowserTreeEntity;
    }
}
