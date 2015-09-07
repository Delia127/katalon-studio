package com.kms.katalon.composer.report.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.DateUtil;
import com.kms.katalon.composer.report.constants.ImageConstants;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.integration.ReportComposerIntegrationFactory;
import com.kms.katalon.composer.report.parts.integration.AbstractReportTestCaseIntegrationView;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.provider.ReportPartTestCaseLabelProvider;
import com.kms.katalon.composer.report.provider.ReportTestCaseTableViewer;
import com.kms.katalon.composer.report.provider.ReportTestCaseTableViewerFilter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class ReportPart implements EventHandler {

	@Inject
	IEventBroker eventBroker;

	private ReportEntity report;
	private StyledText txtTestSuiteId, txtHostName, txtOS, txtPlatform, txtStartTime, txtEndTime, txtRunTime;
	private StyledText txtTotalTestCase, txtTCPasses, txtTCFailures, txtTCIncompleted;
	private TestSuiteLogRecord testSuiteLogRecord;
	private ReportTestCaseTableViewer testCaseTableViewer;
	private Text txtTestCaseSearch;
	private CLabel lblTestCaseSearch;
	private ReportTestCaseTableViewerFilter testCaseTableFilter;
	private Button btnFilterTestCasePassed;
	private Button btnFilterTestCaseFailed;
	private Button btnFilterTestCaseError;

	private ReportPartTestLogView testLogView;

	private Map<String, AbstractReportTestCaseIntegrationView> integratingCompositeMap;

	private int selectedTestCaseRecordIndex;

	boolean isSearching;

	private Combo comboTestCaseIntegration;

	private StyledText txtExecutedMachine;

	@PostConstruct
	public void init(Composite parent, ReportEntity report) {
		testLogView = new ReportPartTestLogView(this);
		isSearching = false;
		registerListeners();
		createControls(parent);
		registerControlModifyListeners();
		updateInput(report);
	}

	private void registerControlModifyListeners() {
		testCaseTableViewer.addPostSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ILogRecord selectedLogRecord = (ILogRecord) getTestCaseSelectedLogRecord();

				if (selectedLogRecord == null) return;
				testLogView.updateSelectedTestCase(selectedLogRecord);
			}
		});

		btnFilterTestCasePassed.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				testCaseTableFilter.setShowPassed(btnFilterTestCasePassed.getSelection());
				testCaseTableViewer.refresh();
				testLogView.updateSelectedTestCase(getTestCaseSelectedLogRecord());
			}
		});

		btnFilterTestCaseFailed.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				testCaseTableFilter.setShowFailed(btnFilterTestCaseFailed.getSelection());
				testCaseTableViewer.refresh();
				testLogView.updateSelectedTestCase(getTestCaseSelectedLogRecord());
			}
		});

		btnFilterTestCaseError.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				testCaseTableFilter.setShowError(btnFilterTestCaseError.getSelection());
				testCaseTableViewer.refresh();
				testLogView.updateSelectedTestCase(getTestCaseSelectedLogRecord());
			}
		});

		txtTestCaseSearch.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					filterTestLogBySearchedText();
				}
			}
		});

		lblTestCaseSearch.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (isSearching) {
					txtTestCaseSearch.setText("");
				}

				filterTestLogBySearchedText();
			}
		});

		comboTestCaseIntegration.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				testCaseTableViewer.refresh();
			}
		});
		
		testCaseTableViewer.getTable().addListener(SWT.MouseDown, new Listener(){

	        @Override
	        public void handleEvent(Event event) {
	            if (event.button == 3) {
	            	createTestCaseTableContextMenuByIntegrationViews();
	            }

	        }

	    });
		testLogView.registerControlModifyListener();
	}
	
	private void createTestCaseTableContextMenuByIntegrationViews() {
		Table testCaseTable = testCaseTableViewer.getTable();
		if (testCaseTable.getMenu() != null) {
			testCaseTable.getMenu().dispose();
		}
		
		Menu contextMenu = new Menu(testCaseTable);
		testCaseTable.setMenu(contextMenu);
		for (AbstractReportTestCaseIntegrationView integrationView : integratingCompositeMap.values()) {
			integrationView.createTableContextMenu(contextMenu, testCaseTableViewer.getSelection());
		}
	}
	

	public void clearMouseDownListener(StyledText styleText) {
		while (styleText.getListeners(SWT.MouseDown).length > 1) {
			styleText.removeListener(SWT.MouseDown,
					styleText.getListeners(SWT.MouseDown)[styleText.getListeners(SWT.MouseDown).length - 1]);
		}
	}

	private void registerTxtTestSuiteClickListener() {
		StyleRange range = new StyleRange();
		range.start = 0;
		range.length = txtTestSuiteId.getText().length();
		range.underline = true;
		range.data = txtTestSuiteId.getText();
		range.underlineStyle = SWT.UNDERLINE_LINK;

		txtTestSuiteId.setStyleRanges(new StyleRange[] { range });

		txtTestSuiteId.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				try {
					int offset = txtTestSuiteId.getOffsetAtLocation(new Point(event.x, event.y));
					StyleRange style = txtTestSuiteId.getStyleRangeAtOffset(offset);
					if (style != null && style.underline && style.underlineStyle == SWT.UNDERLINE_LINK) {
						TestSuiteEntity testSuite = TestSuiteController.getInstance().getTestSuiteByDisplayId(
								(String) style.data, ProjectController.getInstance().getCurrentProject());
						if (testSuite != null) {
							EventBrokerSingleton.getInstance().getEventBroker()
									.post(EventConstants.TEST_SUITE_OPEN, testSuite);
						}
					}

				} catch (IllegalArgumentException e) {
					// no character under event.x, event.y
				} catch (Exception e) {
					MessageDialog.openWarning(null, "Warning", "Test suite not found.");
				}
			}
		});
	}

	public void updateInput(ReportEntity report) {
		try {
			this.report = report;
			
			if (report == null) return;
			
			this.testSuiteLogRecord = ReportUtil.generate(report.getLocation());
			
			if (testSuiteLogRecord == null) return;
			
			try {
				TestSuiteEntity testSuite = ReportController.getInstance().getTestSuiteByReport(report);

				clearMouseDownListener(txtTestSuiteId);

				if (testSuite != null) {
					txtTestSuiteId.setText(TestSuiteController.getInstance().getIdForDisplay(testSuite));
					registerTxtTestSuiteClickListener();
				} else {
					txtTestSuiteId.setText(testSuiteLogRecord.getSource());
				}
			} catch (Exception e) {
				txtTestSuiteId.setText(testSuiteLogRecord.getSource());
			}

			if (testSuiteLogRecord.getHostName() != null && !testSuiteLogRecord.getHostName().isEmpty()) {
				txtHostName.setText(testSuiteLogRecord.getHostName());
			} else {
				txtHostName.setText(ReportUtil.getHostName());
			}

			if (testSuiteLogRecord.getOs() != null && !testSuiteLogRecord.getOs().isEmpty()) {
				txtOS.setText(testSuiteLogRecord.getOs());
			} else {
				txtOS.setText(ReportUtil.getOs());
			}
			
			if (testSuiteLogRecord.getDeviceName() != null && !testSuiteLogRecord.getDeviceName().isEmpty()){
				txtExecutedMachine.setText(testSuiteLogRecord.getDeviceName());
			} 

			if (testSuiteLogRecord.getBrowser() != null && !testSuiteLogRecord.getBrowser().isEmpty()) {
				txtPlatform.setText(testSuiteLogRecord.getBrowser());
			} else {
				txtPlatform.setText(testSuiteLogRecord.getDevicePlatform());
			}

			int totalTestCases = testSuiteLogRecord.getTotalTestCases();
			int totalPassedTestCases = testSuiteLogRecord.getTotalPassedTestCases();
			int totalFailedTestCases = testSuiteLogRecord.getTotalFailedTestCases();
			int totalErrorTestCases = testSuiteLogRecord.getTotalErrorTestCases();
			txtTotalTestCase.setText(Integer.toString(totalTestCases));
			txtTCPasses.setText(Integer.toString(totalPassedTestCases));
			txtTCFailures.setText(Integer.toString(totalFailedTestCases));
			txtTCIncompleted.setText(Integer.toString(totalErrorTestCases));

			txtStartTime.setText(DateUtil.getDateTimeFormatted(testSuiteLogRecord.getStartTime()));
			txtEndTime.setText(DateUtil.getDateTimeFormatted(testSuiteLogRecord.getEndTime()));

			StyledString styleStringElapsed = new StyledString(DateUtil.getElapsedTime(
					testSuiteLogRecord.getStartTime(), testSuiteLogRecord.getEndTime()), StyledString.COUNTER_STYLER);
			txtRunTime.setText(styleStringElapsed.getString());
			txtRunTime.setStyleRanges(styleStringElapsed.getStyleRanges());

			integratingCompositeMap = new LinkedHashMap<String, AbstractReportTestCaseIntegrationView>();

			List<String> comboTestCaseIntegrationInput = new ArrayList<String>();
			for (Entry<String, ReportTestCaseIntegrationViewBuilder> builderEntry : ReportComposerIntegrationFactory
					.getInstance().getIntegrationViewMap().entrySet()) {
				integratingCompositeMap.put(builderEntry.getKey(),
						builderEntry.getValue().getIntegrationView(report, testSuiteLogRecord));
				comboTestCaseIntegrationInput.add(builderEntry.getKey());
			}

			comboTestCaseIntegration.setItems(comboTestCaseIntegrationInput
					.toArray(new String[comboTestCaseIntegrationInput.size()]));
			if (comboTestCaseIntegration.getItemCount() > 0) {
				comboTestCaseIntegration.select(0);
			}

			createTestCaseTableContextMenuByIntegrationViews();

			testCaseTableViewer.setInput(testSuiteLogRecord.getChildRecords());

			testLogView.loadTestCaseIntegrationToolbar(report, testSuiteLogRecord);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}



	public ILogRecord getTestCaseSelectedLogRecord() {
		StructuredSelection selection = (StructuredSelection) testCaseTableViewer.getSelection();
		if (selection == null || selection.size() != 1) return null;
		return (ILogRecord) selection.getFirstElement();
	}

	private void createCompositeTestCaseFilter(Composite compositeTestCaseTree) {
		Composite compositeTestCaseFilter = new Composite(compositeTestCaseTree, SWT.NONE);
		GridLayout gl_compositeTestCaseFilter = new GridLayout(4, false);
		gl_compositeTestCaseFilter.marginHeight = 0;
		compositeTestCaseFilter.setLayout(gl_compositeTestCaseFilter);
		compositeTestCaseFilter.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnFilterTestCasePassed = new Button(compositeTestCaseFilter, SWT.CHECK);
		btnFilterTestCasePassed.setText("Passed");
		btnFilterTestCasePassed.setImage(ImageConstants.IMG_16_PASSED);
		btnFilterTestCasePassed.setSelection(true);

		btnFilterTestCaseFailed = new Button(compositeTestCaseFilter, SWT.CHECK);
		btnFilterTestCaseFailed.setText("Failed");
		btnFilterTestCaseFailed.setImage(ImageConstants.IMG_16_FAILED);
		btnFilterTestCaseFailed.setSelection(true);

		btnFilterTestCaseError = new Button(compositeTestCaseFilter, SWT.CHECK);
		btnFilterTestCaseError.setText("Error");
		btnFilterTestCaseError.setImage(ImageConstants.IMG_16_ERROR);
		btnFilterTestCaseError.setSelection(true);

		Composite compositeTableTestCaseSearch = new Composite(compositeTestCaseFilter, SWT.BORDER);
		compositeTableTestCaseSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeTableTestCaseSearch.setBackground(ColorUtil.getWhiteBackGroundColor());
		GridLayout gl_compositeTableTestCaseSearch = new GridLayout(2, false);
		gl_compositeTableTestCaseSearch.marginWidth = 0;
		gl_compositeTableTestCaseSearch.marginHeight = 0;
		compositeTableTestCaseSearch.setLayout(gl_compositeTableTestCaseSearch);

		txtTestCaseSearch = new Text(compositeTableTestCaseSearch, SWT.NONE);
		txtTestCaseSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
		GridData gd_txtTestCaseSearch = new GridData(GridData.FILL_HORIZONTAL);
		gd_txtTestCaseSearch.grabExcessVerticalSpace = true;
		gd_txtTestCaseSearch.verticalAlignment = SWT.CENTER;
		txtTestCaseSearch.setLayoutData(gd_txtTestCaseSearch);

		Canvas canvasTestCaseSearch = new Canvas(compositeTableTestCaseSearch, SWT.NONE);
		canvasTestCaseSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
		lblTestCaseSearch = new CLabel(canvasTestCaseSearch, SWT.NONE);

		lblTestCaseSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

		updateStatusSearchLabel();
	}

	private void filterTestLogBySearchedText() {
		if (txtTestCaseSearch.getText().isEmpty()) {
			isSearching = false;
		} else {
			isSearching = true;
		}

		testCaseTableViewer.setSearchedString(txtTestCaseSearch.getText());
		testCaseTableViewer.refresh(true);
		updateStatusSearchLabel();
	}

	private void updateStatusSearchLabel() {
		if (isSearching) {
			lblTestCaseSearch
					.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_CLOSE_SEARCH);
			lblTestCaseSearch.setToolTipText(GlobalStringConstants.CLEAR);
		} else {
			lblTestCaseSearch
					.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_SEARCH);
			lblTestCaseSearch.setToolTipText(GlobalStringConstants.SEARCH);
		}
	}

	private void createCompositeTestCaseTableDetails(Composite compositeTestCaseTable) {
		Composite compositeTestCaseTableDetails = new Composite(compositeTestCaseTable, SWT.NONE);
		compositeTestCaseTableDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableColumnLayout tcl_compositeTestCaseTableDetails = new TableColumnLayout();
		compositeTestCaseTableDetails.setLayout(tcl_compositeTestCaseTableDetails);

		testCaseTableViewer = new ReportTestCaseTableViewer(compositeTestCaseTableDetails, SWT.FULL_SELECTION
				| SWT.MULTI);
		Table table = testCaseTableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnTCOrder = tableViewerColumnOrder.getColumn();
		tblclmnTCOrder.setText("No.");
		tableViewerColumnOrder.setLabelProvider(new ReportPartTestCaseLabelProvider(this));
		tcl_compositeTestCaseTableDetails.setColumnData(tblclmnTCOrder, new ColumnWeightData(0, 40));

		TableViewerColumn tableViewerColumnName = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnTCName = tableViewerColumnName.getColumn();
		tblclmnTCName.setText("Name");
		tableViewerColumnName.setLabelProvider(new ReportPartTestCaseLabelProvider(this));
		tcl_compositeTestCaseTableDetails.setColumnData(tblclmnTCName, new ColumnWeightData(80, 0));

		TableViewerColumn tableViewerColumnIntegration = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
		TableColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
		tcl_compositeTestCaseTableDetails.setColumnData(tblclmnTCIntegration, new ColumnWeightData(0, 40));
		tableViewerColumnIntegration.setLabelProvider(new ReportPartTestCaseLabelProvider(this));
		tblclmnTCIntegration.setImage(ImageConstants.IMG_16_INTEGRATION);

		testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		testCaseTableViewer.getTable().setToolTipText("");
		ColumnViewerToolTipSupport.enableFor(testCaseTableViewer);

		testCaseTableFilter = new ReportTestCaseTableViewerFilter();
		testCaseTableFilter.setShowPassed(btnFilterTestCasePassed.getSelection());
		testCaseTableFilter.setShowFailed(btnFilterTestCaseFailed.getSelection());
		testCaseTableFilter.setShowError(btnFilterTestCaseError.getSelection());

		testCaseTableViewer.addFilter(testCaseTableFilter);
	}

	private void createCompositeTestCaseTable(Composite sashFormSummary) {
		Composite compositeTestCaseTable = new Composite(sashFormSummary, SWT.BORDER);
		compositeTestCaseTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout glCompositeTestCaseTable = new GridLayout(1, false);
		glCompositeTestCaseTable.marginHeight = 0;
		glCompositeTestCaseTable.marginWidth = 0;
		compositeTestCaseTable.setLayout(glCompositeTestCaseTable);

		Composite compositeTestCaseTableHeader = new Composite(compositeTestCaseTable, SWT.NONE);
		GridLayout gl_compositeTestCaseTableHeader = new GridLayout(2, false);
		compositeTestCaseTableHeader.setLayout(gl_compositeTestCaseTableHeader);
		compositeTestCaseTableHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblTestCaseTable = new Label(compositeTestCaseTableHeader, SWT.NONE);
		lblTestCaseTable.setText("Test Cases Table");
		setLabelToBeBold(lblTestCaseTable);

		Composite compositeTestCaseIntegrationSelection = new Composite(compositeTestCaseTableHeader, SWT.NONE);
		GridLayout gl_compositeTestCaseIntegrationSelection = new GridLayout(2, false);
		gl_compositeTestCaseIntegrationSelection.marginWidth = 0;
		gl_compositeTestCaseIntegrationSelection.marginHeight = 0;
		compositeTestCaseIntegrationSelection.setLayout(gl_compositeTestCaseIntegrationSelection);
		compositeTestCaseIntegrationSelection.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

		Label lblTestCaseIntegration = new Label(compositeTestCaseIntegrationSelection, SWT.NONE);
		lblTestCaseIntegration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTestCaseIntegration.setText("Integration");
		setLabelToBeBold(lblTestCaseIntegration);

		comboTestCaseIntegration = new Combo(compositeTestCaseIntegrationSelection, SWT.READ_ONLY);
		comboTestCaseIntegration.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		createCompositeTestCaseFilter(compositeTestCaseTable);
		createCompositeTestCaseTableDetails(compositeTestCaseTable);
	}

	private void createCompositeSummary(Composite sashFormSummary) {
		Composite compositeSummary = new Composite(sashFormSummary, SWT.BORDER);
		compositeSummary.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		GridLayout gl_compositeSummary = new GridLayout(1, false);
		gl_compositeSummary.horizontalSpacing = 0;
		gl_compositeSummary.verticalSpacing = 0;
		gl_compositeSummary.marginHeight = 0;
		gl_compositeSummary.marginWidth = 0;
		compositeSummary.setLayout(gl_compositeSummary);

		Composite compositeSummaryHeader = new Composite(compositeSummary, SWT.NONE);
		compositeSummaryHeader.setLayout(new GridLayout(1, false));
		GridData gd_compositeSummaryHeader = new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1);
		gd_compositeSummaryHeader.heightHint = 30;
		compositeSummaryHeader.setLayoutData(gd_compositeSummaryHeader);

		Label lblSummary = new Label(compositeSummaryHeader, SWT.NONE);
		lblSummary.setText("Summary");
		setLabelToBeBold(lblSummary);

		Composite compositeSummaryDetails = new Composite(compositeSummary, SWT.NONE);
		compositeSummaryDetails.setBackground(ColorUtil.getWhiteBackGroundColor());
		GridLayout gl_compositeSummaryDetails = new GridLayout(4, false);
		gl_compositeSummaryDetails.verticalSpacing = 7;
		gl_compositeSummaryDetails.horizontalSpacing = 15;
		compositeSummaryDetails.setLayout(gl_compositeSummaryDetails);
		compositeSummaryDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Label lblTestSuiteId = new Label(compositeSummaryDetails, SWT.NONE);
		lblTestSuiteId.setText("Test Suite ID");
		setLabelToBeBold(lblTestSuiteId);

		txtTestSuiteId = new StyledText(compositeSummaryDetails, SWT.NONE);
		txtTestSuiteId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		Label lblHostName = new Label(compositeSummaryDetails, SWT.NONE);
		lblHostName.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblHostName.setText("Host Name");
		setLabelToBeBold(lblHostName);

		txtHostName = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtHostName.setEditable(false);
		txtHostName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblOS = new Label(compositeSummaryDetails, SWT.NONE);
		lblOS.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblOS.setText("OS");
		setLabelToBeBold(lblOS);

		txtOS = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtOS.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblExecutedMachine = new Label(compositeSummaryDetails, SWT.NONE);
		lblExecutedMachine.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblExecutedMachine.setText("Machine Ex");
		setLabelToBeBold(lblExecutedMachine);

		txtExecutedMachine = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtExecutedMachine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblPlatform = new Label(compositeSummaryDetails, SWT.NONE);
		lblPlatform.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblPlatform.setText("Platform");
		setLabelToBeBold(lblPlatform);

		txtPlatform = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtPlatform.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblStart = new Label(compositeSummaryDetails, SWT.NONE);
		lblStart.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblStart.setText("Start");
		setLabelToBeBold(lblStart);

		txtStartTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtStartTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblEnd = new Label(compositeSummaryDetails, SWT.NONE);
		lblEnd.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblEnd.setText("End");
		setLabelToBeBold(lblEnd);

		txtEndTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtEndTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblRuntime = new Label(compositeSummaryDetails, SWT.NONE);
		lblRuntime.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblRuntime.setText("Elapsed");
		setLabelToBeBold(lblRuntime);

		txtRunTime = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtRunTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(compositeSummaryDetails, SWT.NONE);
		new Label(compositeSummaryDetails, SWT.NONE);

		Label lblTotalTC = new Label(compositeSummaryDetails, SWT.NONE);
		lblTotalTC.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblTotalTC.setText("Total TC");
		setLabelToBeBold(lblTotalTC);

		txtTotalTestCase = new StyledText(compositeSummaryDetails, SWT.READ_ONLY);
		txtTotalTestCase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(compositeSummaryDetails, SWT.NONE);
		new Label(compositeSummaryDetails, SWT.NONE);

		Composite composite = new Composite(compositeSummaryDetails, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1));
		GridLayout gl_composite = new GridLayout(6, false);
		gl_composite.horizontalSpacing = 50;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);

		Label lblPassed = new Label(composite, SWT.NONE);
		lblPassed.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblPassed.setText("Passed");
		setLabelToBeBold(lblPassed);
		lblPassed.setForeground(ColorUtil.getPassedLogBackgroundColor());

		txtTCPasses = new StyledText(composite, SWT.READ_ONLY);
		txtTCPasses.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		txtTCPasses.setForeground(ColorUtil.getPassedLogBackgroundColor());

		Label lblFailed = new Label(composite, SWT.NONE);
		lblFailed.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblFailed.setText("Failed");
		setLabelToBeBold(lblFailed);
		lblFailed.setForeground(ColorUtil.getFailedLogBackgroundColor());

		txtTCFailures = new StyledText(composite, SWT.READ_ONLY);
		txtTCFailures.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		txtTCFailures.setForeground(ColorUtil.getFailedLogBackgroundColor());

		Label lblIncompleted = new Label(composite, SWT.NONE);
		lblIncompleted.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblIncompleted.setText("Error");
		setLabelToBeBold(lblIncompleted);
		lblIncompleted.setForeground(ColorUtil.getWarningLogBackgroundColor());

		txtTCIncompleted = new StyledText(composite, SWT.READ_ONLY);
		txtTCIncompleted.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		txtTCIncompleted.setForeground(ColorUtil.getWarningLogBackgroundColor());
	}

	private void createControls(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		SashForm sashForm = new SashForm(composite, SWT.NONE);
		sashForm.setSashWidth(5);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite sashFormSummary = new Composite(sashForm, SWT.NONE);
		GridLayout gl_sashFormSummary = new GridLayout(1, false);
		gl_sashFormSummary.marginWidth = 0;
		gl_sashFormSummary.marginHeight = 0;
		sashFormSummary.setLayout(gl_sashFormSummary);

		createCompositeTestCaseTable(sashFormSummary);
		createCompositeSummary(sashFormSummary);

		SashForm sashFormDetails = new SashForm(sashForm, SWT.VERTICAL);
		sashFormDetails.setSashWidth(5);

		testLogView.createCompositeTestStepTree(sashFormDetails);
		testLogView.createCompositeSelectedTestLog(sashFormDetails);

		sashFormDetails.setWeights(new int[] { 6, 4 });

		sashForm.setWeights(new int[] { 43, 57 });
	}

	public void setLabelToBeBold(Label label) {
		label.setFont(JFaceResources.getFontRegistry().getBold(""));
	}

	private void registerListeners() {
		// TODO Auto-generated method stub
		eventBroker.subscribe(EventConstants.REPORT_UPDATED, this);
	}

	public MPart getMPart() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReportEntity getReport() {
		return report;
	}

	public void setDirty(boolean dirty) {
		getMPart().setDirty(true);
	}

	public String getIntegratedProductName() {
		return comboTestCaseIntegration.getText();
	}

	public Map<String, AbstractReportTestCaseIntegrationView> getIntegratingCompositeMap() {
		return integratingCompositeMap;
	}

	public void prepareBeforeReloading() {
		if (testCaseTableViewer == null) return;
		selectedTestCaseRecordIndex = testCaseTableViewer.getTable().getSelectionIndex();
	}

	public void prepareAfterReloading() {
		if (selectedTestCaseRecordIndex >= 0) {
			testCaseTableViewer.setSelection(new StructuredSelection(
					Arrays.asList(testSuiteLogRecord.getChildRecords()[selectedTestCaseRecordIndex])));
		}
	}

	@PreDestroy
	public void preDestroy() {
		eventBroker.unsubscribe(this);
	}


	@Override
	public void handleEvent(org.osgi.service.event.Event event) {
		if (event.getTopic().equals(EventConstants.REPORT_UPDATED)) {
			try {
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				String updatedReportId = (String) object;
				if (updatedReportId == null) return;
				
				if (updatedReportId.equals(report.getId())) {
					prepareBeforeReloading();
					updateInput(report);
					prepareAfterReloading();
				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
			
		}
	}


}
