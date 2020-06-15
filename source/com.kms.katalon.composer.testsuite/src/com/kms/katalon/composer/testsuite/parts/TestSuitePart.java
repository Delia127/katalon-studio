package com.kms.katalon.composer.testsuite.parts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.kms.katalon.composer.view.TestSuiteViewFactory;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuitePart implements EventHandler {

    private static final int MINIMUM_COMPOSITE_SIZE = 300;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    private IEventBroker eventBroker;

    private Composite compositeExecution, compositeMain;

    private ScrolledComposite compositeTablePart;

    private boolean isExecutionCompositeExpanded;

    private Text txtUserDefinePageLoadTimeout;

    private MPart mpart;

    private Composite compositeExecutionDetails;

    private org.eclipse.swt.widgets.List listMailRcp;

    private ListViewer listMailRcpViewer;

    private Button btnAddMailRcp, btnDeleteMailRcp, btnClearMailRcp;

    private Button radioUseDefaultPageLoadTimeout, radioUserDefinePageLoadTimeout;


    private ImageButton btnExpandExecutionComposite;

    private TestSuiteCompositePart parentTestSuiteCompositePart;

    private Label lblExecutionInformation;

    private TestSuitePartTestCaseView childrenView;

    private List<Thread> uiThreads;

    private Composite parent;

    private Composite customViews;

    private boolean isLoading;

    private Listener layoutExecutionCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isExecutionCompositeExpanded = !isExecutionCompositeExpanded;
            layoutExecutionInfo();
        }
    };

    private Map<String, ExpandableTestSuiteComposite> viewCompositeMap = new HashMap<>();
    
    private TestSuiteRetryUiPart retryUiProvider;

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
        
        TestSuitePart tmp = this;
        retryUiProvider = new TestSuiteRetryUiPart(new TestSuiteRetryUiAdapter() {
            private TestSuitePart part = tmp;

            @Override
            public void setDirty(boolean value) {
                part.setDirty(value);
            }

            @Override
            public TestSuiteEntity getTestSuite() {
                return part.getTestSuite();
            }
        });
        
        childrenView = new TestSuitePartTestCaseView(this);
        uiThreads = new LinkedList<Thread>();
        isLoading = false;

        initExpandedState();

        registerEventBrokerListerners();

        createComponents(parent);

        registerControlListeners();

        layoutExecutionInfo();

        childrenView.layout();
    }

    public MPart getMPart() {
        return mpart;
    }

    private void initExpandedState() {
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
        eventBroker.subscribe(EventConstants.ADD_TEST_CASE_FROM_TEST_CASE, this);
        // eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM,
        // this);
    }

    private void layoutExecutionInfo() {
        Display.getDefault().timerExec(10, new Runnable() {
            @Override
            public void run() {
                compositeExecutionDetails.setVisible(isExecutionCompositeExpanded);
                if (!isExecutionCompositeExpanded) {
                    ((GridData) compositeExecutionDetails.getLayoutData()).exclude = true;
                    compositeExecution.setSize(compositeExecution.getSize().x,
                            compositeExecution.getSize().y - compositeTablePart.getSize().y);
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
        btnAddMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = Display.getDefault().getActiveShell();
                AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell,
                        listMailRcpViewer.getList().getItems());
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
                } catch (NumberFormatException ex) {}
            }
        });
        txtUserDefinePageLoadTimeout.addVerifyListener(verifyNumberListener);
        
        
        retryUiProvider.registerRetryControlListeners();

        childrenView.registerControlModifyListeners();
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

        createViewsFromViewFactory();
    }

    private void loadTestSuiteInfo(final TestSuiteEntity testSuite) throws Exception {
        retryUiProvider.syncRetryControlStatesWithTestSuiteInfo(testSuite);
 
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

        createExecutionInformationComposite();

        createCustomViewComposite();

        compositeTablePart = childrenView.createCompositeTestCase(compositeMain);
    }

    private void createCustomViewComposite() {
        customViews = new Composite(compositeMain, SWT.NONE);
        customViews.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout glCustomViews = new GridLayout(1, true);
        glCustomViews.marginHeight = 0;
        glCustomViews.marginWidth = 0;
        customViews.setLayout(glCustomViews);
        customViews.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    }

    private void createViewsFromViewFactory() {
        TestSuiteViewFactory.getInstance().getSortedBuilders().forEach(entryBuilder -> {
            String name = entryBuilder.getName();
            if (viewCompositeMap.get(name) == null) {
                AbstractTestSuiteUIDescriptionView descView = entryBuilder.getView(getTestSuite(), getMPart(),
                        parentTestSuiteCompositePart);
                ExpandableTestSuiteComposite view = new ExpandableTestSuiteComposite(customViews, name, descView);
                viewCompositeMap.put(name, view);
            }
        });
    }

    private void redrawBtnExpandExecutionInfo() {
        btnExpandExecutionComposite.getParent().setRedraw(false);
        if (isExecutionCompositeExpanded) {
            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW);
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
        compositeExecutionCompositeHeader
                .setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        compositeExecutionCompositeHeader
                .setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandExecutionComposite = new ImageButton(compositeExecutionCompositeHeader, SWT.NONE);
        redrawBtnExpandExecutionInfo();

        lblExecutionInformation = new Label(compositeExecutionCompositeHeader, SWT.NONE);
        lblExecutionInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblExecutionInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblExecutionInformation.setText("Execution Information");

        compositeExecutionDetails = new Composite(compositeExecution, SWT.NONE);
        compositeExecutionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeExecutionDetail = new GridLayout(2, false);
        glCompositeExecutionDetail.marginLeft = 45;
        glCompositeExecutionDetail.horizontalSpacing = 40;
        compositeExecutionDetails.setLayout(glCompositeExecutionDetail);

        Composite compositePageLoadTimeout = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositePageLoadTimeout = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
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
        radioUserDefinePageLoadTimeout.setLayoutData(gd_radioUserDefinePageLoadTimeout);
        radioUserDefinePageLoadTimeout.setText(StringConstants.PA_LBL_USER_DEFINE);

        txtUserDefinePageLoadTimeout = new Text(grpPageLoadTimeout, SWT.BORDER);
        GridData gdTxtUserDefinePageLoadTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtUserDefinePageLoadTimeout.heightHint = 20;
        txtUserDefinePageLoadTimeout.setLayoutData(gdTxtUserDefinePageLoadTimeout);
        txtUserDefinePageLoadTimeout.setTextLimit(4);

        retryUiProvider.createRetryComposite(compositePageLoadTimeout);

        Composite compositeMailRecipients = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositeMailRecipients = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
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
        listMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        listMailRcpViewer.setContentProvider(ArrayContentProvider.getInstance());

        Composite compositeMailRcpButtons = new Composite(compositeMailRecipients, SWT.NONE);
        compositeMailRcpButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
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
                    String newTestDataId = TestDataController.getInstance()
                            .getTestDataDisplayIdByPk(newTestData.getId(), projectLocation);
                    childrenView.refreshTestSuiteAfterTestDataChanged(oldTestDataId, newTestDataId);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (event.getTopic().equals(EventConstants.ADD_TEST_CASE_FROM_TEST_CASE)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);

                if (object != null) {
                    try {
                        String nameTestSuite = (String) ((Object[]) object)[1];
                        TestCaseEntity testCase = (TestCaseEntity) ((Object[]) object)[0];
                        childrenView.addNewTestCase(nameTestSuite, testCase);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
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

    public void openAddedTestCase(TestCaseEntity testCaseEntity) {
        eventBroker.post(EventConstants.TESTCASE_OPEN, testCaseEntity);
    }

    public void openAddedTestData(DataFileEntity dataFileEntity) {
        eventBroker.post(EventConstants.TEST_DATA_OPEN, dataFileEntity);
    }
}
