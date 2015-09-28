package com.kms.katalon.composer.webui.recorder.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class RecordHandler {

	@Inject
	private EModelService modelService;

	@Inject
	private MApplication application;

	@Inject
	private IEventBroker eventBroker;

	private Map<HTMLElement, FileEntity> entitySavedMap;

	@CanExecute
	public boolean canExecute() {
		if (ProjectController.getInstance().getCurrentProject() != null) {
			MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
					application);
			if (composerStack.isVisible() && composerStack.getSelectedElement() != null) {
				MPart part = (MPart) composerStack.getSelectedElement();
				if (part.getElementId().startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)) {
					return true;
				}
			}
		}
		return false;
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
		try {
			MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
					application);
			MPart selectedPart = (MPart) composerStack.getSelectedElement();
			if (selectedPart.getElementId().startsWith(IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)
					&& selectedPart.getObject() instanceof TestCaseCompositePart) {
				TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) selectedPart.getObject();
				boolean isVerified = verifyTestCase(activeShell, testCaseCompositePart);
				if (!isVerified) {
					return;
				}
				RecorderDialog recordDialog = new RecorderDialog(activeShell,
						LoggerSingleton.getInstance().getLogger(), eventBroker);
				int responseCode = recordDialog.open();
				if (responseCode == Window.OK) {
					List<Statement> generatedStatements = generateStatementsFromRecordedActions(
							recordDialog.getActions(), recordDialog.getElements(), testCaseCompositePart.getTestCase());
					testCaseCompositePart.addStatements(generatedStatements);
					testCaseCompositePart.save();
				}
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, 
					StringConstants.HAND_ERROR_MSG_CANNOT_GEN_TEST_STEPS);
			LoggerSingleton.logError(e);
		}
	}

	private boolean verifyTestCase(Shell activeShell, TestCaseCompositePart testCaseCompositePart) throws Exception {
		if (testCaseCompositePart.getDirty().isDirty()) {
			MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_PLS_SAVE_TEST_CASE);
			return false;
		}
		try {
			testCaseCompositePart.getAstNodesFromScript();
		} catch (CompilationFailedException compilationFailedExcption) {
			MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_PLS_FIX_TEST_CASE);
			return false;
		}
		return true;
	}

	private void addRecordedElement(HTMLElement element, FolderEntity parentFolder, WebElementEntity refElement)
			throws Exception {
		WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
				HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);
		entitySavedMap.put(element, importedElement);
		if (element instanceof HTMLFrameElement) {
			for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
				addRecordedElement(childElement, parentFolder, importedElement);
			}
		}
	}

	private void addRecordedElements(List<HTMLPageElement> recordedElements, FolderEntity parentFolder)
			throws Exception {
		entitySavedMap = new HashMap<HTMLElement, FileEntity>();
		for (HTMLPageElement pageElement : recordedElements) {
			FolderEntity importedFolder = ObjectRepositoryController.getInstance().importWebElementFolder(
					HTMLElementUtil.convertPageElementToFolderEntity(pageElement, parentFolder), parentFolder);
			entitySavedMap.put(pageElement, importedFolder);
			for (HTMLElement childElement : pageElement.getChildElements()) {
				addRecordedElement(childElement, (importedFolder != null) ? importedFolder : parentFolder, null);
			}
		}
	}

	private List<Statement> generateStatementsFromRecordedActions(List<HTMLAction> recordedActions,
			List<HTMLPageElement> recordedElements, TestCaseEntity selectedTestCase) throws Exception {

		FolderEntity objectRepositoryRootFolder = FolderController.getInstance().getObjectRepositoryRoot(
				ProjectController.getInstance().getCurrentProject());
		addRecordedElements(recordedElements, objectRepositoryRootFolder);

		List<Statement> resultStatements = new ArrayList<Statement>();

		// add open browser keyword
		List<Expression> arguments = new ArrayList<Expression>();
		arguments.add(new ConstantExpression(""));
		arguments.add(HTMLActionUtil.generateFailureHandlingExpression());
		MethodCallExpression methodCallExpression = new MethodCallExpression(new VariableExpression(
				WebUiBuiltInKeywords.class.getSimpleName()), "openBrowser", new ArgumentListExpression(arguments));

		resultStatements.add(new ExpressionStatement(methodCallExpression));

		// add switch to window keyword if action in another window
		recordedActions = addSwitchToWindowKeyword(recordedActions);

		for (HTMLAction action : recordedActions) {
			WebElementEntity createdTestObject = null;
			if (action.getTargetElement() != null
					&& entitySavedMap.get(action.getTargetElement()) instanceof WebElementEntity) {
				createdTestObject = (WebElementEntity) entitySavedMap.get(action.getTargetElement());
			}
			Statement generatedStatement = HTMLActionUtil.generateWebUiTestStep(action, createdTestObject);
			if (generatedStatement != null) {
				resultStatements.add(generatedStatement);
			}
		}

		// add close browser keyword
		arguments = new ArrayList<Expression>();
		arguments.add(HTMLActionUtil.generateFailureHandlingExpression());
		methodCallExpression = new MethodCallExpression(new VariableExpression(
				WebUiBuiltInKeywords.class.getSimpleName()), "closeBrowser", new ArgumentListExpression(arguments));

		resultStatements.add(new ExpressionStatement(methodCallExpression));

		return resultStatements;
	}

	private List<HTMLAction> addSwitchToWindowKeyword(List<HTMLAction> recordedActions) {
		List<HTMLAction> newActions = new ArrayList<HTMLAction>();
		String currentWindowId = null;
		for (HTMLAction action : recordedActions) {
			String newId = action.getWindowId();
			if (newId != null) {
				if (currentWindowId == null) {
					currentWindowId = newId;
				} else if (!newId.equals(currentWindowId)) {
					HTMLAction switchToWindowAction = HTMLActionUtil.createNewSwitchToWindowAction(HTMLActionUtil
							.getPageTitleForAction(action));
					newActions.add(switchToWindowAction);
					currentWindowId = newId;
				}
			} else {
				currentWindowId = "";
			}
			newActions.add(action);
		}
		return newActions;
	}
}
