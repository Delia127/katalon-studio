package com.kms.katalon.composer.testcase.components;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeColumn;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.testcase.ast.treetable.AstAbstractKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCustomKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;

public class CTreeViewerKeywordTooltip extends TreeViewerKeywordTooltip {
    
    private static final String CUSTOM_KEYWORD_CLASS = "CustomKeywords";

    public CTreeViewerKeywordTooltip(TreeViewer treeViewer) {
        super(treeViewer);
    }

    @Override
    protected void showTooltip(int x, int y) {
        Point point = new Point(x, y);
        CTreeViewer treeViewer = (CTreeViewer) getViewer();
        ViewerRow row = treeViewer.getViewerRowFromWidgetItem(treeViewer.getTree().getItem(point));
        if (row == null) {
            hideTooltip();
            return;
        }
        ViewerColumn viewerCol = getViewerColumn(treeViewer, point);
        ViewerCell viewerCell = treeViewer.getCell(point);
        if (viewerCol == null || viewerCell.getColumnIndex() != 0
                || (!(row.getItem().getData() instanceof AstBuiltInKeywordTreeTableNode)
                && !(row.getItem().getData() instanceof AstCustomKeywordTreeTableNode))) {
            hideTooltip();
            return;
        }
        AbstractKeywordNodeTooltip tip = getTooltip();
        Object element = row.getItem().getData();
        AstAbstractKeywordTreeTableNode node = (AstAbstractKeywordTreeTableNode) element;
        CellLabelProvider labelProvider = treeViewer.getLabelProvider(viewerCell.getColumnIndex());
        labelProvider.useNativeToolTip(element);
        String keywordName = node.getKeywordName();
        String classKeyword = node instanceof AstBuiltInKeywordTreeTableNode ?
                ((AstBuiltInKeywordTreeTableNode) node).getBuiltInKWClassSimpleName()
                : "CustomKeywords";
        String text = "";
        if (CUSTOM_KEYWORD_CLASS.equals(classKeyword)) {
            text = TestCaseEntityUtil.getCustomKeywordJavadocText(node.getKeywordName(), node.getParameterTypes());
        } else {
            text = TestCaseEntityUtil.getBuiltinKeywordJavadocText(classKeyword, keywordName);
            if (text == null || text.length() < 1) {
                text = keywordName;
            }
        }
        if (tip != null && tip.isVisible() && keywordName != null && keywordName.equals(getCurrentKeyword())) {
            return;
        }
        if (tip != null) {
            tip.hide();
        }
        setCurrentKeyword(keywordName);
        
        createTooltip(text, classKeyword, node.getKeywordName()).show(
                getTooltipLocation(new Point(x, y)));
    }
    
    private String[] getTypes(String[] typeSignatures) {
        return Stream.of(typeSignatures)
                .map(t -> {
                    try {
                        return Signature.toString(t);
                    } catch (Exception e) {
                        return t;
                    }
                }).collect(Collectors.toList())
                .toArray(new String[typeSignatures.length]);
    }

    private ViewerColumn getViewerColumn(CTreeViewer treeViewer, Point point) {
        ViewerCell cell = treeViewer.getCell(point);
        if (cell == null) {
            return null;
        }
        int colIndex = cell.getColumnIndex();
        TreeColumn column = treeViewer.getTree().getColumn(colIndex);
        Object data = column.getData(Policy.JFACE + ".columnViewer");
        if (data instanceof ViewerColumn) {
            return (ViewerColumn) data;
        }

        return null;
    }

    @Override
    public boolean isProcessShowTooltip(Event event) {
        Point point = new Point(event.x, event.y);
        CTreeViewer treeViewer = (CTreeViewer) getViewer();
        ViewerRow row = ((CTreeViewer) treeViewer).getViewerRowFromWidgetItem(treeViewer.getTree().getItem(point));
        if (row == null) {
            return false;
        }
        ViewerColumn viewerCol = getViewerColumn(treeViewer, point);
        ViewerCell viewerCell = treeViewer.getCell(point);
        if (viewerCol == null || viewerCell.getColumnIndex() != 0
                || !(row.getItem().getData() instanceof AstBuiltInKeywordTreeTableNode)) {
            return false;
        }

        return true;
    }
}
