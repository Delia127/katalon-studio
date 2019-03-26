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

import com.katalon.platform.api.extension.TestSuiteUIViewDescription;
import com.katalon.platform.api.extension.TestSuiteUIViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestSuiteUIViewDescription.TestSuiteUIView;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.parts.AbstractTestSuiteUIDescriptionView;
import com.kms.katalon.composer.testsuite.platform.PlatformTestSuiteUIViewBuilder;
import com.kms.katalon.composer.testsuite.view.builder.TestSuiteUIViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.TestSuiteEntityImpl;

public class TestSuiteUIViewPlatformBuilderImpl implements PlatformTestSuiteUIViewBuilder {

    @Inject
    private IEclipseContext context;

    private TestSuiteUIViewBuilder getViewerBuilder(TestSuiteUIViewDescription pluginViewDescription) {
        String name = pluginViewDescription.getName();
        TestSuiteUIView testSuiteUIView = ContextInjectionFactory
                .make(pluginViewDescription.getTestSuiteUIView(), context);
        return new PluginTestSuiteUIViewBuilder(name, testSuiteUIView);
    }
	
	
	@Override
	public List<TestSuiteUIViewBuilder> getBuilders() {
		com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                ProjectController.getInstance().getCurrentProject());
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(TestSuiteUIViewDescription.EXTENSION_POINT_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof TestSuiteUIViewDescription
                            && ((TestSuiteUIViewDescription) e.getImplementationClass()).isEnabled(project);
                })
                .map(e -> getViewerBuilder((TestSuiteUIViewDescription) e.getImplementationClass()))
                .collect(Collectors.toList());
	}
	
	private static class PluginTestSuiteUIViewBuilder implements TestSuiteUIViewBuilder {

		private TestSuiteUIViewDescription description;
		
		private TestSuiteUIView testSuiteUIView;
		
		private String name;
		
		public PluginTestSuiteUIViewBuilder(String name, TestSuiteUIView testSuiteUIView) {
			this.testSuiteUIView = testSuiteUIView;
			this.name = name;
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

		@Override
		public AbstractTestSuiteUIDescriptionView getView(TestSuiteEntity testSuite, MPart mpart,
				SavableCompositePart parentPart) {
			return new PluginTestSuiteUIView(name, testSuite, mpart, testSuiteUIView, parentPart);
		}		
	}
	
	private static class PluginTestSuiteUIView extends AbstractTestSuiteUIDescriptionView {

		private TestSuiteUIView testSuiteUiView;
		
        private PartActionServiceImpl partActionService;

        private SavableCompositePart parentPart;

        private String name;

		
		public PluginTestSuiteUIView(String name, TestSuiteEntity testSuiteEntity, MPart mpart, TestSuiteUIView testSuiteUIView, SavableCompositePart parentPart) {
			super(testSuiteEntity, mpart);			
		}

		@Override
		public Composite createContainer(Composite parent) {
			 Composite container = new Composite(parent, SWT.NONE);
	            container.setLayout(new FillLayout());

	            partActionService = new PartActionServiceImpl(testSuiteEntity, mpart, parentPart);
	            try {
	            	testSuiteUiView.onCreateView(container, partActionService, new TestSuiteEntityImpl(testSuiteEntity));
	            } catch (Exception e) {
	                LogUtil.printAndLogError(e, "Unable to create Test Suite UI view for: " + name);
	            }

	            return container;
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
