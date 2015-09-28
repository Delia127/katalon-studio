package com.kms.katalon.composer.testcase.providers;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;

public class KeywordTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			return ((List<?>) inputElement).toArray();
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IKeywordBrowserTreeEntity) {
			return ((IKeywordBrowserTreeEntity) parentElement).getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IKeywordBrowserTreeEntity) {
			return ((IKeywordBrowserTreeEntity) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IKeywordBrowserTreeEntity) {
			return ((IKeywordBrowserTreeEntity) element).hasChildren();
		}
		return false;
	}

}
