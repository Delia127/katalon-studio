package com.kms.katalon.composer.testcase.providers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.Viewer;

import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;

public class KeywordBrowserEntityViewerFilter extends AbstractEntityViewerFilter {
	private String searchString;

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.equals(StringUtils.EMPTY)) {
			return true;
		}
		if (element instanceof IKeywordBrowserTreeEntity) {
			
			IKeywordBrowserTreeEntity entity = ((IKeywordBrowserTreeEntity) element);
			if (entity.getParent() != null && entity.getParent().getReadableName().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
			
			if (entity.getReadableName().toLowerCase().contains(searchString.toLowerCase())) {
				return true;
			}
			if (entity.hasChildren()) {
				boolean isChildSelected = false;
				for (Object child : entity.getChildren()) {
					isChildSelected |= select(viewer, element, child);
				}
				return isChildSelected;
			}
		}
		return false;
	}
}
