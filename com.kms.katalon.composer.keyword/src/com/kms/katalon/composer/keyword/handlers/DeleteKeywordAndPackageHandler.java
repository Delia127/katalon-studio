package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("restriction")
public class DeleteKeywordAndPackageHandler {
	@Inject
	IEventBroker eventBroker;

	@Inject
	private EPartService partService;
	
	@PostConstruct
	private void registerEventHandler() {
		eventBroker.subscribe(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (object != null && (object instanceof KeywordTreeEntity || object instanceof PackageTreeEntity)) {
					excute((ITreeEntity) object);
				}
			}
		});
	}

	private void excute(ITreeEntity treeEntity) {
		try {
			if ((treeEntity.getObject() != null)
					&& (treeEntity.getObject() instanceof ICompilationUnit || treeEntity.getObject() instanceof IPackageFragment)) {

				if (treeEntity.getObject() instanceof IPackageFragment) {
					for (Object child : treeEntity.getChildren()) {
						excute((ITreeEntity) child);
					}
					IPackageFragment packageFragment = (IPackageFragment) treeEntity.getObject();
                    String parentPath = packageFragment.getParent().getElementName() + IPath.SEPARATOR;
					String packageName = packageFragment.getElementName().isEmpty() ? StringConstants.DEFAULT_PACKAGE_NAME
							: packageFragment.getElementName();
                    eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, parentPath + packageName);
                    packageFragment.delete(true, null);
				} else if (treeEntity.getObject() instanceof ICompilationUnit) {
					ICompilationUnit file = (ICompilationUnit) treeEntity.getObject();
					IFile iFile = (IFile) file.getResource();
					for (MPart part : partService.getParts()) {
						if (part.getObject() instanceof CompatibilityEditor) {
							CompatibilityEditor editor = (CompatibilityEditor) part.getObject();
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.closeEditor(editor.getEditor(), false);
						}
					}

					KeywordController.getInstance().removeMethodNodesCustomKeywordFile(iFile,
							ProjectController.getInstance().getCurrentProject());

					if (file.exists()) {
						if (file.isWorkingCopy()) {
							file = file.getPrimary();
						}
						file.delete(true, null);
					}

				}
				eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, new FolderTreeEntity(FolderController
						.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()), null));
			}
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
					StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_KEYWORD);
		}
	}
}
