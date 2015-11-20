package com.kms.katalon.composer.testsuite.parts;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.AddMailRecipientDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.TestSuiteEventConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuitePart implements EventHandler {

    private static final int MINIMUM_COMPOSITE_SIZE = 300;

    private static final int MAX_HEIGHT_OF_TEXT_BOX = 20;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    private IEventBroker eventBroker;

    private Composite compositeExecution, compositeMain, compositeInformation, compositeInformationHeader,
            compositeInformationDetails;

    private ScrolledComposite compositeTablePart;

    private boolean isGeneralInfoCompositeExpanded, isExecutionCompositeExpanded;

    private Text textTestSuiteName, textDescription, txtTestSuiteId, txtCreatedDate, txtLastUpdate, txtLastRun,
            txtRerun, txtUserDefinePageLoadTimeout;

    private Link lblLastRun;

    private MPart mpart;

    private Composite compositeExecutionDetails;
    private org.eclipse.swt.widgets.List listMailRcp;
    private ListViewer listMailRcpViewer;
    private Button btnAddMailRcp, btnDeleteMailRcp, btnClearMailRcp;
    private Button radioUseDefaultPageLoadTimeout, radioUserDefinePageLoadTimeout;
    private Composite compositeLastRunAndReRun;
    private ImageButton btnExpandInformation, btnExpandExecutionComposite;

    private TestSuiteCompositePart parentTestSuiteCompositePart;

    private Label lblGeneralInformation, lblExecutionInformation;

    private TestSuitePartTestCaseView childrenView;

    private List<Thread> uiThreads;

    private Composite parent;

    private boolean isLoading;

    private Listener layoutGeneralCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isGeneralInfoCompositeExpanded = !isGeneralInfoCompositeExpanded;
            layoutGeneralInfo();
        }
    };

    private Listener layoutExecutionCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isExecutionCompositeExpanded = !isExecutionCompositeExpanded;
            layoutExecutionInfo();
        }
    };

    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
        this.parent = parent;
        this.mpart = mpart;

        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof TestSuiteCompositePart) {
                parentTestSuiteCompositePart = ((TestSuiteCompositePart) compositePart.getObject());
            }
        }

        childrenView = new TestSuitePartTestCaseView(this);
        uiThreads = new LinkedList<Thread>();
        isLoading = false;

        initExpandedState();

        registerEventBrokerListerners();

        createComponents(parent);

        registerControlListeners();

        layoutGeneralInfo();
        layoutExecutionInfo();
        childrenView.layout();
    }

    public MPart getMPart() {
        return mpart;
    }

    private void initExpandedState() {
        isGeneralInfoCompositeExpanded = false;
        isExecutionCompositeExpanded = false;
        childrenView.initExpandedState();
    }

    @Focus
    public void setFocus() {
        compositeMain.setFocus();
    }

    @PreDestroy
    public void preDestroy() {
        setDirty(false);
        eventBroker.unsubscribe(this);
    }

    private void registerEventBrokerListerners() {
        eventBroker.subscribe(TestSuiteEventConstants.TESTSUITE_UPDATE_DIRTY, this);
        eventBroker.subscribe(TestSuiteEventConstants.TESTSUITE_UPDATE_IS_RUN_COLUMN_HEADER, this);
        eventBroker.subscribe(EventConstants.TESTCASE_UPDATED, this);
        eventBroker.subscribe(EventConstants.TEST_SUITE_UPDATED, this);
        eventBroker.subscribe(EventConstants.TEST_DATA_UPDATED, this);
        // eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM,
        // this);
    }

    private void layoutGeneralInfo() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                compositeInformationDetails.setVisible(isGeneralInfoCompositeExpanded);
                if (!isGeneralInfoCompositeExpanded) {
                    ((GridData) compositeInformationDetails.getLayoutData()).exclude = true;
                    compositeInformation.setSize(compositeInformation.getSize().x, compositeInformation.getSize().y
                            - compositeTablePart.getSize().y - compositeExecution.getSize().y);
                } else {
                    ((GridData) compositeInformationDetails.getLayoutData()).exclude = false;
                }
                compositeInformation.layout(true, true);
                compositeInformation.getParent().layout();
                redrawBtnExpandGeneralInfo();
            }
        });
    }

    private void layoutExecutionInfo() {
        Display.getDefault().timerExec(10, new Runnable() {
            @Override
            public void run() {
                compositeExecutionDetails.setVisible(isExecutionCompositeExpanded);
                if (!isExecutionCompositeExpanded) {
                    ((GridData) compositeExecutionDetails.getLayoutData()).exclude = true;
                    compositeExecution.setSize(compositeExecution.getSize().x, compositeExecution.getSize().y
                            - compositeTablePart.getSize().y);
                } else {
                    ((GridData) compositeExecutionDetails.getLayoutData()).exclude = false;
                }
                compositeExecution.layout(true, true);
                compositeExecution.getParent().layout();
                redrawBtnExpandExecutionInfo();
            }
        });
    }

    private void registerControlListeners() {
        textTestSuiteName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getTestSuite().setName(textTestSuiteName.getText());
                setDirty(true);
            }
        });

        textDescription.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getTestSuite().setDescription(textDescription.getText());
                setDirty(true);
            }
        });

        btnExpandInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);

        lblGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);

        btnAddMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = Display.getDefault().getActiveShell();
                AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell, listMailRcpViewer.getList()
                        .getItems());
                addMailDialog.open();

                if (addMailDialog.getReturnCode() == Dialog.OK) {
                    String[] emails = addMailDialog.getEmails();
                    if (emails.length > 0) {
                        listMailRcpViewer.add(addMailDialog.getEmails());
                        setDirty(true);
                    }
                }
            }
        });

        btnClearMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (listMailRcpViewer.getList().getItemCount() > 0) {
                    listMailRcpViewer.setInput(new String[0]);
                    setDirty(true);
                }
            }
        });

        btnDeleteMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] emails = ((IStructuredSelection) listMailRcpViewer.getSelection()).toArray();
                if (emails.length > 0) {
                    listMailRcpViewer.remove(emails);
                    setDirty(true);
                }
            }
        });

        lblExecutionInformation.addListener(SWT.MouseDown, layoutExecutionCompositeListener);

        btnExpandExecutionComposite.addListener(SWT.MouseDown, layoutExecutionCompositeListener);

        radioUseDefaultPageLoadTimeout.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                txtUserDefinePageLoadTimeout.setEnabled(false);
                getTestSuite().setPageLoadTimeoutDefault(true);
                setDirty(true);
            }
        });

        radioUserDefinePageLoadTimeout.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                txtUserDefinePageLoadTimeout.setEnabled(true);
                getTestSuite().setPageLoadTimeoutDefault(false);
                setDirty(true);
            }
        });

        // Number input only
        VerifyListener verifyNumberListener = new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String string = e.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    if (!('0' <= chars[i] && chars[i] <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
                setDirty(true);
            }
        };

        txtUserDefinePageLoadTimeout.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    int timeout = Integer.parseInt(text);
                    getTestSuite().setPageLoadTimeout((short) timeout);
                } catch (NumberFormatException ex) {
                }
            }
        });
        txtUserDefinePageLoadTimeout.addVerifyListener(verifyNumberListener);

        lblLastRun.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openReportOfLastRun();
            }
        });

        txtRerun.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    int rerun = Integer.parseInt(text);
                    // limit to 100 times only
                    if (rerun > 100) {
                        rerun = 100;
                        ((Text) e.getSource()).setText(String.valueOf(rerun));
                    }
                    getTestSuite().setNumberOfRerun(rerun);
                } catch (NumberFormatException ex) {
                }
            }
        });
        txtRerun.addVerifyListener(verifyNumberListener);

        childrenView.registerControlModifyListeners();
    }

    private void openReportOfLastRun() {
        try {
            ReportEntity reportEntity = ReportController.getInstance().getLastRunReportEntity(
                    parentTestSuiteCompositePart.getTestSuiteClone());
            if (reportEntity != null) {
                eventBroker.post(EventConstants.REPORT_OPEN, reportEntity);
            } else {
                MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN_TITLE,
                        StringConstants.PA_WARN_MSG_REPORT_FILE_DOES_NOT_EXIST);
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_OPEN_REPORT);
        }
    }

    public void loadTestSuite(final TestSuiteEntity testSuite) {
        final Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    if (parent.isDisposed()) {
                        return;
                    }
                    loadTestSuiteInfo(testSuite);
                    childrenView.loadInput();
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                    MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                            StringConstants.PA_ERROR_MSG_UNABLE_TO_LOAD_TEST_SUITE);
                }
            }
        };

        Thread loadTestSuiteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                isLoading = true;
                parent.getDisplay().syncExec(task);
                isLoading = false;
            }
        });

        uiThreads.add(loadTestSuiteThread);

        loadTestSuiteThread.start();
    }

    private void loadTestSuiteInfo(final TestSuiteEntity testSuite) throws Exception {
        String testSuiteIdForDisplay = TestSuiteController.getInstance().getIdForDisplay(testSuite);

        // binding name
        textTestSuiteName.setText(testSuite.getName());

        // binding description
        if (testSuite.getDescription() != null) {
            textDescription.setText(testSuite.getDescription());
        }

        txtTestSuiteId.setText(testSuiteIdForDisplay);

        if (testSuite.getDateCreated() != null) {
            txtCreatedDate.setText(testSuite.getDateCreated().toString());
        }

        if (testSuite.getDateModified() != null) {
            txtLastUpdate.setText(testSuite.getDateModified().toString());
        }

        if (testSuite.getLastRun() != null) {
            lblLastRun.setText("<A>" + StringConstants.PA_LBL_LAST_RUN + "</A>");
            lblLastRun.setToolTipText(StringConstants.PA_LBL_TIP_LAST_RUN);
            txtLastRun.setText(testSuite.getLastRun().toString());
        } else {
            lblLastRun.setText(StringConstants.PA_LBL_LAST_RUN);
            lblLastRun.setToolTipText("");
        }

        txtRerun.setText(String.valueOf(testSuite.getNumberOfRerun()));

        // binding mailRecipient
        listMailRcpViewer
                .setInput(TestSuiteController.getInstance().mailRcpStringToArray(testSuite.getMailRecipient()));

        // binding page load timeout values
        short pageLoadTimeOut = testSuite.getPageLoadTimeout();
        if (testSuite.isPageLoadTimeoutDefault()) {
            radioUseDefaultPageLoadTimeout.setSelection(true);
            radioUserDefinePageLoadTimeout.setSelection(false);
            txtUserDefinePageLoadTimeout.setEnabled(false);
        } else {
            radioUseDefaultPageLoadTimeout.setSelection(false);
            radioUserDefinePageLoadTimeout.setSelection(true);
            txtUserDefinePageLoadTimeout.setEnabled(true);
            txtUserDefinePageLoadTimeout.setText(Integer.toString(pageLoadTimeOut));
        }

    }

    private void createComponents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        compositeMain = new Composite(parent, SWT.NONE);
        GridLayout glCompositeMain = new GridLayout(1, false);
        glCompositeMain.marginWidth = 0;
        glCompositeMain.marginHeight = 0;
        compositeMain.setLayout(glCompositeMain);
        compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createGeneralInformationComposite();
        createExecutionInformationComposite();

        compositeTablePart = childrenView.createCompositeTestCase(compositeMain);
    }

    private void createGeneralInformationComposite() {
        compositeInformation = new Composite(compositeMain, SWT.NONE);
        compositeInformation.setBackground(ColorUtil.getCompositeBackgroundColor());
        compositeInformation.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeInformation = new GridLayout(1, false);
        glCompositeInformation.marginWidth = 0;
        glCompositeInformation.marginHeight = 0;
        glCompositeInformation.verticalSpacing = 0;
        compositeInformation.setLayout(glCompositeInformation);

        compositeInformationHeader = new Composite(compositeInformation, SWT.NONE);
        GridLayout glCompositeInformationHeader = new GridLayout(2, false);
        glCompositeInformationHeader.marginWidth = 0;
        glCompositeInformationHeader.marginHeight = 0;
        compositeInformationHeader.setLayout(glCompositeInformationHeader);
        compositeInformationHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeInformationHeader.setCursor(compositeInformationHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandInformation = new ImageButton(compositeInformationHeader, SWT.NONE);
        redrawBtnExpandGeneralInfo();

        lblGeneralInformation = new Label(compositeInformationHeader, SWT.NONE);
        lblGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblGeneralInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblGeneralInformation.setText("General Information");

        compositeInformationDetails = new Composite(compositeInformation, SWT.NONE);
        GridLayout glCompositeInformationDetails = new GridLayout(3, true);
        glCompositeInformationDetails.marginLeft = 45;
        glCompositeInformationDetails.horizontalSpacing = 40;
        compositeInformationDetails.setLayout(glCompositeInformationDetails);
        compositeInformationDetails.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        Composite compositeTestSuiteIdAndName = new Composite(compositeInformationDetails, SWT.NONE);
        GridData gdCompositeIdAndName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCompositeIdAndName.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeTestSuiteIdAndName.setLayoutData(gdCompositeIdAndName);
        GridLayout glCompositeIdAndName = new GridLayout(2, false);
        glCompositeIdAndName.verticalSpacing = 10;
        compositeTestSuiteIdAndName.setLayout(glCompositeIdAndName);

        Label lblTestSuiteID = new Label(compositeTestSuiteIdAndName, SWT.NONE);
        GridData gdLblTestSuiteID = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblTestSuiteID.widthHint = 50;
        lblTestSuiteID.setLayoutData(gdLblTestSuiteID);
        lblTestSuiteID.setText(StringConstants.PA_LBL_ID);

        txtTestSuiteId = new Text(compositeTestSuiteIdAndName, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtTestSuiteId = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTxtTestSuiteId.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
        txtTestSuiteId.setLayoutData(gdTxtTestSuiteId);

        Label lblTestSuiteName = new Label(compositeTestSuiteIdAndName, SWT.NONE);
        GridData gdLblTestSuiteName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblTestSuiteName.widthHint = 50;
        lblTestSuiteName.setLayoutData(gdLblTestSuiteName);
        lblTestSuiteName.setText(StringConstants.PA_LBL_NAME);

        textTestSuiteName = new Text(compositeTestSuiteIdAndName, SWT.BORDER);
        GridData gdTextTestSuiteName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTextTestSuiteName.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
        textTestSuiteName.setLayoutData(gdTextTestSuiteName);

        Composite compositeUpdateAndRun = new Composite(compositeInformationDetails, SWT.NONE);
        GridData gdCompositeUpdateAndRun = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCompositeUpdateAndRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeUpdateAndRun.setLayoutData(gdCompositeUpdateAndRun);
        GridLayout glCompositeUpdateAndRun = new GridLayout(2, false);
        glCompositeUpdateAndRun.verticalSpacing = 10;
        compositeUpdateAndRun.setLayout(glCompositeUpdateAndRun);

        Label lblCreatedDate = new Label(compositeUpdateAndRun, SWT.NONE);
        GridData gdLblCreatedDate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblCreatedDate.widthHint = 85;
        lblCreatedDate.setLayoutData(gdLblCreatedDate);
        lblCreatedDate.setText(StringConstants.PA_LBL_CREATED_DATE);

        txtCreatedDate = new Text(compositeUpdateAndRun, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtCreatedDate = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtCreatedDate.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
        txtCreatedDate.setLayoutData(gdTxtCreatedDate);

        Label lblLastUpdate = new Label(compositeUpdateAndRun, SWT.NONE);
        lblLastUpdate.setText(StringConstants.PA_LBL_LAST_UPDATED);

        txtLastUpdate = new Text(compositeUpdateAndRun, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtLastUpdate = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtLastUpdate.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
        txtLastUpdate.setLayoutData(gdTxtLastUpdate);

        Composite compositeDescription = new Composite(compositeInformationDetails, SWT.NONE);
        GridData gdCompositeDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositeDescription.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeDescription.setLayoutData(gdCompositeDescription);
        GridLayout glCompositeDescription = new GridLayout(2, false);
        glCompositeDescription.verticalSpacing = 10;
        compositeDescription.setLayout(glCompositeDescription);

        Label lblDescription = new Label(compositeDescription, SWT.NONE);
        GridData gdLblDescription = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
        gdLblDescription.widthHint = 85;
        gdLblDescription.heightHint = 20;
        lblDescription.setLayoutData(gdLblDescription);
        lblDescription.setText(StringConstants.PA_LBL_DESC);

        textDescription = new Text(compositeDescription, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        GridData gdTextDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdTextDescription.heightHint = 60;
        textDescription.setLayoutData(gdTextDescription);

        Label lblDescriptionSecondRow = new Label(compositeDescription, SWT.NONE);
        GridData gdLblDescriptionSecondRow = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
        gdLblDescriptionSecondRow.heightHint = 20;
        lblDescriptionSecondRow.setLayoutData(gdLblDescriptionSecondRow);
    }

    private void redrawBtnExpandGeneralInfo() {
        btnExpandInformation.getParent().setRedraw(false);
        if (isGeneralInfoCompositeExpanded) {
            btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandInformation.getParent().setRedraw(true);
    }

    private void redrawBtnExpandExecutionInfo() {
        btnExpandExecutionComposite.getParent().setRedraw(false);
        if (isExecutionCompositeExpanded) {
            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandExecutionComposite.getParent().setRedraw(true);
    }

    private void createExecutionInformationComposite() {
        compositeExecution = new Composite(compositeMain, SWT.NONE);
        compositeExecution.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout glCompositeExecution = new GridLayout(1, true);
        glCompositeExecution.verticalSpacing = 0;
        glCompositeExecution.horizontalSpacing = 0;
        glCompositeExecution.marginHeight = 0;
        glCompositeExecution.marginWidth = 0;
        compositeExecution.setLayout(glCompositeExecution);
        compositeExecution.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeExecutionCompositeHeader = new Composite(compositeExecution, SWT.NONE);
        compositeExecutionCompositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeExecutionCompositeHeader = new GridLayout(2, false);
        glCompositeExecutionCompositeHeader.marginHeight = 0;
        glCompositeExecutionCompositeHeader.marginWidth = 0;
        compositeExecutionCompositeHeader.setLayout(glCompositeExecutionCompositeHeader);
        compositeExecutionCompositeHeader.setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(
                SWT.CURSOR_HAND));
        compositeExecutionCompositeHeader.setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(
                SWT.CURSOR_HAND));

        btnExpandExecutionComposite = new ImageButton(compositeExecutionCompositeHeader, SWT.NONE);
        redrawBtnExpandExecutionInfo();

        lblExecutionInformation = new Label(compositeExecutionCompositeHeader, SWT.NONE);
        lblExecutionInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblExecutionInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblExecutionInformation.setText("Execution Information");

        compositeExecutionDetails = new Composite(compositeExecution, SWT.NONE);
        compositeExecutionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeExecutionDetail = new GridLayout(3, true);
        glCompositeExecutionDetail.marginLeft = 45;
        glCompositeExecutionDetail.horizontalSpacing = 40;
        compositeExecutionDetails.setLayout(glCompositeExecutionDetail);

        Composite compositePageLoadTimeout = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositePageLoadTimeout = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositePageLoadTimeout.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositePageLoadTimeout.setLayoutData(gdCompositePageLoadTimeout);
        GridLayout glCompositePageLoadTimeout = new GridLayout(1, false);
        glCompositePageLoadTimeout.marginWidth = 0;
        glCompositePageLoadTimeout.marginHeight = 0;
        glCompositePageLoadTimeout.horizontalSpacing = 10;
        compositePageLoadTimeout.setLayout(glCompositePageLoadTimeout);

        Group grpPageLoadTimeout = new Group(compositePageLoadTimeout, SWT.NONE);
        grpPageLoadTimeout.setText(StringConstants.PA_LBL_PAGE_LOAD_TIMEOUT);
        grpPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_grpPageLoadTimeout = new GridLayout(2, false);
        gl_grpPageLoadTimeout.marginLeft = 50;
        gl_grpPageLoadTimeout.marginWidth = 0;
        grpPageLoadTimeout.setLayout(gl_grpPageLoadTimeout);

        radioUseDefaultPageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
        radioUseDefaultPageLoadTimeout.setText(StringConstants.PA_LBL_USE_DEFAULT);
        new Label(grpPageLoadTimeout, SWT.NONE);

        radioUserDefinePageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
        GridData gd_radioUserDefinePageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_radioUserDefinePageLoadTimeout.widthHint = 83;
        radioUserDefinePageLoadTimeout.setLayoutData(gd_radioUserDefinePageLoadTimeout);
        radioUserDefinePageLoadTimeout.setText(StringConstants.PA_LBL_USER_DEFINE);

        txtUserDefinePageLoadTimeout = new Text(grpPageLoadTimeout, SWT.BORDER);
        GridData gdTxtUserDefinePageLoadTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtUserDefinePageLoadTimeout.heightHint = 20;
        txtUserDefinePageLoadTimeout.setLayoutData(gdTxtUserDefinePageLoadTimeout);
        // limit the input length for the range of
        // TestEnvironmentController.getInstance().getPageLoadTimeOutMinimumValue() and
        // TestEnvironmentController.getInstance().getPageLoadTimeOutMaximumValue()
        txtUserDefinePageLoadTimeout.setTextLimit(4);

        compositeLastRunAndReRun = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositeTestDataAndLastRun = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositeTestDataAndLastRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeLastRunAndReRun.setLayoutData(gdCompositeTestDataAndLastRun);
        GridLayout glCompositeTestDataAndLastRun = new GridLayout(4, false);
        glCompositeTestDataAndLastRun.verticalSpacing = 10;
        compositeLastRunAndReRun.setLayout(glCompositeTestDataAndLastRun);

        lblLastRun = new Link(compositeLastRunAndReRun, SWT.NONE);
        GridData gdLblLastRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblLastRun.widthHint = 85;
        lblLastRun.setLayoutData(gdLblLastRun);
        lblLastRun.setText(StringConstants.PA_LBL_LAST_RUN);

        txtLastRun = new Text(compositeLastRunAndReRun, SWT.BORDER | SWT.READ_ONLY);
        GridData gdTxtLastRun = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gdTxtLastRun.heightHint = 20;
        txtLastRun.setLayoutData(gdTxtLastRun);

        Label lblReRun = new Label(compositeLastRunAndReRun, SWT.NONE);
        GridData gdLblReRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblReRun.widthHint = 85;
        lblReRun.setLayoutData(gdLblReRun);
        lblReRun.setText(StringConstants.PA_LBL_RETRY);
        lblReRun.setToolTipText(StringConstants.PA_LBL_TOOLTIP_RETRY);

        txtRerun = new Text(compositeLastRunAndReRun, SWT.BORDER);
        GridData gdTxtRerun = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        gdTxtRerun.heightHint = 20;
        txtRerun.setLayoutData(gdTxtRerun);
        txtRerun.setToolTipText(StringConstants.PA_LBL_TOOLTIP_RETRY);
        txtRerun.setTextLimit(3);

        Composite compositeMailRecipients = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositeMailRecipients = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositeMailRecipients.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeMailRecipients.setLayoutData(gdCompositeMailRecipients);
        GridLayout glCompositeMailRecipients = new GridLayout(3, false);
        compositeMailRecipients.setLayout(glCompositeMailRecipients);

        Label lblMailRecipients = new Label(compositeMailRecipients, SWT.NONE);
        GridData gdLblMailRecipients = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblMailRecipients.verticalIndent = 5;
        lblMailRecipients.setLayoutData(gdLblMailRecipients);
        lblMailRecipients.setText(StringConstants.PA_LBL_MAIL_RECIPIENTS);

        listMailRcpViewer = new ListViewer(compositeMailRecipients, SWT.BORDER | SWT.V_SCROLL);
        listMailRcp = listMailRcpViewer.getList();
        GridData gdListMailRcp = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdListMailRcp.heightHint = 70;
        listMailRcp.setLayoutData(gdListMailRcp);
        listMailRcpViewer.setContentProvider(ArrayContentProvider.getInstance());

        Composite compositeMailRcpButtons = new Composite(compositeMailRecipients, SWT.NONE);
        compositeMailRcpButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout glCompositeMailRcpButtons = new GridLayout(1, false);
        glCompositeMailRcpButtons.marginWidth = 0;
        glCompositeMailRcpButtons.marginHeight = 0;
        compositeMailRcpButtons.setLayout(glCompositeMailRcpButtons);

        btnAddMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnAddMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddMailRcp.setText(StringConstants.PA_BTN_ADD);

        btnDeleteMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnDeleteMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnDeleteMailRcp.setText(StringConstants.PA_BTN_DEL);

        btnClearMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnClearMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnClearMailRcp.setText(StringConstants.PA_BTN_CLEAR);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.TESTCASE_UPDATED)) {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            if (object != null && object instanceof Object[]) {
                try {
                    String oldPk = (String) ((Object[]) object)[0];
                    TestCaseEntity testCase = (TestCaseEntity) ((Object[]) object)[1];
                    childrenView.updateTestCaseTable(oldPk, testCase);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        } else if (event.getTopic().equals(EventConstants.TEST_DATA_UPDATED)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof Object[]) {
                    String oldTestDataPk = (String) ((Object[]) object)[0];
                    DataFileEntity newTestData = (DataFileEntity) ((Object[]) object)[1];
                    String projectLocation = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                    String oldTestDataId = TestDataController.getInstance().getTestDataDisplayIdByPk(oldTestDataPk,
                            projectLocation);
                    String newTestDataId = TestDataController.getInstance().getTestDataDisplayIdByPk(
                            newTestData.getId(), projectLocation);
                    childrenView.refreshTestSuiteAfterTestDataChanged(oldTestDataId, newTestDataId);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    public void dispose() {
        eventBroker.unsubscribe(this);
    }

    public void setDirty(boolean dirty) {
        if (!isTestSuiteLoading()) {
            mpart.setDirty(dirty);
            parentTestSuiteCompositePart.checkDirty();
        }
    }

    /* package */TestSuiteEntity getTestSuite() {
        return parentTestSuiteCompositePart.getTestSuiteClone();
    }

    public boolean prepareForSaving() {
        childrenView.beforeSaving();

        getTestSuite().setMailRecipient(
                TestSuiteController.getInstance().arrayMailRcpToString(listMailRcpViewer.getList().getItems()));
        return true;
    }

    /* package */void afterSaving() {
        childrenView.afterSaving();
    }

    /* package */void interuptUIThreads() {
        Iterator<Thread> iterator = uiThreads.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            if (thread.isAlive()) {
                thread.interrupt();
            }
        }
        uiThreads.clear();
        isLoading = false;
    }

    private boolean isTestSuiteLoading() {
        for (Thread thread : uiThreads) {
            if (thread.isAlive()) {
                return true;
            }
        }
        return isLoading;
    }

    public void openAddTestCaseDialog() {
        childrenView.openAddTestCaseDialog();
    }

}
