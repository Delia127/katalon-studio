package com.kms.katalon.composer.integration.qtest.view.testcase;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.handler.QTestUploadTestCaseHandler;
import com.kms.katalon.composer.integration.qtest.job.UploadTestCaseJob;
import com.kms.katalon.composer.integration.qtest.model.TestCaseRepo;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.integration.qtest.QTestIntegrationFolderManager;
import com.kms.katalon.integration.qtest.QTestIntegrationTestCaseManager;
import com.kms.katalon.integration.qtest.entity.QTestModule;
import com.kms.katalon.integration.qtest.entity.QTestProject;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class QTestIntegrationTestCaseView extends AbstractTestCaseIntegrationView {

    public QTestIntegrationTestCaseView(TestCaseEntity testCaseEntity, MPart mpart) {
        super(testCaseEntity, mpart);
    }

    private StyledText txtID, txtParentID, txtAlias;

    private QTestTestCase qTestTestCase;

    private Button btnUpload;

    private Button btnDisintegrate;

    private Button btnNavigate;

    private Composite container;

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        container.setLayout(glContainer);

        Composite compositeButton = new Composite(container, SWT.NONE);
        GridLayout gl_compositeButton = new GridLayout(4, false);
        gl_compositeButton.marginWidth = 0;
        compositeButton.setLayout(gl_compositeButton);
        compositeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        btnUpload = new Button(compositeButton, SWT.FLAT);
        btnUpload.setToolTipText(StringConstants.VIEW_TOOLTIP_UPLOAD_TEST_CASE);
        btnUpload.setText(StringConstants.CM_UPLOAD);

        btnDisintegrate = new Button(compositeButton, SWT.FLAT);
        btnDisintegrate.setToolTipText(StringConstants.VIEW_TOOLTIP_DISINTEGRATE_TEST_CASE);
        btnDisintegrate.setText(StringConstants.CM_DISINTEGRATE);

        btnNavigate = new Button(compositeButton, SWT.FLAT);
        btnNavigate.setToolTipText(StringConstants.VIEW_TOOLTIP_NAVIGATE_TEST_CASE);
        btnNavigate.setText(StringConstants.CM_NAVIGATE);

        Composite compositeInfo = new Composite(container, SWT.BORDER);
        compositeInfo.setBackground(ColorUtil.getWhiteBackgroundColor());
        compositeInfo.setBackgroundMode(SWT.INHERIT_FORCE);

        compositeInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        GridLayout gl_compositeInfo = new GridLayout(2, false);
        gl_compositeInfo.verticalSpacing = 7;
        gl_compositeInfo.horizontalSpacing = 15;
        compositeInfo.setLayout(gl_compositeInfo);

        Label lblTestCaseId = new Label(compositeInfo, SWT.NONE);
        lblTestCaseId.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblTestCaseId.setText(StringConstants.VIEW_TITLE_TEST_CASE_ID);

        txtID = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPID = new Label(compositeInfo, SWT.NONE);
        lblPID.setText(StringConstants.CM_ALIAS);

        txtAlias = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtAlias.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblParentId = new Label(compositeInfo, SWT.NONE);
        lblParentId.setText(StringConstants.CM_PARENT_ID);

        txtParentID = new StyledText(compositeInfo, SWT.READ_ONLY);
        txtParentID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        initialize();
        controlModifyListeners();

        return container;
    }

    private void controlModifyListeners() {
        btnUpload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                performUploadTestCase();
            }
        });

        btnNavigate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                navigateToQTestTestCase();
            }

        });

        btnDisintegrate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                disIntegrateTestCaseWithQTest();
            }
        });
    }

    protected void disIntegrateTestCaseWithQTest() {
        try {
            if (MessageDialog.openConfirm(null, StringConstants.CONFIRMATION,
                    StringConstants.VIEW_CONFIRM_DISINTEGRATE_TEST_CASE)) {
                testCaseEntity.getIntegratedEntities().remove(QTestIntegrationUtil.getIntegratedEntity(testCaseEntity));
                reloadView();
                setDirty(true);
            }
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_DISINTEGRATE_TEST_CASE,
                    e.getClass().getSimpleName());
        }

    }

    private void navigateToQTestTestCase() {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
            QTestProject qTestProject = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity)
                    .getQTestProject();
            URL url = QTestIntegrationTestCaseManager.navigatedUrlToQTestTestCase(qTestProject, qTestTestCase,
                    projectEntity.getFolderLocation());
            Program.launch(url.toString());
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.VIEW_MSG_UNABLE_NAVIGATE_TEST_CASE,
                    e.getClass().getSimpleName());
        }
    }

    private void performUploadTestCase() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

        if (isDirty()) {
            MessageDialog.openInformation(null, StringConstants.INFORMATION,
                    StringConstants.VIEW_MSG_SAVE_BEFORE_UPLOADING);
            return;
        }

        try {
            TestCaseRepo testCaseRepo = QTestIntegrationUtil.getTestCaseRepo(testCaseEntity, projectEntity);
            if (testCaseRepo == null) {
                MessageDialog.openWarning(null, StringConstants.WARN, StringConstants.VIEW_MSG_TEST_CASE_NOT_IN_REPO);
                return;
            }

            QTestModule module = QTestIntegrationFolderManager
                    .getQTestModuleByFolderEntity(testCaseEntity.getParentFolder());

            // If the parent module is qTest's root module, open a warning message that system cannot upload this test
            // case.
            if (module != null && module.getParentId() <= 0) {
                MessageDialog.openWarning(null, StringConstants.WARN,
                        StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_CASE_UNDER_ROOT_MODULE);
                return;
            }

            UploadTestCaseJob uploadJob = new UploadTestCaseJob(StringConstants.JOB_TITLE_UPLOAD_TEST_CASE,
                    UISynchronizeService.getInstance().getSync());
            List<IntegratedFileEntity> uploadedEntities = new ArrayList<IntegratedFileEntity>();
            TestCaseEntity originalEntity = TestCaseController.getInstance().getTestCase(testCaseEntity.getId());
            uploadedEntities.add(originalEntity);
            QTestUploadTestCaseHandler.addParentToUploadedEntities(originalEntity, uploadedEntities);

            uploadJob.setFileEntities(uploadedEntities);

            uploadJob.doTask();
        } catch (Exception ex) {
            MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.VIEW_MSG_UNABLE_UPLOAD_TEST_CASE,
                    ex.getClass().getSimpleName());
        }
    }

    private void initialize() {
        if (testCaseEntity == null) {
            btnUpload.setEnabled(false);
            btnDisintegrate.setEnabled(false);
            btnNavigate.setEnabled(false);
            return;
        }

        reloadView();

        if (qTestTestCase == null) {
            ControlUtils.recursiveSetEnabled(container, false);
            return;
        }
    }

    private void reloadView() {
        IntegratedEntity integratedEntity = QTestIntegrationUtil.getIntegratedEntity(testCaseEntity);

        qTestTestCase = QTestIntegrationTestCaseManager.getQTestTestCaseByIntegratedEntity(integratedEntity);

        if (qTestTestCase == null) {
            btnUpload.setEnabled(true);
            btnDisintegrate.setEnabled(false);
            btnNavigate.setEnabled(false);
        } else {
            btnUpload.setEnabled(false);
            btnDisintegrate.setEnabled(true);
            btnNavigate.setEnabled(true);
        }

        if (qTestTestCase != null) {
            txtID.setText(String.valueOf(qTestTestCase.getId()));
            txtParentID.setText(String.valueOf(qTestTestCase.getParentId()));
            txtAlias.setText(qTestTestCase.getPid());
        } else {
            txtID.setText("");
            txtParentID.setText("");
            txtAlias.setText("");
        }
    }

    public TestCaseEntity getTestCase() {
        return testCaseEntity;
    }

    public void setTestCase(TestCaseEntity testCase) {
        this.testCaseEntity = testCase;
    }
    
    @Override
    public boolean hasDocumentation() {
        return true;
    }
    
    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.TEST_CASE_INTEGRATION_QTEST;
    }
}
