package com.kms.katalon.composer.integration.qtest.view.testsuite;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.CreateNewTestSuiteParentDialog;
import com.kms.katalon.composer.integration.qtest.dialog.model.TestSuiteParentCreationOption;
import com.kms.katalon.composer.integration.qtest.model.TestSuiteRepo;
import com.kms.katalon.composer.integration.qtest.preferences.QTestPreferenceDefaultValueInitializer;
import com.kms.katalon.composer.integration.qtest.view.testsuite.providers.QTestSuiteTableLabelProvider;
import com.kms.katalon.composer.testsuite.parts.integration.AbstractTestSuiteIntegrationView;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationTestSuiteManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.entity.QTestSuite;
import com.kms.katalon.integration.qtest.entity.QTestSuiteParent;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.setting.QTestSettingStore;

public class QTestIntegrationTestSuiteView extends AbstractTestSuiteIntegrationView {
    public QTestIntegrationTestSuiteView(TestSuiteEntity testSuiteEntity, MPart mpart) {
        super(testSuiteEntity, mpart);
    }

    private Text txtID, txtParentID, txtPID;
    private List<QTestSuite> qTestSuites;

    private Button btnUpload, btnDisintegrate, btnNavigate, btnUpdateParent, btnSetDefault;
    private Composite compositeParent, compositeSelectedParent;

    private TableViewer testSuiteParentTableViewer;
    private TableColumn tblclmnName;
    private TableViewerColumn tableViewerColumnName;
    private TableColumn tblclmnType;
    private TableViewerColumn tableViewerColumnType;
    private TableColumn tblclmnDefault;
    private TableViewerColumn tableViewerColumnDefault;
    private Composite compositeTable;
    private Label lblTableParentLabel;
    private Button btnRemove;

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        Composite compositeButton = new Composite(container, SWT.NONE);
        GridLayout glCompositeButton = new GridLayout(6, false);
        glCompositeButton.marginHeight = 0;
        glCompositeButton.marginWidth = 0;
        compositeButton.setLayout(glCompositeButton);

        btnUpload = new Button(compositeButton, SWT.FLAT);
        btnUpload.setToolTipText(StringConstants.VIEW_TOOLTIP_UPLOAD_TEST_SUITE);
        btnUpload.setText(StringConstants.CM_UPLOAD);

        btnDisintegrate = new Button(compositeButton, SWT.FLAT);
        btnDisintegrate
                .setToolTipText(StringConstants.VIEW_TOOLTIP_DISINTEGRATE_TEST_SUITE);
        btnDisintegrate.setText(StringConstants.CM_DISINTEGRATE);

        btnNavigate = new Button(compositeButton, SWT.FLAT);
        btnNavigate.setToolTipText(StringConstants.VIEW_TOOLTIP_NAVIGATE_TEST_SUITE);
        btnNavigate.setText(StringConstants.CM_NAVIGATE);

        btnUpdateParent = new Button(compositeButton, SWT.FLAT);
        btnUpdateParent
                .setToolTipText(StringConstants.VIEW_TOOLTIP_NEW_TEST_SUITE_PARENT);
        btnUpdateParent.setText(StringConstants.VIEW_TITLE_NEW_TEST_SUITE_PARENT);

        btnSetDefault = new Button(compositeButton, SWT.FLAT);
        btnSetDefault
                .setToolTipText(StringConstants.VIEW_TOOLTIP_SET_DEFAULT_TEST_SUITE);
        btnSetDefault.setText(StringConstants.VIEW_TITLE_SET_DEFAULT_TEST_SUITE);

        btnRemove = new Button(compositeButton, SWT.FLAT);
        btnRemove.setToolTipText(StringConstants.VIEW_TOOLTIP_REMOVE_TEST_SUITE_PARENT);
        btnRemove.setText(StringConstants.VIEW_TITLE_REMOVE_TEST_SUITE_PARENT);
        btnRemove.setEnabled(false);

        SashForm sashForm = new SashForm(container, SWT.NONE);
        sashForm.setSashWidth(10);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createTestSuiteParentTreeView(sashForm);

        compositeSelectedParent = new Composite(sashForm, SWT.NONE);
        compositeSelectedParent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeSelectedParent = new GridLayout(1, false);
        glCompositeSelectedParent.marginWidth = 0;
        glCompositeSelectedParent.marginHeight = 0;
        compositeSelectedParent.setLayout(glCompositeSelectedParent);

        Composite compositeSelectedParentHeader = new Composite(compositeSelectedParent, SWT.NONE);
        compositeSelectedParentHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeSelectedParentHeader = new GridLayout(1, false);
        glCompositeSelectedParentHeader.marginWidth = 0;
        compositeSelectedParentHeader.setLayout(glCompositeSelectedParentHeader);

        Label lblSelectedParentHeader = new Label(compositeSelectedParentHeader, SWT.NONE);
        lblSelectedParentHeader.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblSelectedParentHeader.setText(StringConstants.VIEW_TITLE_INTEGRATION_INFORMATION);

        Composite compositeSelectedParentDetails = new Composite(compositeSelectedParent, SWT.BORDER);
        compositeSelectedParentDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_compositeSelectedParentDetails = new GridLayout(2, false);
        gl_compositeSelectedParentDetails.horizontalSpacing = 15;
        compositeSelectedParentDetails.setLayout(gl_compositeSelectedParentDetails);

        Label lblQTestId = new Label(compositeSelectedParentDetails, SWT.NONE);
        lblQTestId.setText(StringConstants.VIEW_TITLE_TEST_SUITE_ID);

        txtID = new Text(compositeSelectedParentDetails, SWT.BORDER | SWT.READ_ONLY);
        txtID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPID = new Label(compositeSelectedParentDetails, SWT.NONE);
        lblPID.setText(StringConstants.CM_ALIAS);

        txtPID = new Text(compositeSelectedParentDetails, SWT.BORDER | SWT.READ_ONLY);
        txtPID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblParentId = new Label(compositeSelectedParentDetails, SWT.NONE);
        lblParentId.setText(StringConstants.CM_PARENT_ID);

        txtParentID = new Text(compositeSelectedParentDetails, SWT.BORDER | SWT.READ_ONLY);
        txtParentID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        sashForm.setWeights(new int[] { 4, 6 });

        initialize();
        controlModifyListeners();

        return container;
    }

    private void createTestSuiteParentTreeView(Composite compositeInfo) {

        compositeParent = new Composite(compositeInfo, SWT.NONE);
        compositeParent.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        GridLayout glCompositeParent = new GridLayout(1, false);
        glCompositeParent.marginWidth = 0;
        glCompositeParent.marginHeight = 0;
        compositeParent.setLayout(glCompositeParent);

        Composite compositeTableHeader = new Composite(compositeParent, SWT.NONE);
        compositeTableHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeTableHeader = new GridLayout(1, false);
        glCompositeTableHeader.marginWidth = 0;
        compositeTableHeader.setLayout(glCompositeTableHeader);

        lblTableParentLabel = new Label(compositeTableHeader, SWT.NONE);
        lblTableParentLabel.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblTableParentLabel.setText(StringConstants.VIEW_TITLE_LIST_TEST_SUITE_PARENT);

        compositeTable = new Composite(compositeParent, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testSuiteParentTableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = testSuiteParentTableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        table.setHeaderVisible(true);

        tableViewerColumnName = new TableViewerColumn(testSuiteParentTableViewer, SWT.NONE);
        tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setText(StringConstants.NAME);

        tableViewerColumnType = new TableViewerColumn(testSuiteParentTableViewer, SWT.NONE);
        tblclmnType = tableViewerColumnType.getColumn();
        tblclmnType.setText(StringConstants.CM_TYPE);

        tableViewerColumnDefault = new TableViewerColumn(testSuiteParentTableViewer, SWT.NONE);
        tblclmnDefault = tableViewerColumnDefault.getColumn();
        tblclmnDefault.setText(StringConstants.CM_DEFAULT);

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnName, new ColumnWeightData(80, 0));
        tableLayout.setColumnData(tblclmnType, new ColumnWeightData(0, 100));
        tableLayout.setColumnData(tblclmnDefault, new ColumnWeightData(0, 50));
        compositeTable.setLayout(tableLayout);

        testSuiteParentTableViewer.setLabelProvider(new QTestSuiteTableLabelProvider());
        testSuiteParentTableViewer.setContentProvider(new ArrayContentProvider());
    }

    private void controlModifyListeners() {
        btnUpload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();

                QTestSuite selectedQTestSuite = (QTestSuite) selection.getFirstElement();
                uploadTestSuite(selectedQTestSuite);
            }
        });

        btnNavigate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                navigateToQTest();
            }

        });

        btnDisintegrate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                disintegrateTestSuiteWithQTest();
            }
        });

        btnUpdateParent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createTestSuiteParent();
            }
        });

        btnSetDefault.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();
                QTestSuite selectedQTestSuite = (QTestSuite) selection.getFirstElement();
                setDefaultQTestSuite(selectedQTestSuite);
            }
        });

        btnRemove.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeParentFromParentList();
            }
        });

        testSuiteParentTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!btnRemove.isEnabled()) {
                    btnRemove.setEnabled(true);
                }

                reloadView();
            }
        });

        compositeTable.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                compositeTable.layout();
            }
        });

    }

    private void removeParentFromParentList() {
        if (!isIntegrationActive()) return;
        IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();
        QTestSuite selectedQTestSuite = (QTestSuite) selection.getFirstElement();
        if (selectedQTestSuite.getId() > 0) {
            if (!disintegrateTestSuiteWithQTest()) return;
        }

        getQTestSuites().remove(selectedQTestSuite);
        testSuiteParentTableViewer.refresh();

        setDirty(true);

    }

    private void setDefaultQTestSuite(QTestSuite selectedQTestSuite) {
        if (!isIntegrationActive()) return;
        selectedQTestSuite.setSelected(true);
        for (QTestSuite testSuite : getQTestSuites()) {
            if (!testSuite.equals(selectedQTestSuite)) {
                testSuite.setSelected(false);
            }
        }
        testSuiteParentTableViewer.refresh();
        reloadView();
        setDirty(true);
    }

    private void showTestSuiteNotValidNotification() {
        MessageDialog.openInformation(null, StringConstants.INFORMATION,
                StringConstants.VIEW_MSG_TEST_SUITE_NOT_IN_REPO);
    }

    private void createTestSuiteParent() {
        try {
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (!isIntegrationActive()) return;
            List<String> currentInUseParentStrings = new ArrayList<String>();
            for (QTestSuite qTestSuite : getQTestSuites()) {
                currentInUseParentStrings.add(Long.toString(qTestSuite.getParent().getId()));
            }

            TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, currentProject);
            if (repo == null) {
                showTestSuiteNotValidNotification();
                return;
            }

            Shell dialogShell = testSuiteParentTableViewer.getTable().getDisplay().getActiveShell();

            CreateNewTestSuiteParentDialog dialog = new CreateNewTestSuiteParentDialog(dialogShell,
                    currentInUseParentStrings, repo.getQTestProject());
            if (dialog.open() == Dialog.OK) {
                QTestSuiteParent qTestSuiteParent = dialog.getNewTestSuiteParent();
                if (qTestSuiteParent == null) return;

                QTestSuite qTestSuite = new QTestSuite();
                qTestSuite.setParent(qTestSuiteParent);
                qTestSuite.setName(testSuiteEntity.getName());

                getQTestSuites().add(qTestSuite);
                testSuiteParentTableViewer.refresh();

                TestSuiteParentCreationOption creationOption = QTestPreferenceDefaultValueInitializer
                        .getCreationOption();
                switch (creationOption) {
                    case CREATE_ONLY:
                        break;
                    case CREATE_AND_UPLOAD:
                        uploadTestSuite(qTestSuite);
                        break;
                    case CREATE_UPLOAD_AND_SET_AS_DEFAULT:
                        uploadTestSuite(qTestSuite);
                        setDefaultQTestSuite(qTestSuite);
                        break;
                    default:
                        break;
                }

                testSuiteParentTableViewer.setSelection(new StructuredSelection(qTestSuite));
                setDirty(true);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private boolean disintegrateTestSuiteWithQTest() {
        try {
            if (!isIntegrationActive()) return false;

            if (MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                    StringConstants.VIEW_CONFIRM_DISINTEGRATE_TEST_SUITE)) {
                IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();
                QTestSuite selectedQTestSuite = (QTestSuite) selection.getFirstElement();

                selectedQTestSuite.setId(0);
                selectedQTestSuite.setPid("");
                selectedQTestSuite.setSelected(false);
                selectedQTestSuite.getTestRuns().clear();

                testSuiteParentTableViewer.update(selectedQTestSuite, null);

                reloadView();
                setDirty(true);
                return true;
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_DISINTEGRATE_TEST_SUITE, e.getClass()
                    .getSimpleName());
        }
        return false;
    }

    private String getProjectDir() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        return projectEntity.getFolderLocation();
    }

    private void navigateToQTest() {
        try {
            if (!isIntegrationActive()) return;
            IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();
            QTestSuite selectedQTestSuite = (QTestSuite) selection.getFirstElement();
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, currentProject);
            if (repo == null) {
                showTestSuiteNotValidNotification();
                return;
            }

            URL url = QTestIntegrationTestSuiteManager.navigatedUrlToQTestTestSuite(getProjectDir(),
                    selectedQTestSuite, repo.getQTestProject());
            IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
            browser.openURL(url);

            // ResourceBundle.
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_NAVIGATE_TEST_SUITE, e.getClass()
                    .getSimpleName());
        }
    }

    private boolean isIntegrationActive() {
        boolean active = QTestSettingStore.isIntegrationActive(getProjectDir());

        if (!active) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION,
                    StringConstants.VIEW_MSG_ENABLE_INTEGRATION);
        }
        return active;
    }

    private void uploadTestSuite(QTestSuite selectedQTestSuite) {
        try {
            if (!isIntegrationActive()) return;
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            TestSuiteRepo repo = QTestIntegrationUtil.getTestSuiteRepo(testSuiteEntity, currentProject);
            if (repo == null) {
                showTestSuiteNotValidNotification();
                return;
            }

            QTestSuite newQTestSuite = QTestIntegrationTestSuiteManager.addTestSuite(getProjectDir(),
                    testSuiteEntity.getName(), selectedQTestSuite.getParent(), repo.getQTestProject());

            selectedQTestSuite.setId(newQTestSuite.getId());
            selectedQTestSuite.setPid(newQTestSuite.getPid());

            testSuiteParentTableViewer.update(selectedQTestSuite, null);

            reloadView();
            setDirty(true);
        } catch (QTestUnauthorizedException ex) {
            MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_SUITE, "Invalid authentication");
        } catch (Exception ex) {
            MessageDialog.openWarning(null, StringConstants.WARN, StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_SUITE);
        }
    }

    private void initialize() {
        try {
            IntegratedEntity testSuiteIntegratedEntity = testSuiteEntity
                    .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);
            setQTestSuites(QTestIntegrationTestSuiteManager
                    .getQTestSuiteListByIntegratedEntity(testSuiteIntegratedEntity));
            testSuiteParentTableViewer.setInput(getQTestSuites());
        } catch (QTestInvalidFormatException e) {
            // User changes test suite format.
        }

        reloadView();
    }

    @Override
    public void setDirty(boolean dirty) {
        IntegratedEntity testSuiteIntegratedEntity = testSuiteEntity
                .getIntegratedEntity(QTestStringConstants.PRODUCT_NAME);

        if (testSuiteIntegratedEntity == null) {
            testSuiteIntegratedEntity = QTestIntegrationTestSuiteManager
                    .getIntegratedEntityByTestSuiteList(getQTestSuites());
            testSuiteEntity.getIntegratedEntities().add(testSuiteIntegratedEntity);
        } else {
            testSuiteIntegratedEntity.getProperties().clear();
            for (QTestSuite qTestSuite : getQTestSuites()) {
                QTestIntegrationTestSuiteManager.addQTestSuiteToIntegratedEntity(qTestSuite, testSuiteIntegratedEntity,
                        getQTestSuites().indexOf(qTestSuite));
            }
        }

        super.setDirty(dirty);
    }

    private void reloadView() {
        if (!QTestSettingStore.isIntegrationActive(testSuiteEntity.getProject().getFolderLocation())) {
            btnUpload.setEnabled(false);
            btnDisintegrate.setEnabled(false);
            btnNavigate.setEnabled(false);
            btnSetDefault.setEnabled(false);
            btnUpdateParent.setEnabled(false);
            return;
        }

        IStructuredSelection selection = (IStructuredSelection) testSuiteParentTableViewer.getSelection();

        QTestSuite qTestSuite = (QTestSuite) selection.getFirstElement();

        if (qTestSuite == null) {
            btnUpload.setEnabled(false);
            btnDisintegrate.setEnabled(false);
            btnNavigate.setEnabled(false);
            btnSetDefault.setEnabled(false);
        } else {
            if (qTestSuite.getId() > 0) {
                btnUpload.setEnabled(false);
                btnDisintegrate.setEnabled(true);
                btnNavigate.setEnabled(true);

                if (qTestSuite.isSelected()) {
                    btnSetDefault.setEnabled(false);
                } else {
                    btnSetDefault.setEnabled(true);
                }

            } else {
                btnUpload.setEnabled(true);
                btnDisintegrate.setEnabled(false);
                btnNavigate.setEnabled(false);
                btnSetDefault.setEnabled(false);
            }

        }

        if (qTestSuite != null && qTestSuite.getId() > 0) {
            txtID.setText(String.valueOf(qTestSuite.getId()));
            txtParentID.setText(String.valueOf(qTestSuite.getParent().getId()));
            txtPID.setText(qTestSuite.getPid());
        } else {
            txtID.setText("");
            txtParentID.setText("");
            txtPID.setText("");
        }
    }

    private List<QTestSuite> getQTestSuites() {
        if (qTestSuites == null) qTestSuites = new ArrayList<QTestSuite>();
        return qTestSuites;
    }

    private void setQTestSuites(List<QTestSuite> qTestSuites) {
        this.qTestSuites = qTestSuites;
    }
}
