package com.kms.katalon.composer.keyword.refactoring;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class KeywordMethodRenamingParticipant extends RenameParticipant {

	private IMethod keywordMethod;
	
	@Override
	protected boolean initialize(Object element) {
		if (element instanceof IMethod) {
			IMethod  method = (IMethod) element;
			String filePath = ((IMethod) element).getParent().getResource().getRawLocation().toString();
			if (GroovyUtil.isKeywordFile(filePath, ProjectController.getInstance().getCurrentProject())) {
				if (method.getAnnotation(Keyword.class.getName()) != null) {
					keywordMethod = method;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Rename Keyword Method";
	}

	@Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context)
			throws OperationCanceledException {	
		return null;
	}

	@SuppressWarnings("restriction")
	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		String className = keywordMethod.getCompilationUnit().getElementName().replace(
				GroovyConstants.GROOVY_FILE_EXTENSION, "");
		String packageName = keywordMethod.getCompilationUnit().getParent().getElementName();
		String classQualifier = packageName.isEmpty() ? className : packageName + "." + className;
		
		RenameArguments arguments = getArguments();
		String oldScript = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + ".'" + classQualifier + "."
				+ keywordMethod.getElementName() + "'";
		String newScript = GroovyConstants.CUSTOM_KEYWORD_LIB_FILE_NAME + ".'" + classQualifier + "."
				+ arguments.getNewName() + "'";
		try {
			GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts(oldScript, newScript, ProjectController
					.getInstance().getCurrentProject());
			PartServiceSingleton.getInstance().getPartService().saveAll(false);
		} catch (IOException e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
		return null;
	}

}
