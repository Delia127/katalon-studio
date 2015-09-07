package com.kms.katalon.composer.keyword.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.reorg.INewNameQuery;
import org.eclipse.jdt.internal.ui.refactoring.reorg.NewNameQueries;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class PastePackageHandler {
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell parentShell;

	@Inject
	private IEventBroker eventBroker;

	@PostConstruct
	private void registerEventHandler() {
		eventBroker.subscribe(EventConstants.EXPLORER_PASTE_SELECTED_ITEM, new EventHandler() {
			@Override
			public void handleEvent(Event event) {
				try {
					Object targetObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
					if (targetObject != null) {
						ITreeEntity targetTreeEntity = null;
						IPackageFragment targetPackageFragment = null;
						if (targetObject instanceof PackageTreeEntity) {
							targetPackageFragment = (IPackageFragment) ((PackageTreeEntity) targetObject).getObject();
							targetTreeEntity = (ITreeEntity) targetObject;
						} else if (targetObject instanceof ITreeEntity
								&& ((ITreeEntity) targetObject).getParent() instanceof PackageTreeEntity) {
							targetPackageFragment = (IPackageFragment) ((PackageTreeEntity) ((ITreeEntity) targetObject)
									.getParent()).getObject();
							targetTreeEntity = (ITreeEntity) ((ITreeEntity) targetObject).getParent();
						}
						if (targetPackageFragment != null) {
							Clipboard clipboard = new Clipboard(Display.getCurrent());

							ITreeEntity[] treeEntities = (ITreeEntity[]) clipboard.getContents(TreeEntityTransfer
									.getInstance());
							if (TransferMoveFlag.isMove()) {
								move(treeEntities, targetPackageFragment);
								GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
										.refreshLocal(IResource.DEPTH_INFINITE, null);
								for (ITreeEntity treeEntity : treeEntities) {
									eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
											treeEntity.getParent());
								}
							} else {
								copy(treeEntities, targetPackageFragment);
								GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject())
										.refreshLocal(IResource.DEPTH_INFINITE, null);
							}
							eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity);
							eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetTreeEntity);
						}
					}
				} catch (Exception ex) {
					LoggerSingleton.getInstance().getLogger().error(ex);
					MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE, 
							StringConstants.HAND_ERROR_MSG_UNABLE_TO_PASTE_DATA);
				}
			}
		});
	}

	private void copy(ITreeEntity[] treeEntities, IPackageFragment targetPackageFragment) throws Exception {
		try {
			for (ITreeEntity treeEntity : treeEntities) {
				if (treeEntity instanceof KeywordTreeEntity) {
					copyKeyword((IFile) ((KeywordTreeEntity) treeEntity).getObject(), targetPackageFragment, null);
				}
			}
		} catch (OperationCanceledException operationCanceledException) {
			return;
		}
	}

	private void move(ITreeEntity[] treeEntities, IPackageFragment targetPackageFragment) throws Exception {
		try {
			for (ITreeEntity treeEntity : treeEntities) {
				if (treeEntity instanceof KeywordTreeEntity) {
					moveKeyword((IFile) ((KeywordTreeEntity) treeEntity).getObject(), targetPackageFragment, null);
				}
			}
		} catch (OperationCanceledException operationCanceledException) {
			return;
		}
	}

	private void copyKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
			throws Exception {
		try {
			GroovyUtil.copyKeyword(keywordFile, targetPackageFragment, newName);
			String copiedKeywordFilePath = getPastedFilePath(keywordFile, targetPackageFragment, newName);
			eventBroker.post(EventConstants.EXPLORER_COPY_PASTED_SELECTED_ITEM, new Object[] {
					keywordFile.getProjectRelativePath().toString(), copiedKeywordFilePath });
		} catch (JavaModelException javaModelException) {
			if (javaModelException.getJavaModelStatus().getCode() == IJavaModelStatusConstants.NAME_COLLISION) {
				NewNameQueries newNameQueries = new NewNameQueries(parentShell);
				INewNameQuery newNameQuery = newNameQueries.createNewCompilationUnitNameQuery(
						(GroovyCompilationUnit) JavaCore.create(keywordFile),
						keywordFile.getName().replace("." + keywordFile.getFileExtension(), ""));
				copyKeyword(keywordFile, targetPackageFragment, newNameQuery.getNewName());
			}
		}
	}

	private void moveKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
			throws Exception {
		try {
			GroovyUtil.moveKeyword(keywordFile, targetPackageFragment, newName);
			String cutKeywordFilePath = getPastedFilePath(keywordFile, targetPackageFragment, newName);
			eventBroker.post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM, new Object[] {
					keywordFile.getProjectRelativePath().toString(), cutKeywordFilePath });
		} catch (JavaModelException javaModelException) {
			if (javaModelException.getJavaModelStatus().getCode() == IJavaModelStatusConstants.NAME_COLLISION) {
				NewNameQueries newNameQueries = new NewNameQueries(parentShell);
				INewNameQuery newNameQuery = newNameQueries.createNewCompilationUnitNameQuery(
						(GroovyCompilationUnit) JavaCore.create(keywordFile),
						keywordFile.getName().replace("." + keywordFile.getFileExtension(), ""));
				moveKeyword(keywordFile, targetPackageFragment, newNameQuery.getNewName());
			}
		}
	}
	
	/**
	 * Get new pasted relative Keyword file path to project
	 * @param keywordFile IFile
	 * @param targetPackageFragment IPackageFragment
	 * @param newName String
	 * @return Project relative path for Keyword file (String)
	 * */
	private String getPastedFilePath(IFile keywordFile, IPackageFragment targetPackageFragment, String newName) {
		String keywordRootPath = targetPackageFragment.getParent().getElementName() + IPath.SEPARATOR;
		String packageName = targetPackageFragment.getElementName();
		String packagePath = keywordRootPath
				+ (packageName.isEmpty() ? packageName : packageName.replaceAll("[.]", String.valueOf(IPath.SEPARATOR))
						+ IPath.SEPARATOR);
		String kwFileName = (newName != null) ? newName + GroovyConstants.GROOVY_FILE_EXTENSION : keywordFile.getName();
		String copiedKeywordFilePath = packagePath + kwFileName;
		return copiedKeywordFilePath;
	}
}
