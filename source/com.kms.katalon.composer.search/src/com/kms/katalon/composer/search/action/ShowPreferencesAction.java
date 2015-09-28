package com.kms.katalon.composer.search.action;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.handlers.SearchHandler;
import com.kms.katalon.composer.search.constants.StringConstants;
import com.kms.katalon.composer.search.view.QSearchQueryBuilder;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ShowPreferencesAction {
	@Inject
	private ESelectionService selectionService;

	/**
	 * Performs show preferences of the selected {@link ITreeEntity}
	 */
	@SuppressWarnings("restriction")
	@Execute
	public void showTreeEntityReferences() {
		try {			
			Object[] objects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			ISearchQuery query = null;
			if (objects[0] instanceof ITreeEntity) {
				ITreeEntity treeEntity = (ITreeEntity) objects[0];
				ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
				query = QSearchQueryBuilder.getReferenceQueryForTreeEntity(treeEntity, projectEntity);
			}
			
			if (query != null) {
				SearchHandler.openSearchView();
				NewSearchUI.runQueryInBackground(query, NewSearchUI.getSearchResultView());				
			}
		} catch (Exception e) {
			MessageDialog.openConfirm(null, StringConstants.ERROR_TITLE, 
					StringConstants.ACT_ERROR_MSG_UNABLE_TO_SHOW_REFERENCES);
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}
	
	/**
	 * Performs show preferences of the selected {@link GlobalVariableEntity}
	 */
	@Inject
	@Optional
	private void showGlobalVariableReferences(
			@UIEventTopic(EventConstants.GLOBAL_VARIABLE_SHOW_REFERENCES) GlobalVariableEntity variable) {
		if (variable != null) {			
			ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
			final ISearchQuery query = QSearchQueryBuilder.getGlobalVariablePrefrenceQuery(variable, 
					GroovyUtil.getGroovyProject(projectEntity));
			if (query != null) {
				SearchHandler.openSearchView();
				NewSearchUI.runQueryInBackground(query, NewSearchUI.getSearchResultView());				
			}
		}
		
	}
}
