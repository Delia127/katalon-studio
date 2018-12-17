package com.kms.katalon.composer.execution.provider;

import org.apache.commons.lang.StringUtils;
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
                    LogLevel resultLevel = LogLevel.valueOf(logParentNode.getResult().getLevel());
                    if (resultLevel == LogLevel.PASSED) {
                        return ImageConstants.IMG_16_LOGVIEW_PASSED;
                    }if (resultLevel == LogLevel.FAILED) {
                        return ImageConstants.IMG_16_LOGVIEW_FAILED;
                    } else if (resultLevel == LogLevel.ERROR) {
                        return ImageConstants.IMG_16_LOGVIEW_ERROR;
                    } else if (resultLevel == LogLevel.WARNING) {
                        return ImageConstants.IMG_16_LOGVIEW_WARNING;
                    } else if (resultLevel == LogLevel.NOT_RUN) {
                        // TODO: Re-factor for removing else if ( change to switch maybe )
                        return ImageConstants.IMG_16_LOGVIEW_NOT_RUN;
                    }
                } else if (logParentNode.getParent() == null) {
                    return null;
//                    return com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_TEST_SUITE;
                }
            }

            return images.getImage(imageKey);
        } else {
            return null;
//            return ImageConstants.IMG_16_LOGVIEW_INFO;
        }

    }

    @Override
    public void update(ViewerCell cell) {
        StyledString styledString = new StyledString();
        if (cell.getElement() == null) {
            return;
        }

        if (cell.getElement() instanceof ILogParentTreeNode) {
            ILogParentTreeNode logParentNode = (ILogParentTreeNode) cell.getElement();
            String indexString = logParentNode.getIndexString();
            if (StringUtils.isNotBlank(indexString)) {
                styledString.append(indexString + " - ");
            }
        }

        if (cell.getElement() instanceof ILogTreeNode) {
            ILogTreeNode logTreeNode = (ILogTreeNode) cell.getElement();
            styledString.append(logTreeNode.getMessage());
        }

        if (cell.getElement() instanceof ILogParentTreeNode) {
            ILogParentTreeNode logParentNode = (ILogParentTreeNode) cell.getElement();
            if (logParentNode.getElapsedTime() != null && !logParentNode.getElapsedTime().isEmpty()) {
                styledString.append(" (" + logParentNode.getElapsedTime() + ")", StyledString.COUNTER_STYLER);
            }
        }

        cell.setText(styledString.toString());
        cell.setStyleRanges(styledString.getStyleRanges());
        cell.setImage(getImage(cell.getElement()));
        super.update(cell);
    }

    @Override
    public String getToolTipText(Object element) {
        if (!(element instanceof ILogTreeNode)) {
            return "";
        }

        StringBuilder cellTextBuilder = new StringBuilder();
        ILogTreeNode logTreeNode = (ILogTreeNode) element;

        if (element instanceof ILogParentTreeNode) {
            ILogParentTreeNode logParentNode = (ILogParentTreeNode) element;
            String indexString = logParentNode.getIndexString();
            if (StringUtils.isNotBlank(indexString)) {
                cellTextBuilder.append(indexString + " - ");
            }
        }

        cellTextBuilder.append(logTreeNode.getMessage());

        if (element instanceof ILogParentTreeNode) {
            ILogParentTreeNode logParentNode = (ILogParentTreeNode) element;
            if (logParentNode.getElapsedTime() != null && !logParentNode.getElapsedTime().isEmpty()) {
                cellTextBuilder.append(" (" + logParentNode.getElapsedTime() + ")");
            }
        }
        return cellTextBuilder.toString();
    }

}
