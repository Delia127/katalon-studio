package com.kms.katalon.platform.internal.testcase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestCaseIntegrationViewDescription.TestCaseIntegrationView;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.testcase.parts.integration.AbstractTestCaseIntegrationView;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationPlatformBuilder;
import com.kms.katalon.composer.testcase.parts.integration.TestCaseIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.TestCaseEntityImpl;

public class TestCaseIntegrationPlatformBuilderImpl implements TestCaseIntegrationPlatformBuilder {

    @Inject
    private IEclipseContext context;

    private TestCaseIntegrationViewBuilder getViewerBuilder(TestCaseIntegrationViewDescription pluginViewDescription) {
        String name = pluginViewDescription.getName();
        TestCaseIntegrationView testCaseIntegrationView = ContextInjectionFactory
                .make(pluginViewDescription.getTestCaseIntegrationView(), context);
        return new PluginInterationViewBuilder(name, testCaseIntegrationView);
    }

    @Override
    public List<TestCaseIntegrationViewBuilder> getBuilders() {
        com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                ProjectController.getInstance().getCurrentProject());
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(TestCaseIntegrationViewDescription.EXTENSION_POINT_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof TestCaseIntegrationViewDescription
                            && ((TestCaseIntegrationViewDescription) e.getImplementationClass()).isEnabled(project);
                })
                .map(e -> getViewerBuilder((TestCaseIntegrationViewDescription) e.getImplementationClass()))
                .collect(Collectors.toList());
    }

    private static class PluginInterationViewBuilder implements TestCaseIntegrationViewBuilder {

        private TestCaseIntegrationViewDescription description;

        private TestCaseIntegrationView testCaseIntegrationView;

        private String name;

        public PluginInterationViewBuilder(String name, TestCaseIntegrationView testCaseIntegrationView) {
            this.name = name;
            this.testCaseIntegrationView = testCaseIntegrationView;
        }

        @Override
        public AbstractTestCaseIntegrationView getIntegrationView(TestCaseEntity testCase, MPart mpart) {
            return new PluginIntegrationView(testCase, mpart, testCaseIntegrationView);
        }

        @Override
        public int preferredOrder() {
            return 0;
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

    private static class PluginIntegrationView extends AbstractTestCaseIntegrationView {

        private TestCaseIntegrationView integrationView;
        private PartActionServiceImpl partActionService;

        public PluginIntegrationView(TestCaseEntity testCaseEntity, MPart mpart,
                TestCaseIntegrationView integrationView) {
            super(testCaseEntity, mpart);
            this.integrationView = integrationView;
        }

        @Override
        public Composite createContainer(Composite parent) {
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new FillLayout());

            partActionService = new PartActionServiceImpl(testCaseEntity, mpart);
            integrationView.onCreateView(container, partActionService,
                    new TestCaseEntityImpl(testCaseEntity));

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
    }

    private static class PartActionServiceImpl implements PartActionService {

        private MPart mpart;

        public PartActionServiceImpl(TestCaseEntity testCaseEntity, MPart mpart) {
            this.mpart = mpart;
        }

        @Override
        public void markDirty() {
            mpart.setDirty(true);
        }

        @Override
        public boolean isDirty() {
            return mpart.isDirty();
        }
    }

}
