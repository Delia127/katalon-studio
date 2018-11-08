package com.kms.katalon.composer.explorer.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.IncludeTreeRootEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.file.FileEntity;

public class ExplorerTreeViewerComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object o1, Object o2){
		ITreeEntity t1 = (ITreeEntity) o1;
		ITreeEntity t2 = (ITreeEntity) o2;
		
		if(t1 instanceof ProfileRootTreeEntity || 
				t1 instanceof FolderTreeEntity ||
				t1 instanceof TestListenerFolderTreeEntity ||
				t1 instanceof IncludeTreeRootEntity){
			return 0;
		}
		
		if(t2 instanceof ProfileRootTreeEntity || 
				t2 instanceof FolderTreeEntity ||
				t2 instanceof TestListenerFolderTreeEntity ||
				t2 instanceof IncludeTreeRootEntity){
			return 0;
		}
		
		try {
			FileEntity f1 = (FileEntity) t1.getObject();
			FileEntity f2 = (FileEntity) t2.getObject();
			return - f1.getName().compareTo(f2.getName());
		} catch (Exception e) {
			// Do nothing for now 
		}
		return 0;
	}
}
