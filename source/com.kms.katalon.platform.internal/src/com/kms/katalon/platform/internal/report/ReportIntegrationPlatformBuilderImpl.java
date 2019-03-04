package com.kms.katalon.platform.internal.report;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.CellDecorator;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.TestCaseColumnDescription;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.TestCaseRecordIntegrationView;
import com.katalon.platform.api.extension.ReportIntegrationViewDescription.TestStepColumnDescription;
import com.katalon.platform.api.report.TestCaseRecord;
import com.katalon.platform.api.report.TestStepRecord;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.report.parts.integration.ReportTestCaseIntegrationViewBuilder;
import com.kms.katalon.composer.report.parts.integration.TestCaseIntegrationColumn;
import com.kms.katalon.composer.report.parts.integration.TestCaseLogDetailsIntegrationView;
import com.kms.katalon.composer.report.parts.integration.TestLogIntegrationColumn;
import com.kms.katalon.composer.report.platform.PlatformReportIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.ReportEntityImpl;
import com.kms.katalon.platform.internal.report.viewer.TestCaseCellDecorator;
import com.kms.katalon.platform.internal.report.viewer.TestStepCellDecorator;

public class ReportIntegrationPlatformBuilderImpl implements PlatformReportIntegrationViewBuilder {

    @Inject
    private IEclipseContext context;

    @Override
    public List<ReportTestCaseIntegrationViewBuilder> getIntegrationViews() {
        com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                ProjectController.getInstance().getCurrentProject());
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(ReportIntegrationViewDescription.EXTENSION_POINT_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof ReportIntegrationViewDescription
                            && ((ReportIntegrationViewDescription) e.getImplementationClass()).isEnabled(project);
                })
                .map(e -> getViewerBuilder((ReportIntegrationViewDescription) e.getImplementationClass()))
                .collect(Collectors.toList());
    }

    private ReportTestCaseIntegrationViewBuilder getViewerBuilder(ReportIntegrationViewDescription desc) {
        TestCaseColumnDescription testCaseColumnDesc = ContextInjectionFactory.make(desc.getTestCaseColumnClass(),
                context);
        TestStepColumnDescription testStepColumnDesc = ContextInjectionFactory.make(desc.getTestStepColumnClass(),
                context);
        TestCaseRecordIntegrationView testCaseViewDesc = ContextInjectionFactory.make(desc.getTestCaseRecordViewClass(),
                context);

        return new ReportTestCaseIntegrationViewBuilder() {
            @Override
            public String getName() {
                return testCaseViewDesc.getName();
            }

            @Override
            public boolean isIntegrationEnabled(ProjectEntity project) {
                return desc.isEnabled(new ProjectEntityImpl(project));
            }

            @Override
            public TestLogIntegrationColumn getTestLogIntegrationColumn(ReportEntity report,
                    TestSuiteLogRecord testSuiteLogRecord) {
                return new PluginTestLogIntegrationColumn(report, testSuiteLogRecord, testStepColumnDesc);
            }

            @Override
            public TestCaseIntegrationColumn getTestCaseIntegrationColumn(ReportEntity report,
                    TestSuiteLogRecord testSuiteLogRecord) {
                return new PluginTestCaseIntegrationColumn(report, testSuiteLogRecord, testCaseColumnDesc);
            }

            @Override
            public int getPreferredOrder() {
                return 0;
            }

            @Override
            public TestCaseLogDetailsIntegrationView getIntegrationDetails(ReportEntity report,
                    TestSuiteLogRecord testSuiteLogRecord) {
                return new PluginTestLogDetailsView(report, testSuiteLogRecord, testCaseViewDesc);
            }
        };
    }

    public class PluginTestCaseIntegrationColumn extends TestCaseIntegrationColumn {

        private TestCaseColumnDescription desc;

        private TestSuiteLogRecord testSuiteLogRecord;

        public PluginTestCaseIntegrationColumn(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord,
                TestCaseColumnDescription desc) {
            super(reportEntity, testSuiteLogRecord);
            this.testSuiteLogRecord = testSuiteLogRecord;
            this.desc = desc;
        }

        @Override
        public Image getProductImage() {
            return desc.getColumnImage(Display.getCurrent());
        }

        @Override
        public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
            TableViewerColumn tableViewerColumnIntegration = new TableViewerColumn((TableViewer) tableViewer, SWT.NONE);
            TableColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();
            ReportEntityImpl report = new ReportEntityImpl(reportEntity);
            CellDecorator<TestCaseRecord> cellDecorator = desc.onCreateLabelProvider(report,
                    new TestSuiteRecordImpl(reportEntity, testSuiteLogRecord));
            tableViewerColumnIntegration.setLabelProvider(new TestCaseCellDecorator(columnIndex, cellDecorator));
            tblclmnTCIntegration.setImage(getProductImage());
            return tableViewerColumnIntegration;
        }
    }

    public class PluginTestLogIntegrationColumn extends TestLogIntegrationColumn {

        private TestStepColumnDescription desc;

        private TreeViewerColumn tableViewerColumnIntegration;

        private TestStepCellDecorator labelProvider;

        public PluginTestLogIntegrationColumn(ReportEntity reportEntity, TestSuiteLogRecord suiteRecord,
                TestStepColumnDescription desc) {
            super(reportEntity, suiteRecord);
            this.desc = desc;
        }

        @Override
        public Image getProductImage() {
            return desc.getColumnImage(Display.getCurrent());
        }

        @Override
        public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
            super.changeTestCase(testCaseLogRecord);
            com.katalon.platform.api.model.ReportEntity report = new ReportEntityImpl(reportEntity);
            CellDecorator<TestStepRecord> cellDecorator = desc.onCreateLabelProvider(report,
                    new TestSuiteRecordImpl(reportEntity, getTestSuiteLogRecord()),
                    new TestCaseRecordImpl(getTestCaseLogRecord()));

            labelProvider.setCellDecorator(cellDecorator);
        }

        @Override
        public ViewerColumn createIntegrationColumn(ColumnViewer tableViewer, int columnIndex) {
            tableViewerColumnIntegration = new TreeViewerColumn((TreeViewer) tableViewer, SWT.NONE);
            TreeColumn tblclmnTCIntegration = tableViewerColumnIntegration.getColumn();

            labelProvider = new TestStepCellDecorator(columnIndex, null);
            tableViewerColumnIntegration.setLabelProvider(labelProvider);
            tblclmnTCIntegration.setImage(getProductImage());
            return tableViewerColumnIntegration;
        }
    }

    public class PluginTestLogDetailsView extends TestCaseLogDetailsIntegrationView {

        private TestCaseRecordIntegrationView desc;

        private Composite container;

        private Control control;

        public PluginTestLogDetailsView(ReportEntity reportEntity, TestSuiteLogRecord testSuiteLogRecord,
                TestCaseRecordIntegrationView desc) {
            super(reportEntity, testSuiteLogRecord);
            this.desc = desc;
        }

        @Override
        public Composite createContainer(Composite parent) {
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new FillLayout());
            this.container = container;
            return container;
        }

        @Override
        public void changeTestCase(TestCaseLogRecord testCaseLogRecord) {
            try {
                if (control != null) {
                    control.dispose();
                }
                control = desc.onCreateView(container, new TestSuiteRecordImpl(reportEntity, getTestSuiteLogRecord()),
                        new TestCaseRecordImpl(testCaseLogRecord));
                container.layout(true, true);
            } catch (PlatformException e) {
                LoggerSingleton.logError(e);
            }
        }

        @Override
        public void createTableContextMenu(Menu parentMenu, ISelection selection) {

        }

    }
}
