package com.kms.katalon.composer.integration.qtest.wizard;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.job.DisintegrateTestCaseJob;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.integration.qtest.wizard.page.AuthenticationWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.FinishPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.OptionalSettingWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.ProjectChoosingWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.QTestModuleSelectionWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.TestCaseFolderSelectionWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.page.TestSuiteFolderSelectionWizardPage;
import com.kms.katalon.composer.integration.qtest.wizard.provider.WizardTableLabelProvider;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationProjectManager;
import com.kms.katalon.integration.qtest.credential.IQTestCredential;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.setting.QTestAttachmentSendingType;
import com.kms.katalon.integration.qtest.setting.QTestResultSendingType;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class SetupWizardDialog extends Dialog implements IWizardPageChangedListerner {

    private static final int BUTTON_WIDTH = 80;

    private int lastTreeWidth;

    // Controls
    private Composite stepArea;
    private TableViewer tableViewer;
    private Button backButton;
    private Button nextButton;
    private Button finishButton;
    private Button cancelButton;
    private Composite stepDetailsComposite;
    private Label lblStepHeader;

    // Fields
    private WizardManager wizardManager;

    private Map<String, Object> sharedData;

    public SetupWizardDialog(Shell parentShell) {
        super(parentShell);
        lastTreeWidth = 220;
        wizardManager = new WizardManager();
    }

    @Override
    protected Control createContents(Composite parent) {
        // create the top level composite for the dialog
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);
        // initialize the dialog units
        initializeDialogUnits(composite);
        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);

        return composite;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogComposite = (Composite) super.createDialogArea(parent);
        GridLayout glDialogComposite = ((GridLayout) dialogComposite.getLayout());
        glDialogComposite.numColumns = 4;
        glDialogComposite.marginHeight = 0;
        glDialogComposite.marginWidth = 0;
        glDialogComposite.verticalSpacing = 0;
        glDialogComposite.horizontalSpacing = 0;

        Composite stepTreeComposite = createStepTableComposite(dialogComposite);

        layoutTreeAreaControl(stepTreeComposite);

        createSash(dialogComposite, stepTreeComposite);

        Label label = new Label(dialogComposite, SWT.SEPARATOR);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        createStepAreaComposite(dialogComposite);

        return dialogComposite;
    }

    private Composite createStepTableComposite(Composite dialogComposite) {
        Composite stepTreeComposite = new Composite(dialogComposite, SWT.NONE);
        stepTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        stepTreeComposite.setLayout(new GridLayout(1, false));
        stepTreeComposite.setBackground(ColorUtil.getWhiteBackgroundColor());

        tableViewer = new TableViewer(stepTreeComposite, SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        return stepTreeComposite;
    }

    private void createStepAreaComposite(Composite dialogComposite) {
        stepArea = new Composite(dialogComposite, SWT.NONE);
        GridLayout glStepArea = new GridLayout(1, false);
        glStepArea.horizontalSpacing = 0;
        glStepArea.verticalSpacing = 0;
        glStepArea.marginWidth = 0;
        glStepArea.marginHeight = 0;
        stepArea.setLayout(glStepArea);
        stepArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite stepHeaderComposite = new Composite(stepArea, SWT.NONE);
        stepHeaderComposite.setLayout(new GridLayout(1, false));
        stepHeaderComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        stepHeaderComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        stepHeaderComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        lblStepHeader = new Label(stepHeaderComposite, SWT.NONE);
        lblStepHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        FontData[] fD = lblStepHeader.getFont().getFontData();
        fD[0].setHeight(14);
        fD[0].setStyle(SWT.BOLD);
        lblStepHeader.setFont(new Font(Display.getCurrent(), fD[0]));

        Label separator = new Label(stepArea, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        stepDetailsComposite = new Composite(stepArea, SWT.NONE);
        stepDetailsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        stepDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label separator1 = new Label(stepArea, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite buttonBarComposite = new Composite(stepArea, SWT.NONE);
        buttonBarComposite.setLayout(new GridLayout(4, false));
        buttonBarComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));

        backButton = new Button(buttonBarComposite, SWT.FLAT);
        GridData gdBackButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdBackButton.widthHint = BUTTON_WIDTH;
        backButton.setLayoutData(gdBackButton);
        backButton.setText(StringConstants.WZ_SETUP_BTN_BACK);

        nextButton = new Button(buttonBarComposite, SWT.FLAT);
        GridData gdNextButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdNextButton.widthHint = BUTTON_WIDTH;
        nextButton.setLayoutData(gdNextButton);
        nextButton.setText(StringConstants.WZ_SETUP_BTN_NEXT);

        finishButton = new Button(buttonBarComposite, SWT.FLAT);
        GridData gdFinishButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdFinishButton.widthHint = BUTTON_WIDTH;
        finishButton.setLayoutData(gdFinishButton);
        finishButton.setText(StringConstants.CM_FINISH);

        cancelButton = new Button(buttonBarComposite, SWT.FLAT);
        GridData gdCancelButton = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gdCancelButton.widthHint = BUTTON_WIDTH;
        cancelButton.setLayoutData(gdCancelButton);
        cancelButton.setText(StringConstants.CM_CANCEL);
    }

    protected void layoutTreeAreaControl(Control control) {
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        gd.horizontalAlignment = SWT.FILL;
        gd.widthHint = getLastRightWidth();
        gd.verticalSpan = 1;
        control.setLayoutData(gd);
    }

    protected int getLastRightWidth() {
        return lastTreeWidth;
    }

    protected Sash createSash(final Composite composite, final Control rightControl) {
        final Sash sash = new Sash(composite, SWT.VERTICAL);
        sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        // the following listener resizes the tree control based on sash deltas.
        // If necessary, it will also grow/shrink the dialog.
        sash.addListener(SWT.Selection, new Listener() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
             */
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.DRAG) {
                    return;
                }
                int shift = event.x - sash.getBounds().x;
                GridData data = (GridData) rightControl.getLayoutData();
                int newWidthHint = data.widthHint + shift;
                if (newWidthHint < 20) {
                    return;
                }
                Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                Point currentSize = getShell().getSize();
                // if the dialog wasn't of a custom size we know we can shrink
                // it if necessary based on sash movement.
                boolean customSize = !computedSize.equals(currentSize);
                data.widthHint = newWidthHint;
                setLastTreeWidth(newWidthHint);
                composite.layout(true);
                // recompute based on new widget size
                computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                // if the dialog was of a custom size then increase it only if
                // necessary.
                if (customSize) {
                    computedSize.x = Math.max(computedSize.x, currentSize.x);
                }
                computedSize.y = Math.max(computedSize.y, currentSize.y);
                if (computedSize.equals(currentSize)) {
                    return;
                }
                setShellSize(computedSize.x, computedSize.y);
            }
        });
        return sash;
    }

    protected void setLastTreeWidth(int newWidthHint) {
        lastTreeWidth = newWidthHint;
    }

    private void setShellSize(int width, int height) {
        Rectangle preferred = getShell().getBounds();
        preferred.width = width;
        preferred.height = height;
        getShell().setBounds(getConstrainedShellBounds(preferred));
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(arg | SWT.RESIZE);
    }

    @Override
    public void create() {
        super.create();
        setInput();
        registerControlModifyListeners();
    }

    private void registerControlModifyListeners() {
        cancelButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                cancelPressed();
            }
        });

        backButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                backPressed();
            }
        });

        nextButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                nextPressed();
            }
        });

        finishButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                finishPressed();
            }
        });

        // Disable user click on step table
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    tableViewer.setSelection(StructuredSelection.EMPTY);
                }
            }
        });

        // Disable color change when selected item changed
        tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(Event event) {
                // Selection:
                event.detail &= ~SWT.SELECTED;
                // Expect: selection now has no visual effect.
                // Actual: selection remains but changes from light blue to white.

                // MouseOver:
                event.detail &= ~SWT.HOT;
                // Expect: mouse over now has no visual effect.
                // Actual: behavior remains unchanged.
            }
        });

        // Set items's height of Step Table to 32 pixel
        tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
            public void handleEvent(Event event) {
                // height cannot be per row so simply set
                event.height = 32;
            }
        });

    }

    private void backPressed() {
        Map<String, Object> pageSharedData = wizardManager.getCurrentPage().storeControlStates();
        if (pageSharedData != null) {
            sharedData.putAll(pageSharedData);
            ;
        }

        showPage(wizardManager.backPage());
    }

    private void nextPressed() {
        Map<String, Object> pageSharedData = wizardManager.getCurrentPage().storeControlStates();
        if (pageSharedData != null) {
            sharedData.putAll(pageSharedData);
            ;
        }

        showPage(wizardManager.nextPage());
    }

    private void setInput() {
        sharedData = new HashMap<String, Object>();

        wizardManager.addPage(new AuthenticationWizardPage()).addPage(new ProjectChoosingWizardPage())
                .addPage(new QTestModuleSelectionWizardPage()).addPage(new TestCaseFolderSelectionWizardPage())
                .addPage(new TestSuiteFolderSelectionWizardPage()).addPage(new OptionalSettingWizardPage())
                .addPage(new FinishPage());
        tableViewer.setLabelProvider(new WizardTableLabelProvider(wizardManager));
        tableViewer.setInput(wizardManager.getWizardPages());

        showPage(wizardManager.getCurrentPage());
    }

    private void showPage(IWizardPage page) {
        tableViewer.refresh(true);

        if (page instanceof AbstractWizardPage) {
            ((AbstractWizardPage) page).addChangedListeners(this);
        }

        lblStepHeader.setText(MessageFormat.format(StringConstants.WZ_SETUP_STEP_TITLE,
                Integer.toString(wizardManager.getWizardPages().indexOf(page) + 1),
                Integer.toString(wizardManager.getWizardPages().size()), page.getTitle()));

        updateStepArea(page);

        updateButtonBar(page);

        page.setInput(sharedData);
        page.registerControlModifyListeners();
    }

    private void updateStepArea(IWizardPage page) {
        while (stepDetailsComposite.getChildren().length > 0) {
            stepDetailsComposite.getChildren()[0].dispose();
        }

        page.createStepArea(stepDetailsComposite);
        stepDetailsComposite.getChildren();
        stepDetailsComposite.layout(true, true);
    }

    private void updateButtonBar(IWizardPage page) {
        finishButton.setEnabled(page.canFinish() && page.canFlipToNextPage());

        backButton.setEnabled(wizardManager.getWizardPages().indexOf(page) > 0);

        nextButton.setEnabled(page.canFlipToNextPage()
                && wizardManager.getWizardPages().indexOf(page) < wizardManager.getWizardPages().size() - 1);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(800, 500);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    private String getDialogTitle() {
        return StringConstants.WZ_SETUP_TITLE;
    }

    private void finishPressed() {
        updateQTestSetting();
        updateProject();
        super.okPressed();
    }

    @SuppressWarnings("unchecked")
    private void updateQTestSetting() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        String projectDir = currentProject.getFolderLocation();
        try {
            QTestSettingStore.saveUserProfile(getCredential(sharedData), projectDir);
            QTestSettingStore.saveAutoSubmit((boolean) sharedData.get(QTestSettingStore.AUTO_SUBMIT_RESULT_PROPERTY),
                    projectDir);
            QTestSettingStore.saveEnableCheckBeforeUploading(
                    (boolean) sharedData.get(QTestSettingStore.CHECK_BEFORE_UPLOADING), projectDir);
            QTestSettingStore.saveResultSendingType(
                    (List<QTestResultSendingType>) sharedData.get(QTestSettingStore.SEND_RESULT_PROPERTY), projectDir);
            QTestSettingStore.saveAttachmentSendingType(
                    (List<QTestAttachmentSendingType>) sharedData.get(QTestSettingStore.SEND_ATTACHMENTS_PROPERTY),
                    projectDir);
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void disintegrateAllTestCaseRepos() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();

        IntegratedEntity projectIntegratedEntity = QTestIntegrationUtil.getIntegratedEntity(currentProject);
        if (projectIntegratedEntity == null) {
            return;
        }
        try {
            List<TestCaseRepo> testCaseRepos = QTestIntegrationUtil.getTestCaseRepositories(currentProject,
                    QTestIntegrationProjectManager.getQTestProjectsByIntegratedEntity(projectIntegratedEntity));

            List<IntegratedFileEntity> integratedTestCaseFolder = new ArrayList<IntegratedFileEntity>();
            for (TestCaseRepo testCaseRepo : testCaseRepos) {
                try {
                    FolderEntity folderEntity = FolderController.getInstance().getFolderByDisplayId(currentProject,
                            testCaseRepo.getFolderId());
                    if (folderEntity != null) {
                        integratedTestCaseFolder.add(folderEntity);
                    }
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            }

            DisintegrateTestCaseJob job = new DisintegrateTestCaseJob();
            job.setFileEntities(integratedTestCaseFolder);
            job.doTask();
            job.join();
        } catch (InterruptedException ex) {

        }
    }

    private void updateProject() {
        try {
            // Disintegrate all current test case repo
            disintegrateAllTestCaseRepos();

            QTestProject defaultQTestPrject = (QTestProject) sharedData.get("qTestProject");
            QTestModule selectedModule = (QTestModule) sharedData.get("qTestModule");
            FolderTreeEntity selectedTestCaseFolderTree = (FolderTreeEntity) sharedData.get("testCaseFolder");
            FolderTreeEntity selectedTestSuiteFolderTree = (FolderTreeEntity) sharedData.get("testSuiteFolder");

            FolderEntity testCaseFolderEntity = (FolderEntity) selectedTestCaseFolderTree.getObject();
            FolderEntity testSuiteFolderEntity = (FolderEntity) selectedTestSuiteFolderTree.getObject();

            String testCaseFolderId = FolderController.getInstance().getIdForDisplay(testCaseFolderEntity);
            String testSuiteFolderId = FolderController.getInstance().getIdForDisplay(testSuiteFolderEntity);

            defaultQTestPrject.getTestCaseFolderIds().add(testCaseFolderId);
            defaultQTestPrject.getTestSuiteFolderIds().add(testSuiteFolderId);

            IntegratedEntity testCaseFolderIntegratedEntity = QTestIntegrationFolderManager
                    .getFolderIntegratedEntityByQTestModule(selectedModule);
            testCaseFolderEntity = (FolderEntity) QTestIntegrationUtil.updateFileIntegratedEntity(testCaseFolderEntity,
                    testCaseFolderIntegratedEntity);
            FolderController.getInstance().saveFolder(testCaseFolderEntity);

            @SuppressWarnings("unchecked")
            IntegratedEntity projectIntegratedEntity = QTestIntegrationProjectManager
                    .getIntegratedEntityByQTestProjects((List<QTestProject>) sharedData.get("qTestProjects"));

            ProjectEntity currentProject = (ProjectEntity) QTestIntegrationUtil.updateFileIntegratedEntity(
                    ProjectController.getInstance().getCurrentProject(), projectIntegratedEntity);
            ProjectController.getInstance().updateProject(currentProject);
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    public void handlePageChanged(WizardPageChangedEvent event) {
        if (event.getWizardPage() == null || !event.getWizardPage().equals(wizardManager.getCurrentPage())) {
            return;
        }
        updateButtonBar(event.getWizardPage());
    }

    public static IQTestCredential getCredential(final Map<String, Object> sharedData) {
        return new IQTestCredential() {

            /**
             * Returns server URL without ending with "/"
             * <p>
             * For example:
             * <li>http://a.com -> http://a.com</li>
             * <li>https://b.com/ -> https://b.com</li>
             * <li>https://c.com/// -> https://c.com</li>
             */
            @Override
            public String getServerUrl() {
                String serverUrl = (String) sharedData.get(QTestSettingStore.SERVER_URL_PROPERTY);

                if (StringUtils.isBlank(serverUrl.trim())) {
                    return "";
                }

                serverUrl = serverUrl.trim();

                while (!serverUrl.isEmpty() && serverUrl.endsWith("/")) {
                    serverUrl = serverUrl.substring(0, serverUrl.length() - 1);
                }
                return serverUrl;
            }

            @Override
            public String getUsername() {
                return (String) sharedData.get(QTestSettingStore.USERNAME_PROPERTY);
            }

            @Override
            public String getPassword() {
                return (String) sharedData.get(QTestSettingStore.PASSWORD_PROPERTY);
            }

            @Override
            public String getToken() {
                return (String) sharedData.get(QTestSettingStore.TOKEN_PROPERTY);
            }
        };
    }
}
