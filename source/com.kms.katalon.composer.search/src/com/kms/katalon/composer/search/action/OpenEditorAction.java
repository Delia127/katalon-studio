package com.kms.katalon.composer.search.action;

import java.text.MessageFormat;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.search.constants.StringConstants;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

/**
 * This class is a utility class that provides navigating methods to
 * {@link MPart} of the given IEntity
 * 
 * @author duyluong
 *
 */
@SuppressWarnings("restriction")
public class OpenEditorAction {

	public static void openTestCase(TestCaseEntity testCase) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.send(EventConstants.TESTCASE_OPEN, testCase);
	}

	public static void openTestObject(WebElementEntity testObject) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.send(EventConstants.TEST_OBJECT_OPEN, testObject);
	}

	public static void openTestSuite(TestSuiteEntity testSuite) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.send(EventConstants.TEST_SUITE_OPEN, testSuite);
	}

	public static void openTestData(DataFileEntity testData) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.send(EventConstants.TEST_DATA_OPEN, testData);
	}

	public static void openReport(ReportEntity report) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.send(EventConstants.REPORT_OPEN, report);
    }

    public static void openExecutionProfile(ExecutionProfileEntity profile) {
        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.send(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, profile);
    }

	/**
	 * Opens {@link TestCaseCompositePart} of the given testCase, sets selected part is <code>Script</code> part
	 * and highlights the Script part at the line number by the given offset and length.
	 * @param testCase the given {@link TestCaseEntity} will be opened.
	 * @param offset: line offset of script of the given testCase
	 * @param length: the highlighting length of the selected line
	 */
	public static void openEditor(TestCaseEntity testCase, int offset, int length) {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		MApplication application = ApplicationSingleton.getInstance().getApplication();
		EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
		try {
			if (testCase == null) {
				//System cannot find test case
				MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
						StringConstants.ACT_WARN_MSG_TEST_CASE_NOT_FOUND);
				return;
			}

			eventBroker.send(EventConstants.TESTCASE_OPEN, testCase);
			MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
			if (stack != null) {
				String testCaseCompositePartId = EntityPartUtil.getTestCaseCompositePartId(testCase.getId());
				MCompositePart mcompositePart = (MCompositePart) modelService.find(testCaseCompositePartId, stack);
				TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) mcompositePart.getObject();

				testCaseCompositePart.setSelectedPart(testCaseCompositePart.getChildCompatibilityPart());

				CompatibilityEditor groovyEditor = (CompatibilityEditor) testCaseCompositePart
						.getChildCompatibilityPart().getObject();
				AbstractTextEditor editor = (AbstractTextEditor) groovyEditor.getEditor();
				try {
					editor.selectAndReveal(offset, length);
				} catch (Exception e) {
					//Editor have already been opened but system cannot find the line to be highlighted.
					MessageDialog
							.openWarning(
									null,
									StringConstants.WARN_TITLE,
									MessageFormat.format(StringConstants.ACT_WARN_MSG_LINE_NOT_FOUND,
											Integer.toString(offset)));
					return;
				}
			}
		} catch (Exception e) {
			//System cannot find test case
			MultiStatusErrorDialog.showErrorDialog(e, StringConstants.WARN_TITLE,
					StringConstants.ACT_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE);
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}
}
