package com.kms.katalon.composer.artifact.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.ExecutionProfileEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.model.TestObjectEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.kms.katalon.composer.artifact.constant.ImageConstants;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.keyword.dialogs.KeywordSelectionDialog;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;

public class ExportTestArtifactDialog extends Dialog {

    private List<TestCaseEntity> selectedTestCases = new ArrayList<>();

    private List<TestObjectEntity> selectedTestObjects = new ArrayList<>();
    
    private List<ExecutionProfileEntity> selectedProfiles = new ArrayList<>();

    private List<File> selectedKeywords = new ArrayList<>();

    private TableViewer testCaseTableViewer;

    private ToolItem btnAddTestCase;

    private ToolItem btnDeleteTestCase;

    private TableViewer testObjectTableViewer;

    private ToolItem btnAddTestObject;

    private ToolItem btnDeleteTestObject;
    
    private TableViewer profileTableViewer;
    
    private ToolItem btnAddProfile;
    
    private ToolItem btnDeleteProfile;
    
    private ToolItem btnAddKeyword;
    
    private ToolItem btnDeleteKeyword;
    
    private TableViewer keywordTableViewer;
    
    private Text txtExportLocation;
    
    private Button btnChooseExportFolder;
    
    private ExportTestArtifactDialogResult dialogResult;

    public ExportTestArtifactDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridLayout glBody = new GridLayout(1, false);
        body.setLayout(glBody);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 500;
        body.setLayoutData(gdBody);

        createTestCaseSelectionSection(body);

        createTestObjectSelectionSection(body);
        
        createProfileSelectionSection(body);
        
        createKeywordSelectionSection(body);
        
        createExportLocationSection(body);

        registerControlListeners();

        return body;
    }

    private void createTestCaseSelectionSection(Composite parent) {
        Label lblTestCase = new Label(parent, SWT.NONE);
        lblTestCase.setText(StringConstants.LBL_TEST_CASE);

        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAddTestCase = new ToolItem(toolBar, SWT.FLAT);
        btnAddTestCase.setText(StringConstants.TOOL_ITEM_ADD);
        btnAddTestCase.setImage(ImageConstants.IMG_16_ADD);

        btnDeleteTestCase = new ToolItem(toolBar, SWT.FLAT);
        btnDeleteTestCase.setText(StringConstants.TOOL_ITEM_DELETE);
        btnDeleteTestCase.setImage(ImageConstants.IMG_16_DELETE);
        btnDeleteTestCase.setEnabled(false);

        Composite testCaseTableComposite = new Composite(parent, SWT.NONE);
        GridData gdTestCaseTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdTestCaseTableComposite.heightHint = 200;
        testCaseTableComposite.setLayoutData(gdTestCaseTableComposite);
        testCaseTableComposite.setLayout(new FillLayout());

        testCaseTableViewer = new TableViewer(testCaseTableComposite, SWT.BORDER | SWT.MULTI);
        testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table testCaseTable = testCaseTableViewer.getTable();
        testCaseTable.setHeaderVisible(true);
        testCaseTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnTestCaseId = new TableViewerColumn(testCaseTableViewer, SWT.LEFT);
        TableColumn tableColumnId = tableViewerColumnTestCaseId.getColumn();
        tableColumnId.setText(StringConstants.COL_TEST_CASE_ID);
        tableViewerColumnTestCaseId.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                TestCaseEntity testCase = (TestCaseEntity) element;
                return testCase.getId();
            }
        });

        TableColumnLayout testCaseTableLayout = new TableColumnLayout();
        testCaseTableLayout.setColumnData(tableColumnId, new ColumnWeightData(100, 30));
        testCaseTableComposite.setLayout(testCaseTableLayout);

        testCaseTableViewer.setInput(selectedTestCases);
        testCaseTableViewer.refresh();
    }

    private void createTestObjectSelectionSection(Composite parent) {
        Label lblTestObject = new Label(parent, SWT.NONE);
        lblTestObject.setText(StringConstants.LBL_TEST_OBJECT);

        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAddTestObject = new ToolItem(toolBar, SWT.FLAT);
        btnAddTestObject.setText(StringConstants.TOOL_ITEM_ADD);
        btnAddTestObject.setImage(ImageConstants.IMG_16_ADD);

        btnDeleteTestObject = new ToolItem(toolBar, SWT.FLAT);
        btnDeleteTestObject.setText(StringConstants.TOOL_ITEM_DELETE);
        btnDeleteTestObject.setImage(ImageConstants.IMG_16_DELETE);
        btnDeleteTestObject.setEnabled(false);

        Composite testObjectTableComposite = new Composite(parent, SWT.NONE);
        GridData gdtestObjectTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdtestObjectTableComposite.heightHint = 200;
        testObjectTableComposite.setLayoutData(gdtestObjectTableComposite);
        testObjectTableComposite.setLayout(new FillLayout());

        testObjectTableViewer = new TableViewer(testObjectTableComposite, SWT.BORDER | SWT.MULTI);
        testObjectTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table testObjectTable = testObjectTableViewer.getTable();
        testObjectTable.setHeaderVisible(true);
        testObjectTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnTestObjectId = new TableViewerColumn(testObjectTableViewer, SWT.LEFT);
        TableColumn tableColumnId = tableViewerColumnTestObjectId.getColumn();
        tableColumnId.setText(StringConstants.COL_TEST_OBJECT_ID);
        tableViewerColumnTestObjectId.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                TestObjectEntity testObject = (TestObjectEntity) element;
                return testObject.getId();
            }
        });

        TableColumnLayout testObjectTableLayout = new TableColumnLayout();
        testObjectTableLayout.setColumnData(tableColumnId, new ColumnWeightData(100, 30));
        testObjectTableComposite.setLayout(testObjectTableLayout);

        testObjectTableViewer.setInput(selectedTestObjects);
        testObjectTableViewer.refresh();
    }
    
    private void createProfileSelectionSection(Composite parent) {
        Label lblProfile = new Label(parent, SWT.NONE);
        lblProfile.setText(StringConstants.LBL_PROFILE);

        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAddProfile = new ToolItem(toolBar, SWT.FLAT);
        btnAddProfile.setText(StringConstants.TOOL_ITEM_ADD);
        btnAddProfile.setImage(ImageConstants.IMG_16_ADD);

        btnDeleteProfile = new ToolItem(toolBar, SWT.FLAT);
        btnDeleteProfile.setText(StringConstants.TOOL_ITEM_DELETE);
        btnDeleteProfile.setImage(ImageConstants.IMG_16_DELETE);
        btnDeleteProfile.setEnabled(false);

        Composite profileTableComposite = new Composite(parent, SWT.NONE);
        GridData gdProfileTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdProfileTableComposite.heightHint = 200;
        profileTableComposite.setLayoutData(gdProfileTableComposite);
        profileTableComposite.setLayout(new FillLayout());

        profileTableViewer = new TableViewer(profileTableComposite, SWT.BORDER | SWT.MULTI);
        profileTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table profileTable = profileTableViewer.getTable();
        profileTable.setHeaderVisible(true);
        profileTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnProfileId = new TableViewerColumn(profileTableViewer, SWT.LEFT);
        TableColumn tableColumnId = tableViewerColumnProfileId.getColumn();
        tableColumnId.setText(StringConstants.COL_PROFILE_ID);
        tableViewerColumnProfileId.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                ExecutionProfileEntity profile = (ExecutionProfileEntity) element;
                return profile.getId();
            }
        });

        TableColumnLayout profileTableLayout = new TableColumnLayout();
        profileTableLayout.setColumnData(tableColumnId, new ColumnWeightData(100, 30));
        profileTableComposite.setLayout(profileTableLayout);

        profileTableViewer.setInput(selectedProfiles);
        profileTableViewer.refresh();
    }
    
    private void createKeywordSelectionSection(Composite parent) {
        Label lblKeyword = new Label(parent, SWT.NONE);
        lblKeyword.setText(StringConstants.LBL_KEYWORD);

        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAddKeyword = new ToolItem(toolBar, SWT.FLAT);
        btnAddKeyword.setText(StringConstants.TOOL_ITEM_ADD);
        btnAddKeyword.setImage(ImageConstants.IMG_16_ADD);

        btnDeleteKeyword = new ToolItem(toolBar, SWT.FLAT);
        btnDeleteKeyword.setText(StringConstants.TOOL_ITEM_DELETE);
        btnDeleteKeyword.setImage(ImageConstants.IMG_16_DELETE);
        btnDeleteKeyword.setEnabled(false);

        Composite keywordTableComposite = new Composite(parent, SWT.NONE);
        GridData gdKeywordTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdKeywordTableComposite.heightHint = 200;
        keywordTableComposite.setLayoutData(gdKeywordTableComposite);
        keywordTableComposite.setLayout(new FillLayout());

        keywordTableViewer = new TableViewer(keywordTableComposite, SWT.BORDER | SWT.MULTI);
        keywordTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        Table keywordTable = keywordTableViewer.getTable();
        keywordTable.setHeaderVisible(true);
        keywordTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnKeywordId = new TableViewerColumn(keywordTableViewer, SWT.LEFT);
        TableColumn tableColumnId = tableViewerColumnKeywordId.getColumn();
        tableColumnId.setText(StringConstants.COL_KEYWORD_ID);
        tableViewerColumnKeywordId.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                File keyword = (File) element;
                String keywordFilePath = keyword.getAbsolutePath();
                String keywordRelativeId = keywordFilePath.substring(keywordFilePath.indexOf("Keywords"));
                return FilenameUtils.removeExtension(keywordRelativeId);
            }
        });

        TableColumnLayout keywordTableLayout = new TableColumnLayout();
        keywordTableLayout.setColumnData(tableColumnId, new ColumnWeightData(100, 30));
        keywordTableComposite.setLayout(keywordTableLayout);

        keywordTableViewer.setInput(selectedKeywords);
        keywordTableViewer.refresh();
    }
    
    private void createExportLocationSection(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        composite.setLayout(glComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        Label lblExportLocation = new Label(composite, SWT.NONE);
        lblExportLocation.setText(StringConstants.LBL_EXPORT_LOCATION);
        
        Composite fileChooserComposite = new Composite(composite, SWT.NONE);
        GridLayout glFileChooserComposite = new GridLayout(2, false);
        glFileChooserComposite.marginWidth = 0;
        fileChooserComposite.setLayout(glFileChooserComposite);
        fileChooserComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        txtExportLocation = new Text(fileChooserComposite, SWT.BORDER);
        txtExportLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        btnChooseExportFolder = new Button(fileChooserComposite, SWT.NONE);
        btnChooseExportFolder.setText(StringConstants.BTN_BROWSE);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    private void registerControlListeners() {
        btnAddTestCase.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    TestCaseEntity[] testCaseEntities = PlatformUtil.getUIService(DialogActionService.class)
                            .showTestCaseSelectionDialog(activeShell, "Select test cases");
                    selectedTestCases.addAll(Arrays.asList(testCaseEntities));
                    testCaseTableViewer.refresh();
                } catch (PlatformException ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });

        btnDeleteTestCase.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] selections = testCaseTableViewer.getStructuredSelection().toArray();
                for (Object testCase : selections) {
                    selectedTestCases.remove((TestCaseEntity) testCase);
                }
                testCaseTableViewer.refresh();
                if (selectedTestCases.isEmpty()) {
                    btnDeleteTestCase.setEnabled(false);
                }
                validateInput();
            }
        });
        
        testCaseTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object[] selections = testCaseTableViewer.getStructuredSelection().toArray();
                if (selections != null && selections.length > 0) {
                    btnDeleteTestCase.setEnabled(true);
                } else {
                    btnDeleteTestCase.setEnabled(false);
                }
            }
            
        });

        btnAddTestObject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    TestObjectEntity[] testObjectEntities = PlatformUtil.getUIService(DialogActionService.class)
                            .showTestObjectSelectionDialog(activeShell, "Select test objects");
                    selectedTestObjects.addAll(Arrays.asList(testObjectEntities));
                    testObjectTableViewer.refresh();
                } catch (PlatformException ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });

        btnDeleteTestObject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] selections = testObjectTableViewer.getStructuredSelection().toArray();
                for (Object testObject : selections) {
                    selectedTestObjects.remove((TestObjectEntity) testObject);
                }
                testObjectTableViewer.refresh();
                if (selectedTestObjects.isEmpty()) {
                    btnDeleteTestObject.setEnabled(false);
                }
                validateInput();
            }
        });
        
        testObjectTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object[] selections = testObjectTableViewer.getStructuredSelection().toArray();
                if (selections != null && selections.length > 0) {
                    btnDeleteTestObject.setEnabled(true);
                } else {
                    btnDeleteTestObject.setEnabled(false);
                }
            }
        });
            
        btnAddProfile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    ExecutionProfileEntity[] profileEntities = PlatformUtil.getUIService(DialogActionService.class)
                            .showExecutionProfileSelectionDialog(activeShell, "Select profiles");
                    selectedProfiles.addAll(Arrays.asList(profileEntities));
                    profileTableViewer.refresh();
                } catch (PlatformException ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });
        
        btnDeleteProfile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] selections = profileTableViewer.getStructuredSelection().toArray();
                for (Object profile : selections) {
                    selectedProfiles.remove((ExecutionProfileEntity) profile);
                }
                profileTableViewer.refresh();
                if (selectedProfiles.isEmpty()) {
                    btnDeleteProfile.setEnabled(false);
                }
                validateInput();
            }
        });
        
        profileTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object[] selections = profileTableViewer.getStructuredSelection().toArray();
                if (selections != null && selections.length > 0) {
                    btnDeleteProfile.setEnabled(true);
                } else {
                    btnDeleteProfile.setEnabled(false);
                }
            }
        });
        
        btnAddKeyword.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell activeShell = e.widget.getDisplay().getActiveShell();
                try {
                    List<File> keywords = showKeywordSelectionDialog(activeShell);
                    selectedKeywords.addAll(keywords);
                    keywordTableViewer.refresh();
                } catch (Exception ex) {
                    MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
                }
                validateInput();
            }
        });
        
        btnDeleteKeyword.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] selections = keywordTableViewer.getStructuredSelection().toArray();
                for (Object keyword : selections) {
                    selectedKeywords.remove((File) keyword);
                }
                keywordTableViewer.refresh();
                if (selectedKeywords.isEmpty()) {
                    btnDeleteKeyword.setEnabled(false);
                }
                validateInput();
            }
        });
        
        keywordTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                Object[] selections = keywordTableViewer.getStructuredSelection().toArray();
                if (selections != null && selections.length > 0) {
                    btnDeleteKeyword.setEnabled(true);
                } else {
                    btnDeleteKeyword.setEnabled(false);
                }
            }
        });
        
        btnChooseExportFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = new DirectoryDialog(e.widget.getDisplay().getActiveShell());
                directoryDialog.setFilterPath(txtExportLocation.getText());
                String directoryLocation = directoryDialog.open();
                txtExportLocation.setText(directoryLocation);
                validateInput();
            }
        });
        
        txtExportLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent arg0) {
                validateInput();
            }
        });
    }
    
    private List<File> showKeywordSelectionDialog(Shell parentShell) throws Exception {
        EntityLabelProvider labelProvider = new EntityLabelProvider();
        EntityProvider contentProvider = new EntityProvider();
        KeywordSelectionDialog selectionDialog = new KeywordSelectionDialog(
                parentShell, labelProvider, contentProvider, new EntityViewerFilter(contentProvider));
        selectionDialog.setTitle("Select keywords");
        selectionDialog.setAllowMultiple(false);
        FolderTreeEntity keywordFolderRoot;
        try {
            keywordFolderRoot = new FolderTreeEntity(FolderController.getInstance()
                    .getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null);
        } catch (Exception e) {
            throw new ResourceException("Could not initialize profile folder", e);
        }
        selectionDialog.setInput(Arrays.asList(keywordFolderRoot));
        if (selectionDialog.open() != TreeEntitySelectionDialog.OK || selectionDialog.getResult() == null) {
            return null;
        }
        KeywordTreeEntity[] selectedTreeEntities = selectionDialog.getSelectedKeywords();
        List<File> selectedKeywords = new ArrayList<>();
        for (KeywordTreeEntity selectedTreeEntity : selectedTreeEntities) {
            ICompilationUnit compilationUnit = ((ICompilationUnit) selectedTreeEntity.getObject());
            String location = compilationUnit.getResource().getLocation().toString();
            File keyword = new File(location);
            selectedKeywords.add(keyword);
        }
        return selectedKeywords;
    }
    
    private void validateInput() {
        boolean isValid = (!selectedTestCases.isEmpty() || !selectedTestObjects.isEmpty() || !selectedProfiles.isEmpty()
                || !selectedKeywords.isEmpty()) && StringUtils.isNotBlank(txtExportLocation.getText());
        getButton(IDialogConstants.OK_ID).setEnabled(isValid);
    }

    @Override
    protected void okPressed() {
        dialogResult = new ExportTestArtifactDialogResult();
        dialogResult.setSelectedTestCases(filterDuplicatedTestCases(selectedTestCases));
        dialogResult.setSelectedTestObjects(filterDuplicatedTestObjects(selectedTestObjects));
        dialogResult.setSelectedProfiles(filterDuplicatedProfiles(selectedProfiles));
        dialogResult.setSelectedKeywords(filterDuplicatedKeywords(selectedKeywords));
        dialogResult.setExportLocation(txtExportLocation.getText());
        super.okPressed();
    }
    
    private List<TestCaseEntity> filterDuplicatedTestCases(List<TestCaseEntity> testCases) {
        Set<String> testCaseIds = new HashSet<>();
        List<TestCaseEntity> filteredResult =  new ArrayList<>();
        for (TestCaseEntity testCase : testCases) {
            if (!testCaseIds.contains(testCase.getId())) {
                filteredResult.add(testCase);
                testCaseIds.add(testCase.getId());
            }
        }
        return filteredResult;
    }
    
    private List<TestObjectEntity> filterDuplicatedTestObjects(List<TestObjectEntity> testObjects) {
        Set<String> testObjectIds = new HashSet<>();
        List<TestObjectEntity> filteredResult =  new ArrayList<>();
        for (TestObjectEntity testObject : testObjects) {
            if (!testObjectIds.contains(testObject.getId())) {
                filteredResult.add(testObject);
                testObjectIds.add(testObject.getId());
            }
        }
        return filteredResult;
    }
    
    private List<ExecutionProfileEntity> filterDuplicatedProfiles(List<ExecutionProfileEntity> profiles) {
        Set<String> profileIds = new HashSet<>();
        List<ExecutionProfileEntity> filteredResult = new ArrayList<>();
        for (ExecutionProfileEntity profile : profiles) {
            if (!profileIds.contains(profile.getId())) {
                filteredResult.add(profile);
                profileIds.add(profile.getId());
            }
        }
        return filteredResult;
    }
    
    private List<File> filterDuplicatedKeywords(List<File> keywords) {
        Set<String> keywordIds = new HashSet<>();
        List<File> filteredResult = new ArrayList<>();
        for (File keyword : keywords) {
            if (!keywordIds.contains(keyword.getAbsolutePath())) {
                filteredResult.add(keyword);
                keywordIds.add(keyword.getAbsolutePath());
            }
        }
        return filteredResult;
    }
    
    public ExportTestArtifactDialogResult getResult() {
        return dialogResult;
    }
    
    @Override
    protected boolean isResizable() {
        return true;
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIALOG_TITLE_EXPORT_TEST_ARTIFACTS);
    }
    
    public class ExportTestArtifactDialogResult {
        private List<TestCaseEntity> selectedTestCases;
        
        private List<TestObjectEntity> selectedTestObjects;
        
        private List<ExecutionProfileEntity> selectedProfiles;
        
        private List<File> selectedKeywords;
        
        private String exportLocation;

        public List<TestCaseEntity> getSelectedTestCases() {
            return selectedTestCases;
        }

        public void setSelectedTestCases(List<TestCaseEntity> selectedTestCases) {
            this.selectedTestCases = selectedTestCases;
        }

        public List<TestObjectEntity> getSelectedTestObjects() {
            return selectedTestObjects;
        }

        public void setSelectedTestObjects(List<TestObjectEntity> selectedTestObjects) {
            this.selectedTestObjects = selectedTestObjects;
        }

        public List<ExecutionProfileEntity> getSelectedProfiles() {
            return selectedProfiles;
        }

        public void setSelectedProfiles(List<ExecutionProfileEntity> selectedProfiles) {
            this.selectedProfiles = selectedProfiles;
        }
        
        public List<File> getSelectedKeywords() {
            return selectedKeywords;
        }

        public void setSelectedKeywords(List<File> selectedKeywords) {
            this.selectedKeywords = selectedKeywords;
        }

        public String getExportLocation() {
            return exportLocation;
        }

        public void setExportLocation(String exportLocation) {
            this.exportLocation = exportLocation;
        }
    }
}
