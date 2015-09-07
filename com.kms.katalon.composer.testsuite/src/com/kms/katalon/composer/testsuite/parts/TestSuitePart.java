package com.kms.katalon.composer.testsuite.parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.dialogs.AddMailRecipientDialog;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.custom.AdvancedSearchDialog;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.constants.TestDataToolItemConstants;
import com.kms.katalon.composer.testsuite.constants.TestSuiteEventConstants;
import com.kms.katalon.composer.testsuite.listeners.TestCaseTableDropListener;
import com.kms.katalon.composer.testsuite.listeners.TestCaseTableKeyListener;
import com.kms.katalon.composer.testsuite.listeners.TestDataToolItemListener;
import com.kms.katalon.composer.testsuite.providers.IsRunColumnLabelProvider;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableLabelProvider;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewerFilter;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeContentProvider;
import com.kms.katalon.composer.testsuite.providers.TestDataTreeLabelProvider;
import com.kms.katalon.composer.testsuite.providers.VariableTableLabelProvider;
import com.kms.katalon.composer.testsuite.support.TestCaseIdColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestCaseIsRunColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataCombinationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIDColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.TestDataIterationColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTestDataLinkColumnEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableTypeEditingSupport;
import com.kms.katalon.composer.testsuite.support.VariableValueEditingSupport;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransfer;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransferData;
import com.kms.katalon.composer.testsuite.tree.TestDataLinkTreeNode;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestEnvironmentController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.testdata.TestDataFactory;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

//import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;

public class TestSuitePart implements EventHandler {
	private static final String IS_RUN_COLUMN_HEADER = StringConstants.PA_COL_RUN;

	private static final String PK_COLUMN_HEADER = StringConstants.PA_COL_ID;

	private static final String NUMBER_COLUMN_HEADER = StringConstants.PA_COL_NO;

	private static final String TEST_CASE_NAME_COLUMN_HEADER = StringConstants.PA_COL_NAME;

	private static final String DESCRIPTION_COLUMN_HEADER = StringConstants.PA_COL_DESC;

	private static final String LAST_UPDATE_LABEL = StringConstants.PA_LBL_LAST_UPDATED;

	private static final String CREATED_DATE_LABEL = StringConstants.PA_LBL_CREATED_DATE;

	private static final String DESCRIPTION_LABEL = StringConstants.PA_LBL_DESC;

	private static final String LAST_RUN_LABEL = StringConstants.PA_LBL_LAST_RUN;

	private static final String LAST_RUN_LABEL_TOOLTIP = StringConstants.PA_LBL_TIP_LAST_RUN;

	private static final String TEST_SUITE_ID_LABEL = StringConstants.PA_LBL_ID;

	private static final String TEST_SUITE_NAME_LABEL = StringConstants.PA_LBL_NAME;

	private static final String MAIL_RECIPIENTS_LABEL = StringConstants.PA_LBL_MAIL_RECIPIENTS;

	private static final String PAGE_LOAD_TIMEOUT_LABEL = StringConstants.PA_LBL_PAGE_LOAD_TIMEOUT;

	private static final int MINIMUM_COMPOSITE_SIZE = 300;

	private static final int MAX_HEIGHT_OF_TEXT_BOX = 20;

	private static final String IMAGE_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_SEARCH;
	private static final String IMAGE_CLOSE_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_CLOSE_SEARCH;
	private static final String IMAGE_ADVANCED_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_ADVANCED_SEARCH;

	private Composite compositeExecution, compositeMain, compositeInformation, compositeInformationHeader,
			compositeTableContent, compositeInformationDetails;

	private ScrolledComposite compositeTablePart;

	private boolean isGeneralInfoCompositeExpanded, isExecutionCompositeExpanded, isTestDataCompositeExpanded;

	private Table testCaseTable;

	private Text textTestSuiteName, textDescription;

	private Button btnExpandInformation;

	private Label lblTestSuiteID, lblCreatedDate, lblLastUpdate;
	private Link lblLastRun;

	private TestCaseTableViewer testCaseTableViewer;
	private TableColumn tblclmnIsRun;
	private MPart mpart;

	private int testSuiteTestCaseSelectedIdx = 0;

	@Inject
	protected EModelService modelService;

	@Inject
	protected MApplication application;

	@Inject
	private IEventBroker eventBroker;

	private Text txtTestSuiteId, txtCreatedDate, txtLastUpdate, txtLastRun, txtRerun;
	private Label lblDescriptionSecondRow;
	private Composite compositePageLoadTimeout;
	private Composite compositeMailRecipients;
	private Label lblMailRecipients;
	private Composite compositeExecutionDetails;
	private Label lblExecutionInformation;
	private org.eclipse.swt.widgets.List listMailRcp;
	private ListViewer listMailRcpViewer;
	private Composite compositeMailRcpButtons;
	private Button btnAddMailRcp, btnDeleteMailRcp, btnClearMailRcp;
	private Button btnExpandExecutionComposite, radioUseDefaultPageLoadTimeout, radioUserDefinePageLoadTimeout;
	private Text txtUserDefinePageLoadTimeout;
	private Group grpPageLoadTimeout;
	private Composite compositeTestCase;
	private SashForm sashForm;
	private Composite compositeBinding;
	private Table testCaseVariableTable;
	private TableViewer testCaseVariableTableViewer;
	private TableColumn tblclmnVariableName;
	private TableViewerColumn variableNameColumnViewer;
	private TableColumn tblclmnVariableValue;
	private TableViewerColumn variableValueColumnViewer;
	private Composite compositeVariableHeader;
	private TableColumn tblclmnVaribaleDefaultValue;
	private TableViewerColumn variableDefaultValueColumnViewer;
	private TableColumn tblclmnVariableType;
	private TableViewerColumn variableTypeColumnViewer;
	private Composite compositeLastRunAndReRun;
	private Composite compositeVariable;
	private Composite compositeTestData;
	private Composite compositeBindingChild;

	private TreeViewer testDataTreeViewer;
	private Composite compositeTestDataTreeTable;
	private TableColumn tblclmnTestDataLinkId;
	private TableViewerColumn variableTestDataLinkIDViewerColumn;
	private TableColumn tblclmnVariableNo;
	private TableViewerColumn variableOrderColumnViewer;

	private Button btnExpandCompositeTestData;
	private Composite compositeTestDataDetails;
	private TreeColumn trclmnNo;
	private TreeViewerColumn treeViewerColumn;

	private TestSuiteCompositePart parentTestSuiteCompositePart;
	private boolean isTestSuiteLoading;

	private GridData gdTestDataTable;
	private Composite compositeTableSearch;
	private Text txtSearch;

	private CLabel lblSearch, lblFilter;

	private boolean isSearching;

	@PostConstruct
	public void createControls(Composite parent, MPart mpart) {
		this.mpart = mpart;

		if (mpart.getParent().getParent() instanceof MGenericTile
				&& ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
			MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
			if (compositePart.getObject() instanceof TestSuiteCompositePart) {
				parentTestSuiteCompositePart = ((TestSuiteCompositePart) compositePart.getObject());
			}
		}

		initExpandedState();

		parent.setLayout(new GridLayout(1, false));

		registerEventBrokerListerners();

		createComponents(parent);

		registerControlModifyListeners();

		layoutGeneralInfo();
		layoutExecutionInfo();
		// layoutTestDataComposite();
	}

	public MPart getMPart() {
		return mpart;
	}

	private void initExpandedState() {
		isGeneralInfoCompositeExpanded = true;
		isExecutionCompositeExpanded = true;
		isTestDataCompositeExpanded = true;
		isSearching = false;
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
				isGeneralInfoCompositeExpanded = !isGeneralInfoCompositeExpanded;

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
				isExecutionCompositeExpanded = !isExecutionCompositeExpanded;
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

	private void layoutTestDataComposite() {
		Display.getDefault().timerExec(10, new Runnable() {
			@Override
			public void run() {
				isTestDataCompositeExpanded = !isTestDataCompositeExpanded;
				compositeTestDataDetails.setVisible(isTestDataCompositeExpanded);

				if (!isTestDataCompositeExpanded) {
					((GridData) compositeTestDataDetails.getLayoutData()).exclude = true;
					compositeTestData.setSize(compositeTestData.getSize().x, compositeTestData.getSize().y
							- compositeVariable.getSize().y);
				} else {
					((GridData) compositeTestDataDetails.getLayoutData()).exclude = false;
				}
				compositeTestData.layout(true, true);
				compositeTestData.getParent().layout();
				redrawBtnExpandCompositeTestData();
			}
		});
	}

	private void registerControlModifyListeners() {
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

		btnExpandInformation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				layoutGeneralInfo();
			}
		});

		btnAddMailRcp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Shell shell = Display.getDefault().getActiveShell();
				AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell);
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

		btnExpandExecutionComposite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				layoutExecutionInfo();
			}
		});

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

		txtUserDefinePageLoadTimeout.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String text = txtUserDefinePageLoadTimeout.getText();
				try {
					int timeout = Integer.parseInt(text);
					if (timeout >= TestEnvironmentController.getInstance().getPageLoadTimeOutMinimumValue()
							&& timeout <= TestEnvironmentController.getInstance().getPageLoadTimeOutMaximumValue()) {
						getTestSuite().setPageLoadTimeout((short) timeout);
					}
				} catch (NumberFormatException ex) {
					// Users input wrong key, Do not care about that.
				}
			}
		});

		lblLastRun.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openReportOfLastRun();
			}
		});

		txtRerun.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		btnExpandCompositeTestData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				layoutTestDataComposite();
			}
		});

		compositeTablePart.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				int compositeHeight = compositeTablePart.getClientArea().height;
				gdTestDataTable.heightHint = Math.min(200, compositeHeight / 3);
				compositeBindingChild.layout(true);
			}
		});

		txtSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					filterTestCaseLinkBySearchedText();
				}
			}
		});

	}

	private void filterTestCaseLinkBySearchedText() {
		if (txtSearch.getText().isEmpty()) {
			isSearching = false;
		} else {
			isSearching = true;
		}

		testCaseTableViewer.setSearchedString(txtSearch.getText());
		testCaseTableViewer.refresh(true);
		updateStatusSearchLabel();
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
		Display.getCurrent().syncExec(new Runnable() {

			@Override
			public void run() {
				try {
					isTestSuiteLoading = true;
					String testSuiteIdForDisplay = TestSuiteController.getInstance().getIdForDisplay(testSuite)
							.replace("\\", "/");

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
						lblLastRun.setText("<A>" + LAST_RUN_LABEL + "</A>");
						lblLastRun.setToolTipText(LAST_RUN_LABEL_TOOLTIP);
						txtLastRun.setText(testSuite.getLastRun().toString());
					} else {
						lblLastRun.setText(LAST_RUN_LABEL);
						lblLastRun.setToolTipText("");
					}

					txtRerun.setText(String.valueOf(testSuite.getNumberOfRerun()));

					// if (testSuite.getDataFileId() != null) {
					// textTestDataId.setText(testSuite.getDataFileId());
					// }

					// binding mailRecipient
					listMailRcpViewer.setInput(TestSuiteController.getInstance().mailRcpStringToArray(
							testSuite.getMailRecipient()));

					testCaseTableViewer.setInput(new ArrayList<TestSuiteTestCaseLink>(testSuite
							.getTestSuiteTestCaseLinks()));
					processTestSuteTestCaseLinkSelected();

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

					isTestSuiteLoading = false;

				} catch (Exception e) {
					LoggerSingleton.logError(e);
					MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
							StringConstants.PA_ERROR_MSG_UNABLE_TO_LOAD_TEST_SUITE);
				}

			}
		});

	}

	private void createComponents(Composite parent) {
		parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
		compositeMain = new Composite(parent, SWT.NONE);
		GridLayout gl_compositeMain = new GridLayout(1, false);
		gl_compositeMain.marginWidth = 0;
		gl_compositeMain.marginHeight = 0;
		compositeMain.setLayout(gl_compositeMain);
		compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		createGeneralInformationComposite();
		createExecutionInformationComposite();
		createCompositeTestCase();
	}

	private void createGeneralInformationComposite() {
		compositeInformation = new Composite(compositeMain, SWT.NONE);
		compositeInformation.setBackground(ColorUtil.getCompositeBackgroundColor());
		compositeInformation.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeInformation = new GridLayout(1, false);
		gl_compositeInformation.marginWidth = 0;
		gl_compositeInformation.marginHeight = 0;
		gl_compositeInformation.verticalSpacing = 0;
		compositeInformation.setLayout(gl_compositeInformation);

		compositeInformationHeader = new Composite(compositeInformation, SWT.NONE);
		GridLayout gl_compositeInformationHeader = new GridLayout(2, false);
		gl_compositeInformationHeader.marginWidth = 0;
		gl_compositeInformationHeader.marginHeight = 0;
		compositeInformationHeader.setLayout(gl_compositeInformationHeader);
		compositeInformationHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeInformationHeader.setBounds(0, 0, 64, 64);

		btnExpandInformation = new Button(compositeInformationHeader, SWT.NONE);
		GridData gdBtnExpandInfo = new GridData();
		gdBtnExpandInfo.widthHint = 18;
		gdBtnExpandInfo.heightHint = 18;
		btnExpandInformation.setLayoutData(gdBtnExpandInfo);
		redrawBtnExpandGeneralInfo();

		Label lblInformation = new Label(compositeInformationHeader, SWT.NONE);
		lblInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
		lblInformation.setText("General Information");

		compositeInformationDetails = new Composite(compositeInformation, SWT.NONE);
		GridLayout gl_compositeInformationDetails = new GridLayout(3, true);
		gl_compositeInformationDetails.marginLeft = 45;
		gl_compositeInformationDetails.horizontalSpacing = 40;
		compositeInformationDetails.setLayout(gl_compositeInformationDetails);
		compositeInformationDetails.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		compositeInformationDetails.setBounds(0, 0, 64, 64);

		Composite compositeTestSuiteIdAndName = new Composite(compositeInformationDetails, SWT.NONE);
		GridData gd_compositeIdAndName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_compositeIdAndName.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositeTestSuiteIdAndName.setLayoutData(gd_compositeIdAndName);
		GridLayout gl_compositeIdAndName = new GridLayout(2, false);
		gl_compositeIdAndName.verticalSpacing = 10;
		compositeTestSuiteIdAndName.setLayout(gl_compositeIdAndName);

		lblTestSuiteID = new Label(compositeTestSuiteIdAndName, SWT.NONE);
		GridData gd_lblTestSuiteID = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTestSuiteID.widthHint = 50;
		lblTestSuiteID.setLayoutData(gd_lblTestSuiteID);
		lblTestSuiteID.setText(TEST_SUITE_ID_LABEL);

		txtTestSuiteId = new Text(compositeTestSuiteIdAndName, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtTestSuiteId = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_txtTestSuiteId.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
		txtTestSuiteId.setLayoutData(gd_txtTestSuiteId);

		Label lblTestSuiteName = new Label(compositeTestSuiteIdAndName, SWT.NONE);
		GridData gd_lblTestSuiteName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblTestSuiteName.widthHint = 50;
		lblTestSuiteName.setLayoutData(gd_lblTestSuiteName);
		lblTestSuiteName.setText(TEST_SUITE_NAME_LABEL);

		textTestSuiteName = new Text(compositeTestSuiteIdAndName, SWT.BORDER);
		GridData gd_textTestSuiteName = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_textTestSuiteName.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
		textTestSuiteName.setLayoutData(gd_textTestSuiteName);

		Composite compositeUpdateAndRun = new Composite(compositeInformationDetails, SWT.NONE);
		GridData gd_compositeUpdateAndRun = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_compositeUpdateAndRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositeUpdateAndRun.setLayoutData(gd_compositeUpdateAndRun);
		GridLayout gl_compositeUpdateAndRun = new GridLayout(2, false);
		gl_compositeUpdateAndRun.verticalSpacing = 10;
		compositeUpdateAndRun.setLayout(gl_compositeUpdateAndRun);

		lblCreatedDate = new Label(compositeUpdateAndRun, SWT.NONE);
		GridData gd_lblCreatedDate = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblCreatedDate.widthHint = 85;
		lblCreatedDate.setLayoutData(gd_lblCreatedDate);
		lblCreatedDate.setText(CREATED_DATE_LABEL);

		txtCreatedDate = new Text(compositeUpdateAndRun, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtCreatedDate = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtCreatedDate.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
		txtCreatedDate.setLayoutData(gd_txtCreatedDate);

		lblLastUpdate = new Label(compositeUpdateAndRun, SWT.NONE);
		lblLastUpdate.setText(LAST_UPDATE_LABEL);

		txtLastUpdate = new Text(compositeUpdateAndRun, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtLastUpdate = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtLastUpdate.heightHint = MAX_HEIGHT_OF_TEXT_BOX;
		txtLastUpdate.setLayoutData(gd_txtLastUpdate);

		Composite compositeDescription = new Composite(compositeInformationDetails, SWT.NONE);
		GridData gd_compositeDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_compositeDescription.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositeDescription.setLayoutData(gd_compositeDescription);
		GridLayout gl_compositeDescription = new GridLayout(2, false);
		gl_compositeDescription.verticalSpacing = 10;
		compositeDescription.setLayout(gl_compositeDescription);

		Label lblDescription = new Label(compositeDescription, SWT.NONE);
		GridData gd_lblDescription = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		gd_lblDescription.widthHint = 85;
		gd_lblDescription.heightHint = 20;
		lblDescription.setLayoutData(gd_lblDescription);
		lblDescription.setText(DESCRIPTION_LABEL);

		textDescription = new Text(compositeDescription, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_textDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gd_textDescription.heightHint = 60;
		textDescription.setLayoutData(gd_textDescription);

		lblDescriptionSecondRow = new Label(compositeDescription, SWT.NONE);
		GridData gd_lblDescriptionSecondRow = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		gd_lblDescriptionSecondRow.heightHint = 20;
		lblDescriptionSecondRow.setLayoutData(gd_lblDescriptionSecondRow);
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

	private void redrawBtnExpandCompositeTestData() {
		if (isTestDataCompositeExpanded) {
			btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
		} else {
			btnExpandCompositeTestData.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
		}
	}

	private void createExecutionInformationComposite() {
		compositeExecution = new Composite(compositeMain, SWT.NONE);
		compositeExecution.setBackground(ColorUtil.getCompositeBackgroundColor());
		GridLayout gl_compositeExecution = new GridLayout(1, true);
		gl_compositeExecution.verticalSpacing = 0;
		gl_compositeExecution.horizontalSpacing = 0;
		gl_compositeExecution.marginHeight = 0;
		gl_compositeExecution.marginWidth = 0;
		compositeExecution.setLayout(gl_compositeExecution);
		compositeExecution.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Composite compositeExecutionCompositeHeader = new Composite(compositeExecution, SWT.NONE);
		compositeExecutionCompositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeExecutionCompositeHeader = new GridLayout(2, false);
		gl_compositeExecutionCompositeHeader.marginHeight = 0;
		gl_compositeExecutionCompositeHeader.marginWidth = 0;
		compositeExecutionCompositeHeader.setLayout(gl_compositeExecutionCompositeHeader);

		btnExpandExecutionComposite = new Button(compositeExecutionCompositeHeader, SWT.NONE);
		redrawBtnExpandExecutionInfo();

		GridData gd_btnExpandExecutionComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExpandExecutionComposite.widthHint = 18;
		gd_btnExpandExecutionComposite.heightHint = 18;
		btnExpandExecutionComposite.setLayoutData(gd_btnExpandExecutionComposite);

		lblExecutionInformation = new Label(compositeExecutionCompositeHeader, SWT.NONE);
		lblExecutionInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
		lblExecutionInformation.setText("Execution Information");

		compositeExecutionDetails = new Composite(compositeExecution, SWT.NONE);
		compositeExecutionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_compositeExecutionDetail = new GridLayout(3, true);
		gl_compositeExecutionDetail.marginLeft = 45;
		gl_compositeExecutionDetail.horizontalSpacing = 40;
		compositeExecutionDetails.setLayout(gl_compositeExecutionDetail);

		compositePageLoadTimeout = new Composite(compositeExecutionDetails, SWT.NONE);
		GridData gd_compositePageLoadTimeout = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_compositePageLoadTimeout.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositePageLoadTimeout.setLayoutData(gd_compositePageLoadTimeout);
		GridLayout gl_compositePageLoadTimeout = new GridLayout(1, false);
		gl_compositePageLoadTimeout.marginWidth = 0;
		gl_compositePageLoadTimeout.marginHeight = 0;
		gl_compositePageLoadTimeout.horizontalSpacing = 10;
		compositePageLoadTimeout.setLayout(gl_compositePageLoadTimeout);

		grpPageLoadTimeout = new Group(compositePageLoadTimeout, SWT.NONE);
		grpPageLoadTimeout.setText(PAGE_LOAD_TIMEOUT_LABEL);
		grpPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_grpPageLoadTimeout = new GridLayout(2, false);
		gl_grpPageLoadTimeout.marginLeft = 50;
		gl_grpPageLoadTimeout.marginWidth = 0;
		grpPageLoadTimeout.setLayout(gl_grpPageLoadTimeout);

		radioUseDefaultPageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
		radioUseDefaultPageLoadTimeout.setText("Use default");
		new Label(grpPageLoadTimeout, SWT.NONE);

		radioUserDefinePageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
		GridData gd_radioUserDefinePageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_radioUserDefinePageLoadTimeout.widthHint = 83;
		radioUserDefinePageLoadTimeout.setLayoutData(gd_radioUserDefinePageLoadTimeout);
		radioUserDefinePageLoadTimeout.setText("User define");

		txtUserDefinePageLoadTimeout = new Text(grpPageLoadTimeout, SWT.BORDER);
		GridData gd_txtUserDefinePageLoadTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtUserDefinePageLoadTimeout.heightHint = 20;
		txtUserDefinePageLoadTimeout.setLayoutData(gd_txtUserDefinePageLoadTimeout);

		compositeLastRunAndReRun = new Composite(compositeExecutionDetails, SWT.NONE);
		GridData gd_compositeTestDataAndLastRun = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_compositeTestDataAndLastRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositeLastRunAndReRun.setLayoutData(gd_compositeTestDataAndLastRun);
		GridLayout gl_compositeTestDataAndLastRun = new GridLayout(4, false);
		gl_compositeTestDataAndLastRun.verticalSpacing = 10;
		compositeLastRunAndReRun.setLayout(gl_compositeTestDataAndLastRun);

		lblLastRun = new Link(compositeLastRunAndReRun, SWT.NONE);
		GridData gd_lblLastRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblLastRun.widthHint = 85;
		lblLastRun.setLayoutData(gd_lblLastRun);
		lblLastRun.setText(LAST_RUN_LABEL);

		txtLastRun = new Text(compositeLastRunAndReRun, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtLastRun = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_txtLastRun.heightHint = 20;
		txtLastRun.setLayoutData(gd_txtLastRun);

		Label lblReRun = new Label(compositeLastRunAndReRun, SWT.NONE);
		GridData gd_lblReRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblReRun.widthHint = 85;
		lblReRun.setLayoutData(gd_lblReRun);
		lblReRun.setText(StringConstants.PA_LBL_RE_RUN);

		txtRerun = new Text(compositeLastRunAndReRun, SWT.BORDER);
		GridData gd_txtRerun = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_txtRerun.heightHint = 20;
		txtRerun.setLayoutData(gd_txtRerun);

		compositeMailRecipients = new Composite(compositeExecutionDetails, SWT.NONE);
		GridData gd_compositeMailRecipients = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_compositeMailRecipients.minimumWidth = MINIMUM_COMPOSITE_SIZE;
		compositeMailRecipients.setLayoutData(gd_compositeMailRecipients);
		GridLayout gl_compositeMailRecipients = new GridLayout(3, false);
		compositeMailRecipients.setLayout(gl_compositeMailRecipients);

		lblMailRecipients = new Label(compositeMailRecipients, SWT.NONE);
		GridData gd_lblMailRecipients = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gd_lblMailRecipients.widthHint = 85;
		gd_lblMailRecipients.verticalIndent = 5;
		lblMailRecipients.setLayoutData(gd_lblMailRecipients);
		lblMailRecipients.setText(MAIL_RECIPIENTS_LABEL);

		listMailRcpViewer = new ListViewer(compositeMailRecipients, SWT.BORDER | SWT.V_SCROLL);
		listMailRcp = listMailRcpViewer.getList();
		GridData gd_listMailRcp = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_listMailRcp.heightHint = 70;
		listMailRcp.setLayoutData(gd_listMailRcp);
		listMailRcpViewer.setContentProvider(ArrayContentProvider.getInstance());

		compositeMailRcpButtons = new Composite(compositeMailRecipients, SWT.NONE);
		compositeMailRcpButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout gl_compositeMailRcpButtons = new GridLayout(1, false);
		gl_compositeMailRcpButtons.marginWidth = 0;
		gl_compositeMailRcpButtons.marginHeight = 0;
		compositeMailRcpButtons.setLayout(gl_compositeMailRcpButtons);

		btnAddMailRcp = new Button(compositeMailRcpButtons, SWT.NONE);
		btnAddMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnAddMailRcp.setText(StringConstants.PA_BTN_ADD);

		btnDeleteMailRcp = new Button(compositeMailRcpButtons, SWT.NONE);
		btnDeleteMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnDeleteMailRcp.setText(StringConstants.PA_BTN_DEL);

		btnClearMailRcp = new Button(compositeMailRcpButtons, SWT.NONE);
		btnClearMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnClearMailRcp.setText(StringConstants.PA_BTN_CLEAR);
	}

	private void createCompositeTestCaseButtons() {
		compositeTestCase = new Composite(sashForm, SWT.NONE);
		compositeTestCase.setLayout(new GridLayout(1, false));
		compositeTestCase.setBackground(ColorUtil.getCompositeBackgroundColor());
		final Composite compositeTableButtons = new Composite(compositeTestCase, SWT.NONE);
		compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeTableButtons = new GridLayout(1, false);
		compositeTableButtons.setBackground(ColorUtil.getCompositeBackgroundColor());
		gl_compositeTableButtons.marginHeight = 0;
		gl_compositeTableButtons.marginWidth = 0;
		compositeTableButtons.setLayout(gl_compositeTableButtons);

		ToolBar toolBar = new ToolBar(compositeTableButtons, SWT.FLAT | SWT.RIGHT);

		ToolItem tltmAddTestCases = new ToolItem(toolBar, SWT.NONE);
		tltmAddTestCases.setText(StringConstants.PA_TOOLITEM_ADD);
		tltmAddTestCases.setToolTipText(StringConstants.PA_TOOLITEM_ADD);
		tltmAddTestCases.setImage(ImageConstants.IMG_24_ADD);
		tltmAddTestCases.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
					if (currentProject != null) {
						EntityProvider entityProvider = new EntityProvider();
						TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(compositeTableButtons
								.getShell(), new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(
								entityProvider));

						dialog.setAllowMultiple(true);
						dialog.setTitle(StringConstants.PA_TITLE_TEST_CASE_BROWSER);
						dialog.setInput(TreeEntityUtil.getChildren(null, FolderController.getInstance()
								.getTestCaseRoot(currentProject)));
						if (dialog.open() == Window.OK) {
							Object[] selectedObjects = dialog.getResult();
							for (Object object : selectedObjects) {
								if (object instanceof ITreeEntity) {
									ITreeEntity treeEntity = (ITreeEntity) object;
									if (treeEntity.getObject() instanceof FolderEntity) {
										addTestCaseFolderToTable((FolderEntity) treeEntity.getObject());
									} else if (treeEntity.getObject() instanceof TestCaseEntity) {
										testCaseTableViewer.addTestCase((TestCaseEntity) treeEntity.getObject());
									}
								}
							}
						}
					}
				} catch (Exception ex) {
					MessageDialog.openError(null, StringConstants.ERROR_TITLE,
							StringConstants.PA_ERROR_MSG_UNABLE_TO_ADD_TEST_CASES);
					LoggerSingleton.logError(ex);
				}
			}
		});

		ToolItem tltmRemoveTestCases = new ToolItem(toolBar, SWT.NONE);
		tltmRemoveTestCases.setText(StringConstants.PA_TOOLITEM_REMOVE);
		tltmRemoveTestCases.setToolTipText(StringConstants.PA_TOOLITEM_REMOVE);
		tltmRemoveTestCases.setImage(ImageConstants.IMG_24_REMOVE);
		tltmRemoveTestCases.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings({ "unchecked" })
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					testCaseTableViewer.removeTestCases(((IStructuredSelection) testCaseTableViewer.getSelection())
							.toList());
				} catch (Exception ex) {
					LoggerSingleton.logError(ex);
				}
			}
		});

		ToolItem tltmUp = new ToolItem(toolBar, SWT.NONE);
		tltmUp.setText(StringConstants.PA_TOOLITEM_UP);
		tltmUp.setToolTipText(StringConstants.PA_TOOLITEM_UP);
		tltmUp.setImage(ImageConstants.IMG_24_UP);
		tltmUp.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				testCaseTableViewer.upTestCase(((IStructuredSelection) testCaseTableViewer.getSelection()).toList());
			}
		});

		ToolItem tltmDown = new ToolItem(toolBar, SWT.NONE);
		tltmDown.setText(StringConstants.PA_TOOLITEM_DOWN);
		tltmDown.setToolTipText(StringConstants.PA_TOOLITEM_DOWN);
		tltmDown.setImage(ImageConstants.IMG_24_DOWN);
		tltmDown.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				testCaseTableViewer.downTestCase(((IStructuredSelection) testCaseTableViewer.getSelection()).toList());
			}
		});
	}

	private void addTestCaseFolderToTable(FolderEntity folder) throws Exception {
		if (folder.getFolderType() == FolderType.TESTCASE) {
			FolderController folderController = FolderController.getInstance();
			for (Object childObject : folderController.getChildren(folder)) {
				if (childObject instanceof TestCaseEntity) {
					testCaseTableViewer.addTestCase((TestCaseEntity) childObject);
				} else if (childObject instanceof FolderEntity) {
					addTestCaseFolderToTable((FolderEntity) childObject);
				}
			}
		}
	}

	private void updateStatusSearchLabel() {
		if (isSearching) {
			lblSearch.setImage(ImageConstants.IMG_16_CLOSE_SEARCH);
			lblSearch.setToolTipText(IMAGE_CLOSE_SEARCH_TOOLTIP);
		} else {
			lblSearch.setImage(ImageConstants.IMG_16_SEARCH);
			lblSearch.setToolTipText(IMAGE_SEARCH_TOOLTIP);
		}
	}

	private void createCompositeTestCaseSearch() {
		compositeTableSearch = new Composite(compositeTestCase, SWT.BORDER);
		compositeTableSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeTableSearch.setBackground(ColorUtil.getWhiteBackGroundColor());
		GridLayout gl_compositeTableSearch = new GridLayout(4, false);
		gl_compositeTableSearch.marginWidth = 0;
		gl_compositeTableSearch.marginHeight = 0;
		compositeTableSearch.setLayout(gl_compositeTableSearch);

		txtSearch = new Text(compositeTableSearch, SWT.NONE);
		txtSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
		GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
		gdTxtInput.grabExcessVerticalSpace = true;
		gdTxtInput.verticalAlignment = SWT.CENTER;
		txtSearch.setLayoutData(gdTxtInput);

		Canvas canvasSearch = new Canvas(compositeTableSearch, SWT.NONE);
		canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
		lblSearch = new CLabel(canvasSearch, SWT.NONE);
		updateStatusSearchLabel();

		lblSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
		lblSearch.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if (isSearching) {
					txtSearch.setText("");
				}

				filterTestCaseLinkBySearchedText();
			}
		});

		Label seperator1 = new Label(compositeTableSearch, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_seperator1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_seperator1.heightHint = 22;
		seperator1.setLayoutData(gd_seperator1);

		// label Filter
		lblFilter = new CLabel(compositeTableSearch, SWT.NONE);
		lblFilter.setImage(ImageConstants.IMG_16_ADVANCED_SEARCH);
		lblFilter.setToolTipText(IMAGE_ADVANCED_SEARCH_TOOLTIP);
		lblFilter.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
		lblFilter.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// TODO Auto-generated method stub
				openAdvancedSearchDialog();
			}
		});
	}

	private void openAdvancedSearchDialog() {
		try {
			Shell shell = new Shell(compositeTableSearch.getShell());
			shell.setSize(0, 0);
			List<String> searchTags = Arrays.asList(TestCaseTreeEntity.SEARCH_TAGS);

			Point pt = compositeTableSearch.toDisplay(1, 1);
			Point location = new Point(pt.x + compositeTableSearch.getBounds().width, pt.y);
			AdvancedSearchDialog dialog = new AdvancedSearchDialog(shell, searchTags.toArray(new String[searchTags
					.size()]), txtSearch.getText(), location);
			// set position for dialog
			if (dialog.open() == Window.OK) {
				txtSearch.setText(dialog.getOutput());
				filterTestCaseLinkBySearchedText();
			}

			shell.getSize();
			shell.dispose();

		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private void createCompositeTestCaseContent() {
		compositeTableContent = new Composite(compositeTestCase, SWT.NONE);
		compositeTableContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		testCaseTableViewer = new TestCaseTableViewer(compositeTableContent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI, eventBroker);
		testCaseTable = testCaseTableViewer.getTable();
		testCaseTable.setHeaderVisible(true);
		testCaseTable.setLinesVisible(true);

		TableViewerColumn tableViewerColumnNo = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
		tblclmnNo.setText(NUMBER_COLUMN_HEADER);

		TableViewerColumn tableViewerColumnPK = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnPK = tableViewerColumnPK.getColumn();
		tblclmnPK.setText(PK_COLUMN_HEADER);

		TableViewerColumn tableViewerColumnName = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnName = tableViewerColumnName.getColumn();
		tblclmnName.setText(TEST_CASE_NAME_COLUMN_HEADER);

		TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnDescription = tableViewerColumnDescription.getColumn();
		tblclmnDescription.setText(DESCRIPTION_COLUMN_HEADER);

		TableViewerColumn tableViewerColumnIsRun = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		tblclmnIsRun = tableViewerColumnIsRun.getColumn();
		tblclmnIsRun.setText(IS_RUN_COLUMN_HEADER);
		tblclmnIsRun.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				testCaseTableViewer.setIsRunValueAllTestCases();
			}
		});

		// set layout of table composite
		TableColumnLayout tableLayout = new TableColumnLayout();
		tableLayout.setColumnData(tblclmnNo, new ColumnWeightData(0, 40));
		tableLayout.setColumnData(tblclmnPK, new ColumnWeightData(15, 100));
		tableLayout.setColumnData(tblclmnName, new ColumnWeightData(15, 100));
		tableLayout.setColumnData(tblclmnDescription, new ColumnWeightData(15, 100));
		tableLayout.setColumnData(tblclmnIsRun, new ColumnWeightData(0, 80));

		compositeTableContent.setLayout(tableLayout);

		testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		testCaseTableViewer.setFilters(new ViewerFilter[] { new TestCaseTableViewerFilter() });
		testCaseTableViewer.getTable().setToolTipText("");
		ColumnViewerToolTipSupport.enableFor(testCaseTableViewer, ToolTip.NO_RECREATE);

		tableViewerColumnNo
				.setLabelProvider(new TestCaseTableLabelProvider(TestCaseTableLabelProvider.COLUMN_NO_INDEX));
		tableViewerColumnPK
				.setLabelProvider(new TestCaseTableLabelProvider(TestCaseTableLabelProvider.COLUMN_ID_INDEX));
		tableViewerColumnName.setLabelProvider(new TestCaseTableLabelProvider(
				TestCaseTableLabelProvider.COLUMN_NAME_INDEX));
		tableViewerColumnDescription.setLabelProvider(new TestCaseTableLabelProvider(
				TestCaseTableLabelProvider.COLUMN_DESCRIPTION_INDEX));

		tableViewerColumnIsRun.setLabelProvider(new IsRunColumnLabelProvider());
		tableViewerColumnIsRun
				.setEditingSupport(new TestCaseIsRunColumnEditingSupport(testCaseTableViewer, eventBroker));

		tableViewerColumnPK.setEditingSupport(new TestCaseIdColumnEditingSupport(testCaseTableViewer, eventBroker));

		testCaseTable.addKeyListener(new TestCaseTableKeyListener(testCaseTableViewer));

		testCaseTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				processTestSuteTestCaseLinkSelected();
			}

		});

		createCompositeTestDataAndVariable();

		sashForm.setWeights(new int[] { 5, 5 });
		hookDropEvent();
		hookDragEvent();
	}

	private void hookDropEvent() {
		DropTarget dt = new DropTarget(testCaseTableViewer.getTable(), DND.DROP_MOVE);
		List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
		treeEntityTransfers.add(new TestSuiteTestCaseLinkTransfer());
		dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
		dt.addDropListener(new TestCaseTableDropListener(testCaseTableViewer, getTestSuite()));
	}

	private void hookDragEvent() {
		int operations = DND.DROP_MOVE | DND.DROP_COPY;

		DragSource dragSource = new DragSource(testCaseTableViewer.getTable(), operations);
		dragSource.setTransfer(new Transfer[] { new TestSuiteTestCaseLinkTransfer() });
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				TableItem[] selection = testCaseTableViewer.getTable().getSelection();
				if (selection.length > 0) {
					event.doit = true;
				} else {
					event.doit = false;
				}
			};

			public void dragSetData(DragSourceEvent event) {
				List<TestSuiteTestCaseLinkTransferData> testSuiteTestCaseLinkTransferDatas = new ArrayList<TestSuiteTestCaseLinkTransferData>();
				TableItem[] selection = testCaseTableViewer.getTable().getSelection();
				for (TableItem item : selection) {
					if (item.getData() instanceof TestSuiteTestCaseLink) {
						testSuiteTestCaseLinkTransferDatas.add(new TestSuiteTestCaseLinkTransferData(getTestSuite(),
								(TestSuiteTestCaseLink) item.getData()));
					}
				}
				event.data = testSuiteTestCaseLinkTransferDatas
						.toArray(new TestSuiteTestCaseLinkTransferData[testSuiteTestCaseLinkTransferDatas.size()]);
			}

			public void dragFinished(DragSourceEvent event) {
				testCaseTableViewer.refresh();
			}
		});
	}

	private void processTestSuteTestCaseLinkSelected() {
		if (testCaseTableViewer.getSelection() == null) return;
		if (!(testCaseTableViewer.getSelection() instanceof IStructuredSelection)) return;

		IStructuredSelection selection = (IStructuredSelection) testCaseTableViewer.getSelection();

		testCaseVariableTableViewer.cancelEditing();
		testCaseVariableTable.clearAll();

		testDataTreeViewer.cancelEditing();
		testDataTreeViewer.getTree().clearAll(true);

		if (selection.size() == 1) {
			TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) selection.getFirstElement();
			try {
				TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(
						testCaseLink.getTestCaseId());
				if (testCaseEntity != null) {
					testDataTreeViewer.setInput(testCaseLink.getTestDataLinks());
					testCaseVariableTableViewer.setInput(testCaseLink.getVariableLinks());
				} else {
					testDataTreeViewer.setInput(null);
					testCaseVariableTableViewer.setInput(null);
					return;
				}

			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}

		} else {
			testDataTreeViewer.setInput(new TestDataLinkTreeNode[0]);
			testCaseVariableTableViewer.setInput(Collections.EMPTY_LIST);
		}
	}

	private void createCompositeTestData() {
		compositeTestData = new Composite(compositeBindingChild, SWT.NONE);
		compositeTestData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeTestData.setBackground(ColorUtil.getCompositeBackgroundColor());

		GridLayout gl_compositeTestData = new GridLayout(1, false);
		gl_compositeTestData.marginWidth = 0;
		gl_compositeTestData.marginHeight = 0;
		compositeTestData.setLayout(gl_compositeTestData);

		Composite compositeTestDataHeader = new Composite(compositeTestData, SWT.NONE);
		GridLayout gl_compositeTestDataHeader = new GridLayout(2, false);
		gl_compositeTestDataHeader.marginWidth = 0;
		gl_compositeTestDataHeader.marginHeight = 0;
		compositeTestDataHeader.setLayout(gl_compositeTestDataHeader);
		compositeTestDataHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeTestDataHeader.setBounds(0, 0, 64, 64);

		btnExpandCompositeTestData = new Button(compositeTestDataHeader, SWT.NONE);
		GridData gd_btnExpandCompositeTestData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExpandCompositeTestData.heightHint = 18;
		gd_btnExpandCompositeTestData.widthHint = 18;
		btnExpandCompositeTestData.setLayoutData(gd_btnExpandCompositeTestData);
		redrawBtnExpandCompositeTestData();

		Label lblTestData = new Label(compositeTestDataHeader, SWT.NONE);
		lblTestData.setText(StringConstants.PA_LBL_TEST_DATA);
		lblTestData.setFont(JFaceResources.getFontRegistry().getBold(""));

		compositeTestDataDetails = new Composite(compositeTestData, SWT.NONE);
		compositeTestDataDetails.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeTestDataDetails = new GridLayout(1, false);
		gl_compositeTestDataDetails.marginHeight = 0;
		gl_compositeTestDataDetails.marginWidth = 0;
		compositeTestDataDetails.setLayout(gl_compositeTestDataDetails);

		Composite compositeTestDataButton = new Composite(compositeTestDataDetails, SWT.NONE);
		compositeTestDataButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeTestDataButton = new GridLayout(1, false);
		gl_compositeTestDataButton.marginWidth = 0;
		gl_compositeTestDataButton.marginHeight = 0;
		compositeTestDataButton.setLayout(gl_compositeTestDataButton);

		ToolBar testDataToolBar = new ToolBar(compositeTestDataButton, SWT.FLAT | SWT.RIGHT);
		ToolItem tltmAddTestData = new ToolItem(testDataToolBar, SWT.DROP_DOWN);
		tltmAddTestData.setText(TestDataToolItemConstants.ADD);
		tltmAddTestData.setToolTipText(TestDataToolItemConstants.ADD);
		tltmAddTestData.setImage(ImageConstants.IMG_24_ADD);

		ToolItem tltmRemoveTestData = new ToolItem(testDataToolBar, SWT.NONE);
		tltmRemoveTestData.setText(TestDataToolItemConstants.REMOVE);
		tltmRemoveTestData.setToolTipText(TestDataToolItemConstants.REMOVE);
		tltmRemoveTestData.setImage(ImageConstants.IMG_24_REMOVE);

		ToolItem tltmUpTestData = new ToolItem(testDataToolBar, SWT.NONE);
		tltmUpTestData.setText(TestDataToolItemConstants.UP);
		tltmUpTestData.setToolTipText(TestDataToolItemConstants.UP);
		tltmUpTestData.setImage(ImageConstants.IMG_24_UP);

		ToolItem tltmDownTestData = new ToolItem(testDataToolBar, SWT.NONE);
		tltmDownTestData.setText(TestDataToolItemConstants.DOWN);
		tltmDownTestData.setToolTipText(TestDataToolItemConstants.DOWN);
		tltmDownTestData.setImage(ImageConstants.IMG_24_DOWN);

		// ToolItem tltmMapTestData = new ToolItem(testDataToolBar, SWT.NONE);
		// tltmMapTestData.setText(TestDataToolItemConstants.MAP);

		ToolItem tltmMapAllTestData = new ToolItem(testDataToolBar, SWT.NONE);
		tltmMapAllTestData.setText(TestDataToolItemConstants.MAPALL);
		tltmMapAllTestData.setToolTipText(TestDataToolItemConstants.MAPALL);
		tltmMapAllTestData.setImage(ImageConstants.IMG_24_MAP_ALL);

		compositeTestDataTreeTable = new Composite(compositeTestDataDetails, SWT.NONE);
		compositeTestDataTreeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_compositeTestDataTreeTable = new GridLayout(1, false);
		gl_compositeTestDataTreeTable.marginBottom = 5;
		gl_compositeTestDataTreeTable.marginHeight = 0;
		compositeTestDataTreeTable.setLayout(gl_compositeTestDataTreeTable);

		testDataTreeViewer = new TreeViewer(compositeTestDataTreeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Tree testDataTable = testDataTreeViewer.getTree();
		gdTestDataTable = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		testDataTable.setLayoutData(gdTestDataTable);
		testDataTable.setLinesVisible(true);
		testDataTable.setHeaderVisible(true);

		treeViewerColumn = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
		trclmnNo = treeViewerColumn.getColumn();
		trclmnNo.setWidth(60);
		trclmnNo.setText(StringConstants.PA_TREE_VIEWER_COL_NO);

		TreeViewerColumn testDataTableViewerColumnID = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
		TreeColumn tblclmnTestDataId = testDataTableViewerColumnID.getColumn();
		tblclmnTestDataId.setWidth(300);
		tblclmnTestDataId.setText(StringConstants.PA_TREE_VIEWER_COL_ID);
		testDataTableViewerColumnID.setEditingSupport(new TestDataIDColumnEditingSupport(testDataTreeViewer, this));

		TreeViewerColumn testDataTableViewerColumnIteration = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
		TreeColumn tblclmnTestDataIteration = testDataTableViewerColumnIteration.getColumn();
		tblclmnTestDataIteration.setWidth(100);
		tblclmnTestDataIteration.setText(StringConstants.PA_TREE_VIEWER_COL_DATA_ITERATION);
		testDataTableViewerColumnIteration.setEditingSupport(new TestDataIterationColumnEditingSupport(
				testDataTreeViewer, this));

		TreeViewerColumn testDataTableViewerColumnCombination = new TreeViewerColumn(testDataTreeViewer, SWT.NONE);
		TreeColumn tblclmnCombination = testDataTableViewerColumnCombination.getColumn();
		tblclmnCombination.setWidth(100);
		tblclmnCombination.setText(StringConstants.PA_TREE_VIEWER_COL_TYPE);
		testDataTableViewerColumnCombination.setEditingSupport(new TestDataCombinationColumnEditingSupport(
				testDataTreeViewer, this));

		testDataTreeViewer.setLabelProvider(new TestDataTreeLabelProvider());
		testDataTreeViewer.setContentProvider(new TestDataTreeContentProvider());

		TestDataToolItemListener testDataToolItemListener = new TestDataToolItemListener(testDataTreeViewer, this);
		tltmAddTestData.addSelectionListener(testDataToolItemListener);
		tltmRemoveTestData.addSelectionListener(testDataToolItemListener);
		tltmUpTestData.addSelectionListener(testDataToolItemListener);
		tltmDownTestData.addSelectionListener(testDataToolItemListener);
		// tltmMapTestData.addSelectionListener(testDataToolItemListener);
		tltmMapAllTestData.addSelectionListener(testDataToolItemListener);
	}

	private void createCompositeVariableBinding() {

		compositeVariable = new Composite(compositeBindingChild, SWT.NONE);
		compositeVariable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeVariable.setLayout(new GridLayout(1, false));
		compositeVariable.setBackground(ColorUtil.getCompositeBackgroundColor());

		compositeVariableHeader = new Composite(compositeVariable, SWT.NONE);
		compositeVariableHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout gl_compositeVariableHeader = new GridLayout(1, false);
		gl_compositeVariableHeader.marginWidth = 0;
		gl_compositeVariableHeader.marginHeight = 0;
		compositeVariableHeader.setLayout(gl_compositeVariableHeader);

		Label lblCompositeVariableName = new Label(compositeVariableHeader, SWT.NONE);
		lblCompositeVariableName.setFont(JFaceResources.getFontRegistry().getBold(""));
		lblCompositeVariableName.setText(StringConstants.PA_LBL_VAR_BINDING);

		testCaseVariableTableViewer = new TableViewer(compositeVariable, SWT.BORDER | SWT.FULL_SELECTION);
		testCaseVariableTable = testCaseVariableTableViewer.getTable();
		testCaseVariableTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		testCaseVariableTable.setHeaderVisible(true);
		testCaseVariableTable.setLinesVisible(true);

		variableOrderColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnVariableNo = variableOrderColumnViewer.getColumn();
		tblclmnVariableNo.setWidth(40);
		tblclmnVariableNo.setText(StringConstants.PA_TREE_VIEWER_COL_NO);

		variableNameColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnVariableName = variableNameColumnViewer.getColumn();
		tblclmnVariableName.setWidth(100);
		tblclmnVariableName.setText(StringConstants.PA_TREE_VIEWER_COL_NAME);

		variableDefaultValueColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnVaribaleDefaultValue = variableDefaultValueColumnViewer.getColumn();
		tblclmnVaribaleDefaultValue.setWidth(100);
		tblclmnVaribaleDefaultValue.setText(StringConstants.PA_TREE_VIEWER_COL_DEFAULT_VAL);

		variableTypeColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnVariableType = variableTypeColumnViewer.getColumn();
		tblclmnVariableType.setWidth(110);
		tblclmnVariableType.setText(StringConstants.PA_TREE_VIEWER_COL_TYPE);
		variableTypeColumnViewer.setEditingSupport(new VariableTypeEditingSupport(testCaseVariableTableViewer, this));

		variableTestDataLinkIDViewerColumn = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnTestDataLinkId = variableTestDataLinkIDViewerColumn.getColumn();
		tblclmnTestDataLinkId.setWidth(200);
		tblclmnTestDataLinkId.setText(StringConstants.PA_TREE_VIEWER_COL_TEST_DATA);
		variableTestDataLinkIDViewerColumn.setEditingSupport(new VariableTestDataLinkColumnEditingSupport(
				testCaseVariableTableViewer, this));

		variableValueColumnViewer = new TableViewerColumn(testCaseVariableTableViewer, SWT.NONE);
		tblclmnVariableValue = variableValueColumnViewer.getColumn();
		tblclmnVariableValue.setWidth(150);
		tblclmnVariableValue.setText(StringConstants.PA_TREE_VIEWER_COL_VALUE);
		variableValueColumnViewer.setEditingSupport(new VariableValueEditingSupport(testCaseVariableTableViewer, this));

		testCaseVariableTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		testCaseVariableTableViewer.setLabelProvider(new VariableTableLabelProvider(testCaseVariableTableViewer, this));
	}

	private void createCompositeTestDataAndVariable() {
		compositeBinding = new Composite(sashForm, SWT.NONE);
		GridLayout gl_compositeBinding = new GridLayout(1, false);
		gl_compositeBinding.marginWidth = 0;
		gl_compositeBinding.marginHeight = 0;
		compositeBinding.setLayout(gl_compositeBinding);
		compositeBinding.setBackground(ColorUtil.getCompositeBackgroundColor());

		compositeBindingChild = new Composite(compositeBinding, SWT.NONE);
		compositeBindingChild.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeBindingChild.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

		GridLayout gl_compositeBindingChild = new GridLayout(1, false);
		gl_compositeBindingChild.marginHeight = 0;
		gl_compositeBindingChild.marginWidth = 0;
		compositeBindingChild.setLayout(gl_compositeBindingChild);

		createCompositeTestData();
		createCompositeVariableBinding();
	}

	private void createCompositeTestCase() {
		compositeTablePart = new ScrolledComposite(compositeMain, SWT.V_SCROLL);
		compositeTablePart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeTablePart.setBackground(ColorUtil.getCompositeBackgroundColor());

		sashForm = new SashForm(compositeTablePart, SWT.NONE);
		sashForm.setSashWidth(5);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

		createCompositeTestCaseButtons();
		createCompositeTestCaseSearch();
		createCompositeTestCaseContent();

		compositeTablePart.setContent(sashForm);
		compositeTablePart.setExpandHorizontal(true);
		compositeTablePart.setExpandVertical(true);
		compositeTablePart.setBackgroundMode(SWT.INHERIT_DEFAULT);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(TestSuiteEventConstants.TESTSUITE_UPDATE_DIRTY)) {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (object != null && object instanceof TestCaseTableViewer) {
				TestCaseTableViewer viewer = (TestCaseTableViewer) object;
				if (viewer == testCaseTableViewer) {
					setDirty(true);
				}
			}
		} else if (event.getTopic().equals(TestSuiteEventConstants.TESTSUITE_UPDATE_IS_RUN_COLUMN_HEADER)) {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (object != null && object instanceof TestCaseTableViewer) {
				TestCaseTableViewer viewer = (TestCaseTableViewer) object;
				if (viewer == testCaseTableViewer) {
					boolean isRunAll = testCaseTableViewer.getIsRunAll();
					Image isRunColumnImageHeader;
					if (isRunAll) {
						isRunColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_CHECKED;
					} else {
						isRunColumnImageHeader = ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
					}
					tblclmnIsRun.setImage(isRunColumnImageHeader);
				}
			}
		} else if (event.getTopic().equals(EventConstants.TESTCASE_UPDATED)) {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (object != null && object instanceof Object[]) {
				try {
					String oldPk = (String) ((Object[]) object)[0];
					TestCaseEntity testCase = (TestCaseEntity) ((Object[]) object)[1];
					testCaseTableViewer.updateTestCaseProperties(oldPk, testCase);
					testCaseVariableTableViewer.refresh();
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
					refreshTestSuiteAfterTestDataChanged(oldTestDataId, newTestDataId);
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
		if (!isTestSuiteLoading) {
			mpart.setDirty(dirty);
			parentTestSuiteCompositePart.checkDirty();
		}
	}

	public void refreshTestSuiteAfterTestDataChanged(String oldTestDataId, String newTestDataId) {
		for (TestSuiteTestCaseLink testCaseLink : getTestSuite().getTestSuiteTestCaseLinks()) {
			for (TestCaseTestDataLink testDataLink : testCaseLink.getTestDataLinks()) {
				if (testDataLink.getTestDataId() == null || !(testDataLink.getTestDataId().equals(oldTestDataId)))
					continue;
				testDataLink.setTestDataId(newTestDataId);
			}

			if (getSelectedTestCaseLink() != null
					&& getSelectedTestCaseLink().getTestCaseId().equals(testCaseLink.getTestCaseId())) {
				testDataTreeViewer.cancelEditing();
				testCaseVariableTableViewer.cancelEditing();

				testDataTreeViewer.refresh();
				testCaseVariableTableViewer.refresh();
			}
		}
	}

	public String[] getTestDataColumnNames(String testDataId) {
		try {
			ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
			if (testDataId != null && !testDataId.isEmpty()) {
				TestData testData = TestDataFactory.findTestDataForExternalBundleCaller(testDataId,
						projectEntity.getFolderLocation());
				return testData.getColumnNames();
			}
		} catch (Exception e) {
			MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
					MessageFormat.format(StringConstants.PA_WARN_MSG_DATA_SRC_NOT_AVAILABLE, testDataId));
		}
		return null;
	}

	private TestSuiteEntity getTestSuite() {
		return parentTestSuiteCompositePart.getTestSuiteClone();
	}

	public TestSuiteTestCaseLink getSelectedTestCaseLink() {
		return testCaseTableViewer.getSelectedTestCaseLink();
	}

	public void refreshVariableTable() {
		testCaseVariableTableViewer.refresh();
	}

	public void refreshVariableLink(VariableLink link) {
		testCaseVariableTableViewer.update(link, null);
	}

	public TestDataTreeContentProvider getTestDataContentProvider() {
		return (TestDataTreeContentProvider) testDataTreeViewer.getContentProvider();
	}

	private boolean verifyRerunInputValue() {
		int rerunNumber;
		try {
			rerunNumber = Integer.valueOf(txtRerun.getText());
		} catch (NumberFormatException exception) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					MessageFormat.format(StringConstants.PA_ERROR_MSG_RERUN_NUMBER_X_INVALID, txtRerun.getText()));
			return false;
		}
		getTestSuite().setNumberOfRerun(rerunNumber);
		return true;
	}

	public boolean prepareForSaving() {
		if (!verifyRerunInputValue()) {
			return false;
		}
		testSuiteTestCaseSelectedIdx = testCaseTableViewer.getTable().getSelectionIndex();

		getTestSuite().getTestSuiteTestCaseLinks().clear();

		for (Object testCaseLink : testCaseTableViewer.getInput()) {
			getTestSuite().getTestSuiteTestCaseLinks().add((TestSuiteTestCaseLink) testCaseLink);
		}

		getTestSuite().setMailRecipient(
				TestSuiteController.getInstance().arrayMailRcpToString(listMailRcpViewer.getList().getItems()));
		return true;
	}

	public void afterSaving() {
		if (testCaseTableViewer == null) return;

		Table testCaseTable = testCaseTableViewer.getTable();
		if (testCaseTable == null || testCaseTable.isDisposed()) return;

		if (testSuiteTestCaseSelectedIdx >= 0
				&& testSuiteTestCaseSelectedIdx < testCaseTableViewer.getTable().getItemCount()) {
			TestSuiteTestCaseLink selectedTestCaseLink = testCaseTableViewer.getInput().get(
					testSuiteTestCaseSelectedIdx);
			IStructuredSelection selection = new StructuredSelection(Arrays.asList(selectedTestCaseLink));
			testCaseTableViewer.setSelection(selection);
		}
	}
}
