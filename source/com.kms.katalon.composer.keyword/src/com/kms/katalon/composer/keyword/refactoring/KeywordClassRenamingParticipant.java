package com.kms.katalon.composer.keyword.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class KeywordClassRenamingParticipant extends RenameParticipant {

	private ICompilationUnit keywordClass;

	@Override
	protected boolean initialize(Object element) {
		if (element instanceof ICompilationUnit) {
			ICompilationUnit unit = (ICompilationUnit) element;
			String filePath = unit.getResource().getRawLocation().toString();
			if (GroovyUtil.isKeywordFile(filePath, getProjectEntity())) {
				keywordClass = unit;
				return true;
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Rename Keyword Class";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {
		return null;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		RenameArguments arguments = getArguments();
		
		String packageName = keywordClass.getParent().getElementName();
		
		String oldClassQualifier = (packageName.isEmpty() ? keywordClass.getElementName() : packageName + "."
				+ keywordClass.getElementName()).replace(GroovyConstants.GROOVY_FILE_EXTENSION, "");
		String newClassQualifier = (packageName.isEmpty() ? arguments.getNewName() : packageName + "."
				+ arguments.getNewName()).replace(GroovyConstants.GROOVY_FILE_EXTENSION, "");		
		
		updateReferences(oldClassQualifier, newClassQualifier);

        try {
            EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, 
                    new FolderTreeEntity(FolderController.getInstance().getKeywordRoot(getProjectEntity()), null));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } 
		return null;
	}
	
    public static void updateReferences(String oldClassQualifier, String newClassQualifier) {
        String oldScript = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + ".'" + oldClassQualifier + ".";
        String newScript = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + ".'" + newClassQualifier + ".";
        try {
            GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts(oldScript, newScript, getProjectEntity());
            PartServiceSingleton.getInstance().getPartService().saveAll(false);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private static ProjectEntity getProjectEntity() {
        return ProjectController.getInstance().getCurrentProject();
    }

}
