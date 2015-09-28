package com.kms.katalon.composer.search.view;

import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.FileTextSearchScope;

@SuppressWarnings("restriction")
public class QSearchQuery extends FileSearchQuery {

	private QSearchResult qResult;

	/**
	 * @see {@link FileSearchQuery#FileSearchQuery(String, boolean, boolean, boolean, FileTextSearchScope)}
	 */
	public QSearchQuery(String searchText, boolean isRegEx, boolean isCaseSensitive, FileTextSearchScope scope) {
		super(searchText, isRegEx, isCaseSensitive, scope);
	}

	@Override
	public ISearchResult getSearchResult() {
		if (qResult == null) {
			qResult = new QSearchResult(this);
		}
		return qResult;
	}

}
