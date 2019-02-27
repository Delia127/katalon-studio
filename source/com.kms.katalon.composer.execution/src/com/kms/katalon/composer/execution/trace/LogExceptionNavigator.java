package com.kms.katalon.composer.execution.trace;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.XmlLogRecordException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class LogExceptionNavigator {

    private IEventBroker eventBroker;

    private EModelService modelService;

    private MApplication application;

    private EPartService partService;

    public LogExceptionNavigator() {
        this.eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        this.modelService = ModelServiceSingleton.getInstance().getModelService();
        this.application = ApplicationSingleton.getInstance().getApplication();
        this.partService = PartServiceSingleton.getInstance().getPartService();
    }

    public TestCaseCompositePart openTestCaseComposite(TestCaseEntity testCase) {
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        if (stack == null) {
            return null;
        }
        String testCaseCompositePartId = EntityPartUtil.getTestCaseCompositePartId(testCase.getId());
        MCompositePart mcompositePart = (MCompositePart) modelService.find(testCaseCompositePartId, stack);
        if (mcompositePart == null) {
            eventBroker.send(EventConstants.TESTCASE_OPEN, testCase);
            mcompositePart = (MCompositePart) modelService.find(testCaseCompositePartId, stack);
        }
        partService.showPart(mcompositePart, PartState.ACTIVATE);

        TestCaseCompositePart testCaseCompositePart = (TestCaseCompositePart) mcompositePart.getObject();
        return testCaseCompositePart;
    }

    public MPart getTestCaseGroovyEditor(TestCaseEntity testCase) {
        TestCaseCompositePart testCaseCompositePart = openTestCaseComposite(testCase);
        if (testCaseCompositePart == null) {
            return null;
        }
        testCaseCompositePart.setSelectedPart(testCaseCompositePart.getChildCompatibilityPart());
        MPart groovyEditor = testCaseCompositePart.getChildCompatibilityPart();
        return groovyEditor;
    }

    public void openTestCaseByLogException(XmlLogRecordException logException) {
        try {
            TestCaseEntity testCase = LogExceptionFilter.getTestCaseByLogException(logException);
            if (testCase == null) {
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                        StringConstants.TRACE_WARN_MSG_TEST_CASE_NOT_FOUND);
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
                    IDocument document = editor.getDocumentProvider()
                            .getDocument(groovyEditor.getEditor().getEditorInput());
                    editor.selectAndReveal(document.getLineOffset(logException.getLineNumber() - 1),
                            document.getLineLength(logException.getLineNumber() - 1));
                } catch (Exception e) {
                    MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                            "Line " + Integer.toString(logException.getLineNumber())
                                    + StringConstants.TRACE_WARN_MSG_NOT_FOUND);
                    return;
                }

            }
        } catch (Exception e) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    StringConstants.TRACE_WARN_MSG_UNABLE_TO_OPEN_TEST_CASE);
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    public void openKeywordByLogException(XmlLogRecordException logException) {
        try {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            IFolder keywordRootFolder = GroovyUtil.getCustomKeywordSourceFolder(project);
            String fullClassName = logException.getClassName();
            int classIndex = fullClassName.lastIndexOf(".");
            String className = fullClassName;
            IFile customKeywordScriptFile = null;
            if (classIndex > 0) {
                String packageName = fullClassName.substring(0, classIndex);
                className = fullClassName.substring(classIndex + 1, fullClassName.length());
                IFolder keywordPackageFolder = keywordRootFolder.getFolder(packageName.replace(".", "/"));
                customKeywordScriptFile = keywordPackageFolder
                        .getFile(className + GroovyConstants.GROOVY_FILE_EXTENSION);
            } else {
                className = fullClassName;
                customKeywordScriptFile = keywordRootFolder.getFile(className + GroovyConstants.GROOVY_FILE_EXTENSION);
            }

//            IType type = JavaCore.create(GroovyUtil.getGroovyProject(project)).findType(className);

            if (customKeywordScriptFile != null) {
                AbstractTextEditor editor = (AbstractTextEditor) IDE.openEditor(
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), customKeywordScriptFile);
                IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
                editor.selectAndReveal(document.getLineOffset(logException.getLineNumber() - 1),
                        document.getLineLength(logException.getLineNumber() - 1));

            }
        } catch (Exception e) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    StringConstants.TRACE_WARN_MSG_UNABLE_TO_OPEN_KEYWORD_FILE);
        }
    }
}
