package com.kms.katalon.composer.execution.provider;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

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
						imageKey = ISharedImages.IMG_OBJS_PUBLIC;
					} else if (resultLevel == LogLevel.FAILED) {
						imageKey = ISharedImages.IMG_OBJS_PRIVATE;
					} else if (resultLevel == LogLevel.ERROR) {
						imageKey = ISharedImages.IMG_OBJS_PROTECTED;
					}
				}
			}
			
			return images.getImage(imageKey);
		}
		return null;
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
