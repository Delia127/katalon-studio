package com.kms.katalon.composer.testlistener.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.eclipse.refactoring.actions.FormatGroovyAction;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testlistener.dialog.NewTestListenerDialog;
import com.kms.katalon.composer.testlistener.dialog.NewTestListenerDialog.NewTestListenerResult;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.entity.file.TestListenerEntity;

public class NewTestListenerHandler extends TestListenerTreeRootCatcher {

    private static final List<String> sampleMethodNames;

    static {
        sampleMethodNames = Arrays.asList("com.kms.katalon.core.annotation.BeforeTestCase",
                "com.kms.katalon.core.annotation.AfterTestCase", "com.kms.katalon.core.annotation.BeforeTestSuite",
                "com.kms.katalon.core.annotation.AfterTestSuite");
    }

    @Inject
    private ESelectionService selectionService;

    @Inject
    private IEventBroker eventBroker;

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
        TestListenerFolderTreeEntity parentTreeFolder = getParentTestListenerTreeFolder(selectionService, true);
        if (parentTreeFolder == null) {
            return;
        }

        try {
            TestListenerController testListenerController = TestListenerController.getInstance();

            NewTestListenerDialog dialog = new NewTestListenerDialog(parentShell,
                    testListenerController.getTestListeners(parentTreeFolder.getObject()));
            if (dialog.open() != NewTestListenerDialog.OK) {
                return;
            }

            NewTestListenerResult result = dialog.getResult();
            TestListenerEntity entity = testListenerController.newTestListener(result.getNewName(),
                    parentTreeFolder.getObject());
            FileUtils.write(new File(entity.getLocation()),
                    new TestListenerScriptBuilder(result).buildTestListenerScript(), StringConstants.DF_CHARSET, true);
            OpenTestListenerHandler openHandler = new OpenTestListenerHandler();

            ITextEditor editor = openHandler.openEditor(entity);
            if (editor != null) {
                formatEditor(editor);
            }

            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeFolder);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void formatEditor(ITextEditor editor) {
        FormatGroovyAction formatAction = (FormatGroovyAction) editor.getAction("Format");

        IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
        formatAction.run(new TextSelection(0, document.getLength()));
        editor.doSave(new NullProgressMonitor());
    }

    private class TestListenerScriptBuilder {
        private final NewTestListenerResult result;

        public TestListenerScriptBuilder(NewTestListenerResult result) {
            this.result = result;
        }

        private String getSampleScript(String annotation) {
            switch (annotation) {
                case "com.kms.katalon.core.annotation.BeforeTestCase": {
                    return getFileContent("resources/template/before_test_case.tpl");
                }
                case "com.kms.katalon.core.annotation.AfterTestCase": {
                    return getFileContent("resources/template/after_test_case.tpl");
                }
                case "com.kms.katalon.core.annotation.BeforeTestSuite": {
                    return getFileContent("resources/template/before_test_suite.tpl");
                }
                case "com.kms.katalon.core.annotation.AfterTestSuite": {
                    return getFileContent("resources/template/after_test_suite.tpl");
                }
                default: {
                    return StringUtils.EMPTY;
                }

            }
        }

        private String buildListSampleMethods() {
            List<String> sampleStrings = result.getSampleMethodAllowed()
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue())
                    .map(e -> e.getKey())
                    .sorted(new Comparator<String>() {
                        @Override
                        public int compare(String methodA, String methodB) {
                            return sampleMethodNames.indexOf(methodA) - sampleMethodNames.indexOf(methodB);
                        }
                    })
                    .map(m -> getSampleScript(m))
                    .collect(Collectors.toList());
            return StringUtils.join(sampleStrings, "\n\n");
        }

        public String buildTestListenerScript() {
            String newTestListenerScript = getFileContent("resources/template/new_test_listener.tpl");
            StringBuilder scriptBuilder = new StringBuilder(newTestListenerScript).append("\n\n")
                    .append(String.format("class %s {\n %s \n}", result.getNewName(), buildListSampleMethods()));
            return scriptBuilder.toString();
        }

        private String getFileContent(String filePath) {
            URL url = FileLocator.find(FrameworkUtil.getBundle(NewTestListenerHandler.class), new Path(filePath), null);
            try {
                return StringUtils.join(IOUtils.readLines(new BufferedInputStream(url.openStream()),
                                GlobalStringConstants.DF_CHARSET), "\n");
            } catch (IOException e) {
                LoggerSingleton.logError(e);
                return StringUtils.EMPTY;
            }
        }
    }
}
