package com.kms.katalon.composer.testcase.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.treetable.AstScriptMainBlockStatmentTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;

public class AstTreeTableContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			List<AstTreeTableNode> treeTableNodes = new ArrayList<AstTreeTableNode>();
			for (Object object : ((List<?>) inputElement)) {
				if (object instanceof AstScriptMainBlockStatmentTreeTableNode) {
					AstScriptMainBlockStatmentTreeTableNode mainBlockNode = (AstScriptMainBlockStatmentTreeTableNode) object;
					try {
						treeTableNodes.addAll(mainBlockNode.getChildren());
					} catch (Exception e) {
						LoggerSingleton.logError(e);
					}
				} else if (object instanceof AstTreeTableNode) {
					treeTableNodes.add((AstTreeTableNode) object);
				}
			}
			return treeTableNodes.toArray();
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof AstTreeTableNode) {
			try {
				return ((AstTreeTableNode) parentElement).getChildren().toArray();
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).getParent();
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof AstTreeTableNode) {
			return ((AstTreeTableNode) element).hasChildren();
		}
		return false;
	}

}
