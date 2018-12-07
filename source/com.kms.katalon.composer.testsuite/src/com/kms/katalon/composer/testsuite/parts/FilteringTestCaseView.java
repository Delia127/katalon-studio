package com.kms.katalon.composer.testsuite.parts;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.util.EntityIndexingUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.custom.AdvancedSearchDialog;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.providers.FilteredTestCaseLabelProvider;
import com.kms.katalon.controller.FilterController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.report.AnalyticsReportService;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.tracking.service.Trackings;

public class FilteringTestCaseView {

    private ParentTestSuiteCompositePart parentPart;

    private Composite container;

    private CLabel lblSearch;

    private boolean isSearching = false;

    private CTableViewer testCaseTableViewer;

    private Button btnPreview;

    private Text txtSearch;

    private Button btnViewHistory;

    private AnalyticsReportService analyticsReportService = new AnalyticsReportService();

    private AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
            ProjectController.getInstance().getCurrentProject().getFolderLocation());

    private CLabel lblFilter;

    public FilteringTestCaseView(ParentTestSuiteCompositePart parentPart) {
        this.parentPart = parentPart;
    }

    public Composite createComponent(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setBackground(ColorUtil.getCompositeBackgroundColor());
        container.setLayout(new GridLayout(1, false));

        Composite kaComposite = new Composite(container, SWT.NONE);
        kaComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        kaComposite.setLayout(new FillLayout());

        btnViewHistory = new Button(kaComposite, SWT.NONE);
        btnViewHistory.setImage(ImageManager.getImage(IImageKeys.KATALON_ANALYTICS_16));
        btnViewHistory.setText(ComposerTestcaseMessageConstants.BTN_TESTCASEHISTORY);

        Composite cpsTestCaseSearch = new Composite(container, SWT.NONE);
        cpsTestCaseSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        cpsTestCaseSearch.setLayout(new FillLayout());
        createCompositeTestCaseSearch(cpsTestCaseSearch);

        Composite cpsTestCasePreview = new Composite(container, SWT.NONE);
        cpsTestCasePreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createTestCaseFilteredPreviewTable(cpsTestCasePreview);

        return container;
    }

    private void createTestCaseFilteredPreviewTable(Composite cpsTestCasePreview) {
        testCaseTableViewer = new CTableViewer(cpsTestCasePreview, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table testCaseTable = testCaseTableViewer.getTable();
        testCaseTable.setHeaderVisible(true);
        testCaseTable.setLinesVisible(true);

        TableViewerColumn tableViewerColumnOrder = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnOrder = tableViewerColumnOrder.getColumn();
        tblclmnOrder.setText("#");

        TableViewerColumn tableViewerColumnPK = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclId = tableViewerColumnPK.getColumn();
        tblclId.setText("Id");

        TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(testCaseTableViewer, SWT.NONE);
        TableColumn tblclmnDescription = tableViewerColumnDescription.getColumn();
        tblclmnDescription.setText("Description");

        // set layout of table composite
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnOrder, new ColumnWeightData(0, 40));
        tableLayout.setColumnData(tblclId, new ColumnWeightData(40, 100));
        tableLayout.setColumnData(tblclmnDescription, new ColumnWeightData(40, 100));

        cpsTestCasePreview.setLayout(tableLayout);

        testCaseTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        testCaseTableViewer.getTable().setToolTipText("");

        ColumnViewerToolTipSupport.enableFor(testCaseTableViewer, ToolTip.NO_RECREATE);

        tableViewerColumnOrder.setLabelProvider(
                new FilteredTestCaseLabelProvider(FilteredTestCaseLabelProvider.CLMN_NO_IDX, testCaseTableViewer));

        tableViewerColumnPK.setLabelProvider(
                new FilteredTestCaseLabelProvider(FilteredTestCaseLabelProvider.CLMN_ID_IDX, testCaseTableViewer));

        tableViewerColumnDescription.setLabelProvider(new FilteredTestCaseLabelProvider(
                FilteredTestCaseLabelProvider.CLMN_DESCRIPTION_IDX, testCaseTableViewer));
    }

    private void createCompositeTestCaseSearch(Composite parent) {
        Composite cpsSearchAndPreview = new Composite(parent, SWT.NONE);
        GridLayout gdSearchAndPreview = new GridLayout(2, false);
        gdSearchAndPreview.marginWidth = 0;
        gdSearchAndPreview.marginHeight = 0;
        cpsSearchAndPreview.setLayout(gdSearchAndPreview);

        Composite compositeTableSearch = new Composite(cpsSearchAndPreview, SWT.BORDER);
        compositeTableSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        compositeTableSearch.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glCompositeTableSearch = new GridLayout(4, false);
        glCompositeTableSearch.marginWidth = 0;
        glCompositeTableSearch.marginHeight = 0;
        compositeTableSearch.setLayout(glCompositeTableSearch);

        txtSearch = new Text(compositeTableSearch, SWT.NONE);
        txtSearch.setMessage(StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE);
        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtSearch.setLayoutData(gdTxtInput);

        Canvas canvasSearch = new Canvas(compositeTableSearch, SWT.NONE);
        canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));

        Label seperator = new Label(compositeTableSearch, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdSeperator = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdSeperator.heightHint = 22;
        seperator.setLayoutData(gdSeperator);

        // label Filter
        lblFilter = new CLabel(compositeTableSearch, SWT.NONE);
        lblFilter.setImage(ImageConstants.IMG_16_ADVANCED_SEARCH);
        lblFilter.setToolTipText(StringConstants.PA_IMAGE_TIP_ADVANCED_SEARCH);
        lblFilter.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

        btnPreview = new Button(cpsSearchAndPreview, SWT.PUSH | SWT.FLAT);
        btnPreview.setText("Preview");
    }

//    private void updateStatusSearchLabel() {
//        if (isSearching) {
//            lblSearch.setImage(ImageConstants.IMG_16_CLOSE_SEARCH);
//            lblSearch.setToolTipText(StringConstants.PA_IMAGE_TIP_CLOSE_SEARCH);
//        } else {
//            lblSearch.setImage(ImageConstants.IMG_16_SEARCH);
//            lblSearch.setToolTipText(StringConstants.PA_IMAGE_TIP_SEARCH);
//        }
//    }

    public void layout() {

    }

    public void beforeSaving() {
        ((FilteringTestSuiteEntity) parentPart.getTestSuiteClone()).setFilteringText(txtSearch.getText().trim());
    }

    public void afterSaving() {

    }

    public void initExpandedState() {

    }

    public void loadInput() {
        FilteringTestSuiteEntity testSuite = (FilteringTestSuiteEntity) parentPart.getTestSuiteClone();
        txtSearch.setText(StringUtils.defaultString(testSuite.getFilteringText()));
    }

    public void registerControlModifyListeners() {
        txtSearch.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                parentPart.setDirty(true);
            }
        });

        btnPreview.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showPreviewTestCases();
            }
        });

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    showPreviewTestCases();
                }
            }
        });

        btnViewHistory.addSelectionListener(new SelectionAdapter() {
            private String createPath(AnalyticsTeam team, AnalyticsProject project, String path, String tokenInfo) {
                String result = "";
                result = ComposerTestcaseMessageConstants.KA_HOMEPAGE + "teamId=" + team.getId() + "&projectId="
                        + project.getId() + "&type=TEST_SUITE" + "&path=" + UrlEncoder.encode(path) + "&token="
                        + tokenInfo;
                return result;

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (analyticsReportService.isIntegrationEnabled() && analyticsSettingStore.getProject() != null) {
                        Program.launch(createPath(analyticsSettingStore.getTeam(), analyticsSettingStore.getProject(),
                                parentPart.getOriginalTestSuite().getIdForDisplay(),
                                analyticsSettingStore.getToken(true)));
                    } else {
                        Program.launch(ComposerTestcaseMessageConstants.KA_WELCOME_PAGE);
                    }
                    Trackings.trackOpenKAIntegration("testSuite");
                } catch (IOException | GeneralSecurityException e1) {
                    LoggerSingleton.logError(e1);
                }
            }
        });

        lblFilter.addListener(SWT.MouseUp, new Listener() {
            private void openAdvancedSearchDialog() {
                try {
                    Shell shell = new Shell(container.getShell());
                    shell.setSize(0, 0);
                    Point pt = lblFilter.toDisplay(1, 1);
                    Point location = new Point(Math.max(0, pt.x - AdvancedSearchDialog.MIN_WIDTH), pt.y);
                    AdvancedSearchDialog dialog = new AdvancedSearchDialog(shell, txtSearch.getText(), location);
                    // set position for dialog
                    if (dialog.open() == Dialog.OK) {
                        txtSearch.setText(dialog.getOutput());
                        showPreviewTestCases();
                    }

                    shell.getSize();
                    shell.dispose();

                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                openAdvancedSearchDialog();
            }
        });
    }

    private void showPreviewTestCases() {
        try {
            EntityIndexingUtil indexer = EntityIndexingUtil
                    .getInstance(ProjectController.getInstance().getCurrentProject());
            List<String> testCaseIds = indexer.getIndexedEntityIds("tc");
            List<TestCaseEntity> testCaseEntities = testCaseIds.stream().map(id -> {
                try {
                    return TestCaseController.getInstance().getTestCaseByDisplayId(id);
                } catch (Exception e1) {
                    return null;
                }
            }).collect(Collectors.toList());

            testCaseTableViewer.setInput(FilterController.getInstance().filter(testCaseEntities, txtSearch.getText()));
        } catch (IOException ex) {

        }
    }

    public Control getComponent() {
        return container;
    }
}
