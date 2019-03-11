package com.kms.katalon.platform.internal.testobject;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.katalon.platform.api.extension.TestObjectIntegrationViewDescription;
import com.katalon.platform.api.extension.TestObjectIntegrationViewDescription.TestObjectIntegrationView;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.objectrepository.integration.AbstractTestObjectIntegrationView;
import com.kms.katalon.composer.objectrepository.integration.TestObjectIntegrationPlatformBuilder;
import com.kms.katalon.composer.objectrepository.integration.TestObjectIntegrationViewBuilder;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.platform.internal.entity.ProjectEntityImpl;

public class TestObjectIntegrationPlatformBuilderImpl implements TestObjectIntegrationPlatformBuilder {
	@Inject
	private IEclipseContext context;

	private TestObjectIntegrationViewBuilder getViewerBuilder(
			TestObjectIntegrationViewDescription pluginViewDescription) {
		String name = pluginViewDescription.getName();
		TestObjectIntegrationView testCaseIntegrationView = ContextInjectionFactory
				.make(pluginViewDescription.getTestObjectIntegrationView(), context);
		return new PluginIntegrationViewBuilder(name, testCaseIntegrationView);

	}

	@Override
	public List<TestObjectIntegrationViewBuilder> getBuilders() {

		com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
				ProjectController.getInstance().getCurrentProject());
		return ApplicationManager.getInstance().getExtensionManager()
				.getExtensions(TestObjectIntegrationViewDescription.EXTENSION_POINT_ID).stream().filter(e -> {
					return e.getImplementationClass() instanceof TestObjectIntegrationViewDescription
							&& ((TestObjectIntegrationViewDescription) e.getImplementationClass()).isEnabled(project);
				}).map(e -> getViewerBuilder((TestObjectIntegrationViewDescription) e.getImplementationClass()))
				.collect(Collectors.toList());
	}

	public class PluginIntegrationViewBuilder implements TestObjectIntegrationViewBuilder {

		private TestObjectIntegrationView testObjectIntegrationView;

		private TestObjectIntegrationViewDescription testObjectIntegrationViewDescription;

		private String name;

		public PluginIntegrationViewBuilder(String name, TestObjectIntegrationView testCaseIntegrationView) {
			this.name = name;
			this.testObjectIntegrationView = testCaseIntegrationView;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean isEnabled(ProjectEntity projectEntity) {
			com.katalon.platform.api.model.ProjectEntity project = new ProjectEntityImpl(
					ProjectController.getInstance().getCurrentProject());
			return testObjectIntegrationViewDescription.isEnabled(project);
		}

		@Override
		public AbstractTestObjectIntegrationView getIntegrationView(WebElementEntity testObject, MPart mpart,
				SavableCompositePart parentPart) {
			return new PluginIntegrationView(name, testObject, mpart, testObjectIntegrationView, parentPart);
		}

		@Override
		public int preferredOrder() {
			return 0;
		}
	}
	
	private class PluginIntegrationView extends AbstractTestObjectIntegrationView {

		public PluginIntegrationView(WebElementEntity testObject, MPart mpart) {
			super(testObject, mpart);
		}

		public PluginIntegrationView(String name, WebElementEntity testObject, MPart mpart,
				TestObjectIntegrationView testCaseIntegrationView, SavableCompositePart parentPart) {
			super(testObject, mpart);
		}

		@Override
		public Composite createContainer(Composite parent) {
			return null;
		}
		
	}

}
