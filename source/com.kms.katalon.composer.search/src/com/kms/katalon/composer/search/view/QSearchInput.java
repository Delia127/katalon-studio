package com.kms.katalon.composer.search.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.search.ui.text.TextSearchQueryProvider.TextSearchInput;

public class QSearchInput extends TextSearchInput {	
	private static final String[] UNQUALIFIED_RESOURCES = {
		".svn", "bin", "Libs", ".classpath", ".project", "GlobalVariables.glbl", "settings"
	};
	
	private String searchText;
	private boolean isCaseSensitive;
	private boolean isRegExSearch;	
	private String[] fileNamePatterns;
	private IProject project;
	
	/**
	 * @see {@link TextSearchInput#TextSearchInput()}
	 */
	public QSearchInput(String searchText, boolean isCaseSensitive, boolean isRegExSearch, String[] fileNamePatterns,
			IProject project) {
		this.isCaseSensitive = isCaseSensitive;
		this.searchText = searchText;
		this.isRegExSearch = isRegExSearch;
		this.fileNamePatterns = fileNamePatterns;
		this.project = project;
	}

	@Override
	public String getSearchText() {
		return searchText;
	}

	@Override
	public boolean isCaseSensitiveSearch() {
		return isCaseSensitive;
	}

	@Override
	public boolean isRegExSearch() {
		return isRegExSearch;
	}

	@Override
	public FileTextSearchScope getScope() {
		try {
			return FileTextSearchScope.newSearchScope(getValidRootResouces(), fileNamePatterns, false);
		} catch (CoreException e) {
			return null;
		}
	}
	
	private IResource[] getValidRootResouces() throws CoreException {
		List<IResource> validResources = new ArrayList<IResource>();
		
		for (IResource resourceRoot : project.members()) {
			if (isValidResourceToSearch(resourceRoot)) {
				validResources.add(resourceRoot);
			}
		}
		return validResources.toArray(new IResource[validResources.size()]);
	}
	
	public static boolean isValidResourceToSearch(IResource resource) {
		if (!resource.exists()) return false;
		
		for (String unqualifiedName : UNQUALIFIED_RESOURCES) {			
			if (unqualifiedName.equals(resource.getName())) {
				return false;
			}
			
			//Don't search project file
			if (resource.getFileExtension() != null && resource.getFileExtension().equals("prj")) {
				return false;
			}
		}
		return true;
	}

}
