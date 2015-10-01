package com.kms.katalon.composer.execution.provider;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.tree.ILogParentTreeNode;
import com.kms.katalon.composer.execution.tree.ILogTreeNode;
import com.kms.katalon.core.logging.LogLevel;

public class LogRecordTreeViewerLabelProvider extends StyledCellLabelProvider {

    public Image getImage(Object element) {
        ISharedImages images = (ISharedImages) JavaUI.getSharedImages();

        if (element instanceof ILogParentTreeNode) {
            ILogParentTreeNode logParentNode = (ILogParentTreeNode) element;
            String imageKey = null;
            if (logParentNode.getElapsedTime().isEmpty()) {
                imageKey = ISharedImages.IMG_OBJS_DEFAULT;
            } else {
                if (logParentNode.getResult() != null) {
                    LogLevel resultLevel = (LogLevel) logParentNode.getResult().getLevel();
                    if (resultLevel == LogLevel.PASSED) {
                        return ImageConstants.IMG_16_LOGVIEW_PASSED;
                    } else if (resultLevel == LogLevel.FAILED) {
                        return ImageConstants.IMG_16_LOGVIEW_FAILED;
                    } else if (resultLevel == LogLevel.ERROR) {
                        return ImageConstants.IMG_16_LOGVIEW_ERROR;
                    }
                } else if (logParentNode.getParent() == null){
                    return com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_TEST_SUITE;
                }
            }

            return images.getImage(imageKey);
        } else {
            return ImageConstants.IMG_16_LOGVIEW_INFO;
        }
        
    }

    @Override
    public void update(ViewerCell cell) {
        StyledString styledString = new StyledString();

        if (cell.getElement() != null) {
            if (cell.getElement() instanceof ILogTreeNode) {
                ILogTreeNode logTreeNode = (ILogTreeNode) cell.getElement();
                String indexString = logTreeNode.getIndexString();
                styledString.append((indexString.isEmpty() ? "" : (indexString + " - ")) + logTreeNode.getMessage());
            }

            if (cell.getElement() instanceof ILogParentTreeNode) {
                ILogParentTreeNode logParentNode = (ILogParentTreeNode) cell.getElement();
                if (logParentNode.getElapsedTime() != null && !logParentNode.getElapsedTime().isEmpty()) {
                    styledString.append(" (" + logParentNode.getElapsedTime() + ")", StyledString.COUNTER_STYLER);
                }
            }
        }

        cell.setText(styledString.toString());
        cell.setStyleRanges(styledString.getStyleRanges());
        cell.setImage(getImage(cell.getElement()));
        super.update(cell);
    }

}
