package com.kms.katalon.composer.keyword.handlers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

@SuppressWarnings("restriction")
public class RenamePackageHandler {

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private EPartService partService;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventHandler() {
            @Override
            public void handleEvent(Event event) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof PackageTreeEntity) {
                    execute((PackageTreeEntity) object);
                }
            }
        });
    }

    private void execute(PackageTreeEntity packageTreeEntity) {
        try {
        	IPackageFragment packageFragment = (IPackageFragment) packageTreeEntity.getObject();
        	
        	ITreeEntity parentTreeEntity = packageTreeEntity.getParent();
        	IFolder parentPackageFragmentFolder = (IFolder) packageFragment.getParent().getResource();
        	
        	List<String> childrenEntityPathBeforeRenaming = new ArrayList<String>();
        	for (Object object : parentTreeEntity.getChildren()) {
        		if (!packageTreeEntity.equals(object)) {
        			String treeEntityPath = "";
	        		if (object instanceof PackageTreeEntity) {
	        			IPackageFragment childPackageFragment = (IPackageFragment) ((PackageTreeEntity) object).getObject();
	        			treeEntityPath = childPackageFragment.getResource().getFullPath().toString();
	        		}
	        		childrenEntityPathBeforeRenaming.add(treeEntityPath);
        		}
        	}
        	String oldPackagePath = parentPackageFragmentFolder.getName() + IPath.SEPARATOR + packageFragment.getElementName();
            RenameSupport support = RenameSupport.create(packageFragment, null,
                    RenameSupport.UPDATE_REFERENCES);
            if (support != null && support.preCheck().isOK()) {
            	//oldScript: custom keyword scripts that contain the current package before renaming.
            	String oldScript = "CustomKeywords.'" + packageFragment.getElementName() + ".";
            	
                support.openDialog(parentShell);
                
                //refresh explorer.
                eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                
                //regenerate CustomKeywords.groovy file.
                KeywordController.getInstance().parseAllCustomKeywords(project, null);
                
                //refresh project to all editors know that change.
                parentPackageFragmentFolder.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());                
                String newPkName = "";
                //after renaming completely, update references of the renamed package.
                for (Object object : parentTreeEntity.getChildren()) {
					String treeEntityPathAfterRenaming = "";
					String newPackageName = ""; 
					if (object instanceof PackageTreeEntity) {
						IPackageFragment childPackageFragment = (IPackageFragment) ((PackageTreeEntity) object)
								.getObject();
						treeEntityPathAfterRenaming = childPackageFragment.getResource().getFullPath().toString();
						newPackageName = childPackageFragment.getElementName();
					}
					
					if (!childrenEntityPathBeforeRenaming.contains(treeEntityPathAfterRenaming)) {
						newPkName = newPackageName;
						//newScript: custom keyword scripts that contain the current package before renaming. 
		        		String newScript = "CustomKeywords.'" + newPackageName + ".";
		        		GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts(oldScript, newScript, 
		        				ProjectController.getInstance().getCurrentProject());
		        		partService.saveAll(false);
						break;
					}
            	}               		
				eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldPackagePath,
						parentPackageFragmentFolder.getName() + IPath.SEPARATOR + newPkName });
            }
        } catch (Exception e) {
        	MessageDialog.openError(null, StringConstants.ERROR_TITLE, 
        			StringConstants.HAND_ERROR_MSG_UNABLE_TO_RENAME_PACKAGE);
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

}
