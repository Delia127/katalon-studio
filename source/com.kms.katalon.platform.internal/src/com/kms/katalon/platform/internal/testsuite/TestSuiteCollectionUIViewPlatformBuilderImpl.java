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

import com.katalon.platform.api.extension.TestSuiteCollectionUIViewDescription;
import com.katalon.platform.api.extension.TestSuiteCollectionUIViewDescription.PartActionService;
import com.katalon.platform.api.extension.TestSuiteCollectionUIViewDescription.TestSuiteCollectionUIView;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.testsuite.collection.part.AbstractTestSuiteCollectionUIDescriptionView;
import com.kms.katalon.composer.testsuite.collection.platform.PlatformTestSuiteCollectionUIViewBuilder;
import com.kms.katalon.composer.testsuite.collection.view.builder.TestSuiteCollectionUIViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;
import com.kms.katalon.platform.internal.entity.TestSuiteCollectionEntityImpl;

public class TestSuiteCollectionUIViewPlatformBuilderImpl implements PlatformTestSuiteCollectionUIViewBuilder {

    @Inject
    private IEclipseContext context;

    private TestSuiteCollectionUIViewBuilder getViewerBuilder(TestSuiteCollectionUIViewDescription pluginViewDescription) {
        String name = pluginViewDescription.getName();
        TestSuiteCollectionUIView testSuiteCollectionUIView = ContextInjectionFactory
                .make(pluginViewDescription.getTestSuiteCollectionUIView(), context);
        return new PluginTestSuiteCollectionUIViewBuilder(name, testSuiteCollectionUIView);
    }
	
	@Override
	public List<TestSuiteCollectionUIViewBuilder> getBuilders() {
		com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
                ProjectController.getInstance().getCurrentProject());
        return ApplicationManager.getInstance()
                .getExtensionManager()
                .getExtensions(TestSuiteCollectionUIViewDescription.EXTENSION_POINT_ID)
                .stream()
                .filter(e -> {
                    return e.getImplementationClass() instanceof TestSuiteCollectionUIViewDescription
                            && ((TestSuiteCollectionUIViewDescription) e.getImplementationClass()).isEnabled(project);
                })
                .map(e -> getViewerBuilder((TestSuiteCollectionUIViewDescription) e.getImplementationClass()))
                .collect(Collectors.toList());
	}
	
	private static class PluginTestSuiteCollectionUIViewBuilder implements TestSuiteCollectionUIViewBuilder {

		private TestSuiteCollectionUIViewDescription description;
		
		private TestSuiteCollectionUIView testSuiteCollectionUIView;
		
		private String name;
		
		public PluginTestSuiteCollectionUIViewBuilder(String name, TestSuiteCollectionUIView testSuiteCollectionUIView) {
			this.testSuiteCollectionUIView = testSuiteCollectionUIView;
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
		public AbstractTestSuiteCollectionUIDescriptionView getView(TestSuiteCollectionEntity testSuiteCollection, MPart mpart,
				MPart parentPart) {
			return new PluginTestSuiteCollectionUIView(name, testSuiteCollection, mpart, testSuiteCollectionUIView, parentPart);
		}
	}
	
	private static class PluginTestSuiteCollectionUIView extends AbstractTestSuiteCollectionUIDescriptionView {

		private TestSuiteCollectionUIView testSuiteCollectionUiView;
		
        private PartActionServiceImpl partActionService;

        private MPart parentPart;

        private String name;

		
		public PluginTestSuiteCollectionUIView(String name, TestSuiteCollectionEntity testSuiteCollectionEntity
				, MPart mpart, TestSuiteCollectionUIView testSuiteCollectionUiView, MPart parentPart) {
			super(testSuiteCollectionEntity, mpart);
			this.testSuiteCollectionUiView = testSuiteCollectionUiView;
			this.parentPart = parentPart;
			this.mpart = mpart;
		}

		@Override
		public Composite createContainer(Composite parent) {
			 Composite container = new Composite(parent, SWT.NONE);
	            container.setLayout(new FillLayout());

	            partActionService = new PartActionServiceImpl(testSuiteCollectionEntity, mpart, parentPart);
	            try {
	            	testSuiteCollectionUiView.onCreateView(container, partActionService
	            			, new TestSuiteCollectionEntityImpl(testSuiteCollectionEntity));
	            } catch (Exception e) {
	                LogUtil.printAndLogError(e, "Unable to create Test Suite Collection UI view for: " + name);
	            }

	            return container;
		}

		@Override
		public void postContainerCreated() {
			testSuiteCollectionUiView.onPostCreateView();
		}
	}
	
	private static class PartActionServiceImpl implements PartActionService {

        private MPart mpart;

        private MPart parentPart;

        public PartActionServiceImpl(TestSuiteCollectionEntity testSuiteCollectionEntity, MPart mpart, MPart parentPart) {
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
