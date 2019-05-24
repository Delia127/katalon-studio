package com.kms.katalon.composer.testsuite.parts;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.katalon.platform.api.Extension;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.extension.DynamicQueryingTestSuiteDescription;
import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.control.StyledTextMessage;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.custom.AdvancedSearchDialog;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testsuite.providers.FilteredTestCaseLabelProvider;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.FilteringTestSuiteEntity;
import com.kms.katalon.execution.exception.ExecutionException;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.report.AnalyticsReportService;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.tracking.service.Trackings;

public class FilteringTestCaseView {

    private ParentTestSuiteCompositePart parentPart;

    private Composite container;

    private CTableViewer testCaseTableViewer;

    private Button btnPreview;

    private StyledText txtSearch;

    private Button btnViewHistory;

    private Label lblSummary;

    private AnalyticsReportService analyticsReportService = new AnalyticsReportService();

    private AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
            ProjectController.getInstance().getCurrentProject().getFolderLocation());

    private Button btnQueryBuilder;

    private FilteringTestSuitePart part;

    private Combo cbbExtensions;

    private DynamicQueryingTestSuiteDescription selectedExtensionDescription;

    private List<Extension> extensions;

    private Link lnkFindOnStore;

    public FilteringTestCaseView(ParentTestSuiteCompositePart parentPart, FilteringTestSuitePart part) {
        this.parentPart = parentPart;
        this.part = part;
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
        GridLayout glTestCasePreview = new GridLayout();
        glTestCasePreview.marginWidth = 0;
        glTestCasePreview.marginHeight = 0;
        cpsTestCasePreview.setLayout(glTestCasePreview);

        Composite cpsTestCaseSummary = new Composite(cpsTestCasePreview, SWT.NONE);
        cpsTestCaseSummary.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        cpsTestCaseSummary.setLayout(new GridLayout());

        lblSummary = new Label(cpsTestCaseSummary, SWT.NONE);

        Composite cpsTestCaseTalbe = new Composite(cpsTestCasePreview, SWT.NONE);
        cpsTestCaseTalbe.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createTestCaseFilteredPreviewTable(cpsTestCaseTalbe);

        return container;
    }

    private void createTestCaseFilteredPreviewTable(Composite cpsTestCasePreview) {
        testCaseTableViewer = new CTableViewer(cpsTestCasePreview, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table testCaseTable = testCaseTableViewer.getTable();
        testCaseTable.setHeaderVisible(true);
        testCaseTable.setLinesVisible(ControlUtils.shouldLineVisble(testCaseTable.getDisplay()));

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
        parent.setLayout(new GridLayout());
        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        ToolBar toolbar = toolBarManager.createControl(parent);
        ToolItem tltmAdvancedSearchGuide = new ToolItem(toolbar, SWT.NONE);
        tltmAdvancedSearchGuide.setText("Help");
        tltmAdvancedSearchGuide.setImage(ImageManager.getImage(IImageKeys.HELP_16));
        tltmAdvancedSearchGuide.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch("https://docs.katalon.com/katalon-studio/docs/advanced-search.html");
            }
        });

        Composite cpsSearchAndPreview = new Composite(parent, SWT.NONE);
        cpsSearchAndPreview.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gdSearchAndPreview = new GridLayout(2, false);
        gdSearchAndPreview.marginWidth = 0;
        gdSearchAndPreview.marginHeight = 0;
        cpsSearchAndPreview.setLayout(gdSearchAndPreview);

        Composite compositeTableSearch = new Composite(cpsSearchAndPreview, SWT.NONE);
        compositeTableSearch.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glCompositeTableSearch = new GridLayout(2, false);
        glCompositeTableSearch.marginWidth = 0;
        glCompositeTableSearch.marginHeight = 0;
        compositeTableSearch.setLayout(glCompositeTableSearch);

        Label lblFilteringPlugin = new Label(compositeTableSearch, SWT.NONE);
        lblFilteringPlugin.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblFilteringPlugin.setText("Query Provider");

        Composite compositeQueryProvider = new Composite(compositeTableSearch, SWT.NONE);
        compositeQueryProvider.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glCompositeQueryProvider = new GridLayout(1, false);
        glCompositeQueryProvider.marginWidth = 0;
        glCompositeQueryProvider.marginHeight = 0;
        compositeQueryProvider.setLayout(glCompositeQueryProvider);

        cbbExtensions = new Combo(compositeQueryProvider, SWT.READ_ONLY);
        GridData gdCbbPlugins = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdCbbPlugins.widthHint = 500;
        gdCbbPlugins.horizontalIndent = 10;
        cbbExtensions.setLayoutData(gdCbbPlugins);

        Label lblQuery = new Label(compositeTableSearch, SWT.NONE);
        lblQuery.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        lblQuery.setText("Query");

        Composite customSearchComposite = new Composite(compositeTableSearch, SWT.NONE);
        customSearchComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glCustomSearchComposite = new GridLayout(2, false);
        glCompositeTableSearch.marginWidth = glCustomSearchComposite.marginHeight = 0;
        customSearchComposite.setLayout(glCustomSearchComposite);

        txtSearch = new StyledText(customSearchComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData gdTxtInput = new GridData(SWT.FILL, SWT.TOP, true, false);
        gdTxtInput.heightHint = 80;
        gdTxtInput.horizontalIndent = 5;
        txtSearch.setLayoutData(gdTxtInput);

        StyledTextMessage styledTextMessage = new StyledTextMessage(txtSearch);
        styledTextMessage.setMessage("Type a query in the text box and then select Preview");

        Composite buttonsComposite = new Composite(customSearchComposite, SWT.NONE);
        GridLayout glButtonsComposite = new GridLayout();
        glButtonsComposite.marginWidth = glButtonsComposite.marginHeight = 0;
        buttonsComposite.setLayout(glButtonsComposite);
        buttonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        btnPreview = new Button(buttonsComposite, SWT.PUSH | SWT.FLAT);
        btnPreview.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        btnPreview.setText("Preview");
        btnPreview.setEnabled(false);

        btnQueryBuilder = new Button(buttonsComposite, SWT.PUSH | SWT.FLAT);
        btnQueryBuilder.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        btnQueryBuilder.setText("Query Builder");

        lnkFindOnStore = new Link(compositeTableSearch, SWT.NONE);
        lnkFindOnStore.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
        lnkFindOnStore.setText("Search function is provided by plugins. Find a plugin on <a>Katalon Store</a>.");
    }

    public void layout() {

    }

    public void beforeSaving() {
        FilteringTestSuiteEntity testSuiteClone = (FilteringTestSuiteEntity) parentPart.getTestSuiteClone();
        testSuiteClone.setFilteringText(txtSearch.getText());
        if (selectedExtensionDescription != null) {
            Extension selectedExtension = extensions.get(cbbExtensions.getSelectionIndex());
            testSuiteClone.setFilteringPlugin(selectedExtension.getPluginId());
            testSuiteClone.setFilteringExtension(selectedExtension.getExtensionId());
        }
    }

    public void afterSaving() {
    }

    public void initExpandedState() {

    }

    public void loadInput() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        extensions = part.getExtensionProvider().getAvailableExtensions(currentProject);

        if (extensions.isEmpty()) {
            cbbExtensions.setItems("No query provider available");
            cbbExtensions.select(0);
        } else {
            List<DynamicQueryingTestSuiteDescription> extensionDescriptions = extensions.stream()
                    .map(e -> part.getExtensionProvider().getDynamicQueryingDescription(e))
                    .collect(Collectors.toList());

            String[] extensionNames = extensionDescriptions.stream()
                    .map(desc -> desc.getQueryingType())
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            cbbExtensions.setItems(extensionNames);

            FilteringTestSuiteEntity testSuite = (FilteringTestSuiteEntity) part.getTestSuite();
            Extension selectedExtension = extensions.stream()
                    .filter(ext -> ext.getPluginId().equals(testSuite.getFilteringPlugin())
                            && ext.getExtensionId().equals(testSuite.getFilteringExtension()))
                    .findFirst()
                    .orElse(null);
            if (selectedExtension == null) {
                selectedExtension = part.getExtensionProvider().getSuggestedExtension(currentProject, testSuite);
            }
            int selectedIndex = Math.max(0, extensions.indexOf(selectedExtension));
            cbbExtensions.select(selectedIndex);
        }
        onCbbExtensionChangeItem();

        FilteringTestSuiteEntity testSuite = (FilteringTestSuiteEntity) parentPart.getTestSuiteClone();
        txtSearch.setText(StringUtils.defaultString(testSuite.getFilteringText()));

        testCaseTableViewer.getTable().setVisible(false);
        lblSummary.getParent().getParent().layout(true, true);

        showPreviewTestCases();
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
                    Trackings.trackOpenKAIntegration("dynamicQueryingTestSuite");
                } catch (IOException | GeneralSecurityException e1) {
                    LoggerSingleton.logError(e1);
                }
            }
        });

        btnQueryBuilder.addListener(SWT.MouseUp, new Listener() {
            private void openAdvancedSearchDialog() {
                try {
                    Shell shell = new Shell(container.getShell());
                    shell.setSize(0, 0);
                    Point pt = btnQueryBuilder.toDisplay(1, 1);
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

        cbbExtensions.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onCbbExtensionChangeItem();
            }
        });

        lnkFindOnStore.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch("https://store.katalon.com/search?search=dynamic+execution");
            }
        });
    }

    private void onCbbExtensionChangeItem() {
        if (extensions.isEmpty()) {
            return;
        }
        int selectedIndex = cbbExtensions.getSelectionIndex();
        selectedExtensionDescription = extensions.size() > 0
                ? (DynamicQueryingTestSuiteDescription) extensions.get(selectedIndex).getImplementationClass() : null;

        btnPreview.setEnabled(selectedExtensionDescription != null);

        parentPart.setDirty(true);
    }

    private void showPreviewTestCases() {
        try {
            if (selectedExtensionDescription == null) {
                return;
            }
            List<TestCaseEntity> filteredTestCases = part.getExtensionProvider().getFilteredTestCases(
                    ProjectController.getInstance().getCurrentProject(),
                    (FilteringTestSuiteEntity) parentPart.getTestSuiteClone(), selectedExtensionDescription,
                    txtSearch.getText());

            testCaseTableViewer.setInput(filteredTestCases);

            setInputForPreviewComposite(filteredTestCases);
        } catch (ResourceException | ExecutionException ex) {
            LoggerSingleton.logError(ex);
            testCaseTableViewer.getTable().setVisible(false);
            lblSummary.setText("No test case found");
            lblSummary.getParent().getParent().layout(true, true);
        }
    }

    private void setInputForPreviewComposite(List<TestCaseEntity> filteredTestCases) {
        if (filteredTestCases.isEmpty()) {
            testCaseTableViewer.getTable().setVisible(false);
            lblSummary.setText("No test case found");
        } else {
            testCaseTableViewer.getTable().setVisible(true);
            lblSummary.setText(String.format("Test cases found: %d", filteredTestCases.size()));
        }

        lblSummary.getParent().getParent().layout(true, true);
    }

    public Control getComponent() {
        return container;
    }
}
