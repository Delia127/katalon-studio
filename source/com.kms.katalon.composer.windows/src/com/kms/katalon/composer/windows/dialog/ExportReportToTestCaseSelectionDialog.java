package com.kms.katalon.composer.windows.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.dialogs.TestCaseFolderSelectionDialog;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ExportReportToTestCaseSelectionDialog extends AbstractDialog {

    public static enum ExportTestCaseOption {
        EXPORT_TO_NEW_TEST_CASE("Export to new test case"),
        APPEND_TO_TEST_CASE("Append to test case"),
        OVERWRITE_TEST_CASE("Overwrite test case");

        private final String text;

        private ExportTestCaseOption(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static String[] textValues() {
            List<String> textValues = new ArrayList<>();
            for (ExportTestCaseOption option : values()) {
                textValues.add(option.text);
            }
            return textValues.toArray(new String[0]);
        }

        public static ExportTestCaseOption fromText(String text) {
            for (ExportTestCaseOption option : values()) {
                if (option.getText().equals(text)) {
                    return option;
                }
            }
            return null;
        }
    }

    private static interface ExportOptionComposite {
        Composite createContent(Composite parent);

        Composite getCreatedContent();

        boolean isAbleToOk();

        ExportTestCaseSelectionResult getResult();
    }

    private static interface ExportParentComposite {
        void updateState();
    }

    public static class ExportTestCaseSelectionResult {
        private final ExportTestCaseOption option;

        private final String testCaseName;

        private final FolderEntity folder;

        public ExportTestCaseSelectionResult(ExportTestCaseOption option, String testCaseName, FolderEntity folder) {
            this.option = option;
            this.testCaseName = testCaseName;
            this.folder = folder;
        }

        public ExportTestCaseOption getOption() {
            return option;
        }

        public String getTestCaseName() {
            return testCaseName;
        }

        public FolderEntity getFolder() {
            return folder;
        }
    }

    private Combo cbbExportOption;

    private StackLayout slExportDetails;

    private Composite exportDetailsComposite;

    private Map<ExportTestCaseOption, ExportOptionComposite> exportCompositeOptions = new HashMap<>();

    private ExportTestCaseOption selectedOption;

    private ExportTestCaseSelectionResult result;

    public ExportReportToTestCaseSelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
        cbbExportOption.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedOption = ExportTestCaseOption.fromText(cbbExportOption.getText());
                slExportDetails.topControl = exportCompositeOptions.get(selectedOption).getCreatedContent();
                exportDetailsComposite.layout();
                updateButtonState();
            }
        });
    }

    @Override
    protected void setInput() {
        updateButtonState();
    }

    private void updateButtonState() {
        getButton(OK).setEnabled(exportCompositeOptions.get(selectedOption).isAbleToOk());
    }

    private ExportParentComposite refreshStateHandler = new ExportParentComposite() {

        @Override
        public void updateState() {
            updateButtonState();
        }
    };

    @Override
    protected Composite createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        Label lblExportSelection = new Label(container, SWT.NONE);
        lblExportSelection.setText("Export Option");

        cbbExportOption = new Combo(container, SWT.READ_ONLY);
        cbbExportOption.setItems(ExportTestCaseOption.textValues());
        cbbExportOption.select(0);
        selectedOption = ExportTestCaseOption.EXPORT_TO_NEW_TEST_CASE;

        exportDetailsComposite = new Composite(container, SWT.NONE);
        exportDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        slExportDetails = new StackLayout();
        exportDetailsComposite.setLayout(slExportDetails);

        ExportNewTestCaseComposite exportNewTestCaseComposite = new ExportNewTestCaseComposite(refreshStateHandler);
        Composite exportNewTestCaseControl = exportNewTestCaseComposite.createContent(exportDetailsComposite);
        exportCompositeOptions.put(ExportTestCaseOption.EXPORT_TO_NEW_TEST_CASE, exportNewTestCaseComposite);

        AppendTestCaseComposite appendTestCaseComposite = new AppendTestCaseComposite(refreshStateHandler);
        appendTestCaseComposite.createContent(exportDetailsComposite);
        exportCompositeOptions.put(ExportTestCaseOption.APPEND_TO_TEST_CASE, appendTestCaseComposite);

        OverwriteTestCaseComposite overwriteTestCaseComposite = new OverwriteTestCaseComposite(refreshStateHandler);
        overwriteTestCaseComposite.createContent(exportDetailsComposite);
        exportCompositeOptions.put(ExportTestCaseOption.OVERWRITE_TEST_CASE, overwriteTestCaseComposite);

        slExportDetails.topControl = exportNewTestCaseControl;

        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }
    
    @Override
    protected void okPressed() {
        setResult(exportCompositeOptions.get(selectedOption).getResult());
        super.okPressed();
    }

    public ExportTestCaseSelectionResult getResult() {
        return result;
    }

    public void setResult(ExportTestCaseSelectionResult result) {
        this.result = result;
    }

    private class ExportNewTestCaseComposite implements ExportOptionComposite {

        private ExportParentComposite parentComposite;

        private Text txtTestCaseName;

        private Button btnBrowserParent;

        private FolderEntity selectedFolder;

        private List<String> currentNames = new ArrayList<>();

        private Text txtParentFolderId;

        private CLabel lblMessage;

        private Composite exportNewTestCaseComposite;

        public ExportNewTestCaseComposite(ExportParentComposite parentComposite) {
            this.parentComposite = parentComposite;
        }

        @Override
        public Composite createContent(Composite parent) {
            exportNewTestCaseComposite = new Composite(parent, SWT.NONE);
            GridLayout glExportNewTestCase = new GridLayout(2, false);
            glExportNewTestCase.marginWidth = 0;
            glExportNewTestCase.marginHeight = 0;
            exportNewTestCaseComposite.setLayout(glExportNewTestCase);

            new Label(exportNewTestCaseComposite, SWT.NONE);

            lblMessage = new CLabel(exportNewTestCaseComposite, SWT.NONE);
            lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            setInfoMessage();

            Label lblParentFolder = new Label(exportNewTestCaseComposite, SWT.NONE);
            lblParentFolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblParentFolder.setText("Folder");

            Composite parentFolderComposite = new Composite(exportNewTestCaseComposite, SWT.NONE);
            parentFolderComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridLayout glParentFolder = new GridLayout(2, false);
            glParentFolder.marginWidth = 0;
            glParentFolder.marginHeight = 0;
            glParentFolder.verticalSpacing = 10;
            parentFolderComposite.setLayout(glParentFolder);

            txtParentFolderId = new Text(parentFolderComposite, SWT.BORDER | SWT.READ_ONLY);
            txtParentFolderId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            btnBrowserParent = new Button(parentFolderComposite, SWT.PUSH);
            btnBrowserParent.setText("Browse...");

            Label lblTestCaseName = new Label(exportNewTestCaseComposite, SWT.NONE);
            lblTestCaseName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            lblTestCaseName.setText("Test Case Name");

            txtTestCaseName = new Text(exportNewTestCaseComposite, SWT.BORDER);
            txtTestCaseName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            setInput();

            registerControlListeners();

            return exportNewTestCaseComposite;
        }

        private void setInput() {
            try {
                this.selectedFolder = FolderController.getInstance()
                        .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());
                updateParentFolderText(selectedFolder);
                String name = TestCaseController.getInstance().getAvailableTestCaseName(selectedFolder, "New Test Case");
                this.txtTestCaseName.setText(name);
                txtTestCaseName.forceFocus();
                txtTestCaseName.selectAll();
            } catch (ControllerException e) {
                LoggerSingleton.logError(e);
            }
        }

        private void updateParentFolderText(FolderEntity folder) throws ControllerException {
            selectedFolder = folder;
            List<TestCaseEntity> siblings = FolderController.getInstance().getTestCaseChildren(selectedFolder);
            currentNames = siblings.stream().map(s -> s.getName()).collect(Collectors.toList());
            txtParentFolderId.setText(selectedFolder.getIdForDisplay());
        }

        private void registerControlListeners() {
            txtTestCaseName.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    parentComposite.updateState();
                }
            });

            btnBrowserParent.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TestCaseFolderSelectionDialog dialog = new TestCaseFolderSelectionDialog(
                            btnBrowserParent.getShell());
                    if (dialog.open() != TestCaseFolderSelectionDialog.OK) {
                        return;
                    }
                    try {
                        updateParentFolderText(dialog.getSelectedFolder().getObject());
                        parentComposite.updateState();
                    } catch (Exception ex) {
                        MultiStatusErrorDialog.showErrorDialog(ex, GlobalStringConstants.WARN, ex.getMessage());
                        LoggerSingleton.logError(ex);
                    }
                }
            });
        }

        @Override
        public boolean isAbleToOk() {
            String text = txtTestCaseName.getText();
            if (StringUtils.isEmpty(text)) {
                setInfoMessage();
                return false;
            }
            if (!currentNames.contains(text)) {
                setInfoMessage();
                return true;
            }
            setDuplicatedErrorMessage();
            return false;
        }

        private void setDuplicatedErrorMessage() {
            lblMessage.setImage(ImageManager.getImage(IImageKeys.ERROR_16));
            lblMessage.setText("Inputted name already existed");
        }

        private void setInfoMessage() {
            lblMessage.setImage(ImageManager.getImage(IImageKeys.INFO_16));
            lblMessage.setText("Enter a new test case name");
        }

        @Override
        public Composite getCreatedContent() {
            return exportNewTestCaseComposite;
        }

        @Override
        public ExportTestCaseSelectionResult getResult() {
            String testCaseName = txtTestCaseName.getText();
            return new ExportTestCaseSelectionResult(ExportTestCaseOption.EXPORT_TO_NEW_TEST_CASE, testCaseName,
                    selectedFolder);
        }
    }

    private class OverwriteTestCaseComposite implements ExportOptionComposite {

        private CLabel lblMessage;

        private Button btnBrowserTestCase;

        private ExportParentComposite parentComposite;

        private Composite appendToTestCaseComposite;

        private TestCaseEntity testCase;

        private Text txtTestCaseId;

        public OverwriteTestCaseComposite(ExportParentComposite parentComposite) {
            this.parentComposite = parentComposite;
        }

        @Override
        public Composite createContent(Composite parent) {
            appendToTestCaseComposite = new Composite(parent, SWT.NONE);
            appendToTestCaseComposite.setLayout(new GridLayout(2, false));
            new Label(appendToTestCaseComposite, SWT.NONE);

            lblMessage = new CLabel(appendToTestCaseComposite, SWT.NONE);
            lblMessage.setText("Select a test case");
            lblMessage.setImage(ImageManager.getImage(IImageKeys.INFO_16));

            Label lblTestCase = new Label(appendToTestCaseComposite, SWT.NONE);
            lblTestCase.setText("Test Case");

            Composite testCaseComposite = new Composite(appendToTestCaseComposite, SWT.NONE);
            testCaseComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridLayout glTestCaseComposite = new GridLayout(2, false);
            glTestCaseComposite.marginWidth = 0;
            glTestCaseComposite.marginHeight = 0;
            testCaseComposite.setLayout(glTestCaseComposite);

            txtTestCaseId = new Text(testCaseComposite, SWT.BORDER | SWT.READ_ONLY);
            txtTestCaseId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            btnBrowserTestCase = new Button(testCaseComposite, SWT.PUSH);
            btnBrowserTestCase.setText("Browse...");

            registerControlListeners();

            return appendToTestCaseComposite;
        }

        private void registerControlListeners() {
            btnBrowserTestCase.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    EntityProvider entityProvider = new EntityProvider();
                    TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(
                            Display.getCurrent().getActiveShell(), new EntityLabelProvider(), new EntityProvider(),
                            new EntityViewerFilter(entityProvider));
                    try {
                        FolderEntity rootFolder = FolderController.getInstance()
                                .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());

                        dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
                        if (dialog.open() != TreeEntitySelectionDialog.OK) {
                            return;
                        }

                        TestCaseTreeEntity testCaseTree = (TestCaseTreeEntity) dialog.getFirstResult();
                        testCase = testCaseTree.getObject();
                        txtTestCaseId.setText(testCaseTree.getObject().getIdForDisplay());
                        parentComposite.updateState();
                    } catch (Exception ex) {
                        MultiStatusErrorDialog.showErrorDialog(ex, GlobalStringConstants.WARN, ex.getMessage());
                    }
                }
            });
        }

        @Override
        public boolean isAbleToOk() {
            return StringUtils.isNotEmpty(txtTestCaseId.getText());
        }

        @Override
        public Composite getCreatedContent() {
            return appendToTestCaseComposite;
        }

        @Override
        public ExportTestCaseSelectionResult getResult() {
            return new ExportTestCaseSelectionResult(ExportTestCaseOption.OVERWRITE_TEST_CASE, testCase.getName(),
                    testCase.getParentFolder());
        }
    }

    private class AppendTestCaseComposite implements ExportOptionComposite {

        private CLabel lblMessage;

        private Button btnBrowserTestCase;

        private ExportParentComposite parentComposite;

        private Composite appendToTestCaseComposite;

        private TestCaseEntity testCase;

        private Text txtTestCaseId;

        public AppendTestCaseComposite(ExportParentComposite parentComposite) {
            this.parentComposite = parentComposite;
        }

        @Override
        public Composite createContent(Composite parent) {
            appendToTestCaseComposite = new Composite(parent, SWT.NONE);
            appendToTestCaseComposite.setLayout(new GridLayout(2, false));
            new Label(appendToTestCaseComposite, SWT.NONE);

            lblMessage = new CLabel(appendToTestCaseComposite, SWT.NONE);
            lblMessage.setText("Select a test case");
            lblMessage.setImage(ImageManager.getImage(IImageKeys.INFO_16));

            Label lblTestCase = new Label(appendToTestCaseComposite, SWT.NONE);
            lblTestCase.setText("Test Case");

            Composite testCaseComposite = new Composite(appendToTestCaseComposite, SWT.NONE);
            testCaseComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            GridLayout glTestCaseComposite = new GridLayout(2, false);
            glTestCaseComposite.marginWidth = 0;
            glTestCaseComposite.marginHeight = 0;
            testCaseComposite.setLayout(glTestCaseComposite);

            txtTestCaseId = new Text(testCaseComposite, SWT.BORDER | SWT.READ_ONLY);
            txtTestCaseId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            btnBrowserTestCase = new Button(testCaseComposite, SWT.PUSH);
            btnBrowserTestCase.setText("Browse...");

            registerControlListeners();

            return appendToTestCaseComposite;
        }

        private void registerControlListeners() {
            btnBrowserTestCase.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    EntityProvider entityProvider = new EntityProvider();
                    TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(
                            Display.getCurrent().getActiveShell(), new EntityLabelProvider(), new EntityProvider(),
                            new EntityViewerFilter(entityProvider));
                    try {
                        FolderEntity rootFolder = FolderController.getInstance()
                                .getTestCaseRoot(ProjectController.getInstance().getCurrentProject());

                        dialog.setInput(TreeEntityUtil.getChildren(null, rootFolder));
                        if (dialog.open() != TreeEntitySelectionDialog.OK) {
                            return;
                        }

                        TestCaseTreeEntity testCaseTree = (TestCaseTreeEntity) dialog.getFirstResult();
                        testCase = testCaseTree.getObject();
                        txtTestCaseId.setText(testCaseTree.getObject().getIdForDisplay());
                        parentComposite.updateState();
                    } catch (Exception ex) {
                        MultiStatusErrorDialog.showErrorDialog(ex, GlobalStringConstants.WARN, ex.getMessage());
                    }
                }
            });
        }

        @Override
        public boolean isAbleToOk() {
            return StringUtils.isNotEmpty(txtTestCaseId.getText());
        }

        @Override
        public Composite getCreatedContent() {
            return appendToTestCaseComposite;
        }

        @Override
        public ExportTestCaseSelectionResult getResult() {
            return new ExportTestCaseSelectionResult(ExportTestCaseOption.APPEND_TO_TEST_CASE, testCase.getName(),
                    testCase.getParentFolder());
        }
    }
}
