package com.kms.katalon.platform.internal.testsuite;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.katalon.platform.api.extension.TestSuiteIntegrationViewDescription;
import com.katalon.platform.api.extension.TestSuiteIntegrationViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestSuiteIntegrationViewDescription.TestSuiteIntegrationView;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.parts.integration.AbstractTestSuiteIntegrationView;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationPlatformBuilder;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.TestSuiteEntityImpl;

public class TestSuiteIntegrationPlatformBuilderImpl implements TestSuiteIntegrationPlatformBuilder {
    @Inject
    private IEclipseContext context;

    private TestSuiteIntegrationViewBuilder getViewerBuilder(
            TestSuiteIntegrationViewDescription pluginViewDescription) {
        String name = pluginViewDescription.getName();
        TestSuiteIntegrationView testCaseIntegrationView = ContextInjectionFactory
                .make(pluginViewDescription.getTestSuiteIntegrationView(), context);
        return new PluginInterationViewBuilder(name, testCaseIntegrationView);
    }

    @Override
    public List<TestSuiteIntegrationViewBuilder> getBuilders() {
        com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                ProjectController.getInstance().getCurrentProject());
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(TestSuiteIntegrationViewDescription.EXTENSION_POINT_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof TestSuiteIntegrationViewDescription
                            && ((TestSuiteIntegrationViewDescription) e.getImplementationClass()).isEnabled(project);
                })
                .map(e -> getViewerBuilder((TestSuiteIntegrationViewDescription) e.getImplementationClass()))
                .collect(Collectors.toList());
    }

    private static class PluginInterationViewBuilder implements TestSuiteIntegrationViewBuilder {

        private TestSuiteIntegrationViewDescription description;

        private TestSuiteIntegrationView testCaseIntegrationView;

        private String name;

        public PluginInterationViewBuilder(String name, TestSuiteIntegrationView testCaseIntegrationView) {
            this.name = name;
            this.testCaseIntegrationView = testCaseIntegrationView;
        }

        @Override
        public AbstractTestSuiteIntegrationView getIntegrationView(TestSuiteEntity testCase, MPart mpart,
                SavableCompositePart parentPart) {
            return new PluginIntegrationView(name, testCase, mpart, testCaseIntegrationView, parentPart);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isEnabled(ProjectEntity projectEntity) {
            com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                    ProjectController.getInstance().getCurrentProject());
            return description.isEnabled(project);
        }
    }

    private static class PluginIntegrationView extends AbstractTestSuiteIntegrationView {

        private TestSuiteIntegrationView integrationView;

        private PartActionServiceImpl partActionService;

        private SavableCompositePart parentPart;

        private String name;

        public PluginIntegrationView(String integrationName, TestSuiteEntity testSuiteEntity, MPart mpart,
                TestSuiteIntegrationView integrationView, SavableCompositePart parentPart) {
            super(testSuiteEntity, mpart);
            this.name = integrationName;
            this.integrationView = integrationView;
            this.parentPart = parentPart;
        }

        @Override
        public Composite createContainer(Composite parent) {
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new FillLayout());

            partActionService = new PartActionServiceImpl(testSuiteEntity, mpart, parentPart);
            try {
                integrationView.onCreateView(container, partActionService, new TestSuiteEntityImpl(testSuiteEntity));
            } catch (Exception e) {
                LogUtil.printAndLogError(e, "Unable to create integration view for: " + name);
            }

            return container;
        }

        @Override
        public boolean needsSaving() {
            return integrationView.needsSaving();
        }

        @Override
        public IntegratedEntity getEditingIntegrated() {
            Integration integration = integrationView.getIntegrationBeforeSaving();
            if (integration == null) {
                return null;
            }
            IntegratedEntity integratedEntity = new IntegratedEntity();
            integratedEntity.setProductName(integration.getName());
            integratedEntity.setType(IntegratedType.TESTCASE);
            integratedEntity.setProperties(integration.getProperties());
            return integratedEntity;
        }

        @Override
        public void onSaveSuccess(TestSuiteEntity testSuite) {
            this.testSuiteEntity = testSuite;
            integrationView.onSaveSuccess(new TestSuiteEntityImpl(testSuiteEntity));
        }

        @Override
        public void onSaveFailure(Exception failure) {
            integrationView.onSaveFailure(failure);
        }
    }

    private static class PartActionServiceImpl implements PartActionService {

        private MPart mpart;

        private SavableCompositePart parentPart;

        public PartActionServiceImpl(TestSuiteEntity testSuiteEntity, MPart mpart, SavableCompositePart parentPart) {
            this.mpart = mpart;
            this.parentPart = parentPart;
        }

        @Override
        public void markDirty() {
            parentPart.setDirty(true);
        }

        @Override
        public boolean isDirty() {
            return parentPart.isDirty();
        }
    }

}
