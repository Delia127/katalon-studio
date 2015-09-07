package com.kms.katalon.composer.keyword.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class RenameKeywordHandler {

	@Inject
	private IEventBroker eventBroker;

	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell parentShell;

	@PostConstruct
	public void registerEventHandler() {
		eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object != null && object instanceof KeywordTreeEntity) {
					execute((KeywordTreeEntity) object);
				}
			}
		});
	}

	private void execute(KeywordTreeEntity keywordTreeEntity) {
		try {
			if (keywordTreeEntity.getParent() instanceof PackageTreeEntity) {
				IFile keywordFile = (IFile) ((ICompilationUnit) keywordTreeEntity.getObject()).getResource();
				RenameSupport renameSupport = GroovyUtil.getRenameSupportForRenamingGroovyClass(
						((IPackageFragment) ((PackageTreeEntity) keywordTreeEntity.getParent()).getObject()),
						keywordFile);				
				if (renameSupport != null && renameSupport.preCheck().isOK()) {
					ProjectEntity project = ProjectController.getInstance().getCurrentProject();
					KeywordController.getInstance().removeMethodNodesCustomKeywordFile(keywordFile, project);

					IPackageFragment packageFragment = ((IPackageFragment) ((PackageTreeEntity) keywordTreeEntity
							.getParent()).getObject());

					List<String> oldClassNames = new ArrayList<String>();
					for (ICompilationUnit unit : packageFragment.getCompilationUnits()) {
						oldClassNames.add(unit.getElementName());
					}
					
					renameSupport.openDialog(parentShell);
					eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, keywordTreeEntity.getParent());

					KeywordController.getInstance().parseCustomKeywordInPackage(packageFragment, project);
				}
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}
}
